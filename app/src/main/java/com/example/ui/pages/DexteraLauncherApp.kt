package com.example.ui.pages

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.MainActivity
import com.example.SearchResult
import com.example.UniversalSearchEngine
import com.example.UserEntity
import com.example.model.AppInfo
import com.example.model.ListEntry
import com.example.ui.components.AdvancedSearchBar
import com.example.ui.components.AlphabetHeaderRow
import com.example.ui.components.AppContextMenuOverlay
import com.example.ui.components.AppRow
import com.example.ui.components.CategoryHeader
import com.example.ui.components.ClockStyleWidget
import com.example.ui.components.FolderExpandCard
import com.example.ui.components.StyledAppIcon
import com.example.ui.components.UsageBreakerOverlay
import com.example.ui.components.UserProfileScreen
import com.example.ui.components.WakeSleepScreen
import com.example.ui.settings.SettingsPanel
import com.example.viewmodel.LauncherViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun DexteraLauncherApp(modifier: Modifier = Modifier, viewModel: LauncherViewModel = viewModel()) {
    val context = LocalContext.current
    val letters = remember { ('A'..'Z').toList() }
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val alphabetListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val isImeVisible = WindowInsets.ime.getBottom(density) > 0
    
    val activity = remember(context) {
        var cur = context
        var result: MainActivity? = null
        while (cur is ContextWrapper) {
            if (cur is MainActivity) {
                result = cur
                break
            }
            cur = cur.baseContext
        }
        result ?: error("Context must be MainActivity")
    }
    
    // Config state collectors
    val selectedWallpaper by activity.selectedWallpaper.collectAsState()
    val clockStyleVal by activity.clockStyle.collectAsState()
    val selectedFontVal by activity.selectedFont.collectAsState()
    val iconPackVal by activity.iconPack.collectAsState()
    val materialYouEnabledVal by activity.materialYouEnabled.collectAsState()
    val declutterModeVal by activity.declutterMode.collectAsState()
    val categoriseByUsageVal by activity.categoriseByUsage.collectAsState()
    val usageLimitCountVal by activity.usageLimitCount.collectAsState()
    val appUsageScoresVal by activity.appUsageScores.collectAsState()
    val gesturesEnabledVal by activity.gesturesEnabled.collectAsState()
    val hiddenAppsSet by activity.hiddenApps.collectAsState()
    val folderMap by activity.folderMapState.collectAsState()
    val isLocationGrantedVal by activity.isLocationPermissionGranted.collectAsState()
    val use24HourFormatVal by activity.use24HourFormat.collectAsState()
    val useFahrenheitVal by activity.useFahrenheit.collectAsState()
    val bingWallpaperUrlVal by activity.bingWallpaperUrl.collectAsState()
    val wallpaperBlurEnabledVal by activity.wallpaperBlurEnabled.collectAsState()
    val extractedWallpaperColorVal by activity.extractedWallpaperColor.collectAsState()
    val allowedNotificationCategoriesVal by activity.allowedNotificationCategories.collectAsState()
    
    val wallpaperLuminance by activity.wallpaperLuminance.collectAsState()

    // === Adaptive text / UI colors based on wallpaper luminance ===
    // If the background (wallpaper) is light, use dark text for readability.
    // If dark, use light text. This makes the launcher text react to the background.
    val isLightBackground = wallpaperLuminance > 0.52f
    val adaptiveTextColor = if (isLightBackground) Color(0xFF1C1C1E) else Color.White
    val adaptiveShadowColor = if (isLightBackground) Color.White.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.6f)
    val adaptiveTextSecondary = if (isLightBackground) Color(0xFF3A3A3C) else Color.White.copy(alpha = 0.72f)
    val adaptiveTextMuted = if (isLightBackground) Color(0xFF5C5C5E) else Color.White.copy(alpha = 0.55f)
    val adaptiveIconTint = adaptiveTextColor

    // Adaptive "glassmorphic" / frosted elements (chips, cards, search scrims)
    val adaptiveGlassBg = if (isLightBackground) Color.Black.copy(alpha = 0.055f) else Color.White.copy(alpha = 0.12f)
    val adaptiveGlassBorder = if (isLightBackground) Color.Black.copy(alpha = 0.11f) else Color.White.copy(alpha = 0.08f)

    // Adaptive scrim: lighter touch on bright wallpapers so dark text remains readable without over-darkening the wallpaper
    val scrimAlpha by animateFloatAsState(
        targetValue = if (isLightBackground) {
            0.022f + (wallpaperLuminance * 0.038f)
        } else {
            0.10f + (wallpaperLuminance * 0.08f)
        },
        animationSpec = tween(600),
        label = "scrimAlpha"
    )
    
    val activePagesVal by activity.activePages.collectAsState()
    val mediaTrackInfoVal by activity.mediaTrackInfo.collectAsState()
    val notificationsListState by activity.notificationList.collectAsState()
    val widgetDataListVal by activity.widgetDataList.collectAsState()
    var currentPageIndex by remember { mutableIntStateOf(0) }
    
    val displayedPages = remember(activePagesVal, mediaTrackInfoVal) {
        val filtered = activePagesVal.filter { page ->
            if (page == "Music") mediaTrackInfoVal != null else true
        }
        if (filtered.isEmpty()) listOf("App List") else filtered
    }
    
    LaunchedEffect(displayedPages) {
        if (currentPageIndex >= displayedPages.size) {
            currentPageIndex = (displayedPages.size - 1).coerceAtLeast(0)
        }
    }
    
    val hapticFeedback = LocalHapticFeedback.current

    // Navigation / State flags
    var searchQuery by remember { mutableStateOf("") }
    var isSearchFocused by remember { mutableStateOf(false) }
    var activeSearchCategoryFilter by remember { mutableStateOf("All") }
    var searchResults by remember { mutableStateOf(emptyList<SearchResult>()) }

    val displayedResults = remember(searchResults, activeSearchCategoryFilter) {
        if (activeSearchCategoryFilter == "All") {
            searchResults
        } else {
            searchResults.filter { result ->
                when (activeSearchCategoryFilter) {
                    "Contacts" -> result is SearchResult.ContactResult
                    "Apps" -> result is SearchResult.AppResult
                    "Web" -> result is SearchResult.WebResult
                    "Settings & Files" -> result is SearchResult.SettingResult || result is SearchResult.FileResult
                    else -> true
                }
            }
        }
    }
    
    val allUsersVal by activity.userRepository.allUsers.collectAsState(initial = emptyList())
    val webSuggestionsVal by activity.webSuggestions.collectAsState()

    val contextForSearch = LocalContext.current
    LaunchedEffect(searchQuery, allUsersVal, uiState.apps, webSuggestionsVal) {
        if (searchQuery.trim().isEmpty()) {
            searchResults = emptyList()
        } else {
            delay(150)
            searchResults = UniversalSearchEngine.executeUniversalSearch(
                context = contextForSearch,
                query = searchQuery,
                roomUsers = allUsersVal,
                installedApps = uiState.apps,
                webSuggestions = webSuggestionsVal
            )
        }
    }

    var showAddUserDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<UserEntity?>(null) }
    var focusedContextMenuApp by remember { mutableStateOf<AppInfo?>(null) }
    var expandedFolderPackageName by remember { mutableStateOf<String?>(null) }
    var currentFolderAppsPackageList by remember { mutableStateOf<List<String>?>(null) }
    var showSettingsPanel by remember { mutableStateOf(false) }
    var activeSettingsCategory by remember { mutableStateOf<String?>(null) }
    var activeBreakerApp by remember { mutableStateOf<AppInfo?>(null) }
    var isSleepingState by remember { mutableStateOf(false) }
    var zoomLevel by remember { mutableIntStateOf(0) }
    var hoveredApp by remember { mutableStateOf<AppInfo?>(null) }
    var highlightedApp by remember { mutableStateOf<AppInfo?>(null) }
    var isNotificationsExpanded by remember { mutableStateOf(false) }

    val activeEditId by activity._longPressedWidgetId.collectAsState()
    
    // Limit app breaker list
    var limitedAppsSet by remember { mutableStateOf(setOf<String>()) }
    
    val currentFontFamily = when (selectedFontVal) {
        "Space Mono" -> FontFamily.Monospace
        "Serif Elegant" -> FontFamily.Serif
        "Inter Elegant" -> FontFamily.SansSerif
        else -> FontFamily.SansSerif
    }
    
    val currentThemeColor = if (materialYouEnabledVal && extractedWallpaperColorVal != null) {
        extractedWallpaperColorVal!!
    } else {
        MaterialTheme.colorScheme.primary
    }
    val iconThemeColor = if (materialYouEnabledVal && extractedWallpaperColorVal != null) {
        extractedWallpaperColorVal!!
    } else {
        MaterialTheme.colorScheme.primary
    }

    val focusManager = LocalFocusManager.current

    val homeEntryCount by activity.homeEntryTrigger.collectAsState()
    LaunchedEffect(homeEntryCount) {
        if (homeEntryCount > 0) {
            activity.animatingAppPackage.value = null
            activity.isClosingApp.value = false
            activity.isLaunchingApp.value = false
            activity.appTransitionProgress.value = 0f

            searchQuery = ""
            zoomLevel = 0
            showSettingsPanel = false
            showAddUserDialog = false
            selectedUser = null
            activeBreakerApp = null
            expandedFolderPackageName = null
            isNotificationsExpanded = false
            focusedContextMenuApp = null
            activity._longPressedWidgetId.value = null
            focusManager.clearFocus()
            coroutineScope.launch { listState.scrollToItem(0) }
        }
    }

    var selectedCategoryFilter by remember { mutableStateOf<String?>("All") }

    val finalFilteredAppsList = remember(uiState.apps, searchQuery, hiddenAppsSet, selectedCategoryFilter, folderMap) {
        val baseList = uiState.apps.filter { 
            it.packageName !in hiddenAppsSet && 
            (searchQuery.isEmpty() || it.label.contains(searchQuery, ignoreCase = true)) 
        }
        if (selectedCategoryFilter == "All" || selectedCategoryFilter == null) {
            baseList
        } else {
            val allowedPackages = folderMap[selectedCategoryFilter] ?: emptyList()
            baseList.filter { it.packageName in allowedPackages }
        }
    }

    val topNApps = remember(finalFilteredAppsList, appUsageScoresVal, usageLimitCountVal) {
        if (finalFilteredAppsList.isEmpty()) emptyList<AppInfo>()
        else {
            val limit = usageLimitCountVal
            finalFilteredAppsList.sortedWith(
                compareByDescending<AppInfo> { appUsageScoresVal[it.packageName] ?: 0L }
                    .thenBy { it.label.lowercase() }
            ).take(limit).sortedBy { it.label.lowercase() }
        }
    }
    val restApps = remember(finalFilteredAppsList, topNApps) {
        val topPackages = topNApps.map { it.packageName }.toSet()
        finalFilteredAppsList.filter { it.packageName !in topPackages }
    }

    val standardListEntries = remember(finalFilteredAppsList) {
        val entries = mutableListOf<ListEntry>()
        var lastChar: Char? = null
        finalFilteredAppsList.forEach { app ->
            val firstChar = app.label.firstOrNull()?.uppercaseChar() ?: '#'
            if (firstChar != lastChar) {
                entries.add(ListEntry.Header(firstChar))
                lastChar = firstChar
            }
            entries.add(ListEntry.App(app))
        }
        entries
    }

    val restAppsEntries = remember(restApps) {
        val entries = mutableListOf<ListEntry>()
        var lastChar: Char? = null
        restApps.forEach { app ->
            val firstChar = app.label.firstOrNull()?.uppercaseChar() ?: '#'
            if (firstChar != lastChar) {
                entries.add(ListEntry.Header(firstChar))
                lastChar = firstChar
            }
            entries.add(ListEntry.App(app))
        }
        entries
    }

    val dragProgressState = remember { mutableStateOf(0f) }

    val targetMainIndex by remember {
        derivedStateOf {
            val mainListTotalItems = if (categoriseByUsageVal && searchQuery.isEmpty()) {
                topNApps.size + restAppsEntries.size + 2
            } else {
                standardListEntries.size
            }
            if (mainListTotalItems > 0) {
                (dragProgressState.value * (mainListTotalItems - 1)).toInt().coerceIn(0, mainListTotalItems - 1)
            } else 0
        }
    }

    val targetAlphabetIndex by remember {
        derivedStateOf {
            (dragProgressState.value * (letters.size - 1)).toInt().coerceIn(0, letters.size - 1)
        }
    }

    val touchedLetterByProgress by remember {
        derivedStateOf {
            val letterIndex = (dragProgressState.value * (letters.size - 1)).toInt().coerceIn(0, letters.size - 1)
            letters[letterIndex]
        }
    }

    var touchedLetter by remember { mutableStateOf<Char?>(null) }
    var lockedLetterState by remember { mutableStateOf<Char?>(null) }
    var scrolledLetter by remember { mutableStateOf<Char?>(null) }
    var isTouchingSidebar by remember { mutableStateOf(false) }
    var sidebarTouchY by remember { mutableStateOf<Float?>(null) }
    var sidebarTouchX by remember { mutableStateOf<Float?>(null) }

    LaunchedEffect(targetMainIndex, targetAlphabetIndex) {
        if (isTouchingSidebar) {
            listState.scrollToItem(targetMainIndex)
            alphabetListState.scrollToItem(targetAlphabetIndex)
        }
    }

    LaunchedEffect(touchedLetterByProgress) {
        if (isTouchingSidebar) {
            touchedLetter = touchedLetterByProgress
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }
    val displayLetter = if (isTouchingSidebar) touchedLetter else scrolledLetter
    
    val firstVisibleIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    LaunchedEffect(firstVisibleIndex) {
        if (!isTouchingSidebar) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
        val letter: Char? = if (categoriseByUsageVal && searchQuery.isEmpty()) {
            if (firstVisibleIndex == 0) {
                topNApps.firstOrNull()?.label?.firstOrNull()?.uppercaseChar()
            } else if (firstVisibleIndex <= topNApps.size) {
                val app = topNApps.getOrNull(firstVisibleIndex - 1)
                app?.label?.firstOrNull()?.uppercaseChar()
            } else if (firstVisibleIndex == topNApps.size + 1) {
                when (val firstRest = restAppsEntries.firstOrNull()) {
                    is ListEntry.Header -> firstRest.letter
                    is ListEntry.App -> firstRest.appInfo.label.firstOrNull()?.uppercaseChar()
                    null -> null
                }
            } else {
                val restIndex = firstVisibleIndex - topNApps.size - 2
                when (val entry = restAppsEntries.getOrNull(restIndex)) {
                    is ListEntry.Header -> entry.letter
                    is ListEntry.App -> entry.appInfo.label.firstOrNull()?.uppercaseChar()
                    null -> null
                }
            }
        } else {
            when (val entry = standardListEntries.getOrNull(firstVisibleIndex)) {
                is ListEntry.Header -> entry.letter
                is ListEntry.App -> entry.appInfo.label.firstOrNull()?.uppercaseChar()
                null -> null
            }
        }
        if (letter != null) {
            scrolledLetter = letter
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadApps(context.packageManager)
        activity.refreshAppUsageScores()
    }

    BackHandler(enabled = true) {
        if (showSettingsPanel) {
            showSettingsPanel = false
        } else if (isNotificationsExpanded) {
            isNotificationsExpanded = false
        } else if (zoomLevel > 0) {
            zoomLevel = 0
        } else if (searchQuery.isNotEmpty()) {
            searchQuery = ""
        } else if (expandedFolderPackageName != null) {
            expandedFolderPackageName = null
            currentFolderAppsPackageList = null
        } else if (focusedContextMenuApp != null) {
            focusedContextMenuApp = null
        } else if (selectedCategoryFilter != "All" && selectedCategoryFilter != null) {
            selectedCategoryFilter = "All"
        } else if (displayedPages.size > 1) {
            currentPageIndex = (currentPageIndex + 1) % displayedPages.size
        } else {
            // Already home, consume to prevent exiting/reloading
        }
    }

    if (activeEditId != null) {
        BackHandler { activity._longPressedWidgetId.value = null }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(showSettingsPanel, zoomLevel, searchQuery, expandedFolderPackageName) {
                try {
                    awaitEachGesture {
                        try {
                            val down = awaitFirstDown(requireUnconsumed = false, pass = androidx.compose.ui.input.pointer.PointerEventPass.Initial)
                            var totalDragY = 0f
                            val startedNearBottom = down.position.y > size.height - with(density) { 60.dp.toPx() }
                            do {
                                val event = awaitPointerEvent(pass = androidx.compose.ui.input.pointer.PointerEventPass.Initial)
                                val changes = event.changes
                                if (changes.isNotEmpty()) {
                                    val change = changes[0]
                                    val diffY = change.position.y - change.previousPosition.y
                                    totalDragY += diffY
                                    if (startedNearBottom && totalDragY < -100f) {
                                        zoomLevel = 0
                                        searchQuery = ""
                                        showSettingsPanel = false
                                        showAddUserDialog = false
                                        selectedUser = null
                                        expandedFolderPackageName = null
                                        currentFolderAppsPackageList = null
                                        focusedContextMenuApp = null
                                        isNotificationsExpanded = false
                                        totalDragY = 0f
                                    }
                                }
                            } while (event.changes.any { it.pressed })
                        } catch (e: kotlinx.coroutines.CancellationException) { 
                            throw e
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } catch (e: kotlinx.coroutines.CancellationException) { 
                    throw e
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            .pointerInput(gesturesEnabledVal, searchQuery) {
                if (searchQuery.isNotEmpty()) return@pointerInput
                try {
                    if (gesturesEnabledVal) {
                        detectTapGestures(
                            onDoubleTap = { isSleepingState = true },
                            onTap = { activity._longPressedWidgetId.value = null }
                        )
                    } else {
                        detectTapGestures(onTap = { activity._longPressedWidgetId.value = null })
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            .pointerInput(gesturesEnabledVal, searchQuery) {
                if (searchQuery.isNotEmpty()) return@pointerInput
                if (gesturesEnabledVal) {
                    try {
                        awaitEachGesture {
                            try {
                                var isPinching = false
                                var initialDistance = 0f
                                var zoomChangedInThisGesture = false
                                
                                awaitFirstDown(requireUnconsumed = false)
                                
                                do {
                                    val event = awaitPointerEvent()
                                    val activeChanges = event.changes.filter { it.pressed }
                                    if (activeChanges.size >= 2) {
                                        val p1 = activeChanges[0].position
                                        val p2 = activeChanges[1].position
                                        val currentDistance = kotlin.math.hypot(p1.x - p2.x, p1.y - p2.y)
                                        
                                        if (!isPinching) {
                                            isPinching = true
                                            initialDistance = currentDistance
                                        } else if (!zoomChangedInThisGesture) {
                                            if (initialDistance > 0f) {
                                                val scale = currentDistance / initialDistance
                                                if (scale < 0.82f) {
                                                    if (zoomLevel < 2) {
                                                        zoomLevel++
                                                        zoomChangedInThisGesture = true
                                                        event.changes.forEach { it.consume() }
                                                    }
                                                } else if (scale > 1.22f) {
                                                    if (zoomLevel > 0) {
                                                        zoomLevel--
                                                        zoomChangedInThisGesture = true
                                                        event.changes.forEach { it.consume() }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        isPinching = false
                                        initialDistance = 0f
                                    }
                                } while (event.changes.any { it.pressed })
                            } catch (e: kotlinx.coroutines.CancellationException) {
                                throw e
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } catch (e: kotlinx.coroutines.CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
    ) {
        // Wallpaper visualizer canvas - REMOVED for transparency
        // WallpaperBackground(selectedWallpaper, bingWallpaperUrlVal, wallpaperBlurEnabledVal)
        
        // Adaptive Readability Scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = scrimAlpha))
        )

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = currentThemeColor)
        } else {
            val zoomFactor by animateFloatAsState(
                targetValue = if (zoomLevel > 0) 0.65f else 1.0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                label = "zoomFactor"
            )
            val translationYFactor by animateFloatAsState(
                targetValue = if (zoomLevel > 0) (-95).dp.value else 0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                label = "translationY"
            )
            val mainAlphaFactor by animateFloatAsState(
                targetValue = if (zoomLevel == 2) 0f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                label = "mainAlphaFactor"
            )

            val appTransitionProgressVal by activity.appTransitionProgress.collectAsState()
            val wsScale = 1.0f - 0.05f * appTransitionProgressVal
            val wsAlpha = 1.0f - appTransitionProgressVal

            val isNotifPage = displayedPages.getOrNull(currentPageIndex) == "Notifications"

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = zoomFactor * wsScale
                        scaleY = zoomFactor * wsScale
                        translationY = translationYFactor * density.density
                        clip = zoomLevel > 0 || appTransitionProgressVal > 0.01f
                        shape = RoundedCornerShape(24.dp)
                        shadowElevation = 0f
                        alpha = mainAlphaFactor * wsAlpha
                    }
                    .then(
                        if (zoomLevel == 1) {
                            Modifier.clickable(
                                indication = null,
                                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                            ) {
                                zoomLevel = 0
                            }
                        } else Modifier
                    )
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    val searchModeActive = searchQuery.isNotEmpty() || isSearchFocused || isImeVisible
                    val topWeight by animateFloatAsState(
                        targetValue = if (searchModeActive) {
                            0.0001f
                        } else if (isNotificationsExpanded) {
                            0.20f
                        } else {
                            0.25f
                        },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                        label = "topWeight"
                    )
                    val middleWeight by animateFloatAsState(
                        targetValue = if (searchModeActive) {
                            0.0001f
                        } else if (isNotificationsExpanded) {
                            0.80f
                        } else if (isNotifPage) {
                            0.12f
                        } else {
                            0.25f
                        },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                        label = "middleWeight"
                    )
                    val bottomWeight by animateFloatAsState(
                        targetValue = if (searchModeActive) {
                            0.9998f
                        } else if (isNotificationsExpanded) {
                            0.0001f
                        } else if (isNotifPage) {
                            0.63f
                        } else {
                            0.50f
                        },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                        label = "bottomWeight"
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        // 1. Top Space 25% (Mandatory blank space for one handed ergonomic layout)
                        Box(
                            modifier = Modifier
                                .weight(topWeight.coerceAtLeast(0.0001f))
                                .fillMaxWidth()
                                .then(
                                    if (isNotificationsExpanded) {
                                        Modifier.clickable(
                                            indication = null,
                                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                                        ) {
                                            isNotificationsExpanded = false
                                        }
                                    } else {
                                        Modifier.pointerInput(gesturesEnabledVal, searchQuery) {
                                            if (searchQuery.isNotEmpty()) return@pointerInput
                                            if (gesturesEnabledVal) {
                                                detectDragGestures(
                                                    onDrag = { _, dragAmount ->
                                                        if (dragAmount.y > 45f) {
                                                            searchQuery = " "
                                                            searchQuery = ""
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    }
                                )
                            )
                        
                        // 2. Middle Space (Clock & notification cards and folder chips)
                        Column(
                            modifier = Modifier
                                .weight(middleWeight.coerceAtLeast(0.0001f))
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            if (!searchModeActive) {
                                val clickToCollapseModifier = if (isNotificationsExpanded) {
                                    Modifier.clickable(
                                        indication = null,
                                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                                    ) {
                                        isNotificationsExpanded = false
                                    }
                                } else Modifier

                                if (!declutterModeVal) {
                                    Box(modifier = clickToCollapseModifier) {
                                        ClockStyleWidget(
                                            clockStyleVal,
                                            currentFontFamily,
                                            currentThemeColor,
                                            isLocationGrantedVal,
                                            use24HourFormatVal,
                                            useFahrenheitVal,
                                            contentColor = adaptiveTextColor
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    // Categories Filter
                                    androidx.compose.animation.AnimatedVisibility(
                                        visible = !isNotifPage,
                                        enter = fadeIn() + expandVertically(),
                                        exit = fadeOut() + shrinkVertically()
                                    ) {
                                        Box(modifier = clickToCollapseModifier) {
                                            androidx.compose.foundation.lazy.LazyRow(
                                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                item {
                                                    val isAllSelected = selectedCategoryFilter == "All" || selectedCategoryFilter == null
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(16.dp))
                                                            .then(
                                                                if (isAllSelected) Modifier
                                                                    .background(Brush.horizontalGradient(listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))))
                                                                    .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFF8E2DE2))
                                                                else Modifier.background(adaptiveGlassBg)
                                                            )
                                                            .border(
                                                                if (isAllSelected) 0.dp else 1.dp,
                                                                if (isAllSelected) Color.Transparent else adaptiveGlassBorder,
                                                                RoundedCornerShape(16.dp)
                                                            )
                                                            .clickable { selectedCategoryFilter = "All" }
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Icon(
                                                                Icons.Default.Menu, 
                                                                contentDescription = null, 
                                                                tint = if (isAllSelected) Color.White else adaptiveIconTint, 
                                                                modifier = Modifier.size(16.dp).padding(end = 6.dp)
                                                            )
                                                            Text(
                                                                text = "All Apps",
                                                                fontSize = 12.sp,
                                                                fontFamily = currentFontFamily,
                                                                color = if (isAllSelected) Color.White else adaptiveTextColor,
                                                                fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                                                                style = TextStyle(
                                                                    shadow = Shadow(
                                                                        color = adaptiveShadowColor,
                                                                        offset = Offset(1f, 1f),
                                                                        blurRadius = 3f
                                                                    )
                                                                )
                                                            )
                                                        }
                                                    }
                                                }

                                                folderMap.forEach { (name, appsList) ->
                                                    item {
                                                        val isSelected = selectedCategoryFilter == name
                                                        val iconVector = when (name.lowercase()) {
                                                            "social" -> Icons.Default.Person
                                                            "utilities" -> Icons.Default.Build
                                                            "media" -> Icons.Default.PlayArrow
                                                            else -> Icons.AutoMirrored.Filled.List
                                                        }
                                                        val textLabel = name
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(16.dp))
                                                                .then(
                                                                    if (isSelected) Modifier
                                                                        .background(Brush.horizontalGradient(listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))))
                                                                        .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFF8E2DE2))
                                                                    else Modifier.background(adaptiveGlassBg)
                                                                )
                                                                .border(
                                                                    if (isSelected) 0.dp else 1.dp,
                                                                    if (isSelected) Color.Transparent else adaptiveGlassBorder,
                                                                    RoundedCornerShape(16.dp)
                                                                )
                                                                .clickable { 
                                                                    selectedCategoryFilter = if (isSelected) "All" else name
                                                                }
                                                        ) {
                                                            Row(
                                                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Icon(
                                                                    iconVector, 
                                                                    contentDescription = null, 
                                                                    tint = if (isSelected) Color.White else adaptiveIconTint, 
                                                                    modifier = Modifier.size(16.dp).padding(end = 6.dp)
                                                                )
                                                                Text(
                                                                    text = "$textLabel (${appsList.size})",
                                                                    fontSize = 12.sp,
                                                                    fontFamily = currentFontFamily,
                                                                    color = if (isSelected) Color.White else adaptiveTextColor,
                                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                                    style = TextStyle(
                                                                        shadow = Shadow(
                                                                            color = adaptiveShadowColor,
                                                                            offset = Offset(1f, 1f),
                                                                            blurRadius = 3f
                                                                        )
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        // 3. Bottom Space (Scrolling app list & quick scrollbar)
                        Box(
                            modifier = Modifier
                                .weight(bottomWeight.coerceAtLeast(0.0001f))
                                .fillMaxWidth()
                        ) {
                            if (searchQuery.isNotEmpty() || isSearchFocused) {
                                val searchListState = rememberLazyListState()
                                val searchFocusManager = LocalFocusManager.current
                                
                                BackHandler(enabled = true) {
                                    if (activeSearchCategoryFilter != "All") {
                                        activeSearchCategoryFilter = "All"
                                    } else {
                                        searchQuery = ""
                                        isSearchFocused = false
                                        searchFocusManager.clearFocus()
                                    }
                                }

                                LaunchedEffect(searchListState.isScrollInProgress) {
                                    if (searchListState.isScrollInProgress) {
                                        searchFocusManager.clearFocus()
                                    }
                                }

                                // Interactive Scrim click collapse background
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(adaptiveGlassBg)
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                                        ) {
                                            searchQuery = ""
                                            isSearchFocused = false
                                            searchFocusManager.clearFocus()
                                        }
                                )

                                LazyColumn(
                                    state = searchListState,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                        .padding(horizontal = 14.dp),
                                    verticalArrangement = Arrangement.Bottom,
                                    reverseLayout = true,
                                    contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
                                ) {
                                    // CATEGORY FILTER CHIPS
                                    item {
                                        androidx.compose.foundation.lazy.LazyRow(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 12.dp),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp)
                                        ) {
                                            items(listOf("All", "Contacts", "Apps", "Web", "Settings & Files")) { cat ->
                                                val isSelected = activeSearchCategoryFilter == cat
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(16.dp))
                                                        .background(
                                                            if (isSelected) currentThemeColor.copy(alpha = 0.25f)
                                                            else adaptiveGlassBg
                                                        )
                                                        .border(
                                                            1.dp,
                                                            if (isSelected) currentThemeColor.copy(alpha = 0.5f)
                                                            else adaptiveGlassBorder,
                                                            RoundedCornerShape(16.dp)
                                                        )
                                                        .clickable {
                                                            activeSearchCategoryFilter = cat
                                                        }
                                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                                ) {
                                                    Text(
                                                        text = cat,
                                                        color = if (isSelected) currentThemeColor else adaptiveTextColor,
                                                        fontSize = 12.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                        fontFamily = currentFontFamily,
                                                        style = TextStyle(
                                                            shadow = Shadow(
                                                                color = Color.Black.copy(alpha = 0.6f),
                                                                offset = Offset(1f, 1f),
                                                                blurRadius = 3f
                                                            )
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Onboarding Invitation empty search focused state
                                    if (searchQuery.isEmpty()) {
                                        item {
                                            Column(
                                                modifier = Modifier
                                                    .fillParentMaxSize()
                                                    .padding(24.dp),
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Search,
                                                    contentDescription = null,
                                                    tint = currentThemeColor.copy(alpha = 0.5f),
                                                    modifier = Modifier.size(56.dp)
                                                )
                                                Spacer(modifier = Modifier.height(16.dp))
                                                Text(
                                                    text = "Unified Search Engine",
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = adaptiveTextColor,
                                                    fontFamily = currentFontFamily,
                                                    style = TextStyle(
                                                        shadow = Shadow(
                                                            color = Color.Black.copy(alpha = 0.5f),
                                                            offset = Offset(1.5f, 1.5f),
                                                            blurRadius = 4f
                                                        )
                                                    )
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = "Type below to browse on-device contacts, installed apps, system settings, files, or query the web in real-time.",
                                                    fontSize = 13.sp,
                                                    color = adaptiveTextSecondary,
                                                    textAlign = TextAlign.Center,
                                                    fontFamily = currentFontFamily,
                                                    modifier = Modifier.padding(horizontal = 24.dp),
                                                    style = TextStyle(
                                                        shadow = Shadow(
                                                            color = Color.Black.copy(alpha = 0.5f),
                                                            offset = Offset(1f, 1f),
                                                            blurRadius = 3f
                                                        )
                                                    )
                                                )
                                            }
                                        }
                                    } else {
                                        val contacts = displayedResults.filterIsInstance<SearchResult.ContactResult>()
                                        val apps = displayedResults.filterIsInstance<SearchResult.AppResult>()
                                        val webs = displayedResults.filterIsInstance<SearchResult.WebResult>()
                                        val others = displayedResults.filter { it is SearchResult.SettingResult || it is SearchResult.FileResult }

                                        if (others.isNotEmpty()) {
                                            items(others) { result ->
                                                when (result) {
                                                    is SearchResult.SettingResult -> {
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .clickable {
                                                                    UniversalSearchEngine.recordSelection(contextForSearch, result)
                                                                    if (result.action.startsWith("launcher_")) {
                                                                        val cat = when (result.action) {
                                                                            "launcher_perf" -> "Performance"
                                                                            "launcher_gestures" -> "Gestures"
                                                                            "launcher_permissions" -> "Permissions"
                                                                            "launcher_search" -> "Search"
                                                                            "launcher_pages" -> "Pages"
                                                                            else -> null
                                                                        }
                                                                        isSearchFocused = false
                                                                        searchQuery = ""
                                                                        searchFocusManager.clearFocus()
                                                                        activeSettingsCategory = cat
                                                                        showSettingsPanel = true
                                                                    } else {
                                                                        try {
                                                                            val intent = Intent(result.action)
                                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                                            contextForSearch.startActivity(intent)
                                                                        } catch (_: Exception) {}
                                                                    }
                                                                }
                                                                .padding(vertical = 8.dp, horizontal = 12.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(36.dp)
                                                                    .background(currentThemeColor.copy(alpha = 0.15f), CircleShape),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Settings,
                                                                    contentDescription = null,
                                                                    tint = currentThemeColor,
                                                                    modifier = Modifier.size(16.dp)
                                                                )
                                                            }
                                                            Spacer(modifier = Modifier.width(14.dp))
                                                            Column(modifier = Modifier.weight(1f)) {
                                                                Text(
                                                                    text = result.label,
                                                                    color = adaptiveTextColor,
                                                                    fontSize = 14.sp,
                                                                    fontWeight = FontWeight.SemiBold,
                                                                    fontFamily = currentFontFamily
                                                                )
                                                                Text(
                                                                    text = "System Setting",
                                                                    color = adaptiveTextMuted,
                                                                    fontSize = 11.sp,
                                                                    fontFamily = currentFontFamily
                                                                )
                                                            }
                                                        }
                                                    }
                                                    is SearchResult.FileResult -> {
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .clickable {
                                                                    UniversalSearchEngine.recordSelection(contextForSearch, result)
                                                                    Toast.makeText(contextForSearch, "File Selected: ${result.label}", Toast.LENGTH_SHORT).show()
                                                                }
                                                                .padding(vertical = 8.dp, horizontal = 12.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(36.dp)
                                                                    .background(Color.White.copy(alpha = 0.1f), CircleShape),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Text("📄", fontSize = 16.sp)
                                                            }
                                                            Spacer(modifier = Modifier.width(14.dp))
                                                            Column(modifier = Modifier.weight(1f)) {
                                                                Text(
                                                                    text = result.label,
                                                                    color = adaptiveTextColor,
                                                                    fontSize = 14.sp,
                                                                    fontWeight = FontWeight.SemiBold,
                                                                    fontFamily = currentFontFamily,
                                                                    maxLines = 1,
                                                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                                                )
                                                                val kbSize = result.size / 1024
                                                                Text(
                                                                    text = "Local File • ${kbSize} KB • ${result.mimeType ?: "Unknown type"}",
                                                                    color = adaptiveTextMuted,
                                                                    fontSize = 11.sp,
                                                                    fontFamily = currentFontFamily
                                                                )
                                                            }
                                                        }
                                                    }
                                                    else -> {}
                                                }
                                            }
                                            item {
                                                CategoryHeader("SYSTEM SETTINGS & FILES", currentThemeColor, currentFontFamily)
                                            }
                                        }

                                        if (webs.isNotEmpty()) {
                                            items(webs) { result ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            UniversalSearchEngine.recordSelection(contextForSearch, result)
                                                            try {
                                                                val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                                                                    putExtra(android.app.SearchManager.QUERY, result.label)
                                                                }
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                                contextForSearch.startActivity(intent)
                                                            } catch (e: Exception) {
                                                                try {
                                                                    val intent = Intent(Intent.ACTION_VIEW, "https://www.google.com/search?q=${java.net.URLEncoder.encode(result.label, "UTF-8")}".toUri())
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                                    contextForSearch.startActivity(intent)
                                                                } catch (_: Exception) {}
                                                            }
                                                        }
                                                        .padding(vertical = 8.dp, horizontal = 12.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(36.dp)
                                                            .background(Color.White.copy(alpha = 0.05f), CircleShape),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Search,
                                                            contentDescription = null,
                                                            tint = adaptiveTextColor.copy(alpha = 0.6f),
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(14.dp))
                                                    Text(
                                                        text = result.label,
                                                        color = adaptiveTextColor,
                                                        fontSize = 14.sp,
                                                        fontFamily = currentFontFamily,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    Icon(
                                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                        contentDescription = null,
                                                        tint = adaptiveTextColor.copy(alpha = 0.3f),
                                                        modifier = Modifier
                                                            .size(14.dp)
                                                            .graphicsLayer { rotationZ = 135f }
                                                    )
                                                }
                                            }
                                            item {
                                                CategoryHeader("WEB SUGGESTIONS", currentThemeColor, currentFontFamily)
                                            }
                                        }

                                        if (apps.isNotEmpty()) {
                                            items(apps) { result ->
                                                val appInfo = uiState.apps.find { it.packageName == result.packageName }
                                                val isLimitedApp = result.packageName in limitedAppsSet
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .alpha(if (isLimitedApp) 0.4f else 1f)
                                                        .pointerInput(result) {
                                                            detectTapGestures(
                                                                onLongPress = { 
                                                                    if (appInfo != null) {
                                                                        focusedContextMenuApp = appInfo
                                                                    }
                                                                },
                                                                onTap = {
                                                                    UniversalSearchEngine.recordSelection(contextForSearch, result)
                                                                    if (isLimitedApp && appInfo != null) {
                                                                        activeBreakerApp = appInfo
                                                                    } else {
                                                                        activity.launchAppWithTracker(result.packageName)
                                                                    }
                                                                }
                                                            )
                                                        }
                                                        .padding(vertical = 8.dp, horizontal = 12.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    if (appInfo != null) {
                                                        StyledAppIcon(appInfo.icon, iconPackVal, iconThemeColor)
                                                    } else {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(36.dp)
                                                                .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text("📱", fontSize = 16.sp)
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.width(14.dp))
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = result.label,
                                                            color = adaptiveTextColor,
                                                            fontSize = 14.sp,
                                                            fontWeight = FontWeight.SemiBold,
                                                            fontFamily = currentFontFamily
                                                        )
                                                        Text(
                                                            text = "App • ${result.packageName}",
                                                            color = adaptiveTextMuted,
                                                            fontSize = 11.sp,
                                                            fontFamily = currentFontFamily,
                                                            maxLines = 1,
                                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                                        )
                                                    }
                                                }
                                            }
                                            item {
                                                CategoryHeader("APPLICATIONS", currentThemeColor, currentFontFamily)
                                            }
                                        }

                                        if (contacts.isNotEmpty()) {
                                            items(contacts) { result ->
                                                val initials = result.label.split(" ").mapNotNull { it.firstOrNull() }.joinToString("").uppercase().take(2)
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            UniversalSearchEngine.recordSelection(contextForSearch, result)
                                                            if (result.isRoomUser) {
                                                                val userId = result.id.removePrefix("room_").toIntOrNull()
                                                                val match = allUsersVal.find { it.id == userId }
                                                                if (match != null) {
                                                                    selectedUser = match
                                                                } else {
                                                                    try {
                                                                        val intent = Intent(Intent.ACTION_DIAL, "tel:${result.phoneNumber}".toUri())
                                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                                        contextForSearch.startActivity(intent)
                                                                    } catch (_: Exception) {}
                                                                }
                                                            } else {
                                                                try {
                                    val intent = Intent(Intent.ACTION_DIAL, "tel:${result.phoneNumber}".toUri())
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    contextForSearch.startActivity(intent)
                                } catch (_: Exception) {}
                                                            }
                                                        }
                                                        .padding(vertical = 8.dp, horizontal = 12.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(36.dp)
                                                            .background(currentThemeColor.copy(alpha = 0.2f), CircleShape)
                                                            .border(1.dp, currentThemeColor.copy(alpha = 0.4f), CircleShape),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = if (initials.isNotEmpty()) initials else "👤",
                                                            color = currentThemeColor,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 12.sp,
                                                            fontFamily = currentFontFamily
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(14.dp))
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = result.label,
                                                            color = adaptiveTextColor,
                                                            fontSize = 14.sp,
                                                            fontWeight = FontWeight.SemiBold,
                                                            fontFamily = currentFontFamily
                                                        )
                                                        Text(
                                                            text = "Contact • ${result.phoneNumber}",
                                                            color = adaptiveTextMuted,
                                                            fontSize = 11.sp,
                                                            fontFamily = currentFontFamily
                                                        )
                                                    }
                                                    IconButton(
                                                        onClick = {
                                                            UniversalSearchEngine.recordSelection(contextForSearch, result)
                                                            try {
                                                                val intent = Intent(Intent.ACTION_DIAL, "tel:${result.phoneNumber}".toUri())
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                                contextForSearch.startActivity(intent)
                                                            } catch (_: Exception) {}
                                                        },
                                                        modifier = Modifier.size(28.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Phone,
                                                            contentDescription = "Call Contact",
                                                            tint = currentThemeColor,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            item {
                                                CategoryHeader("CONTACTS", currentThemeColor, currentFontFamily)
                                            }
                                        }
                                    }
                                }
                            } else {
                                val outerCurrentPageName = displayedPages.getOrNull(currentPageIndex) ?: "App List"
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(
                                            start = if (outerCurrentPageName == "App List") 36.dp else 12.dp,
                                            end = if (outerCurrentPageName == "App List") 16.dp else 12.dp
                                        ),
                                    horizontalArrangement = if (outerCurrentPageName == "App List") Arrangement.SpaceBetween else Arrangement.Center
                                ) {
                                    val getLazyColumnIndex: (Int, Boolean) -> Int = { targetIdx, isLocked ->
                                        if (categoriseByUsageVal && searchQuery.isEmpty()) {
                                            val app = finalFilteredAppsList.getOrNull(targetIdx)
                                            if (app != null) {
                                                val topIndex = topNApps.indexOfFirst { it.packageName == app.packageName }
                                                if (topIndex != -1) {
                                                    1 + topIndex
                                                } else {
                                                    val restIndex = restAppsEntries.indexOfFirst { it is ListEntry.App && it.appInfo.packageName == app.packageName }
                                                    if (restIndex != -1) {
                                                        if (isLocked) {
                                                            topNApps.size + 2 + restIndex
                                                        } else {
                                                            val appFirstLetter = app.label.firstOrNull()?.uppercaseChar()
                                                            val headerIndex = restAppsEntries.indexOfFirst { it is ListEntry.Header && it.letter == appFirstLetter }
                                                            if (headerIndex != -1) {
                                                                topNApps.size + 2 + headerIndex
                                                            } else {
                                                                topNApps.size + 2 + restIndex
                                                            }
                                                        }
                                                    } else {
                                                        targetIdx
                                                     }
                                                }
                                            } else {
                                                targetIdx
                                            }
                                        } else {
                                            val app = finalFilteredAppsList.getOrNull(targetIdx)
                                            if (app != null) {
                                                val stdIndex = standardListEntries.indexOfFirst { it is ListEntry.App && it.appInfo.packageName == app.packageName }
                                                if (stdIndex != -1) {
                                                    if (isLocked) {
                                                        stdIndex
                                                    } else {
                                                        val appFirstLetter = app.label.firstOrNull()?.uppercaseChar()
                                                        val headerIndex = standardListEntries.indexOfFirst { it is ListEntry.Header && it.letter == appFirstLetter }
                                                        if (headerIndex != -1) {
                                                            headerIndex
                                                        } else {
                                                            stdIndex
                                                        }
                                                    }
                                                } else {
                                                    targetIdx
                                                }
                                            } else {
                                                targetIdx
                                            }
                                        }
                                    }
                                    val scrollToItemCenter: suspend (Int, Float?, Boolean) -> Unit = { targetIndex, touchY, isLocked ->
                                        val actualIndex = getLazyColumnIndex(targetIndex, isLocked)
                                        try {
                                            if (isLocked) {
                                                val viewportHeight = listState.layoutInfo.viewportSize.height
                                                val itemHeight = with(density) { 60.dp.toPx() }
                                                val targetY = touchY ?: (viewportHeight / 2f)
                                                val centerOffset = if (viewportHeight > 0) - (targetY.toInt()) + (itemHeight / 2).toInt() else 0
                                                listState.scrollToItem(actualIndex, scrollOffset = centerOffset)
                                            } else {
                                                listState.scrollToItem(actualIndex, scrollOffset = 0)
                                            }
                                        } catch (_: Exception) {
                                            listState.scrollToItem(actualIndex, scrollOffset = 0)
                                        }
                                    }

                                    val listColumn: @Composable RowScope.() -> Unit = {
                                        val sidebarInteractiveProgress by animateFloatAsState(
                                            targetValue = if (isTouchingSidebar) 1.0f else 0.0f,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessMediumLow
                                            ),
                                            label = "sidebarInteractiveProgress"
                                        )

                                        LazyColumn(
                                            state = listState,
                                            modifier = Modifier.fillMaxHeight().weight(1.0f).graphicsLayer { clip = false },
                                            contentPadding = PaddingValues(bottom = 120.dp, start = 24.dp)
                                        ) {
                                            if (categoriseByUsageVal && searchQuery.isEmpty()) {
                                                if (topNApps.isNotEmpty()) {
                                                    item {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            modifier = Modifier.padding(start = 4.dp, top = 20.dp, bottom = 8.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Star,
                                                                contentDescription = null,
                                                                tint = MaterialTheme.colorScheme.onSurface,
                                                                modifier = Modifier.size(13.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Text(
                                                                text = "FREQUENTLY USED",
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                fontFamily = currentFontFamily,
                                                                color = if (isLightBackground) currentThemeColor.copy(alpha = 0.85f) else currentThemeColor.copy(alpha = 0.7f),
                                                                letterSpacing = 1.sp
                                                            )
                                                        }
                                                    }

                                                    itemsIndexed(items = topNApps, key = { _, app -> "top_" + app.packageName }) { idx, app ->
                                                        val isAppHovered = highlightedApp == app
                                                        val alphaTarget = if (app.packageName in limitedAppsSet) {
                                                            0.15f
                                                        } else if (isTouchingSidebar) {
                                                            if (isAppHovered) {
                                                                val touchX = sidebarTouchX ?: 0f
                                                                if (touchX > 165f) 1.0f else 0.3f
                                                            } else {
                                                                0.3f
                                                            }
                                                        } else {
                                                            1.0f
                                                        }
                                                        val animatedAlpha by animateFloatAsState(targetValue = alphaTarget, label = "app_alpha")
                                                        
                                                        AppRow(
                                                            index = 1 + idx,
                                                            app = app,
                                                            alphaProvider = { animatedAlpha },
                                                            isAppHoveredProvider = { highlightedApp == app },
                                                            sidebarTouchXProvider = { sidebarTouchX },
                                                            sidebarTouchYProvider = { sidebarTouchY },
                                                            sidebarInteractiveProgressProvider = { sidebarInteractiveProgress },
                                                            listState = listState,
                                                            currentFontFamily = currentFontFamily,
                                                            iconPackVal = iconPackVal,
                                                            iconThemeColor = iconThemeColor,
                                                            contentColor = adaptiveTextColor,
                                                            onLongPress = { focusedContextMenuApp = app },
                                                            onTap = {
                                                                val isLimited = app.packageName in limitedAppsSet
                                                                if (isLimited) {
                                                                    activeBreakerApp = app
                                                                } else {
                                                                    activity.launchAppWithTracker(app.packageName)
                                                                }
                                                            }
                                                        )
                                                    }

                                                    item {
                                                        Column(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(horizontal = 14.dp, vertical = 22.dp),
                                                            horizontalAlignment = Alignment.CenterHorizontally
                                                        ) {
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.Center
                                                            ) {
                                                                Box(
                                                                    modifier = Modifier
                                                                        .weight(1f)
                                                                        .height(1.dp)
                                                                        .background(Color.White.copy(alpha = 0.08f))
                                                                )
                                                                Text(
                                                                    text = "•   all applications   •",
                                                                    fontSize = 9.sp,
                                                                    fontWeight = FontWeight.Normal,
                                                                    fontFamily = currentFontFamily,
                                                                    color = adaptiveTextColor.copy(alpha = 0.25f),
                                                                    modifier = Modifier.padding(horizontal = 16.dp),
                                                                    letterSpacing = 1.sp
                                                                )
                                                                Box(
                                                                    modifier = Modifier
                                                                        .weight(1f)
                                                                        .height(1.dp)
                                                                        .background(Color.White.copy(alpha = 0.08f))
                                                                 )
                                                             }
                                                         }
                                                     }
                                                 }

                                                itemsIndexed(
                                                    items = restAppsEntries,
                                                    key = { _, entry ->
                                                        when (entry) {
                                                            is ListEntry.Header -> "rest_header_${entry.letter}"
                                                            is ListEntry.App -> "rest_${entry.appInfo.packageName}"
                                                        }
                                                    }
                                                ) { index, entry ->
                                                    when (entry) {
                                                        is ListEntry.Header -> {
                                                            AlphabetHeaderRow(
                                                                letter = entry.letter,
                                                                isActiveProvider = { touchedLetter == entry.letter },
                                                                isTouchingSidebarProvider = { isTouchingSidebar },
                                                                currentFontFamily = currentFontFamily,
                                                                themeColor = currentThemeColor
                                                            )
                                                        }
                                                        is ListEntry.App -> {
                                                            val app = entry.appInfo
                                                            val isAppHovered = highlightedApp == app
                                                            val alphaTarget = if (app.packageName in limitedAppsSet) {
                                                                0.15f
                                                            } else if (isTouchingSidebar) {
                                                                if (isAppHovered) {
                                                                    val touchX = sidebarTouchX ?: 0f
                                                                    if (touchX > 165f) 1.0f else 0.3f
                                                                } else {
                                                                    0.3f
                                                                }
                                                            } else {
                                                                1.0f
                                                            }
                                                            val animatedAlpha by animateFloatAsState(targetValue = alphaTarget, label = "app_alpha_rest")

                                                            AppRow(
                                                                index = topNApps.size + 2 + index,
                                                                app = app,
                                                                alphaProvider = { animatedAlpha },
                                                                isAppHoveredProvider = { highlightedApp == app },
                                                                sidebarTouchXProvider = { sidebarTouchX },
                                                                sidebarTouchYProvider = { sidebarTouchY },
                                                                sidebarInteractiveProgressProvider = { sidebarInteractiveProgress },
                                                                listState = listState,
                                                                currentFontFamily = currentFontFamily,
                                                                iconPackVal = iconPackVal,
                                                                iconThemeColor = iconThemeColor,
                                                                contentColor = adaptiveTextColor,
                                                                onLongPress = { focusedContextMenuApp = app },
                                                                onTap = {
                                                                    val isLimited = app.packageName in limitedAppsSet
                                                                    if (isLimited) {
                                                                        activeBreakerApp = app
                                                                    } else {
                                                                        activity.launchAppWithTracker(app.packageName)
                                                                    }
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                            } else {
                                                itemsIndexed(
                                                    items = standardListEntries,
                                                    key = { _, entry ->
                                                        when (entry) {
                                                            is ListEntry.Header -> "flat_header_${entry.letter}"
                                                            is ListEntry.App -> "flat_${entry.appInfo.packageName}"
                                                        }
                                                    }
                                                ) { index, entry ->
                                                    when (entry) {
                                                        is ListEntry.Header -> {
                                                            AlphabetHeaderRow(
                                                                letter = entry.letter,
                                                                isActiveProvider = { touchedLetter == entry.letter },
                                                                isTouchingSidebarProvider = { isTouchingSidebar },
                                                                currentFontFamily = currentFontFamily,
                                                                themeColor = currentThemeColor
                                                            )
                                                        }
                                                        is ListEntry.App -> {
                                                            val app = entry.appInfo
                                                            val isAppHovered = highlightedApp == app
                                                            val alphaTarget = if (app.packageName in limitedAppsSet) {
                                                                0.15f
                                                            } else if (isTouchingSidebar) {
                                                                if (isAppHovered) {
                                                                    val touchX = sidebarTouchX ?: 0f
                                                                    if (touchX > 165f) 1.0f else 0.3f
                                                                } else {
                                                                    0.3f
                                                                }
                                                            } else {
                                                                1.0f
                                                            }
                                                            val animatedAlpha by animateFloatAsState(targetValue = alphaTarget, label = "app_alpha_std")

                                                            AppRow(
                                                                index = index,
                                                                app = app,
                                                                alphaProvider = { animatedAlpha },
                                                                isAppHoveredProvider = { highlightedApp == app },
                                                                sidebarTouchXProvider = { sidebarTouchX },
                                                                sidebarTouchYProvider = { sidebarTouchY },
                                                                sidebarInteractiveProgressProvider = { sidebarInteractiveProgress },
                                                                listState = listState,
                                                                currentFontFamily = currentFontFamily,
                                                                iconPackVal = iconPackVal,
                                                                iconThemeColor = iconThemeColor,
                                                                contentColor = adaptiveTextColor,
                                                                onLongPress = { focusedContextMenuApp = app },
                                                                onTap = {
                                                                    val isLimited = app.packageName in limitedAppsSet
                                                                    if (isLimited) {
                                                                        activeBreakerApp = app
                                                                    } else {
                                                                        activity.launchAppWithTracker(app.packageName)
                                                                    }
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    val alphabetBox: @Composable RowScope.() -> Unit = {
                                        val showTopFade by remember {
                                            derivedStateOf {
                                                alphabetListState.firstVisibleItemIndex > 0 || alphabetListState.firstVisibleItemScrollOffset > 0
                                            }
                                        }
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight(0.85f)
                                                .align(Alignment.CenterVertically)
                                                .width(48.dp)
                                                .padding(bottom = 120.dp)
                                                .drawWithContent {
                                                    drawContent()
                                                    drawRect(
                                                        brush = Brush.verticalGradient(
                                                            0.0f to if (showTopFade) Color.Transparent else Color.Black,
                                                            0.15f to Color.Black,
                                                            0.85f to Color.Black,
                                                            1.0f to Color.Transparent
                                                        ),
                                                        blendMode = BlendMode.DstIn
                                                    )
                                                }
                                                .pointerInput(letters) {
                                                    detectVerticalDragGestures(
                                                        onDragStart = { isTouchingSidebar = true },
                                                        onDragEnd = { isTouchingSidebar = false },
                                                        onDragCancel = { isTouchingSidebar = false },
                                                        onVerticalDrag = { change, _ ->
                                                            change.consume()
                                                            val totalHeight = size.height.toFloat()
                                                            val currentY = change.position.y.coerceIn(0f, totalHeight)
                                                            dragProgressState.value = currentY / totalHeight
                                                        }
                                                    )
                                                }
                                        ) {
                                            LazyColumn(
                                                state = alphabetListState,
                                                userScrollEnabled = false,
                                                modifier = Modifier.fillMaxSize(),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                items(letters) { letter ->
                                                    Text(
                                                        text = letter.toString(),
                                                        style = MaterialTheme.typography.titleMedium.copy(
                                                            shadow = Shadow(
                                                                color = Color.Black.copy(alpha = 0.5f),
                                                                offset = Offset(2f, 2f),
                                                                blurRadius = 4f
                                                            )
                                                        ),
                                                        color = adaptiveTextColor,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(vertical = 4.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Column(modifier = Modifier.fillMaxSize()) {
                                        if (displayedPages.size > 1) {
                                            Row(
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                                            ) {
                                                displayedPages.forEachIndexed { idx, _ ->
                                                    val isSelected = idx == currentPageIndex
                                                    val dotWidth by animateDpAsState(targetValue = if (isSelected) 16.dp else 6.dp, label = "dot_width")
                                                    val dotColor = if (isSelected) currentThemeColor else adaptiveTextColor
                                                    Box(
                                                        modifier = Modifier
                                                            .padding(horizontal = 4.dp)
                                                            .width(dotWidth)
                                                            .height(6.dp)
                                                            .clip(CircleShape)
                                                            .background(dotColor.copy(alpha = if (isSelected) 1f else 0.35f))
                                                            .clickable {
                                                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                                currentPageIndex = idx
                                                            }
                                                    )
                                                }
                                            }
                                        }

                                        Box(modifier = Modifier.weight(1f)) {
                                            AnimatedContent(
                                                targetState = currentPageIndex,
                                                transitionSpec = {
                                                    val springSpec = spring<androidx.compose.ui.unit.IntOffset>(
                                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                                        stiffness = Spring.StiffnessMediumLow
                                                    )
                                                    val fadeSpec = tween<Float>(durationMillis = 300, easing = FastOutSlowInEasing)
                                                    if (targetState > initialState) {
                                                        (slideInHorizontally(animationSpec = springSpec) { width -> width / 2 } + fadeIn(animationSpec = fadeSpec))
                                                            .togetherWith(slideOutHorizontally(animationSpec = springSpec) { width -> -width / 2 } + fadeOut(animationSpec = fadeSpec))
                                                    } else {
                                                        (slideInHorizontally(animationSpec = springSpec) { width -> -width / 2 } + fadeIn(animationSpec = fadeSpec))
                                                            .togetherWith(slideOutHorizontally(animationSpec = springSpec) { width -> width / 2 } + fadeOut(animationSpec = fadeSpec))
                                                    }
                                                },
                                                label = "PageSlideTransition"
                                            ) { targetIndex ->
                                                val targetPageName = displayedPages.getOrNull(targetIndex) ?: "App List"
                                                Box(modifier = Modifier.fillMaxSize()) {
                                                    if (targetPageName == "Music") {
                                                        MusicPage(
                                                            themeColor = currentThemeColor,
                                                            fontFamily = currentFontFamily,
                                                            activity = activity,
                                                            modifier = Modifier.fillMaxSize().background(Color.Transparent),
                                                            contentColor = adaptiveTextColor,
                                                            shadowColor = if (isLightBackground) Color.White.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.6f)
                                                        )
                                                    } else if (targetPageName == "Notifications") {
                                                        NotificationsPage(
                                                            notifications = notificationsListState,
                                                            themeColor = currentThemeColor,
                                                            fontFamily = currentFontFamily,
                                                            activity = activity,
                                                            allowedCategories = allowedNotificationCategoriesVal,
                                                            onLongPressApp = { packageAppInfo -> focusedContextMenuApp = packageAppInfo },
                                                            onNotificationClick = { appName, _, defaultPkg ->
                                                                val foundApp = uiState.apps.firstOrNull { 
                                                                    it.label.equals(appName, ignoreCase = true) || 
                                                                    it.packageName.equals(defaultPkg, ignoreCase = true) ||
                                                                    it.packageName.contains(appName, ignoreCase = true) 
                                                                }
                                                                if (foundApp != null) {
                                                                    activity.launchAppWithTracker(foundApp.packageName)
                                                                }
                                                            },
                                                            contentColor = adaptiveTextColor,
                                                            modifier = Modifier.fillMaxSize().background(Color.Transparent)
                                                        )
                                                    } else if (targetPageName == "App List") {
                                                        Box(modifier = Modifier.fillMaxSize().background(Color.Transparent)) {
                                                            Row(
                                                                modifier = Modifier.fillMaxSize(),
                                                                horizontalArrangement = Arrangement.SpaceBetween
                                                             ) {
                                                                 alphabetBox()
                                                                 listColumn()
                                                             }

                                                             val bubbleAlpha by animateFloatAsState(
                                                                 targetValue = if (isTouchingSidebar && lockedLetterState == null && touchedLetter != null) 1f else 0f,
                                                                 label = "bubbleAlpha"
                                                             )
                                                             if (bubbleAlpha > 0f) {
                                                                 Box(
                                                                     modifier = Modifier
                                                                         .align(Alignment.Center)
                                                                         .graphicsLayer { alpha = bubbleAlpha }
                                                                         .size(120.dp)
                                                                         .background(
                                                                             Color.Black.copy(alpha = 0.65f),
                                                                             shape = RoundedCornerShape(24.dp)
                                                                         ),
                                                                     contentAlignment = Alignment.Center
                                                                 ) {
                                                                     Text(
                                                                         text = (touchedLetter ?: 'A').toString(),
                                                                         color = Color.White,
                                                                         fontSize = 64.sp,
                                                                         fontWeight = FontWeight.ExtraBold,
                                                                         fontFamily = currentFontFamily
                                                                     )
                                                                 }
                                                             }
                                                         }
                                                     } else {
                                                         val pageWidgets = widgetDataListVal.filter { it.pageName == targetPageName }
                                                         WidgetPage(
                                                             pageName = targetPageName,
                                                             widgetDataList = pageWidgets,
                                                             themeColor = currentThemeColor,
                                                             fontFamily = currentFontFamily,
                                                             activity = activity,
                                                             modifier = Modifier.fillMaxSize().background(Color.Transparent)
                                                         )
                                                     }
                                                 }
                                             }
                                         }
                                    }
                                }
                            }
                            if (isNotificationsExpanded) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.25f))
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                                        ) {
                                            isNotificationsExpanded = false
                                        }
                                )
                            }
                        }
                    }


                }

            }

            AnimatedVisibility(
                visible = zoomLevel == 2,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                PageReorderOverview(
                    activePagesVal = displayedPages,
                    currentPageIndex = currentPageIndex,
                    onPageClick = { idx ->
                        currentPageIndex = idx
                        zoomLevel = 0
                    },
                    onReorder = { newList ->
                        activity.saveActivePages(newList)
                    },
                    activity = activity,
                    themeColor = currentThemeColor,
                    fontFamily = currentFontFamily,
                    widgetDataListVal = widgetDataListVal,
                    notificationsListState = notificationsListState,
                    mediaTrackInfoVal = mediaTrackInfoVal,
                    allowedNotificationCategoriesVal = allowedNotificationCategoriesVal,
                    uiState = uiState
                )
            }

            AnimatedVisibility(
                visible = zoomLevel == 1,
                enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .background(Color(0xE6101010), RoundedCornerShape(24.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(24.dp))
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "PAGE OVERVIEW",
                        fontSize = 11.sp,
                        fontFamily = currentFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = currentThemeColor,
                        style = TextStyle(letterSpacing = 1.5.sp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                zoomLevel = 0
                                showSettingsPanel = true
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f)),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings, 
                                contentDescription = null, 
                                tint = MaterialTheme.colorScheme.onSurface, 
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Settings", 
                                fontSize = 11.sp, 
                                fontFamily = currentFontFamily, 
                                color = adaptiveTextColor,
                                maxLines = 1
                            )
                        }

                        Button(
                            onClick = {
                                zoomLevel = 0
                                try {
                                    val intent = Intent(Intent.ACTION_SET_WALLPAPER)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    activity.startActivity(intent)
                                } catch (_: Exception) {
                                    try {
                                        val intent = Intent(android.provider.Settings.ACTION_SETTINGS)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        activity.startActivity(intent)
                                    } catch (_: Exception) {}
                                }
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f)),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add, 
                                contentDescription = null, 
                                tint = MaterialTheme.colorScheme.onSurface, 
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Wallpaper", 
                                fontSize = 11.sp, 
                                fontFamily = currentFontFamily, 
                                color = adaptiveTextColor,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        if (expandedFolderPackageName != null && currentFolderAppsPackageList != null) {
            FolderExpandCard(
                folderName = expandedFolderPackageName!!,
                packages = currentFolderAppsPackageList!!,
                allApps = uiState.apps,
                themeColor = currentThemeColor,
                fontFamily = currentFontFamily,
                onClose = { 
                    expandedFolderPackageName = null
                    currentFolderAppsPackageList = null
                },
                onLaunchApp = { app ->
                    activity.launchAppWithTracker(app.packageName)
                    expandedFolderPackageName = null
                }
            )
        }

        if (focusedContextMenuApp != null) {
            AppContextMenuOverlay(
                app = focusedContextMenuApp!!,
                folders = listOf("Social", "Utilities", "Media"),
                hiddenList = hiddenAppsSet,
                isBreakerEnabled = focusedContextMenuApp!!.packageName in limitedAppsSet,
                themeColor = currentThemeColor,
                fontFamily = currentFontFamily,
                onClose = { focusedContextMenuApp = null },
                onToggleHide = {
                    val now = hiddenAppsSet.toMutableSet()
                    if (focusedContextMenuApp!!.packageName in now) {
                        now.remove(focusedContextMenuApp!!.packageName)
                    } else {
                        now.add(focusedContextMenuApp!!.packageName)
                    }
                    activity.hiddenApps.value = now
                    activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putStringSet("hidden_packages", now) }
                    focusedContextMenuApp = null
                },
                onToggleBreaker = {
                    val pkg = focusedContextMenuApp!!.packageName
                    limitedAppsSet = if (pkg in limitedAppsSet) {
                        limitedAppsSet - pkg
                    } else {
                        limitedAppsSet + pkg
                    }
                    focusedContextMenuApp = null
                },
                onUninstall = {
                    val pkg = focusedContextMenuApp!!.packageName
                    val intent = Intent(Intent.ACTION_DELETE)
                    intent.data = "package:$pkg".toUri()
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    focusedContextMenuApp = null
                },
                onAddToFolder = { folder ->
                    val now = folderMap.toMutableMap()
                    val lst = (now[folder] ?: emptyList()).toMutableList()
                    if (focusedContextMenuApp!!.packageName !in lst) {
                        lst.add(focusedContextMenuApp!!.packageName)
                    }
                    now[folder] = lst
                    activity.folderMapState.value = now
                    focusedContextMenuApp = null
                }
            )
        }

        if (activeBreakerApp != null) {
            UsageBreakerOverlay(
                appName = activeBreakerApp!!.label,
                icon = activeBreakerApp!!.icon,
                onLaunch = { 
                    activity.launchAppWithTracker(activeBreakerApp!!.packageName)
                    activeBreakerApp = null
                },
                onClose = { activeBreakerApp = null }
            )
        }

        AnimatedVisibility(
            visible = showSettingsPanel,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            SettingsPanel(
                onClose = { 
                    showSettingsPanel = false
                    activeSettingsCategory = null
                },
                themeColor = currentThemeColor,
                fontFamily = currentFontFamily,
                activity = activity,
                initialCategory = activeSettingsCategory
            )
        }

        if (isSleepingState) {
            WakeSleepScreen(onWake = { isSleepingState = false })
        }
        
        val currentUser = selectedUser
        if (currentUser != null) {
            UserProfileScreen(
                user = currentUser,
                onDismiss = { selectedUser = null }
            )
        }

        val isLaunchingAppVal by activity.isLaunchingApp.collectAsState()
        val isClosingAppVal by activity.isClosingApp.collectAsState()
        val transitionProgressVal by activity.appTransitionProgress.collectAsState()
        val animatingPackageVal by activity.animatingAppPackage.collectAsState()
        val animatingBoundsVal by activity.animatingAppIconBounds.collectAsState()

        if (isLaunchingAppVal || isClosingAppVal) {
            val progress = transitionProgressVal
            val bounds = animatingBoundsVal ?: android.graphics.Rect(150, 450, 350, 650)
            
            val configuration = androidx.compose.ui.platform.LocalConfiguration.current
            
            val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
            val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
            
            val startWidth = bounds.width().toFloat().coerceAtLeast(10f)
            val startHeight = bounds.height().toFloat().coerceAtLeast(10f)
            val startCenterX = bounds.centerX().toFloat()
            val startCenterY = bounds.centerY().toFloat()
            
            val endWidth = screenWidthPx
            val endHeight = screenHeightPx
            val endCenterX = screenWidthPx / 2f
            val endCenterY = screenHeightPx / 2f
            
            val currentWidth = startWidth + (endWidth - startWidth) * progress
            val currentHeight = startHeight + (endHeight - startHeight) * progress
            val currentCenterX = startCenterX + (endCenterX - startCenterX) * progress
            val currentCenterY = startCenterY + (endCenterY - startCenterY) * progress
            
            val leftOffset = currentCenterX - currentWidth / 2f
            val topOffset = currentCenterY - currentHeight / 2f
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f * progress))
            ) {
                Box(
                    modifier = Modifier
                        .offset {
                            androidx.compose.ui.unit.IntOffset(
                                leftOffset.roundToInt(),
                                topOffset.roundToInt()
                            )
                        }
                        .size(
                            width = with(density) { currentWidth.toDp() },
                            height = with(density) { currentHeight.toDp() }
                        )
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            shape = RoundedCornerShape((16 * (1f - progress)).dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (animatingPackageVal != null) {
                        val appInfo = uiState.apps.find { it.packageName == animatingPackageVal }
                        if (appInfo != null) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                val iconSize = (36 + 48 * progress).dp
                                StyledAppIcon(
                                    icon = appInfo.icon,
                                    pack = iconPackVal,
                                    themeColor = iconThemeColor,
                                    size = iconSize
                                )
                                if (progress < 0.6f) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = appInfo.label,
                                        style = MaterialTheme.typography.labelMedium.copy(color = Color.White),
                                        fontFamily = currentFontFamily,
                                        maxLines = 1,
                                        modifier = Modifier.graphicsLayer {
                                            alpha = 1f - (progress / 0.6f)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Global Floating Search Bar
        androidx.compose.animation.AnimatedVisibility(
            visible = !isNotificationsExpanded && !isLaunchingAppVal && !isClosingAppVal,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            AdvancedSearchBar(
                query = searchQuery,
                onQueryChange = {
                    searchQuery = it
                    activity.fetchWebSuggestions(it)
                },
                fontFamily = currentFontFamily,
                onSearchWeb = { q ->
                    try {
                        val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                            putExtra(android.app.SearchManager.QUERY, q)
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                },
                isSearchFocused = isSearchFocused,
                onFocusChanged = { isSearchFocused = it },
                onSearchExecute = {
                    val hasLocalMatch = displayedResults.any {
                        it is SearchResult.AppResult ||
                        it is SearchResult.ContactResult ||
                        it is SearchResult.SettingResult ||
                        it is SearchResult.FileResult
                    }
                    if (hasLocalMatch) {
                        val topResult = displayedResults.firstOrNull { it !is SearchResult.WebResult } ?: displayedResults.firstOrNull()
                        if (topResult != null) {
                            UniversalSearchEngine.recordSelection(contextForSearch, topResult)
                            when (topResult) {
                                is SearchResult.ContactResult -> {
                                    if (topResult.isRoomUser) {
                                        val userId = topResult.id.removePrefix("room_").toIntOrNull()
                                        val match = allUsersVal.find { it.id == userId }
                                        if (match != null) {
                                            selectedUser = match
                                        } else {
                                            try {
                                                val intent = Intent(Intent.ACTION_DIAL, "tel:${topResult.phoneNumber}".toUri())
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                contextForSearch.startActivity(intent)
                                            } catch (_: Exception) {}
                                        }
                                    } else {
                                        try {
                                            val intent = Intent(Intent.ACTION_DIAL, "tel:${topResult.phoneNumber}".toUri())
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            contextForSearch.startActivity(intent)
                                        } catch (_: Exception) {}
                                    }
                                }
                                is SearchResult.AppResult -> {
                                    val isLimitedApp = topResult.packageName in limitedAppsSet
                                    val appInfo = uiState.apps.find { it.packageName == topResult.packageName }
                                    if (isLimitedApp && appInfo != null) {
                                        activeBreakerApp = appInfo
                                    } else {
                                        activity.launchAppWithTracker(topResult.packageName)
                                    }
                                }
                                is SearchResult.WebResult -> {
                                    try {
                                        val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                                            putExtra(android.app.SearchManager.QUERY, topResult.label)
                                        }
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        contextForSearch.startActivity(intent)
                                    } catch (e: Exception) {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, "https://www.google.com/search?q=${java.net.URLEncoder.encode(topResult.label, "UTF-8")}".toUri())
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            contextForSearch.startActivity(intent)
                                        } catch (_: Exception) {}
                                    }
                                }
                                is SearchResult.SettingResult -> {
                                    if (topResult.action.startsWith("launcher_")) {
                                        val cat = when (topResult.action) {
                                            "launcher_perf" -> "Performance"
                                            "launcher_gestures" -> "Gestures"
                                            "launcher_permissions" -> "Permissions"
                                            "launcher_search" -> "Search"
                                            "launcher_pages" -> "Pages"
                                            else -> null
                                        }
                                        activeSettingsCategory = cat
                                        showSettingsPanel = true
                                        focusManager.clearFocus()
                                    } else {
                                        try {
                                            val intent = Intent(topResult.action)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            contextForSearch.startActivity(intent)
                                        } catch (_: Exception) {}
                                    }
                                }
                                is SearchResult.FileResult -> {
                                    Toast.makeText(contextForSearch, "Opening match: ${topResult.label}", Toast.LENGTH_SHORT).show()
                                }
                            }
                            searchQuery = ""
                            isSearchFocused = false
                        }
                    } else {
                        val webSearchQuery = displayedResults.find { it is SearchResult.WebResult }?.label ?: searchQuery
                        if (webSearchQuery.trim().isNotEmpty()) {
                            try {
                                val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                                    putExtra(android.app.SearchManager.QUERY, webSearchQuery)
                                }
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                contextForSearch.startActivity(intent)
                            } catch (_: Exception) {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, "https://www.google.com/search?q=${java.net.URLEncoder.encode(webSearchQuery, "UTF-8")}".toUri())
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    contextForSearch.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                        }
                        searchQuery = ""
                        isSearchFocused = false
                    }
                }
            )
        }
    }
}
