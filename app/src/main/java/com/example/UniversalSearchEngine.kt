package com.example

import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings
import com.example.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max
import kotlin.math.min

sealed class SearchResult {
    abstract val id: String
    abstract val label: String
    abstract val score: Double

    data class ContactResult(
        override val id: String,
        override val label: String,
        val phoneNumber: String,
        val isRoomUser: Boolean,
        override val score: Double
    ) : SearchResult()

    data class AppResult(
        override val id: String,
        override val label: String,
        val packageName: String,
        override val score: Double
    ) : SearchResult()

    data class WebResult(
        override val id: String,
        override val label: String,
        override val score: Double
    ) : SearchResult()

    data class SettingResult(
        override val id: String,
        override val label: String,
        val action: String,
        override val score: Double
    ) : SearchResult()

    data class FileResult(
        override val id: String,
        override val label: String,
        val size: Long,
        val mimeType: String?,
        override val score: Double
    ) : SearchResult()
}

object JaroWinkler {
    fun similarity(s1: String, s2: String): Double {
        val str1 = s1.lowercase().trim()
        val str2 = s2.lowercase().trim()

        if (str1 == str2) return 1.0
        if (str1.isEmpty() || str2.isEmpty()) return 0.0

        val len1 = str1.length
        val len2 = str2.length
        val matchDistance = max(len1, len2) / 2 - 1

        val hashS1 = BooleanArray(len1)
        val hashS2 = BooleanArray(len2)

        var matches = 0
        for (i in 0 until len1) {
            val start = max(0, i - matchDistance)
            val end = min(len2 - 1, i + matchDistance)
            for (j in start..end) {
                if (!hashS2[j] && str1[i] == str2[j]) {
                    hashS1[i] = true
                    hashS2[j] = true
                    matches++
                    break
                }
            }
        }

        if (matches == 0) return 0.0

        var transpositions = 0
        var k = 0
        for (i in 0 until len1) {
            if (hashS1[i]) {
                while (!hashS2[k]) {
                    k++
                }
                if (str1[i] != str2[k]) {
                    transpositions++
                }
                k++
            }
        }

        val jaro = (matches.toDouble() / len1 + matches.toDouble() / len2 + (matches - transpositions / 2.0) / matches) / 3.0

        // Winkler boost
        val p = 0.1
        var prefixLength = 0
        for (i in 0 until min(4, min(len1, len2))) {
            if (str1[i] == str2[i]) {
                prefixLength++
            } else {
                break
            }
        }

        return jaro + prefixLength * p * (1.0 - jaro)
    }
}

object UnifiedSearchMetrics {
    fun levenshteinDistance(s1: String, s2: String): Int {
        val len1 = s1.length
        val len2 = s2.length
        if (len1 == 0) return len2
        if (len2 == 0) return len1
        val dp = Array(len1 + 1) { IntArray(len2 + 1) }

        for (i in 0..len1) dp[i][0] = i
        for (j in 0..len2) dp[0][j] = j

        for (i in 1..len1) {
            for (j in 1..len2) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = min(
                    min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[len1][len2]
    }

    fun levenshteinSimilarity(s1: String, s2: String): Double {
        val str1 = s1.lowercase().trim()
        val str2 = s2.lowercase().trim()
        if (str1.isEmpty() && str2.isEmpty()) return 1.0
        if (str1.isEmpty() || str2.isEmpty()) return 0.0
        val maxLen = max(str1.length, str2.length)
        val distance = levenshteinDistance(str1, str2)
        return 1.0 - (distance.toDouble() / maxLen)
    }

    fun prefixSimilarity(query: String, candidate: String): Double {
        val q = query.lowercase().trim()
        val cand = candidate.lowercase().trim()
        if (q.isEmpty() || cand.isEmpty()) return 0.0
        if (cand.startsWith(q)) return 1.0
        
        // Check word tokens
        val tokens = cand.split("\\s+".toRegex()).filter { it.isNotEmpty() }
        for (token in tokens) {
            if (token.startsWith(q)) {
                return 0.8
            }
        }
        return 0.0
    }

    fun acronymSimilarity(query: String, candidate: String): Double {
        val q = query.lowercase().replace("\\s+".toRegex(), "")
        if (q.isEmpty()) return 0.0
        val words = candidate.lowercase().split("\\s+".toRegex()).filter { it.isNotEmpty() }
        val initials = words.mapNotNull { it.firstOrNull() }.joinToString("")
        
        if (initials.startsWith(q) || initials.contains(q)) {
            return 1.0
        }
        return 0.0
    }

    fun computeTextSimilarity(query: String, candidate: String): Double {
        val q = query.lowercase().trim()
        val cand = candidate.lowercase().trim()
        if (q.isEmpty()) return 0.0
        if (q == cand) return 1.0

        val sPref = prefixSimilarity(q, cand)
        val sAcronym = acronymSimilarity(q, cand)
        val sLev = levenshteinSimilarity(q, cand)
        
        return max(sPref, max(sAcronym, sLev))
    }
}

data class SearchSetting(val label: String, val action: String)
data class SearchFile(val id: Long, val name: String, val mimeType: String?, val fileSize: Long)
data class RawContact(val id: String, val name: String, val phone: String, val isRoomUser: Boolean = false)

class CacheEntry<T>(val data: T, val timestamp: Long = System.currentTimeMillis()) {
    fun isExpired(maxAgeMs: Long): Boolean {
        return System.currentTimeMillis() - timestamp > maxAgeMs
    }
}

object UniversalSearchEngine {
    
    private const val CACHE_EXPIRY_MS = 15000L // 15 seconds for general query results
    private const val CONTENT_CACHE_EXPIRY_MS = 30000L // 30 seconds for slower provider queries (contacts, files)
    private const val MAX_CACHE_ENTRIES = 120

    // Caches to avoid redundant expensive ContentResolver and filesystem calls
    private val contactsCache = ConcurrentHashMap<String, CacheEntry<List<RawContact>>>()
    private val filesCache = ConcurrentHashMap<String, CacheEntry<List<SearchFile>>>()
    private val searchResultsCache = ConcurrentHashMap<String, CacheEntry<List<SearchResult>>>()

    val systemSettingsList = listOf(
        SearchSetting("Settings > Wi-Fi", Settings.ACTION_WIFI_SETTINGS),
        SearchSetting("Settings > Bluetooth", Settings.ACTION_BLUETOOTH_SETTINGS),
        SearchSetting("Settings > Battery Saver", Settings.ACTION_BATTERY_SAVER_SETTINGS),
        SearchSetting("Settings > Display & Brightness", Settings.ACTION_DISPLAY_SETTINGS),
        SearchSetting("Settings > Manage Apps", Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS),
        SearchSetting("Settings > Language & Locale", Settings.ACTION_LOCALE_SETTINGS),
        SearchSetting("Settings > General Settings", Settings.ACTION_SETTINGS),
        SearchSetting("Dextera Launcher > 120Hz Core Optimization", "launcher_perf"),
        SearchSetting("Dextera Launcher > Touch Gestures & Taps", "launcher_gestures"),
        SearchSetting("Dextera Launcher > System Access & Services", "launcher_permissions"),
        SearchSetting("Dextera Launcher > Search & Discovery", "launcher_search"),
        SearchSetting("Dextera Launcher > Customize Screen Pages", "launcher_pages")
    )

    fun queryDeviceDocuments(context: Context, searchString: String): List<SearchFile> {
        val cached = filesCache[searchString]
        if (cached != null && !cached.isExpired(CONTENT_CACHE_EXPIRY_MS)) {
            return cached.data
        }

        if (context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return emptyList()
        }
        val uri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.SIZE
        )
        val selection = "${MediaStore.Files.FileColumns.DISPLAY_NAME} LIKE ? AND " +
                "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ${MediaStore.Files.FileColumns.MEDIA_TYPE_NONE}"
        val selectionArgs = arrayOf("%$searchString%")
        val records = mutableListOf<SearchFile>()
        try {
            context.contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
                while (cursor.moveToNext()) {
                    records.add(SearchFile(
                        id = cursor.getLong(idCol),
                        name = cursor.getString(nameCol) ?: "Unknown",
                        mimeType = cursor.getString(mimeCol),
                        fileSize = cursor.getLong(sizeCol)
                    ))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Keep cache bounded
        if (filesCache.size >= MAX_CACHE_ENTRIES) {
            filesCache.keys.firstOrNull()?.let { filesCache.remove(it) }
        }
        filesCache[searchString] = CacheEntry(records)
        return records
    }

    private fun getContacts(context: Context, query: String, roomUsers: List<UserEntity>): List<RawContact> {
        val cached = contactsCache[query]
        if (cached != null && !cached.isExpired(CONTENT_CACHE_EXPIRY_MS)) {
            return cached.data
        }

        val list = mutableListOf<RawContact>()

        // Fallback or addition: Add existing DB users so search ALWAYS yields immediate contact results
        roomUsers.forEach {
            if (it.name.contains(query, ignoreCase = true) || it.phoneNumber.contains(query, ignoreCase = true)) {
                list.add(RawContact(id = "room_${it.id}", name = it.name, phone = it.phoneNumber, isRoomUser = true))
            }
        }

        // Search device contacts provider
        if (context.checkSelfPermission(android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            try {
                // Querying common phone directory
                val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI.buildUpon()
                    .appendPath(query)
                    .build()
                val projection = arrayOf(
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                )
                context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                    val idCol = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                    val nameCol = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val numCol = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                    while (cursor.moveToNext()) {
                        val id = if (idCol >= 0) cursor.getString(idCol) else ""
                        val name = if (nameCol >= 0) cursor.getString(nameCol) else ""
                        val number = if (numCol >= 0) cursor.getString(numCol) else ""
                        if (id.isNotEmpty() && name.isNotEmpty()) {
                            list.add(RawContact(id = id, name = name, phone = number, isRoomUser = false))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val finalResult = list.distinctBy { it.name.lowercase() + "_" + it.phone.replace("-", "").trim() }
        if (contactsCache.size >= MAX_CACHE_ENTRIES) {
            contactsCache.keys.firstOrNull()?.let { contactsCache.remove(it) }
        }
        contactsCache[query] = CacheEntry(finalResult)
        return finalResult
    }

    suspend fun executeUniversalSearch(
        context: Context,
        query: String,
        roomUsers: List<UserEntity>,
        installedApps: List<AppInfo>,
        webSuggestions: List<String>
    ): List<SearchResult> = withContext(Dispatchers.IO) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isEmpty()) return@withContext emptyList<SearchResult>()

        val cached = searchResultsCache[query]
        if (cached != null && !cached.isExpired(CACHE_EXPIRY_MS)) {
            return@withContext cached.data
        }

        val sp = context.getSharedPreferences("launcher_search_engagements", Context.MODE_PRIVATE)
        fun getBoost(key: String): Double {
            val clickCount = sp.getInt(key, 0)
            return clickCount * 50000.0 // Generous frecency boost to elevate historically selected items
        }

        val results = mutableListOf<SearchResult>()
        val wSim = 1.0
        val wProp = 0.2

        // Query all sources starting from length >= 1:
        val contacts = getContacts(context, trimmedQuery, roomUsers)

        val apps = installedApps.filter {
            val labelSim = UnifiedSearchMetrics.computeTextSimilarity(trimmedQuery, it.label)
            val pkgSim = UnifiedSearchMetrics.computeTextSimilarity(trimmedQuery, it.packageName)
            it.label.contains(trimmedQuery, ignoreCase = true) ||
            it.packageName.contains(trimmedQuery, ignoreCase = true) ||
            max(labelSim, pkgSim) >= 0.40
        }

        val settings = systemSettingsList.filter {
            val labelClean = it.label.lowercase().replace("[^a-z0-9]".toRegex(), "")
            val queryClean = trimmedQuery.lowercase().replace("[^a-z0-9]".toRegex(), "")
            val sim = UnifiedSearchMetrics.computeTextSimilarity(trimmedQuery, it.label)
            it.label.contains(trimmedQuery, ignoreCase = true) || 
            labelClean.contains(queryClean) || 
            sim > 0.4
        }

        val web = run {
            val list = webSuggestions.toMutableList()
            if (!list.contains(trimmedQuery)) {
                list.add(0, trimmedQuery)
            }
            list
        }

        // Multipliers corresponding strictly to specified hierarchy ranks
        val mApps = 1_000_000.0       // Apps (Rank 1)
        val mContacts = 10_000.0      // Contacts (Rank 2)
        val mWeb = 100.0              // Web (Rank 3)
        val mFilesSettings = 1.0      // Files & Settings (Rank 4)

        // 1. Process Contacts
        contacts.forEach { contact ->
            val nameSim = UnifiedSearchMetrics.computeTextSimilarity(trimmedQuery, contact.name)
            val phoneSim = UnifiedSearchMetrics.computeTextSimilarity(trimmedQuery, contact.phone)
            val maxSim = max(nameSim, phoneSim)
            val propWeight = if (nameSim >= phoneSim) 1.0 else 0.5
            val score = (wSim * maxSim + wProp * propWeight) * mContacts + getBoost("contact_${contact.id}")
            results.add(
                SearchResult.ContactResult(
                    id = contact.id,
                    label = contact.name,
                    phoneNumber = contact.phone,
                    isRoomUser = contact.isRoomUser,
                    score = score
                )
            )
        }

        // 2. Process Apps
        apps.forEach { app ->
            val labelSim = UnifiedSearchMetrics.computeTextSimilarity(trimmedQuery, app.label)
            val pkgSim = UnifiedSearchMetrics.computeTextSimilarity(trimmedQuery, app.packageName)
            val maxSim = max(labelSim, pkgSim)
            val propWeight = if (labelSim >= pkgSim) 1.0 else 0.5
            val labelPrefixBoost = if (app.label.startsWith(trimmedQuery, ignoreCase = true)) 10_000_000.0
                                   else if (app.label.contains(trimmedQuery, ignoreCase = true)) 5_000_000.0
                                   else 0.0
            val score = (wSim * maxSim + wProp * propWeight) * mApps + labelPrefixBoost + getBoost("app_${app.packageName}")
            results.add(
                SearchResult.AppResult(
                    id = app.packageName,
                    label = app.label,
                    packageName = app.packageName,
                    score = score
                )
            )
        }

        // 3. Process Web Suggestions
        web.forEach { item ->
            val sim = UnifiedSearchMetrics.computeTextSimilarity(trimmedQuery, item)
            val score = (wSim * sim + wProp * 1.0) * mWeb + getBoost("web_$item")
            results.add(
                SearchResult.WebResult(
                    id = item,
                    label = item,
                    score = score
                )
            )
        }

        // 4. Process Settings
        settings.forEach { setting ->
            val sim = UnifiedSearchMetrics.computeTextSimilarity(trimmedQuery, setting.label)
            val score = (wSim * sim + wProp * 1.0) * mFilesSettings + getBoost("setting_${setting.action}")
            results.add(
                SearchResult.SettingResult(
                    id = setting.action,
                    label = setting.label,
                    action = setting.action,
                    score = score
                )
            )
        }

        // Define category sorting precedence rank:
        // 0: Apps (highest)
        // 1: Contacts
        // 2: Web
        // 3: Settings (System shortcuts)
        // 4: Files (or others fallback)
        val categoryRank = { res: SearchResult ->
            when (res) {
                is SearchResult.AppResult -> 0
                is SearchResult.ContactResult -> 1
                is SearchResult.WebResult -> 2
                is SearchResult.SettingResult -> 3
                is SearchResult.FileResult -> 4
            }
        }

        // Read user's configured setting limit
        val maxResults = context.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE)
            .getInt("search_results_limit", 5)

        // Trim each category individually to maxResults rather than doing a global flat truncation
        val appsResults = results.filterIsInstance<SearchResult.AppResult>().sortedByDescending { it.score }.take(maxResults)
        val contactsResults = results.filterIsInstance<SearchResult.ContactResult>().sortedByDescending { it.score }.take(maxResults)
        val webResults = results.filterIsInstance<SearchResult.WebResult>().sortedByDescending { it.score }.take(maxResults)
        val settingsResults = results.filter { it is SearchResult.SettingResult || it is SearchResult.FileResult }.sortedWith(
            compareBy<SearchResult> { categoryRank(it) }.thenByDescending { it.score }
        ).take(maxResults)

        val trimmedResults = appsResults + contactsResults + webResults + settingsResults

        // Keep cache bounded
        if (searchResultsCache.size >= MAX_CACHE_ENTRIES) {
            searchResultsCache.keys.firstOrNull()?.let { searchResultsCache.remove(it) }
        }
        searchResultsCache[query] = CacheEntry(trimmedResults)
        trimmedResults
    }

    fun recordSelection(context: Context, result: SearchResult) {
        val sp = context.getSharedPreferences("launcher_search_engagements", Context.MODE_PRIVATE)
        val key = when (result) {
            is SearchResult.ContactResult -> "contact_${result.id}"
            is SearchResult.AppResult -> "app_${result.packageName}"
            is SearchResult.WebResult -> "web_${result.label}"
            is SearchResult.SettingResult -> "setting_${result.action}"
            is SearchResult.FileResult -> "file_${result.id}"
        }
        val current = sp.getInt(key, 0)
        sp.edit().putInt(key, current + 1).apply()

        // Invalidate cached query results to reflect updated scoring frecency boosts immediately
        searchResultsCache.clear()
    }
}
