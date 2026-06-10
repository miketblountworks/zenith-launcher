package com.example

import android.app.AppOpsManager
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.view.Choreographer
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.core.content.edit
import androidx.core.os.BundleCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.launcher.LauncherState
import com.example.model.WidgetData
import com.example.service.MyNotificationListenerService
import com.example.ui.pages.DexteraLauncherApp
import com.example.ui.screens.OnboardingScreen
import com.example.utils.OnboardingManager
import com.example.widgets.CustomAppWidgetHost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

class MainActivity : ComponentActivity() {
    lateinit var database: AppDatabase
    lateinit var userRepository: UserRepository

    lateinit var appWidgetHost: CustomAppWidgetHost
    lateinit var appWidgetManager: AppWidgetManager
    
    private val onboardingManager by lazy { OnboardingManager(this) }

    val _longPressedWidgetId get() = LauncherState._longPressedWidgetId
    val draggingWidgetId get() = LauncherState.draggingWidgetId
    val dragOffsetX get() = LauncherState.dragOffsetX
    val dragOffsetY get() = LauncherState.dragOffsetY

    val _widgetDataList get() = LauncherState._widgetDataList
    val widgetDataList: StateFlow<List<WidgetData>> get() = LauncherState.widgetDataList
    val widgetTargetPage get() = LauncherState.widgetTargetPage

    val activePages: MutableStateFlow<List<String>> get() = LauncherState.activePages
    val mediaTrackInfo = MyNotificationListenerService.mediaFlow

    fun saveActivePages(pages: List<String>) {
        activePages.value = pages
        getSharedPreferences("launcher_settings", MODE_PRIVATE).edit {
            putString("active_pages", pages.joinToString(","))
        }
    }

    fun dispatchMediaKey(keyCode: Int) {
        com.example.media.MediaController.dispatchMediaKey(this, keyCode)
    }

    val _dispatchMediaKey = ::dispatchMediaKey // suppress unused
    val _generateLocationBasedLandscape = ::generateLocationBasedLandscape // suppress unused
    val _swapWidgets = ::swapWidgets // suppress unused
    val _startDragging = ::startDragging // suppress unused
    val _updateDragOffset = ::updateDragOffset // suppress unused
    val _endDragging = ::endDragging // suppress unused
    val _hasUsageStatsPermission = ::hasUsageStatsPermission // suppress unused

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_HOME) return true
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            homeEntryTrigger.value += 1
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    val isAIGenerating = MutableStateFlow(false)

    fun generateLocationBasedLandscape(currentWallpaper: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            isAIGenerating.value = true
            try {
                val location = withContext(Dispatchers.IO) {
                    try {
                        val lm = getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
                        @Suppress("MissingPermission")
                        lm.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER)
                    } catch (_: Exception) { null }
                }
                
                val locationName = if (location != null) {
                    val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    addresses?.firstOrNull()?.locality ?: "Nature"
                } else "Nature"

                val prompt = "A beautiful high resolution landscape of $locationName, cinematic lighting, 8k"
                val outputFile = filesDir.resolve("ai_location_landscape.jpg")
                val success = com.example.ui.ai.GeminiImageGenerator.generateWallpaper(prompt, outputFile)
                
                if (success) {
                    withContext(Dispatchers.Main) {
                        selectedWallpaper.value = "AI Generated Location"
                        getSharedPreferences("launcher_settings", MODE_PRIVATE).edit {
                            putString("wallpaper", "AI Generated Location")
                        }
                        decodeAndExtractWallpaperColor()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isAIGenerating.value = false
            }
        }
    }

    val animatingAppPackage = MutableStateFlow<String?>(null)
    val animatingAppIconBounds = MutableStateFlow<Rect?>(null)
    val appTransitionProgress = MutableStateFlow(0f)
    val isLaunchingApp = MutableStateFlow(false)
    val isClosingApp = MutableStateFlow(false)
    val appIconBoundsMap = ConcurrentHashMap<String, Rect>()

    val selectedWallpaper = MutableStateFlow("Pitch Black")
    val clockStyle = MutableStateFlow("Dextera Date")
    val selectedFont = MutableStateFlow("System Default")
    val iconPack = MutableStateFlow("Classic")
    val themeMode = MutableStateFlow("Dark")
    val materialYouEnabled = MutableStateFlow(true)
    val declutterMode = MutableStateFlow(false)
    val categoriseByUsage = MutableStateFlow(true)
    val usageLimitCount = MutableStateFlow(6)
    val appUsageScores = MutableStateFlow<Map<String, Long>>(emptyMap())
    val gesturesEnabled = MutableStateFlow(true)
    val usageBreakerMinutes = MutableStateFlow(5)
    val hiddenApps = MutableStateFlow<Set<String>>(emptySet())
    val notificationSummaryEnabled = MutableStateFlow(true)
    val use24HourFormat = MutableStateFlow(false)
    val useFahrenheit = MutableStateFlow(true)
    val dynamicIconColorEnabled = MutableStateFlow(true)
    val leftHandedScrollEnabled = MutableStateFlow(false)
    val bingWallpaperUrl = MutableStateFlow("")
    val wallpaperBlurEnabled = MutableStateFlow(false)
    val extractedWallpaperColor = MutableStateFlow<Color?>(null)
    val allowedNotificationCategories = MutableStateFlow<Set<String>>(
        setOf("Finance 💰", "Travel ✈️", "Social 💬", "Internet 🌐", "Entertainment 🎵", "Shopping 🛍️", "General 📦")
    )

    val isNotificationPermissionGranted = MutableStateFlow(false)
    val isLocationPermissionGranted = MutableStateFlow(false)
    val webSuggestions = MutableStateFlow<List<String>>(emptyList())

    val overdrawElimination = MutableStateFlow(false)
    val asyncImageDecoding = MutableStateFlow(true)
    val jvmZgcConfiguration = MutableStateFlow(false)
    val adaptiveFpsThermal = MutableStateFlow(true)
    val syntheticClickDelay = MutableStateFlow(false)
    val touchInterpolation = MutableStateFlow(true)
    val cpuThreadAffinity = MutableStateFlow(false)
    val asyncVfsMmap = MutableStateFlow(false)

    val liveFps = MutableStateFlow(0.0)
    val currentThermalStatus = MutableStateFlow("Optimal")
    val adaptiveVsyncTarget = MutableStateFlow("120Hz")
    val isPerformanceFpsActive = MutableStateFlow(false)

    val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        updatePermissionStates()
    }

    val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    contentResolver.openInputStream(uri)?.use { input ->
                        filesDir.resolve("local_wallpaper.jpg").outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    selectedWallpaper.value = "Local Image"
                    getSharedPreferences("launcher_settings", MODE_PRIVATE).edit { putString("wallpaper", "Local Image") }
                    decodeAndExtractWallpaperColor()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun pickLocalWallpaper() {
        imagePickerLauncher.launch("image/*")
    }

    fun fetchBingWallpaper() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val url = "https://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US"
                val response = java.net.URL(url).readText()
                val json = org.json.JSONObject(response)
                val images = json.getJSONArray("images")
                if (images.length() > 0) {
                    val imageUrl = images.getJSONObject(0).getString("url")
                    val fullUrl = "https://www.bing.com$imageUrl"
                    
                    val bitmap = BitmapFactory.decodeStream(java.net.URL(fullUrl).openStream())
                    filesDir.resolve("bing_wallpaper.jpg").outputStream().use { 
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
                    }
                    
                    withContext(Dispatchers.Main) {
                        bingWallpaperUrl.value = fullUrl
                        selectedWallpaper.value = "Bing Daily"
                        getSharedPreferences("launcher_settings", MODE_PRIVATE).edit {
                            putString("bing_wallpaper_url", fullUrl)
                            putString("wallpaper", "Bing Daily")
                        }
                        decodeAndExtractWallpaperColor()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val isAIGeneratingImage = MutableStateFlow(false)

    fun generateStableDiffusionWallpaper(prompt: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            isAIGeneratingImage.value = true
            val outputFile = filesDir.resolve("ai_wallpaper.jpg")
            val success = com.example.ui.ai.GeminiImageGenerator.generateWallpaper(prompt, outputFile)
            if (success) {
                withContext(Dispatchers.Main) {
                    selectedWallpaper.value = "AI Generated"
                    getSharedPreferences("launcher_settings", MODE_PRIVATE).edit {
                        putString("wallpaper", "AI Generated")
                    }
                    decodeAndExtractWallpaperColor()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "AI Generation Failed", Toast.LENGTH_SHORT).show()
                }
            }
            isAIGeneratingImage.value = false
        }
    }

    fun decodeAndExtractWallpaperColor() {
        lifecycleScope.launch(Dispatchers.IO) {
            val wall = selectedWallpaper.value
            val bitmap = when (wall) {
                "Local Image" -> {
                    val file = filesDir.resolve("local_wallpaper.jpg")
                    if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
                }
                "AI Generated" -> {
                    val file = filesDir.resolve("ai_wallpaper.jpg")
                    if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
                }
                "AI Generated Location" -> {
                    val file = filesDir.resolve("ai_location_landscape.jpg")
                    if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
                }
                "Bing Daily" -> {
                    val file = filesDir.resolve("bing_wallpaper.jpg")
                    if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
                }
                else -> null
            }
            
            if (bitmap != null) {
                val dominantColor = extractDominantColor(bitmap)
                withContext(Dispatchers.Main) {
                    extractedWallpaperColor.value = dominantColor
                }
            } else {
                withContext(Dispatchers.Main) {
                    extractedWallpaperColor.value = null
                }
            }
        }
    }

    private fun extractDominantColor(bitmap: Bitmap): Color {
        return try {
            val resized = Bitmap.createScaledBitmap(bitmap, 16, 16, false)
            var rSum = 0
            var gSum = 0
            var bSum = 0
            var count = 0
            for (x in 0 until 16) {
                for (y in 0 until 16) {
                    val pixel = resized.getPixel(x, y)
                    val a = android.graphics.Color.alpha(pixel)
                    if (a > 128) {
                        rSum += android.graphics.Color.red(pixel)
                        gSum += android.graphics.Color.green(pixel)
                        bSum += android.graphics.Color.blue(pixel)
                        count++
                    }
                }
            }
            if (count == 0) return Color(0xFF8C9EFF)
            val avgR = rSum / count
            val avgG = gSum / count
            val avgB = bSum / count
            
            val hsv = FloatArray(3)
            android.graphics.Color.RGBToHSV(avgR, avgG, avgB, hsv)
            hsv[1] = (hsv[1] * 1.2f).coerceIn(0.4f, 0.8f)
            hsv[2] = (hsv[2] * 1.1f).coerceIn(0.5f, 0.9f)
            
            val rgb = android.graphics.Color.HSVToColor(hsv)
            Color(rgb)
        } catch (_: Exception) {
            Color(0xFF8C9EFF)
        }
    }

    private var lastFrameTimeNanos: Long = 0
    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (lastFrameTimeNanos > 0) {
                val diff = frameTimeNanos - lastFrameTimeNanos
                val fps = 1_000_000_000.0 / diff
                liveFps.value = fps
            }
            lastFrameTimeNanos = frameTimeNanos
            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    fun startChoreographerMonitoring() {
        Choreographer.getInstance().postFrameCallback(frameCallback)
    }

    fun stopChoreographerMonitoring() {
        Choreographer.getInstance().removeFrameCallback(frameCallback)
    }

    fun registerThermalListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val powerManager = getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
                powerManager.addThermalStatusListener { status ->
                    currentThermalStatus.value = when (status) {
                        android.os.PowerManager.THERMAL_STATUS_NONE -> "Optimal"
                        android.os.PowerManager.THERMAL_STATUS_LIGHT -> "Light Throttling"
                        android.os.PowerManager.THERMAL_STATUS_MODERATE -> "Moderate Throttling"
                        android.os.PowerManager.THERMAL_STATUS_SEVERE -> "Severe Throttling"
                        android.os.PowerManager.THERMAL_STATUS_CRITICAL -> "Critical Throttling"
                        android.os.PowerManager.THERMAL_STATUS_EMERGENCY -> "Emergency Shutdown"
                        android.os.PowerManager.THERMAL_STATUS_SHUTDOWN -> "Hardware Shutdown"
                        else -> "Unknown"
                    }
                    
                    if (adaptiveFpsThermal.value) {
                        when (status) {
                            android.os.PowerManager.THERMAL_STATUS_SEVERE,
                            android.os.PowerManager.THERMAL_STATUS_CRITICAL,
                            android.os.PowerManager.THERMAL_STATUS_EMERGENCY -> {
                                adaptiveVsyncTarget.value = "60Hz"
                            }
                            android.os.PowerManager.THERMAL_STATUS_MODERATE -> {
                                adaptiveVsyncTarget.value = "90Hz"
                            }
                            else -> {
                                adaptiveVsyncTarget.value = "120Hz"
                            }
                        }
                    }
                }
            } catch (_: Exception) {
                currentThermalStatus.value = "Unmanaged (Pre-Q)"
            }
        } else {
            currentThermalStatus.value = "Unmanaged (Pre-Q)"
        }
    }

    val notificationList = MyNotificationListenerService.notificationsFlow

    val folderMapState = MutableStateFlow<Map<String, List<String>>>(
        mapOf(
            "Social" to emptyList(),
            "Utilities" to emptyList(),
            "Media" to emptyList()
        )
    )

    val pickWidgetLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val widgetId = data?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1) ?: -1
            if (widgetId != -1) addWidget(widgetId)
        }
    }

    val configureWidgetLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val widgetId = result.data?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1) ?: -1
            if (widgetId != -1) {
                val current = _widgetDataList.value.toMutableList()
                val idx = current.indexOfFirst { it.id == widgetId }
                if (idx != -1) {
                    current[idx] = current[idx].copy(revision = current[idx].revision + 1)
                    _widgetDataList.value = current
                    saveWidgets(current)
                }
            }
        }
    }

    var configuringWidgetId = -1

    val reconfigureWidgetLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val current = _widgetDataList.value.toMutableList()
            val idx = current.indexOfFirst { it.id == configuringWidgetId }
            if (idx != -1) {
                current[idx] = current[idx].copy(revision = current[idx].revision + 1)
                _widgetDataList.value = current
                saveWidgets(current)
            }
        }
        configuringWidgetId = -1
    }

    fun addWidget(widgetId: Int) {
        val info = appWidgetManager.getAppWidgetInfo(widgetId)
        if (info?.configure != null) {
            configuringWidgetId = widgetId
            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE).apply {
                component = info.configure
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            }
            configureWidgetLauncher.launch(intent)
        }
        val current = _widgetDataList.value.toMutableList()
        current.add(WidgetData(widgetId, pageName = widgetTargetPage.value))
        _widgetDataList.value = current
        saveWidgets(current)
    }

    fun removeWidget(widgetId: Int) {
        val current = _widgetDataList.value.toMutableList()
        current.removeAll { it.id == widgetId }
        _widgetDataList.value = current
        saveWidgets(current)
        appWidgetHost.deleteAppWidgetId(widgetId)
    }

    fun updateWidgetSize(id: Int, w: Int, h: Int) {
        val current = _widgetDataList.value.toMutableList()
        val idx = current.indexOfFirst { it.id == id }
        if (idx != -1) {
            current[idx] = current[idx].copy(widthSpan = w, heightSpan = h)
            _widgetDataList.value = current
            saveWidgets(current)
        }
    }

    fun swapWidgets(id1: Int, id2: Int) {
        val current = _widgetDataList.value.toMutableList()
        val idx1 = current.indexOfFirst { it.id == id1 }
        val idx2 = current.indexOfFirst { it.id == id2 }
        if (idx1 != -1 && idx2 != -1) {
            val temp = current[idx1]
            current[idx1] = current[idx2]
            current[idx2] = temp
            _widgetDataList.value = current
            saveWidgets(current)
        }
    }

    fun startDragging(id: Int) {
        draggingWidgetId.value = id
    }

    fun updateDragOffset(id: Int, x: Float, y: Float, screenX: Float, screenY: Float) {
        dragOffsetX.value = x
        dragOffsetY.value = y
    }

    fun endDragging(id: Int) {
        draggingWidgetId.value = null
        dragOffsetX.value = 0f
        dragOffsetY.value = 0f
    }

    private fun saveWidgets(list: List<WidgetData>) {
        val str = list.joinToString(",") { "${it.id}:${it.widthSpan}:${it.heightSpan}:${it.pageName}" }
        getSharedPreferences("launcher", MODE_PRIVATE).edit {
            putString("widgets_v2", str)
        }
    }

    fun hasUsageStatsPermission(context: Context): Boolean {
        return try {
            val appOps = context.getSystemService(APP_OPS_SERVICE) as AppOpsManager
            @Suppress("DEPRECATION")
            val mode = appOps.noteOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
            mode == AppOpsManager.MODE_ALLOWED
        } catch (_: Exception) {
            false
        }
    }

    fun refreshAppUsageScores() {
        @Suppress("DEPRECATION")
        if (checkSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) != PackageManager.PERMISSION_GRANTED) {
            // Silently skip if no permission, or we could request it. 
            // In a launcher, usually user has to grant this manually in settings.
            return
        }
        lifecycleScope.launch(Dispatchers.IO) {
            val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as android.app.usage.UsageStatsManager
            val endTime = System.currentTimeMillis()
            val startTime = endTime - 1000 * 60 * 60 * 24 * 7 // Last 7 days
            
            val stats = usageStatsManager.queryUsageStats(android.app.usage.UsageStatsManager.INTERVAL_WEEKLY, startTime, endTime)
            val scoreMap = mutableMapOf<String, Long>()
            
            val localLaunchPrefs = getSharedPreferences("launcher_local_launches", MODE_PRIVATE)
            
            if (stats != null) {
                for (usageStat in stats) {
                    val pkg = usageStat.packageName
                    val time = usageStat.totalTimeInForeground
                    val localCount = localLaunchPrefs.getLong(pkg, 0L)
                    scoreMap[pkg] = time + (localCount * 1000 * 60 * 5) // Boost by 5 min per local launch
                }
            }
            
            withContext(Dispatchers.Main) {
                appUsageScores.value = scoreMap
            }
        }
    }

    fun incrementLocalLaunchCount(packageName: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val prefs = getSharedPreferences("launcher_local_launches", MODE_PRIVATE)
            val current = prefs.getLong(packageName, 0L)
            prefs.edit { putLong(packageName, current + 1L) }
            refreshAppUsageScores()
        }
    }

    fun launchAppWithTracker(packageName: String) {
        launchAppWithSourceBoundsInternal(packageName)
    }

    private fun launchAppWithSourceBoundsInternal(packageName: String) {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            try {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(launchIntent)
                incrementLocalLaunchCount(packageName)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updatePermissionStates()
        refreshAppUsageScores()
    }

    fun updatePermissionStates() {
        isNotificationPermissionGranted.value = MyNotificationListenerService.isConnected
        isLocationPermissionGranted.value = checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private var suggestionJob: Job? = null
    fun fetchWebSuggestions(query: String) {
        suggestionJob?.cancel()
        if (query.trim().isEmpty()) {
            webSuggestions.value = emptyList()
            return
        }
        
        suggestionJob = lifecycleScope.launch(Dispatchers.IO) {
            delay(200)
            try {
                val encoded = java.net.URLEncoder.encode(query, "UTF-8")
                val url = "https://suggestqueries.google.com/complete/search?client=firefox&q=$encoded"
                val response = java.net.URL(url).readText()
                val json = org.json.JSONArray(response)
                val suggestions = json.getJSONArray(1)
                val list = mutableListOf<String>()
                for (i in 0 until suggestions.length()) {
                    list.add(suggestions.getString(i))
                }
                withContext(Dispatchers.Main) {
                    webSuggestions.value = list
                }
            } catch (_: Exception) {}
        }
    }

    val homeEntryTrigger = MutableStateFlow(0)

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (Intent.ACTION_MAIN == intent.action && intent.hasCategory(Intent.CATEGORY_HOME)) {
            homeEntryTrigger.value += 1
        }
        handleGestureNavContract(intent)
    }

    private fun handleGestureNavContract(intent: Intent) {
        val contractBundle = intent.getBundleExtra("gesture_nav_contract_v1") ?: return
        val componentName = BundleCompat.getParcelable(contractBundle, "gesture_nav_contract_component", ComponentName::class.java) ?: return
        val callbackMessage = BundleCompat.getParcelable(contractBundle, "android.intent.extra.REMOTE_CALLBACK", Message::class.java) ?: return
        
        val targetIconBounds = locateIconOnGrid(componentName)
        
        val resultBundle = Bundle()
        resultBundle.putParcelable("gesture_nav_contract_icon_bounds", targetIconBounds)
        val reply = Message.obtain()
        reply.copyFrom(callbackMessage)
        reply.data = resultBundle
        try {
            reply.replyTo.send(reply)
        } catch (_: Exception) {}
    }

    private fun locateIconOnGrid(componentName: ComponentName): Rect {
        return appIconBoundsMap[componentName.packageName] ?: Rect(0,0,0,0)
    }

    private fun registerBackCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                android.window.OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                // Handle back logic if needed, or let system handle it
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        database = AppDatabase.getDatabase(this)
        userRepository = UserRepository(database.userDao())
        
        appWidgetManager = AppWidgetManager.getInstance(this)
        appWidgetHost = CustomAppWidgetHost(this, 1024)
        appWidgetHost.startListening()

        val prefs = getSharedPreferences("launcher_settings", MODE_PRIVATE)
        selectedWallpaper.value = prefs.getString("wallpaper", "Pitch Black") ?: "Pitch Black"
        clockStyle.value = prefs.getString("clock_style", "Dextera Date") ?: "Dextera Date"
        selectedFont.value = prefs.getString("font", "System Default") ?: "System Default"
        iconPack.value = prefs.getString("icon_pack", "Classic") ?: "Classic"
        materialYouEnabled.value = prefs.getBoolean("material_you", true)
        declutterMode.value = prefs.getBoolean("declutter_mode", false)
        categoriseByUsage.value = prefs.getBoolean("categorise_by_usage", true)
        usageLimitCount.value = prefs.getInt("usage_limit_count", 6)
        gesturesEnabled.value = prefs.getBoolean("gestures_enabled", true)
        usageBreakerMinutes.value = prefs.getInt("usage_breaker_min", 5)
        hiddenApps.value = prefs.getStringSet("hidden_packages", emptySet()) ?: emptySet()
        notificationSummaryEnabled.value = prefs.getBoolean("notification_summary", true)
        use24HourFormat.value = prefs.getBoolean("use_24_hour", false)
        useFahrenheit.value = prefs.getBoolean("use_fahrenheit", true)
        dynamicIconColorEnabled.value = prefs.getBoolean("dynamic_icon_color", true)
        bingWallpaperUrl.value = prefs.getString("bing_wallpaper_url", "") ?: ""
        wallpaperBlurEnabled.value = prefs.getBoolean("wallpaper_blur", false)
        allowedNotificationCategories.value = prefs.getStringSet("allowed_notification_categories", allowedNotificationCategories.value) ?: allowedNotificationCategories.value
        
        val activePagesStr = prefs.getString("active_pages", "App List,Music,Notifications") ?: "App List,Music,Notifications"
        activePages.value = activePagesStr.split(",").filter { it.isNotEmpty() }

        val widgetPrefs = getSharedPreferences("launcher", MODE_PRIVATE)
        val widgetsStr = widgetPrefs.getString("widgets_v2", "") ?: ""
        if (widgetsStr.isNotEmpty()) {
            val list = widgetsStr.split(",").mapNotNull {
                val parts = it.split(":")
                if (parts.size >= 4) {
                    WidgetData(parts[0].toInt(), parts[1].toInt(), parts[2].toInt(), parts[3])
                } else null
            }
            _widgetDataList.value = list
        }

        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                val isOnboardingCompleted by onboardingManager.isOnboardingCompleted
                    .collectAsStateWithLifecycle(initialValue = null)

                Crossfade(targetState = isOnboardingCompleted, label = "onboarding_fade") { completed ->
                    when (completed) {
                        null -> { /* Loading state if needed */ }
                        false -> {
                            OnboardingScreen(onFinish = {
                                lifecycleScope.launch {
                                    onboardingManager.setOnboardingCompleted(true)
                                }
                            })
                        }
                        true -> {
                            DexteraLauncherApp()
                        }
                    }
                }
            }
        }
        
        decodeAndExtractWallpaperColor()
        registerThermalListener()
        startChoreographerMonitoring()
    }

    override fun onDestroy() {
        super.onDestroy()
        appWidgetHost.stopListening()
        stopChoreographerMonitoring()
    }
}
