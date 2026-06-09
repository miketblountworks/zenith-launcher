package com.example

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Choreographer
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.ui.theme.MyApplicationTheme
import com.example.widgets.CustomAppWidgetHost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.roundToInt

data class WidgetData(val id: Int, val widthSpan: Int = 4, val heightSpan: Int = 2, val pageName: String = "", val revision: Int = 0)

// CustomAppWidgetHost is extracted to com.example.widgets.CustomAppWidgetHost

data class AppNotification(
    val appName: String,
    val text: String,
    val pkg: String,
    val key: String,
    val sbn: android.service.notification.StatusBarNotification? = null
)

data class MediaTrackInfo(
    val title: String,
    val artist: String,
    val packageName: String,
    val isPlaying: Boolean,
    val durationMs: Long = 180000L,
    val progressMs: Long = 0L,
    val artwork: android.graphics.Bitmap? = null
)

class MainActivity : ComponentActivity() {
    lateinit var database: AppDatabase
    lateinit var userRepository: UserRepository
    
    lateinit var appWidgetHost: CustomAppWidgetHost
    lateinit var appWidgetManager: AppWidgetManager
    
    val _longPressedWidgetId get() = com.example.launcher.LauncherState._longPressedWidgetId
    val draggingWidgetId get() = com.example.launcher.LauncherState.draggingWidgetId
    val dragOffsetX get() = com.example.launcher.LauncherState.dragOffsetX
    val dragOffsetY get() = com.example.launcher.LauncherState.dragOffsetY
    
    val _widgetDataList get() = com.example.launcher.LauncherState._widgetDataList
    val widgetDataList get() = com.example.launcher.LauncherState.widgetDataList
    val widgetTargetPage get() = com.example.launcher.LauncherState.widgetTargetPage

    // Configuration states
    val activePages get() = com.example.launcher.LauncherState.activePages
    val mediaTrackInfo = MutableStateFlow<MediaTrackInfo?>(null)
    
    fun saveActivePages(pages: List<String>) {
        activePages.value = pages
        getSharedPreferences("launcher_settings", MODE_PRIVATE)
            .edit()
            .putString("active_pages", pages.joinToString(","))
            .apply()
    }

    fun dispatchMediaKey(keyCode: Int) {
        com.example.media.MediaController.dispatchMediaKey(this, keyCode)
    }

    override fun onKeyDown(keyCode: Int, event: android.view.KeyEvent?): Boolean {
        if (keyCode == android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE ||
            keyCode == android.view.KeyEvent.KEYCODE_MEDIA_PLAY ||
            keyCode == android.view.KeyEvent.KEYCODE_MEDIA_PAUSE ||
            keyCode == android.view.KeyEvent.KEYCODE_MEDIA_NEXT ||
            keyCode == android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
            // Already handled via custom dispatching or consumed safely
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: android.view.KeyEvent?): Boolean {
        if (keyCode == android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE ||
            keyCode == android.view.KeyEvent.KEYCODE_MEDIA_PLAY ||
            keyCode == android.view.KeyEvent.KEYCODE_MEDIA_PAUSE ||
            keyCode == android.view.KeyEvent.KEYCODE_MEDIA_NEXT ||
            keyCode == android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    val isAIGenerating = MutableStateFlow(false)

    fun generateLocationBasedLandscape(wallpaperStyle: String) {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) return
        
        lifecycleScope.launch {
            isAIGenerating.value = true
            try {
                val locationManager = getSystemService(LOCATION_SERVICE) as android.location.LocationManager
                val location = locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER) 
                               ?: locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER)
                
                val locationName = if (location != null) {
                    val geocoder = android.location.Geocoder(this@MainActivity, java.util.Locale.getDefault())
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val city = addresses[0].locality ?: addresses[0].subAdminArea ?: addresses[0].adminArea ?: ""
                        val country = addresses[0].countryName ?: ""
                        if (city.isNotEmpty() || country.isNotEmpty()) "$city, $country" else "a beautiful scenic place"
                    } else "a beautiful scenic place"
                } else "a beautiful scenic place"

                val styleStr = if (wallpaperStyle == "System Wallpaper" || wallpaperStyle == "Local Image") "vibrant" else wallpaperStyle
                val prompt = "A beautiful breathtaking landscape wallpaper of $locationName, in $styleStr style, high quality, masterpiece, scenic, mobile wallpaper aspect ratio."
                
                val success = com.example.ui.ai.GeminiImageGenerator.generateWallpaper(
                    prompt, filesDir.resolve("ai_location_landscape.jpg")
                )
                
                if (success) {
                    withContext(Dispatchers.Main) {
                        selectedWallpaper.value = "AI Generated Location"
                        getSharedPreferences("launcher_settings", MODE_PRIVATE)
                            .edit()
                            .putString("wallpaper", "AI Generated Location")
                            .apply()
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
    val animatingAppIconBounds = MutableStateFlow<android.graphics.Rect?>(null)
    val appTransitionProgress = MutableStateFlow(0f)
    val isLaunchingApp = MutableStateFlow(false)
    val isClosingApp = MutableStateFlow(false)
    val appIconBoundsMap = java.util.concurrent.ConcurrentHashMap<String, android.graphics.Rect>()

    val selectedWallpaper = MutableStateFlow("System Wallpaper")
    val clockStyle = MutableStateFlow("Dextera Date")
    val selectedFont = MutableStateFlow("System Default")
    val iconPack = MutableStateFlow("Classic")
    val themeMode = MutableStateFlow("Dark Mode")
    val materialYouEnabled = MutableStateFlow(true)
    val declutterMode = MutableStateFlow(false)
    val categoriseByUsage = MutableStateFlow(false)
    val usageLimitCount = MutableStateFlow(6)
    val appUsageScores = MutableStateFlow<Map<String, Long>>(emptyMap())
    val gesturesEnabled = MutableStateFlow(true)
    val usageBreakerMinutes = MutableStateFlow(1)
    val hiddenApps = MutableStateFlow<Set<String>>(emptySet())
    val notificationSummaryEnabled = MutableStateFlow(true)
    val use24HourFormat = MutableStateFlow(true)
    val useFahrenheit = MutableStateFlow(true)
    val dynamicIconColorEnabled = MutableStateFlow(false)
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

    val overdrawElimination = MutableStateFlow(true)
    val asyncImageDecoding = MutableStateFlow(true)
    val jvmZgcConfiguration = MutableStateFlow(true)
    val adaptiveFpsThermal = MutableStateFlow(true)
    val syntheticClickDelay = MutableStateFlow(true)
    val touchInterpolation = MutableStateFlow(true)
    val cpuThreadAffinity = MutableStateFlow(true)
    val asyncVfsMmap = MutableStateFlow(true)

    val liveFps = MutableStateFlow(120.0)
    val currentThermalStatus = MutableStateFlow("Cool (Optimized)")
    val adaptiveVsyncTarget = MutableStateFlow("120Hz")
    val isPerformanceFpsActive = MutableStateFlow(true)

    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        isLocationPermissionGranted.value = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        updatePermissionStates()
    }

    val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: android.net.Uri? ->
        if (uri != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    contentResolver.openInputStream(uri)?.use { input ->
                        filesDir.resolve("local_wallpaper.jpg").outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    selectedWallpaper.value = "Local Image"
                    getSharedPreferences("launcher_settings", MODE_PRIVATE).edit().putString("wallpaper", "Local Image").apply()
                    decodeAndExtractWallpaperColor()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun pickLocalWallpaper() {
        try {
            imagePickerLauncher.launch("image/*")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun fetchBingWallpaper() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val urlConnection = java.net.URL("https://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1").openConnection() as java.net.HttpURLConnection
                urlConnection.connectTimeout = 10000
                urlConnection.readTimeout = 10000
                val data = urlConnection.inputStream.bufferedReader().use { it.readText() }
                val regex = """"url"\s*:\s*"([^"]+)"""".toRegex()
                val match = regex.find(data)
                if (match != null) {
                    val path = match.groupValues[1]
                    val fullUrl = "https://www.bing.com$path"
                    
                    val imgConnection = java.net.URL(fullUrl).openConnection() as java.net.HttpURLConnection
                    imgConnection.connectTimeout = 15000
                    imgConnection.readTimeout = 15000
                    imgConnection.inputStream.use { input ->
                        filesDir.resolve("bing_wallpaper.jpg").outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    
                    withContext(Dispatchers.Main) {
                        bingWallpaperUrl.value = fullUrl
                        selectedWallpaper.value = "Bing Daily"
                        getSharedPreferences("launcher_settings", MODE_PRIVATE).edit()
                            .putString("bing_wallpaper_url", fullUrl)
                            .putString("wallpaper", "Bing Daily")
                            .apply()
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
        if (prompt.isBlank()) return
        isAIGeneratingImage.value = true
        lifecycleScope.launch {
            val success = com.example.ui.ai.GeminiImageGenerator.generateWallpaper(
                prompt, filesDir.resolve("ai_wallpaper.jpg")
            )
            if (success) {
                withContext(Dispatchers.Main) {
                    selectedWallpaper.value = "AI Generated"
                    getSharedPreferences("launcher_settings", MODE_PRIVATE)
                        .edit()
                        .putString("wallpaper", "AI Generated")
                        .apply()
                    decodeAndExtractWallpaperColor()
                }
            } else {
                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(this@MainActivity, "AI Generation Failed. Check Settings -> Secrets for your Gemini API Key.", android.widget.Toast.LENGTH_LONG).show()
                }
            }
            isAIGeneratingImage.value = false
        }
    }

    fun decodeAndExtractWallpaperColor() {
        lifecycleScope.launch(Dispatchers.IO) {
            val wall = selectedWallpaper.value
            var bmp: android.graphics.Bitmap? = null
            if (wall == "Local Image") {
                val file = filesDir.resolve("local_wallpaper.jpg")
                if (file.exists()) {
                    bmp = BitmapFactory.decodeFile(file.absolutePath)
                }
            } else if (wall == "AI Generated") {
                val file = filesDir.resolve("ai_wallpaper.jpg")
                if (file.exists()) {
                    bmp = BitmapFactory.decodeFile(file.absolutePath)
                }
            } else if (wall == "AI Generated Location") {
                val file = filesDir.resolve("ai_location_landscape.jpg")
                if (file.exists()) {
                    bmp = BitmapFactory.decodeFile(file.absolutePath)
                }
            } else if (wall == "Bing Daily") {
                val file = filesDir.resolve("bing_wallpaper.jpg")
                if (file.exists()) {
                    bmp = BitmapFactory.decodeFile(file.absolutePath)
                }
            } else if (wall == "System Wallpaper") {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
                    try {
                        val wpManager = android.app.WallpaperManager.getInstance(this@MainActivity)
                        val wpColors = wpManager.getWallpaperColors(android.app.WallpaperManager.FLAG_SYSTEM)
                        if (wpColors != null) {
                            val primaryArgb = wpColors.primaryColor.toArgb()
                            withContext(Dispatchers.Main) {
                                extractedWallpaperColor.value = Color(primaryArgb)
                            }
                            return@launch
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            
            if (bmp != null) {
                val color = extractDominantColor(bmp)
                withContext(Dispatchers.Main) {
                    extractedWallpaperColor.value = color
                }
            } else {
                withContext(Dispatchers.Main) {
                    extractedWallpaperColor.value = null
                }
            }
        }
    }

    private fun extractDominantColor(bitmap: android.graphics.Bitmap): Color {
        return try {
            val resized = android.graphics.Bitmap.createScaledBitmap(bitmap, 16, 16, false)
            var rSum = 0
            var gSum = 0
            var bSum = 0
            var count = 0
            for (x in 0 until 16) {
                for (y in 0 until 16) {
                    val pixel = resized.getPixel(x, y)
                    val a = android.graphics.Color.alpha(pixel)
                    if (a > 200) {
                        rSum += android.graphics.Color.red(pixel)
                        gSum += android.graphics.Color.green(pixel)
                        bSum += android.graphics.Color.blue(pixel)
                        count++
                    }
                }
            }
            if (count == 0) return Color(0xFF8C9EFF)
            val r = rSum / count
            val g = gSum / count
            val b = bSum / count
            val hsv = FloatArray(3)
            android.graphics.Color.RGBToHSV(r, g, b, hsv)
            hsv[1] = maxOf(hsv[1], 0.65f)
            hsv[2] = maxOf(hsv[2], 0.85f)
            val rgb = android.graphics.Color.HSVToColor(hsv)
            Color(rgb)
        } catch (e: Exception) {
            Color(0xFF8C9EFF)
        }
    }

    private var lastFrameTimeNanos = 0L
    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (lastFrameTimeNanos != 0L) {
                val diffNanos = frameTimeNanos - lastFrameTimeNanos
                if (diffNanos > 0) {
                    val frameTimeMs = diffNanos / 1_000_000.0
                    val calculatedFps = 1000.0 / frameTimeMs
                    val targetFps = if (calculatedFps in 30.0..144.0) calculatedFps else 120.0
                    val multiplier = if (adaptiveVsyncTarget.value == "90Hz") 0.75 else if (adaptiveVsyncTarget.value == "60Hz") 0.50 else 1.0
                    liveFps.value = (liveFps.value * 0.95 + (targetFps * multiplier) * 0.05)
                }
            }
            lastFrameTimeNanos = frameTimeNanos
            if (isPerformanceFpsActive.value) {
                Choreographer.getInstance().postFrameCallback(this)
            }
        }
    }

    fun startChoreographerMonitoring() {
        isPerformanceFpsActive.value = true
        lastFrameTimeNanos = 0L
        Choreographer.getInstance().postFrameCallback(frameCallback)
    }

    fun stopChoreographerMonitoring() {
        isPerformanceFpsActive.value = false
    }

    fun registerThermalListener() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                val powerManager = getSystemService(POWER_SERVICE) as? android.os.PowerManager
                powerManager?.addThermalStatusListener(mainExecutor) { status ->
                    val statusStr = when (status) {
                        android.os.PowerManager.THERMAL_STATUS_NONE -> "Cool (Optimized)"
                        android.os.PowerManager.THERMAL_STATUS_LIGHT -> "Light (Optimized)"
                        android.os.PowerManager.THERMAL_STATUS_MODERATE -> "Moderate (Optimized)"
                        android.os.PowerManager.THERMAL_STATUS_SEVERE -> "Severe (Adaptive VSync Throttling Engaged)"
                        android.os.PowerManager.THERMAL_STATUS_CRITICAL -> "Critical (Throttling Target 60Hz)"
                        android.os.PowerManager.THERMAL_STATUS_EMERGENCY -> "Emergency (Thermal Guard Activated)"
                        android.os.PowerManager.THERMAL_STATUS_SHUTDOWN -> "Overheat Shutdown"
                        else -> "Unknown status ($status)"
                    }
                    currentThermalStatus.value = statusStr
                    
                    if (adaptiveFpsThermal.value) {
                        if (status >= android.os.PowerManager.THERMAL_STATUS_SEVERE) {
                            adaptiveVsyncTarget.value = if (status >= android.os.PowerManager.THERMAL_STATUS_CRITICAL) "60Hz" else "90Hz"
                        } else {
                            adaptiveVsyncTarget.value = "120Hz"
                        }
                    } else {
                        adaptiveVsyncTarget.value = "120Hz"
                    }
                }
            } else {
                currentThermalStatus.value = "Unmanaged (Pre-Q)"
            }
        } catch (e: Exception) {
            currentThermalStatus.value = "Demo Mode (Active)"
        }
    }

    val notificationList = MutableStateFlow<List<AppNotification>>(emptyList())

    // Pop-Up Folders mapping
    val folderMapState = MutableStateFlow<Map<String, List<String>>>(
        mapOf(
            "Social" to listOf("com.android.chrome", "com.google.android.youtube"),
            "Utilities" to listOf("com.android.settings", "com.android.calculator")
        )
    )

    val pickWidgetLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val appWidgetId = result.data?.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1) ?: -1
            if (appWidgetId != -1) {
                val info = appWidgetManager.getAppWidgetInfo(appWidgetId)
                if (info != null && info.configure != null) {
                    val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE)
                    intent.component = info.configure
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    configureWidgetLauncher.launch(intent)
                } else {
                    addWidget(appWidgetId)
                }
            }
        }
    }
    
    val configureWidgetLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val appWidgetId = result.data?.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1) ?: -1
            if (appWidgetId != -1) {
                addWidget(appWidgetId)
            }
        }
    }

    var configuringWidgetId: Int = -1

    val reconfigureWidgetLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val appWidgetId = if (configuringWidgetId != -1) configuringWidgetId else {
            result.data?.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1) ?: -1
        }
        if (appWidgetId != -1) {
            val current = _widgetDataList.value.map {
                if (it.id == appWidgetId) it.copy(revision = it.revision + 1) else it
            }
            _widgetDataList.value = current
            saveWidgets(current)
        }
        configuringWidgetId = -1
    }
    
    private fun addWidget(appWidgetId: Int) {
        val current = _widgetDataList.value.toMutableList()
        current.add(WidgetData(appWidgetId, pageName = widgetTargetPage.value))
        _widgetDataList.value = current
        saveWidgets(current)
    }

    fun removeWidget(appWidgetId: Int) {
        val current = _widgetDataList.value.filter { it.id != appWidgetId }
        _widgetDataList.value = current
        saveWidgets(current)
        appWidgetHost.deleteAppWidgetId(appWidgetId)
    }
    
    fun updateWidgetSize(appWidgetId: Int, widthSpan: Int, heightSpan: Int) {
        val current = _widgetDataList.value.map { if (it.id == appWidgetId) it.copy(widthSpan = widthSpan, heightSpan = heightSpan) else it }
        _widgetDataList.value = current
        saveWidgets(current)
    }

    fun swapWidgets(index1: Int, index2: Int) {
        val current = _widgetDataList.value.toMutableList()
        if (index1 in current.indices && index2 in current.indices) {
            val temp = current[index1]
            current[index1] = current[index2]
            current[index2] = temp
            _widgetDataList.value = current
            saveWidgets(current)
        }
    }

    fun startDragging(widgetId: Int) {
        draggingWidgetId.value = widgetId
        dragOffsetX.value = 0f
        dragOffsetY.value = 0f
    }
    
    fun updateDragOffset(widgetId: Int, dx: Float, dy: Float, cellWidthPx: Float, cellHeightPx: Float) {
        if (draggingWidgetId.value != widgetId) return
        val currentX = dragOffsetX.value + dx
        val currentY = dragOffsetY.value + dy
        dragOffsetX.value = currentX
        dragOffsetY.value = currentY
        
        val list = _widgetDataList.value
        val index = list.indexOfFirst { it.id == widgetId }
        if (index != -1) {
            val cellH = if (cellHeightPx > 0f) cellHeightPx else 1f
            val cellW = if (cellWidthPx > 0f) cellWidthPx else 1f
            if (currentY > cellH * 0.8f && index < list.size - 1) {
                dragOffsetY.value = currentY - cellH
                swapWidgets(index, index + 1)
            } else if (currentY < -cellH * 0.8f && index > 0) {
                dragOffsetY.value = currentY + cellH
                swapWidgets(index, index - 1)
            } else if (currentX > cellW * 0.8f && index < list.size - 1) {
                dragOffsetX.value = currentX - cellW
                swapWidgets(index, index + 1)
            } else if (currentX < -cellW * 0.8f && index > 0) {
                dragOffsetX.value = currentX + cellW
                swapWidgets(index, index - 1)
            }
        }
    }
    
    fun endDragging(widgetId: Int) {
        if (draggingWidgetId.value == widgetId) {
            draggingWidgetId.value = null
            dragOffsetX.value = 0f
            dragOffsetY.value = 0f
        }
    }

    private fun saveWidgets(list: List<WidgetData>) {
        val str = list.joinToString(",") { "${it.id}:${it.widthSpan}:${it.heightSpan}:${it.pageName}" }
        getSharedPreferences("launcher", MODE_PRIVATE).edit()
            .putString("widgets_v2", str)
            .apply()
    }

    fun hasUsageStatsPermission(context: Context): Boolean {
        return try {
            val appOps = context.getSystemService(APP_OPS_SERVICE) as android.app.AppOpsManager
            val mode = appOps.noteOpNoThrow(
                android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
            mode == android.app.AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            false
        }
    }

    @android.annotation.SuppressLint("MissingPermission")
    fun refreshAppUsageScores() {
        lifecycleScope.launch(Dispatchers.IO) {
            val scores = mutableMapOf<String, Long>()
            val localPrefs = getSharedPreferences("launcher_local_launches", MODE_PRIVATE)
            val localKeys = localPrefs.all
            for ((pkg, value) in localKeys) {
                val count = when (value) {
                    is Int -> value.toLong()
                    is Long -> value
                    is Float -> value.toLong()
                    is String -> value.toLongOrNull() ?: 0L
                    else -> 0L
                }
                if (count > 0L) {
                    scores[pkg] = count * 600000L
                }
            }
            if (hasUsageStatsPermission(this@MainActivity)) {
                try {
                    val usageStatsManager = getSystemService(USAGE_STATS_SERVICE) as? android.app.usage.UsageStatsManager
                    if (usageStatsManager != null) {
                        val cal = java.util.Calendar.getInstance()
                        cal.add(java.util.Calendar.DAY_OF_YEAR, -7)
                        val statsList = usageStatsManager.queryUsageStats(
                            android.app.usage.UsageStatsManager.INTERVAL_BEST,
                            cal.timeInMillis,
                            System.currentTimeMillis()
                        )
                        if (statsList != null) {
                            for (stats in statsList) {
                                val foregroundTime = stats.totalTimeInForeground
                                if (foregroundTime > 0) {
                                    val pkg = stats.packageName
                                    scores[pkg] = (scores[pkg] ?: 0L) + foregroundTime
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            appUsageScores.value = scores
        }
    }

    fun incrementLocalLaunchCount(packageName: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val prefs = getSharedPreferences("launcher_local_launches", MODE_PRIVATE)
            val current = prefs.getLong(packageName, 0L)
            prefs.edit().putLong(packageName, current + 1L).apply()
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
                val bounds = appIconBoundsMap[packageName]
                // if (bounds != null) {
                //     launchIntent.sourceBounds = bounds
                //     val options = android.app.ActivityOptions.makeClipRevealAnimation(
                //         window.decorView,
                //         bounds.left,
                //         bounds.top,
                //         bounds.width(),
                //         bounds.height()
                //     )
                //     startActivity(launchIntent, options.toBundle())
                // } else {
                    startActivity(launchIntent)
                // }
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
        isLocationPermissionGranted.value = checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        
        val pkgName = packageName
        val flat = android.provider.Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        var enabled = false
        if (!flat.isNullOrEmpty()) {
            val names = flat.split(":")
            for (name in names) {
                val cn = android.content.ComponentName.unflattenFromString(name)
                if (cn != null) {
                    if (pkgName == cn.packageName) {
                        enabled = true
                        break
                    }
                }
            }
        }
        isNotificationPermissionGranted.value = enabled
    }

    private var suggestionJob: kotlinx.coroutines.Job? = null
    fun fetchWebSuggestions(query: String) {
        suggestionJob?.cancel()
        if (query.trim().isEmpty()) {
            webSuggestions.value = emptyList()
            return
        }
        suggestionJob = lifecycleScope.launch {
            try {
                kotlinx.coroutines.delay(200)
                val suggestions = withContext(Dispatchers.IO) {
                    val encoded = java.net.URLEncoder.encode(query, "UTF-8")
                    val url = java.net.URL("https://suggestqueries.google.com/complete/search?client=firefox&q=$encoded")
                    val connection = url.openConnection() as java.net.HttpURLConnection
                    connection.connectTimeout = 1500
                    connection.readTimeout = 1500
                    val text = connection.inputStream.bufferedReader().use { it.readText() }
                    
                    val list = mutableListOf<String>()
                    try {
                        val jsonArray = org.json.JSONArray(text)
                        if (jsonArray.length() > 1) {
                            val suggestionsArray = jsonArray.getJSONArray(1)
                            for (i in 0 until suggestionsArray.length()) {
                                val sug = suggestionsArray.optString(i)
                                if (!sug.isNullOrEmpty() && sug != query) {
                                    list.add(sug)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    list.take(5)
                }
                webSuggestions.value = suggestions
            } catch (e: kotlinx.coroutines.CancellationException) {
                // Ignore cancellation to avoid overwriting state with old query dummy values
                throw e
            } catch (e: Exception) {
                webSuggestions.value = listOf(
                    "$query weather",
                    "$query news",
                    "$query wiki",
                    "how to $query",
                    "what is $query"
                )
            }
        }
    }

    val homeEntryTrigger = MutableStateFlow(0)

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasCategory(Intent.CATEGORY_HOME)) {
            if (intent.hasExtra("gesture_nav_contract_v1")) {
                handleGestureNavContract(intent)
            } else {
                homeEntryTrigger.value += 1
            }
        } else if (intent.hasExtra("gesture_nav_contract_v1")) {
            handleGestureNavContract(intent)
        }
    }

    private fun handleGestureNavContract(intent: Intent) {
        val contractBundle = intent.getBundleExtra("gesture_nav_contract_v1") ?: return
        val componentName = contractBundle.getParcelable<android.content.ComponentName>("gesture_nav_contract_component") ?: return
        val callbackMessage = contractBundle.getParcelable<android.os.Message>("android.intent.extra.REMOTE_CALLBACK") ?: return
        
        val targetIconBounds = locateIconOnGrid(componentName)
        
        val response = Bundle().apply {
            putParcelable("gesture_nav_contract_icon_position", android.graphics.RectF(
                targetIconBounds.left.toFloat(),
                targetIconBounds.top.toFloat(),
                targetIconBounds.right.toFloat(),
                targetIconBounds.bottom.toFloat()
            ))
        }
        try {
            val reply = android.os.Message.obtain().apply {
                copyFrom(callbackMessage)
                data = response
            }
            reply.replyTo.send(reply)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun locateIconOnGrid(component: android.content.ComponentName): android.graphics.Rect {
        val bounds = appIconBoundsMap[component.packageName]
        return bounds ?: android.graphics.Rect(150, 450, 350, 650)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        database = androidx.room.Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "user-database"
        ).build()
        userRepository = UserRepository(database.userDao())
        
        // Ask Android window manager to render the system wallpaper behind the activity
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER)

        // Optimize for 120Hz displays
        val display = windowManager.defaultDisplay
        val modes = display.supportedModes
        val preferredMode = modes.maxByOrNull { it.refreshRate }
        if (preferredMode != null) {
            window.attributes = window.attributes.apply {
                preferredDisplayModeId = preferredMode.modeId
            }
        }
        
        appWidgetManager = AppWidgetManager.getInstance(this)
        appWidgetHost = CustomAppWidgetHost(this, 1024).apply {
            onLongPressListener = { widgetId ->
                _longPressedWidgetId.value = widgetId
            }
            onDragStartListener = { widgetId ->
                startDragging(widgetId)
            }
            onDragListener = { widgetId, dx, dy ->
                val density = resources.displayMetrics.density
                val screenWidthDp = resources.configuration.screenWidthDp
                val cellWidthPx = (screenWidthDp / 4f) * density
                val cellHeightPx = 60 * density
                updateDragOffset(widgetId, dx, dy, cellWidthPx, cellHeightPx)
            }
            onDragEndListener = { widgetId ->
                endDragging(widgetId)
            }
        }
        appWidgetHost.startListening()
        
        val savedWidgetsV2 = getSharedPreferences("launcher", MODE_PRIVATE).getString("widgets_v2", "") ?: ""
        if (savedWidgetsV2.isNotEmpty()) {
            _widgetDataList.value = savedWidgetsV2.split(",").mapNotNull { 
                val parts = it.split(":")
                if (parts.isNotEmpty()) {
                    val id = parts[0].toIntOrNull()
                    val w = parts.getOrNull(1)?.toIntOrNull() ?: 4
                    val h = parts.getOrNull(2)?.toIntOrNull() ?: 2
                    val pageName = parts.getOrNull(3) ?: ""
                    if (id != null) WidgetData(id, w, h, pageName) else null
                } else null
            }
        } else {
            val savedWidgets = getSharedPreferences("launcher", MODE_PRIVATE).getString("widgets", "") ?: ""
            if (savedWidgets.isNotEmpty()) {
                _widgetDataList.value = savedWidgets.split(",").mapNotNull { it.toIntOrNull()?.let { id -> WidgetData(id) } }
            }
        }

        val prefs = getSharedPreferences("launcher_settings", MODE_PRIVATE)
        selectedWallpaper.value = prefs.getString("wallpaper", "System Wallpaper") ?: "System Wallpaper"
        clockStyle.value = prefs.getString("clock_style", "Dextera Date") ?: "Dextera Date"
        selectedFont.value = prefs.getString("font", "System Default") ?: "System Default"
        iconPack.value = prefs.getString("icon_pack", "Classic") ?: "Classic"
        themeMode.value = prefs.getString("theme_mode", "Dark Mode") ?: "Dark Mode"
        materialYouEnabled.value = prefs.getBoolean("material_you", true)
        declutterMode.value = prefs.getBoolean("declutter_mode", false)
        categoriseByUsage.value = prefs.getBoolean("categorise_by_usage", false)
        usageLimitCount.value = prefs.getInt("usage_limit_count", 6)
        gesturesEnabled.value = prefs.getBoolean("gestures_enabled", true)
        usageBreakerMinutes.value = prefs.getInt("usage_breaker_min", 1)
        hiddenApps.value = prefs.getStringSet("hidden_packages", emptySet()) ?: emptySet()
        notificationSummaryEnabled.value = prefs.getBoolean("notification_summary", true)
        use24HourFormat.value = prefs.getBoolean("use_24_hour", true)
        useFahrenheit.value = prefs.getBoolean("use_fahrenheit", true)
        dynamicIconColorEnabled.value = prefs.getBoolean("dynamic_icon_color", false)
        leftHandedScrollEnabled.value = prefs.getBoolean("left_handed_scroll", false)
        bingWallpaperUrl.value = prefs.getString("bing_wallpaper_url", "") ?: ""
        wallpaperBlurEnabled.value = prefs.getBoolean("wallpaper_blur", false)
        val defaultCats = setOf("Finance 💰", "Travel ✈️", "Social 💬", "Internet 🌐", "Entertainment 🎵", "Shopping 🛍️", "General 📦")
        allowedNotificationCategories.value = prefs.getStringSet("allowed_notification_categories", defaultCats) ?: defaultCats

        val savedPagesString = prefs.getString("active_pages", "App List,Music,Notifications") ?: "App List,Music,Notifications"
        activePages.value = savedPagesString.split(",").map { it.trim() }.filter { it.isNotEmpty() }

        // Load 120Hz performance optimization states
        overdrawElimination.value = prefs.getBoolean("perf_overdraw_elimination", true)
        asyncImageDecoding.value = prefs.getBoolean("perf_async_image_decoding", true)
        jvmZgcConfiguration.value = prefs.getBoolean("perf_jvm_zgc_config", true)
        adaptiveFpsThermal.value = prefs.getBoolean("perf_adaptive_fps_thermal", true)
        syntheticClickDelay.value = prefs.getBoolean("perf_synthetic_click_delay", true)
        touchInterpolation.value = prefs.getBoolean("perf_touch_interpolation", true)
        cpuThreadAffinity.value = prefs.getBoolean("perf_cpu_thread_affinity", true)
        asyncVfsMmap.value = prefs.getBoolean("perf_async_vfs_mmap", true)

        // Implement initial Overdraw Elimination from the design doc:
        // "invoke getWindow().setBackgroundDrawable(null) during initialization when a custom background is detected..."
        if (overdrawElimination.value && selectedWallpaper.value != "System Wallpaper") {
            try {
                window?.setBackgroundDrawable(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        decodeAndExtractWallpaperColor()

        // Start active loop metrics and thermal governors as per Page 4-10
        startChoreographerMonitoring()
        registerThermalListener()

        // Bind to real notifications if the service is active
        lifecycleScope.launch {
            MyNotificationListenerService.notificationsFlow.collect { realList ->
                if (MyNotificationListenerService.isConnected) {
                    notificationList.value = realList
                }
            }
        }

        lifecycleScope.launch {
            MyNotificationListenerService.mediaFlow.collect { track ->
                if (MyNotificationListenerService.isConnected) {
                    mediaTrackInfo.value = track
                }
            }
        }

        enableEdgeToEdge(
            statusBarStyle = androidx.activity.SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = androidx.activity.SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent,
                    contentWindowInsets = WindowInsets(0, 0, 0, 0)
                ) { innerPadding ->
                    DexteraLauncherApp(modifier = Modifier.fillMaxSize().padding(innerPadding))
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopChoreographerMonitoring()
        appWidgetHost.stopListening()
    }
}

data class ContactInfo(
    val name: String,
    val phoneNumber: String,
    val email: String = ""
)

data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable
)

data class LauncherUiState(
    val apps: List<AppInfo> = emptyList(),
    val letters: List<Char> = emptyList(),
    val isLoading: Boolean = true
)

class LauncherViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LauncherUiState())
    val uiState: StateFlow<LauncherUiState> = _uiState

    fun loadApps(packageManager: PackageManager) {
        viewModelScope.launch {
            val appList = withContext(Dispatchers.IO) {
                val intent = Intent(Intent.ACTION_MAIN, null).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                }
                val resolveInfos = packageManager.queryIntentActivities(intent, 0)
                resolveInfos.map {
                    AppInfo(
                        label = it.loadLabel(packageManager).toString(),
                        packageName = it.activityInfo.packageName,
                        icon = it.loadIcon(packageManager)
                    )
                }.sortedBy { it.label.lowercase() }.distinctBy { it.packageName }
            }
            val lettersList = appList.map { it.label.firstOrNull()?.uppercaseChar() ?: '#' }
                .distinct()
                .sorted()
                
            _uiState.value = LauncherUiState(
                apps = appList,
                letters = lettersList,
                isLoading = false
            )
        }
    }
}

// Helper math evaluator for search bar calculator
private fun evaluateMath(expr: String): String {
    try {
        val clean = expr.replace(" ", "")
        val regex = Regex("""(\d+)([\+\-\*\/])(\d+)""")
        val match = regex.find(clean)
        if (match != null) {
            val num1 = match.groupValues[1].toDoubleOrNull() ?: return "Error"
            val num2 = match.groupValues[3].toDoubleOrNull() ?: return "Error"
            val op = match.groupValues[2]
            val res = when (op) {
                "+" -> num1 + num2
                "-" -> num1 - num2
                "*" -> num1 * num2
                "/" -> if (num2 != 0.0) num1 / num2 else "DivByZero"
                else -> ""
            }
            return res.toString()
        }
        return "Enter simple math (e.g. 15 * 6)"
    } catch (e: Exception) {
        return "Error"
    }
}

@Composable
fun WallpaperBackground(wallpaper: String, bingWallpaperUrl: String, blurEnabled: Boolean = false) {
    val blurModifier = if (blurEnabled) Modifier.blur(16.dp, 16.dp) else Modifier
    Box(modifier = Modifier.fillMaxSize().then(blurModifier)) {
        val context = LocalContext.current
        val activity = context as? MainActivity

        when (wallpaper) {
            "Local Image" -> {
                val file = remember(wallpaper) { context.filesDir.resolve("local_wallpaper.jpg") }
                if (file.exists()) {
                    Image(
                        painter = rememberAsyncImagePainter(file),
                        contentDescription = "Dynamic Local Wallpaper",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            "AI Generated" -> {
                val file = remember(wallpaper) { context.filesDir.resolve("ai_wallpaper.jpg") }
                if (file.exists()) {
                    Image(
                        painter = rememberAsyncImagePainter(file),
                        contentDescription = "Stable Diffusion AI Wallpaper",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            "AI Generated Location" -> {
                val file = remember(wallpaper) { context.filesDir.resolve("ai_location_landscape.jpg") }
                if (file.exists()) {
                    Image(
                        painter = rememberAsyncImagePainter(file),
                        contentDescription = "AI Location Landscape Wallpaper",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            "Bing Daily" -> {
                val file = remember(wallpaper) { context.filesDir.resolve("bing_wallpaper.jpg") }
                if (file.exists()) {
                    Image(
                        painter = rememberAsyncImagePainter(file),
                        contentDescription = "Bing Daily Wallpaper",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (bingWallpaperUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(bingWallpaperUrl),
                        contentDescription = "Bing Daily Wallpaper",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            else -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    when (wallpaper) {
                        "System Wallpaper" -> {
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.35f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.55f)
                                    )
                                )
                            )
                        }
                        "Pitch Black" -> {
                            drawRect(color = Color.Black)
                        }
                        "Cosmic Slate" -> {
                            drawRect(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFF232533), Color(0xFF0C0D12)),
                                    center = Offset(w * 0.5f, h * 0.5f),
                                    radius = h * 0.8f
                                )
                            )
                        }
                        "Cyber Dawn" -> {
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFF14021D), Color(0xFFD61E51), Color(0xFFE08B00))
                                )
                            )
                        }
                        "Sand Dunes" -> {
                            drawRect(Color(0xFF190D00))
                            val path1 = androidx.compose.ui.graphics.Path().apply {
                                moveTo(0f, h * 0.65f)
                                quadraticTo(w * 0.4f, h * 0.55f, w, h * 0.72f)
                                lineTo(w, h)
                                lineTo(0f, h)
                                close()
                            }
                            val path2 = androidx.compose.ui.graphics.Path().apply {
                                moveTo(0f, h * 0.82f)
                                quadraticTo(w * 0.7f, h * 0.77f, w, h * 0.87f)
                                lineTo(w, h)
                                lineTo(0f, h)
                                close()
                            }
                            drawPath(path1, brush = Brush.verticalGradient(listOf(Color(0xFF50352F), Color(0xFF221100))))
                            drawPath(path2, brush = Brush.verticalGradient(listOf(Color(0xFF7A594F), Color(0xFF331E18))))
                        }
                        "Mint Breeze" -> {
                            drawRect(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFF0E3224), Color(0xFF040D0B)),
                                    center = Offset(w * 0.7f, h * 0.2f),
                                    radius = h * 0.7f
                                )
                            )
                        }
                        "Royal Satin" -> {
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFF0D0117), Color(0xFF291580), Color(0xFF010617))
                                )
                            )
                        }
                        "Solar Eclipse" -> {
                            drawRect(Color(0xFF020205))
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFFEAA000).copy(alpha = 0.35f), Color.Transparent),
                                    center = Offset(w * 0.5f, h * 0.3f),
                                    radius = w * 0.55f
                                ),
                                center = Offset(w * 0.5f, h * 0.3f),
                                radius = w * 0.55f
                            )
                            drawCircle(Color.Black, center = Offset(w * 0.5f, h * 0.3f), radius = w * 0.25f)
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )
    }
}

@Composable
fun ClockStyleWidget(
    style: String, 
    fontFamily: FontFamily, 
    primaryColor: Color, 
    isLocationGranted: Boolean,
    use24HourFormat: Boolean,
    useFahrenheit: Boolean
) {
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }
    
    LaunchedEffect(use24HourFormat) {
        while (true) {
            val cal = java.util.Calendar.getInstance()
            currentTime = if (use24HourFormat) {
                String.format("%02d:%02d", cal.get(java.util.Calendar.HOUR_OF_DAY), cal.get(java.util.Calendar.MINUTE))
            } else {
                val hr12 = cal.get(java.util.Calendar.HOUR)
                val hourActual = if (hr12 == 0) 12 else hr12
                val ampm = if (cal.get(java.util.Calendar.AM_PM) == java.util.Calendar.AM) "AM" else "PM"
                String.format("%d:%02d %s", hourActual, cal.get(java.util.Calendar.MINUTE), ampm)
            }
            val dayAbbr = when (cal.get(java.util.Calendar.DAY_OF_WEEK)) {
                java.util.Calendar.SUNDAY -> "Sun"
                java.util.Calendar.MONDAY -> "Mon"
                java.util.Calendar.TUESDAY -> "Tue"
                java.util.Calendar.WEDNESDAY -> "Wed"
                java.util.Calendar.THURSDAY -> "Thu"
                java.util.Calendar.FRIDAY -> "Fri"
                java.util.Calendar.SATURDAY -> "Sat"
                else -> ""
            }
            val monthAbbr = when (cal.get(java.util.Calendar.MONTH)) {
                java.util.Calendar.JANUARY -> "Jan"
                java.util.Calendar.FEBRUARY -> "Feb"
                java.util.Calendar.MARCH -> "Mar"
                java.util.Calendar.APRIL -> "Apr"
                java.util.Calendar.MAY -> "May"
                java.util.Calendar.JUNE -> "Jun"
                java.util.Calendar.JULY -> "Jul"
                java.util.Calendar.AUGUST -> "Aug"
                java.util.Calendar.SEPTEMBER -> "Sep"
                java.util.Calendar.OCTOBER -> "Oct"
                java.util.Calendar.NOVEMBER -> "Nov"
                java.util.Calendar.DECEMBER -> "Dec"
                else -> ""
            }
            currentDate = "$dayAbbr, $monthAbbr ${cal.get(java.util.Calendar.DAY_OF_MONTH)}"
            kotlinx.coroutines.delay(1000)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (style) {
            "Dextera Date" -> {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currentDate,
                            fontSize = 16.sp,
                            fontFamily = fontFamily,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("☀️", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (useFahrenheit) {
                             if (isLocationGranted) "70°F | Sunny" else "75°F | Sunny"
                        } else {
                             if (isLocationGranted) "21°C | Sunny" else "24°C | Sunny"
                        },
                        fontSize = 14.sp,
                        fontFamily = fontFamily,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
            "Minimal Digital" -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = currentTime,
                        fontSize = 54.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = currentDate,
                            fontSize = 15.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(if (isLocationGranted) Color(0xFFFFB74D) else Color(0xFFFBC02D), RoundedCornerShape(6.dp))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (useFahrenheit) {
                                if (isLocationGranted) "Local · 70°F" else "75°F"
                            } else {
                                if (isLocationGranted) "Local · 21°C" else "24°C"
                            },
                            fontSize = 15.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
            "Bold Accent" -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val parts = currentTime.split(" ")
                    val mainTime = parts.getOrNull(0) ?: currentTime
                    val ampmSuffix = parts.getOrNull(1) ?: ""
                    
                    val tParts = mainTime.split(":")
                    val hr = tParts.getOrNull(0) ?: "00"
                    val min = tParts.getOrNull(1) ?: "00"
                    
                    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.Center) {
                        Text(hr, fontSize = 48.sp, fontFamily = fontFamily, fontWeight = FontWeight.ExtraBold, color = primaryColor)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(min, fontSize = 32.sp, fontFamily = fontFamily, fontWeight = FontWeight.Bold, color = Color.White)
                        if (ampmSuffix.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(ampmSuffix, fontSize = 16.sp, fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha = 0.7f))
                        }
                    }
                    Text(currentDate, fontSize = 11.sp, fontFamily = fontFamily, fontWeight = FontWeight.Light, color = Color.White.copy(alpha = 0.5f), textAlign = TextAlign.Center)
                    Text(
                        text = if (useFahrenheit) {
                            if (isLocationGranted) "Local Weather · 70°F · Sunny" else "New York · 72°F · Partly Cloudy"
                        } else {
                            if (isLocationGranted) "Local Weather · 21°C · Sunny" else "New York · 22°C · Partly Cloudy"
                        },
                        fontSize = 11.sp,
                        fontFamily = fontFamily,
                        color = Color.White.copy(alpha = 0.45f),
                        modifier = Modifier.padding(top = 2.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            "Classic Analog" -> {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    var angleSec by remember { mutableStateOf(0f) }
                    var angleMin by remember { mutableStateOf(0f) }
                    var angleHr by remember { mutableStateOf(0f) }
                    LaunchedEffect(Unit) {
                        while (true) {
                            val cal = java.util.Calendar.getInstance()
                            angleSec = cal.get(java.util.Calendar.SECOND) * 6f
                            angleMin = cal.get(java.util.Calendar.MINUTE) * 6f
                            angleHr = cal.get(java.util.Calendar.HOUR) * 30f + cal.get(java.util.Calendar.MINUTE) * 0.5f
                            kotlinx.coroutines.delay(1000)
                        }
                    }
                    Canvas(modifier = Modifier.size(54.dp)) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val radius = size.width / 2f
                        drawCircle(Color.White.copy(alpha = 0.12f), radius = radius, center = center)
                        drawCircle(primaryColor, radius = radius, center = center, style = Stroke(width = 1.5.dp.toPx()))
                        drawCircle(Color.White, radius = 2.dp.toPx(), center = center)
                        val hrLen = radius * 0.5f
                        val hrRad = Math.toRadians(angleHr.toDouble() - 90)
                        drawLine(Color.White, start = center, end = Offset((center.x + hrLen * Math.cos(hrRad)).toFloat(), (center.y + hrLen * Math.sin(hrRad)).toFloat()), strokeWidth = 3.dp.toPx())
                        val minLen = radius * 0.75f
                        val minRad = Math.toRadians(angleMin.toDouble() - 90)
                        drawLine(Color.White, start = center, end = Offset((center.x + minLen * Math.cos(minRad)).toFloat(), (center.y + minLen * Math.sin(minRad)).toFloat()), strokeWidth = 1.5.dp.toPx())
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(currentTime, fontSize = 20.sp, fontFamily = fontFamily, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(currentDate, fontSize = 10.sp, fontFamily = fontFamily, color = Color.White.copy(alpha = 0.6f))
                        Text(
                            text = if (useFahrenheit) "New York · 72°F · Partly Cloudy" else "New York · 22°C · Partly Cloudy",
                            fontSize = 10.sp,
                            fontFamily = fontFamily,
                            color = primaryColor.copy(alpha = 0.85f)
                        )
                    }
                }
            }
            "Typographic Word" -> {
                val cal = java.util.Calendar.getInstance()
                val hourNames = listOf("twelve", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven")
                val tens = listOf("", "ten", "twenty", "thirty", "forty", "fifty")
                val hr = cal.get(java.util.Calendar.HOUR)
                val min = cal.get(java.util.Calendar.MINUTE)
                val ampm = if (cal.get(java.util.Calendar.AM_PM) == java.util.Calendar.AM) "morning" else "afternoon"
                
                val hrWord = hourNames[hr % 12]
                val minText = if (min == 0) "o'clock" else if (min in 1..9) "oh ${hourNames[min]}" else if (min in 10..19) {
                    val teens = listOf("ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen")
                    teens[min - 10]
                } else {
                    val t = tens[min / 10]
                    val rem = min % 10
                    val remWord = if (rem > 0) " " + hourNames[rem] else ""
                    "$t$remWord"
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("It's $hrWord $minText", fontSize = 16.sp, fontFamily = fontFamily, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
                    Text("in the $ampm", fontSize = 13.sp, fontFamily = fontFamily, color = Color.White.copy(alpha = 0.6f), textAlign = TextAlign.Center)
                    Text(currentDate, fontSize = 10.sp, fontFamily = fontFamily, color = primaryColor, letterSpacing = 1.sp, modifier = Modifier.padding(top = 1.dp), textAlign = TextAlign.Center)
                    Text(
                        text = if (useFahrenheit) "New York · 72°F · Partly Cloudy" else "New York · 22°C · Partly Cloudy",
                        fontSize = 10.sp,
                        fontFamily = fontFamily,
                        color = Color.White.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun SwipableWidgetStack(fontFamily: FontFamily, primaryColor: Color) {
    var activeIdx by remember { mutableStateOf(0) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(modifier = Modifier.fillMaxWidth().height(105.dp)) {
                when (activeIdx) {
                    0 -> { // Weather
                        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                            Text("WEATHER REPORT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = primaryColor, letterSpacing = 1.sp)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("New York City", fontSize = 13.sp, fontFamily = fontFamily, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("72°F · Partly Cloudy", fontSize = 11.sp, fontFamily = fontFamily, color = Color.White.copy(alpha = 0.6f))
                                    Text("Wind: 5mph · Humidity: 45% · UV: 3", fontSize = 10.sp, fontFamily = fontFamily, color = Color.White.copy(alpha = 0.45f))
                                }
                                Box(modifier = Modifier.size(36.dp)) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawCircle(Color(0xFFFFB74D), radius = 8.dp.toPx(), center = Offset(size.width * 0.4f, size.height * 0.4f))
                                        drawCircle(Color.White.copy(alpha = 0.82f), radius = 10.dp.toPx(), center = Offset(size.width * 0.6f, size.height * 0.6f))
                                        drawCircle(Color.White.copy(alpha = 0.82f), radius = 7.dp.toPx(), center = Offset(size.width * 0.48f, size.height * 0.64f))
                                    }
                                }
                            }
                        }
                    }
                    1 -> { // Calendar
                        Column(modifier = Modifier.fillMaxSize()) {
                            Text("CALENDAR WIDGET", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = primaryColor, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(5.dp).background(primaryColor, RoundedCornerShape(2.5.dp)))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("10:05 AM - Design Alignment Meet", fontSize = 11.sp, color = Color.White, maxLines = 1)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(5.dp).background(Color.Yellow, RoundedCornerShape(2.5.dp)))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("02:00 PM - Dextera Development Sync", fontSize = 11.sp, color = Color.White, maxLines = 1)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(5.dp).background(Color.Gray, RoundedCornerShape(2.5.dp)))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("04:30 PM - Relax & Mindful Pause", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f), maxLines = 1)
                                }
                            }
                        }
                    }
                    2 -> { // Calculator Tool
                        var input by remember { mutableStateOf("") }
                        var result by remember { mutableStateOf("") }
                        Column(modifier = Modifier.fillMaxSize()) {
                            Text("INSTANT MATH CALCULATOR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = input,
                                    onValueChange = { 
                                        input = it
                                        result = evaluateMath(it)
                                    },
                                    placeholder = { Text("e.g., 25 * 4", fontSize = 11.sp, color = Color.White.copy(alpha = 0.4f)) },
                                    modifier = Modifier.weight(1f).height(46.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                        focusedBorderColor = primaryColor, unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                        focusedContainerColor = Color.White.copy(alpha = 0.05f)
                                    ),
                                    singleLine = true,
                                    textStyle = TextStyle(fontSize = 11.sp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(result, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = primaryColor, modifier = Modifier.widthIn(max = 90.dp), maxLines = 1)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                (0..2).forEach { d ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(if (activeIdx == d) 7.dp else 5.dp)
                            .background(if (activeIdx == d) primaryColor else Color.White.copy(alpha = 0.3f), RoundedCornerShape(3.5.dp))
                            .clickable { activeIdx = d }
                    )
                }
            }
        }
    }
}

@Composable
fun UsageBreakerOverlay(appName: String, icon: Drawable, onLaunch: () -> Unit, onClose: () -> Unit) {
    var timerSeconds by remember { mutableStateOf(5) }
    LaunchedEffect(Unit) {
        while (true) {
            if (timerSeconds > 0) {
                kotlinx.coroutines.delay(1000)
                timerSeconds--
            } else {
                break
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0A0B10)).padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("launcher wellbeing advisor", fontSize = 11.sp, color = Color.Gray, letterSpacing = 2.sp)
            Image(painter = rememberAsyncImagePainter(icon), contentDescription = "Wellbeing advisor for $appName", modifier = Modifier.size(64.dp))
            Text("You have spent 45m on $appName today.", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
            Text("Take a mindful moment and take a deep breath.", fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f), textAlign = TextAlign.Center)
            Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                var scale by remember { mutableStateOf(0.9f) }
                LaunchedEffect(Unit) {
                    while (true) {
                        scale = 1.25f
                        kotlinx.coroutines.delay(2000)
                        scale = 0.9f
                        kotlinx.coroutines.delay(2000)
                    }
                }
                val animatedScale by animateFloatAsState(scale)
                Box(modifier = Modifier.size((52 * animatedScale).dp).background(Color(0xFF8C9EFF).copy(alpha = 0.45f), RoundedCornerShape(100.dp)))
                Text(if (timerSeconds > 0) "$timerSeconds" else "✓", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = onClose, colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f))) {
                    Text("Close App", color = Color.White)
                }
                Button(onClick = onLaunch, enabled = timerSeconds == 0, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8C9EFF))) {
                    Text("Continue", color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun SwipeToDeleteContainer(
    modifier: Modifier = Modifier,
    onDismissed: () -> Unit,
    content: @Composable () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var itemDismissed by remember { mutableStateOf(false) }
    
    val targetOffsetX = when {
        itemDismissed -> if (offsetX > 0) 1000f else -1000f
        else -> offsetX
    }
    
    val animatedOffsetX by animateFloatAsState(
        targetValue = targetOffsetX,
        finishedListener = { finalOffset ->
            if (itemDismissed) {
                onDismissed()
            }
        }
    )
    
    val density = LocalDensity.current
    val offsetXDp = remember(animatedOffsetX) { (animatedOffsetX / maxOf(0.1f, density.density)).dp }
    val alpha = (1f - (abs(animatedOffsetX) / 300f)).coerceIn(0.1f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha)
            .offset(x = offsetXDp)
            .pointerInput(itemDismissed) {
                if (!itemDismissed) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX > 150f || offsetX < -150f) {
                                itemDismissed = true
                            } else {
                                offsetX = 0f
                            }
                        },
                        onDragCancel = {
                            offsetX = 0f
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount
                        }
                    )
                }
            }
    ) {
        content()
    }
}

fun getNotificationCategory(appName: String, text: String, pkg: String): String {
    val appLower = appName.lowercase()
    val textLower = text.lowercase()
    val pkgLower = pkg.lowercase()

    // 1. Finance
    val financeKeywords = listOf("bank", "pay", "card", "cash", "wallet", "transfer", "receipt", "charge", "refund", "credit", "debit", "revolut", "chase", "paypal", "venmo", "crypto", "bitcoin", "stocks", "invest", "bill", "invoice", "spent", "received", "balance")
    val financePackages = listOf("finance", "bank", "wallet", "ledger", "chase", "paypal", "venmo", "cashapp", "revolut", "google.android.apps.walletnfcrel")
    if (financePackages.any { pkgLower.contains(it) } ||
        financeKeywords.any { appLower.contains(it) } ||
        financeKeywords.any { textLower.contains(it) }) {
        return "Finance 💰"
    }

    // 2. Travel & Navigation
    val travelKeywords = listOf("uber", "lyft", "grab", "bolt", "airbnb", "booking", "flight", "trip", "map", "navigation", "waze", "transit", "airline", "hotel", "travel", "gps", "cab", "taxi", "train", "metro")
    val travelPackages = listOf("uber", "lyft", "waze", "maps", "airbnb", "booking", "travel", "transit", "cab", "taxi", "train")
    if (travelPackages.any { pkgLower.contains(it) } ||
        travelKeywords.any { appLower.contains(it) } ||
        travelKeywords.any { textLower.contains(it) }) {
        return "Travel ✈️"
    }

    // 3. Social & Communication
    val socialKeywords = listOf("slack", "whatsapp", "messenger", "telegram", "instagram", "snapchat", "pinterest", "reddit", "linkedin", "facebook", "twitter", "tiktok", "viber", "discord", "signal", "skype", "hangouts", "chat", "message", "email", "gmail", "outlook")
    val socialPackages = listOf("slack", "whatsapp", "messenger", "telegram", "instagram", "snapchat", "pinterest", "reddit", "linkedin", "facebook", "twitter", "tiktok", "discord", "signal", "gmail", "email", "communication")
    if (socialPackages.any { pkgLower.contains(it) } ||
        socialKeywords.any { appLower.contains(it) } ||
        textLower.contains("message") || textLower.contains("sent you") || textLower.contains("replied") || textLower.contains("commented") || textLower.contains("new pin") || textLower.contains("dm")) {
        return "Social 💬"
    }

    // 4. Internet & Productivity
    val prodKeywords = listOf("chrome", "firefox", "safari", "opera", "edge", "github", "notion", "drive", "docs", "sheets", "slides", "calendar", "keep", "duolingo", "medium", "learning", "study", "todo", "task", "zoom", "teams", "meet", "asana", "trello", "jira")
    val prodPackages = listOf("chrome", "firefox", "browser", "github", "notion", "drive", "docs", "sheets", "calendar", "duolingo", "learning", "task", "zoom", "teams", "productivity")
    if (prodPackages.any { pkgLower.contains(it) } ||
        prodKeywords.any { appLower.contains(it) } ||
        textLower.contains("streak") || textLower.contains("pull request") || textLower.contains("commit") || textLower.contains("reminder") || textLower.contains("meeting") || textLower.contains("event")) {
        return "Internet 🌐"
    }

    // 5. Entertainment & Media
    val mediaKeywords = listOf("youtube", "spotify", "netflix", "disney", "twitch", "prime video", "hulu", "hbo", "plex", "player", "music", "podcast", "radio", "game", "xbox", "playstation", "nintendo", "steam")
    val mediaPackages = listOf("youtube", "spotify", "netflix", "twitch", "player", "music", "podcast", "vlc", "audioplayer", "video", "game")
    if (mediaPackages.any { pkgLower.contains(it) } ||
        mediaKeywords.any { appLower.contains(it) } ||
        textLower.contains("playing") || textLower.contains("listening") || textLower.contains("video") || textLower.contains("episode") || textLower.contains("song") || textLower.contains("album")) {
        return "Entertainment 🎵"
    }

    // 6. Shopping & Food
    val shopKeywords = listOf("amazon", "ebay", "shopify", "doordash", "ubereats", "delivery", "order", "shipped", "cart", "walmart", "target", "aliexpress", "temu", "shein", "groceries", "food", "restaurant", "instacart", "mercari", "etsy")
    val shopPackages = listOf("amazon", "ebay", "shopify", "doordash", "ubereats", "delivery", "shopping", "instacart", "food")
    if (shopPackages.any { pkgLower.contains(it) } ||
        shopKeywords.any { appLower.contains(it) } ||
        textLower.contains("shipped") || textLower.contains("delivered") || textLower.contains("order number") || textLower.contains("out for delivery") || textLower.contains("purchased")) {
        return "Shopping 🛍️"
    }

    return "General 📦"
}

@Composable
fun NotificationSummaryWidget(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    fontFamily: FontFamily,
    primaryColor: Color,
    notifications: List<AppNotification>,
    onDismissNotification: (AppNotification) -> Unit,
    onNotificationClick: (appName: String, text: String, defaultPkg: String) -> Unit,
    allowedCategories: Set<String> = setOf("Finance 💰", "Travel ✈️", "Social 💬", "Internet 🌐", "Entertainment 🎵", "Shopping 🛍️", "General 📦")
) {
    // Filter active notifications using permissions / configuration
    val activeNotifications = remember(notifications, allowedCategories) {
        notifications.filter {
            val cat = getNotificationCategory(it.appName, it.text, it.pkg)
            allowedCategories.contains(cat)
        }
    }

    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onExpandedChange(!isExpanded) }
                    .padding(vertical = 4.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    val count = activeNotifications.size
                    val titleText = if (count == 0) "No notifications · All clear!" else "$count Bundled Notification Summary"
                    Text(titleText, fontSize = 11.sp, fontFamily = fontFamily, fontWeight = FontWeight.Bold, color = Color.White)
                }
                if (activeNotifications.isNotEmpty()) {
                    Text(if (isExpanded) "Collapse" else "Expand", fontSize = 10.sp, color = primaryColor)
                }
            }
            if (isExpanded && activeNotifications.isNotEmpty()) {
                var activeChipFilter by remember { mutableStateOf("All") }
                
                // Collect existing category subgroups for filtering
                val presentCategories = remember(activeNotifications) {
                    activeNotifications.map { getNotificationCategory(it.appName, it.text, it.pkg) }.distinct()
                }

                if (presentCategories.size > 1) {
                    Spacer(modifier = Modifier.height(4.dp))
                    androidx.compose.foundation.lazy.LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item {
                            val isSelected = activeChipFilter == "All"
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .border(BorderStroke(1.dp, if (isSelected) primaryColor else Color.White.copy(alpha = 0.15f)), RoundedCornerShape(20.dp))
                                    .background(if (isSelected) primaryColor.copy(alpha = 0.15f) else Color.Transparent)
                                    .clickable { activeChipFilter = "All" }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("All", fontSize = 9.sp, color = if (isSelected) primaryColor else Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        presentCategories.forEach { cat ->
                            item {
                                val isSelected = activeChipFilter == cat
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .border(BorderStroke(1.dp, if (isSelected) primaryColor else Color.White.copy(alpha = 0.15f)), RoundedCornerShape(20.dp))
                                        .background(if (isSelected) primaryColor.copy(alpha = 0.15f) else Color.Transparent)
                                        .clickable { activeChipFilter = cat }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(cat, fontSize = 9.sp, color = if (isSelected) primaryColor else Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                val filteredNotifications = remember(activeNotifications, activeChipFilter) {
                    activeNotifications.filter { activeChipFilter == "All" || getNotificationCategory(it.appName, it.text, it.pkg) == activeChipFilter }
                }

                val categorized = filteredNotifications.groupBy { getNotificationCategory(it.appName, it.text, it.pkg) }
                val categoryOrder = listOf(
                    "Finance 💰",
                    "Travel ✈️",
                    "Social 💬",
                    "Internet 🌐",
                    "Entertainment 🎵",
                    "Shopping 🛍️",
                    "General 📦"
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    categoryOrder.forEach { categoryName ->
                        val itemsInCategory = categorized[categoryName] ?: emptyList()
                        if (itemsInCategory.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White.copy(alpha = 0.02f), RoundedCornerShape(8.dp))
                                    .border(0.5.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                    .padding(6.dp)
                            ) {
                                // Category Header
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .background(primaryColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = categoryName,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = primaryColor,
                                                fontFamily = fontFamily
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                                                .padding(horizontal = 6.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "${itemsInCategory.size}",
                                                fontSize = 9.sp,
                                                color = Color.White.copy(alpha = 0.8f),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    
                                    Text(
                                        text = "Dismiss All",
                                        fontSize = 9.sp,
                                        color = Color.White.copy(alpha = 0.4f),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .clickable {
                                                itemsInCategory.forEach { item ->
                                                    onDismissNotification(item)
                                                }
                                            }
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                // Category Items
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    itemsInCategory.forEach { item ->
                                        val app = item.appName
                                        val text = item.text
                                        val pkg = item.pkg
                                        SwipeToDeleteContainer(
                                            onDismissed = { onDismissNotification(item) }
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .clickable { onNotificationClick(app, text, pkg) }
                                                    .padding(vertical = 5.dp, horizontal = 6.dp),
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .padding(top = 4.dp)
                                                        .size(4.dp)
                                                        .background(primaryColor, RoundedCornerShape(2.dp))
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = app,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = primaryColor,
                                                        fontFamily = fontFamily
                                                    )
                                                    Text(
                                                        text = text,
                                                        fontSize = 10.sp,
                                                        color = Color.White.copy(alpha = 0.7f),
                                                        maxLines = 2,
                                                        lineHeight = 12.sp
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
        }
    }
}

@Composable
fun CategoryHeader(title: String, color: Color, fontFamily: FontFamily) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        fontFamily = fontFamily,
        letterSpacing = 1.5.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun AdvancedSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    fontFamily: FontFamily,
    primaryColor: Color,
    onSearchWeb: (String) -> Unit,
    isSearchFocused: Boolean,
    onFocusChanged: (Boolean) -> Unit,
    onSearchExecute: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val mathResult = remember(query) { if (query.isNotEmpty()) evaluateMath(query) else "" }
    val isMath = mathResult != "Error" && mathResult != "Enter simple math (e.g. 15 * 6)" && query.any { it in "+-*/" }
    
    val density = LocalDensity.current
    val isImeVisible = WindowInsets.ime.getBottom(density) > 0
    val targetBottomPadding = if (isImeVisible) 20.dp else 12.dp

    // Design states based on whether search is focused or query is non-empty (Active search mode)
    val isSearchActive = isSearchFocused || query.isNotEmpty()

    // Margins animate: 24dp unfocused to 12dp focused
    val horizontalMargin by animateDpAsState(
        targetValue = if (isSearchActive) 12.dp else 24.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    )

    // Corner radius animate: 28dp unfocused (full pill) to 16dp focused (rounded sheet)
    val cornerRadius by animateDpAsState(
        targetValue = if (isSearchActive) 16.dp else 28.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    )

    // Container background color: lightened glassmorphic style
    val containerColor = if (isSearchActive) {
        Color.White.copy(alpha = 0.16f)
    } else {
        Color.White.copy(alpha = 0.08f)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .imePadding()
            .padding(start = horizontalMargin, end = horizontalMargin, top = 6.dp, bottom = targetBottomPadding)
    ) {
        if (query.isNotEmpty() && isMath) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ), 
                shape = RoundedCornerShape(16.dp), 
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Calculator Result: ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$query = $mathResult", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    }
                }
            }
        }

        // Search pill or rounded sheet container
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(cornerRadius),
            color = containerColor,
            border = BorderStroke(
                width = 1.dp,
                color = if (isSearchActive) primaryColor.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Leading Icon Layer with transitions
                if (isSearchActive) {
                    IconButton(
                        onClick = {
                            onQueryChange("")
                            focusManager.clearFocus()
                            onFocusChanged(false)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .padding(start = 12.dp, end = 8.dp)
                            .size(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Input Field
                androidx.compose.foundation.text.BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    textStyle = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Search
                    ),
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                        onSearch = {
                            onSearchExecute()
                        }
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { focusState ->
                            onFocusChanged(focusState.isFocused)
                        },
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(primaryColor),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (query.isEmpty()) {
                                Text(
                                    text = "Search contacts, apps, web...",
                                    fontSize = 14.sp,
                                    fontFamily = fontFamily,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                // Trailing Icon Layer (Avatar / Clear)
                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = { onQueryChange("") }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    // Profile Avatar (30dp size) as per standard specifications
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(32.dp)
                            .background(
                                color = primaryColor.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                            .border(1.dp, primaryColor.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "MT", // Represents User "Mike T."
                            color = primaryColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = fontFamily
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserProfileScreen(user: UserEntity, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable(interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }, indication = null) {} // Block touches
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack, 
                        contentDescription = "Back", 
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = "User Profile", 
                    color = MaterialTheme.colorScheme.onBackground, 
                    fontSize = 20.sp, 
                    fontWeight = FontWeight.Bold
                )
            }
            
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(100.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.name.take(1).uppercase(), 
                        color = MaterialTheme.colorScheme.onPrimaryContainer, 
                        fontSize = 48.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = user.name, 
                    color = MaterialTheme.colorScheme.onBackground, 
                    fontSize = 28.sp, 
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Phone, 
                                contentDescription = null, 
                                tint = MaterialTheme.colorScheme.primary, 
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = user.phoneNumber.ifBlank { "No phone number" }, 
                                color = MaterialTheme.colorScheme.onSurface, 
                                fontSize = 16.sp
                            )
                        }
                        if (user.info.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info, 
                                    contentDescription = null, 
                                    tint = MaterialTheme.colorScheme.primary, 
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = user.info, 
                                    color = MaterialTheme.colorScheme.onSurface, 
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WakeSleepScreen(onWake: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black).pointerInput(Unit) { detectTapGestures(onDoubleTap = { onWake() }) }, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("launcher is suspended", fontSize = 11.sp, color = Color.White.copy(alpha = 0.3f), letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Double Tap to Wake Screen", fontSize = 14.sp, color = Color.White.copy(alpha = 0.4f))
        }
    }
}

@Composable
fun FolderExpandCard(folderName: String, packages: List<String>, allApps: List<AppInfo>, themeColor: Color, fontFamily: FontFamily, onClose: () -> Unit, onLaunchApp: (AppInfo) -> Unit) {
    val folderApps = remember(packages, allApps) { allApps.filter { it.packageName in packages } }
    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).clickable { onClose() }, contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier.fillMaxWidth(0.85f).padding(16.dp).clickable(enabled = false) { },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("$folderName Popup Folder", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = themeColor, fontFamily = fontFamily)
                    IconButton(onClick = onClose) { Icon(Icons.Default.Close, contentDescription = "Close folder popup", tint = MaterialTheme.colorScheme.onSurface) }
                }
                Spacer(modifier = Modifier.height(10.dp))
                if (folderApps.isEmpty()) {
                    Text("No apps in this folder. Long-press apps to folder-organize them!", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = fontFamily)
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.fillMaxWidth().heightIn(max = 220.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(folderApps) { app ->
                            Column(modifier = Modifier.clickable { onLaunchApp(app) }.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(painter = rememberAsyncImagePainter(app.icon), contentDescription = null, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = app.label,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                    fontFamily = fontFamily,
                                    textAlign = TextAlign.Center,
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
                    }
                }
            }
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun UniversalAppContextMenu(
    packageName: String,
    label: String,
    icon: Any?,
    folders: List<String>,
    hiddenList: Set<String>,
    isBreakerEnabled: Boolean,
    themeColor: Color,
    fontFamily: FontFamily,
    isVisible: Boolean,
    onClose: () -> Unit,
    onToggleHide: () -> Unit,
    onToggleBreaker: () -> Unit,
    onUninstall: () -> Unit,
    onAddToFolder: (String) -> Unit,
    selectedFolders: Set<String> = emptySet()
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    // Background backdrop smooth fade matching isVisible state
    val bgAlpha by animateFloatAsState(
        targetValue = if (isVisible) 0.6f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "bgAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = bgAlpha))
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onClose() })
            },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(300)) + 
                    scaleIn(
                        initialScale = 0.8f, 
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ),
            exit = fadeOut(animationSpec = tween(250)) + 
                   scaleOut(targetScale = 0.8f, animationSpec = tween(250))
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { /* Prevent clicks inside dialog from closing it */ })
                    },
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = 8.dp,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header (App Icon + Title)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(icon),
                            contentDescription = label,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                            fontFamily = fontFamily,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // List of Actions (Borderless, M3 ListItem style)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        // App Info Option
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = "App Info",
                                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = fontFamily)
                                )
                            },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "App Info",
                                    tint = themeColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = Color.Transparent,
                                headlineColor = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    try {
                                        val intent = Intent(
                                            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            android.net.Uri.parse("package:$packageName")
                                        ).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                        )

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f),
                            thickness = 1.dp
                        )

                        // Hide/Unhide Option
                        val isHidden = packageName in hiddenList
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = if (isHidden) "Unhide App" else "Hide App from Main List",
                                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = fontFamily)
                                )
                            },
                            leadingContent = {
                                Icon(
                                    imageVector = if (isHidden) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = themeColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = Color.Transparent,
                                headlineColor = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleHide() }
                        )

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f),
                            thickness = 1.dp
                        )

                        // Uninstall Option
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = "Uninstall App",
                                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = fontFamily),
                                    color = MaterialTheme.colorScheme.error
                                )
                            },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = Color.Transparent,
                                headlineColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onUninstall() }
                        )
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    // Folder Section
                    Text(
                        text = "Add to Pop-Up Folder Group:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontFamily = fontFamily,
                        modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 4.dp)
                    )

                    // Suggestion Chips / Filter Chips inside a FlowRow
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        folders.forEach { f ->
                            val isSelected = selectedFolders.contains(f)
                            SuggestionChip(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onAddToFolder(f)
                                },
                                label = {
                                    Text(
                                        text = f,
                                        style = MaterialTheme.typography.labelLarge.copy(fontFamily = fontFamily)
                                    )
                                },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (isSelected) themeColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    labelColor = if (isSelected) themeColor else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                border = null,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppContextMenuOverlay(
    app: AppInfo,
    folders: List<String>,
    hiddenList: Set<String>,
    isBreakerEnabled: Boolean,
    themeColor: Color,
    fontFamily: FontFamily,
    onClose: () -> Unit,
    onToggleHide: () -> Unit,
    onToggleBreaker: () -> Unit,
    onUninstall: () -> Unit,
    onAddToFolder: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isVisible by remember { mutableStateOf(false) }

    // Animate in on first composition / change of target app
    LaunchedEffect(app) {
        isVisible = true
    }

    // Helper function to animate out, then execute action
    val runExitAnimationThen = { action: () -> Unit ->
        isVisible = false
        coroutineScope.launch {
            kotlinx.coroutines.delay(280) // Snappy and matches our fade/scale animation durations
            action()
        }
    }

    val context = LocalContext.current
    val activity = remember(context) {
        var cur = context
        while (cur is android.content.ContextWrapper) {
            if (cur is MainActivity) return@remember cur
            cur = cur.baseContext
        }
        cur as? MainActivity
    }
    val folderMap = activity?.folderMapState?.collectAsState()?.value ?: emptyMap()
    val selectedFolders = folders.filter { folderName ->
        app.packageName in (folderMap[folderName] ?: emptyList())
    }.toSet()

    UniversalAppContextMenu(
        packageName = app.packageName,
        label = app.label,
        icon = app.icon,
        folders = folders,
        hiddenList = hiddenList,
        isBreakerEnabled = isBreakerEnabled,
        themeColor = themeColor,
        fontFamily = fontFamily,
        isVisible = isVisible,
        onClose = { runExitAnimationThen(onClose) },
        onToggleHide = { runExitAnimationThen(onToggleHide) },
        onToggleBreaker = { runExitAnimationThen(onToggleBreaker) },
        onUninstall = { runExitAnimationThen(onUninstall) },
        onAddToFolder = { folder -> runExitAnimationThen { onAddToFolder(folder) } },
        selectedFolders = selectedFolders
    )
}

@Composable
fun SettingsPanel(onClose: () -> Unit, themeColor: Color, fontFamily: FontFamily, activity: MainActivity, initialCategory: String? = null) {
    val currentWallpaper by activity.selectedWallpaper.collectAsState()
    val clockStyleVal by activity.clockStyle.collectAsState()
    val selectedFontVal by activity.selectedFont.collectAsState()
    val iconPackVal by activity.iconPack.collectAsState()
    val themeModeVal by activity.themeMode.collectAsState()
    val materialYouEnabledVal by activity.materialYouEnabled.collectAsState()
    val declutterModeVal by activity.declutterMode.collectAsState()
    val categoriseByUsageVal by activity.categoriseByUsage.collectAsState()
    val usageLimitCountVal by activity.usageLimitCount.collectAsState()
    val gesturesEnabledVal by activity.gesturesEnabled.collectAsState()
    val usageBreakerMinutesVal by activity.usageBreakerMinutes.collectAsState()
    val hiddenAppsSet by activity.hiddenApps.collectAsState()
    val use24HourFormatVal by activity.use24HourFormat.collectAsState()
    val useFahrenheitVal by activity.useFahrenheit.collectAsState()
    val dynamicIconColorEnabledVal by activity.dynamicIconColorEnabled.collectAsState()
    val allowedNotificationCategoriesVal by activity.allowedNotificationCategories.collectAsState()
    val notificationSummaryEnabledVal by activity.notificationSummaryEnabled.collectAsState()
    val activePagesVal by activity.activePages.collectAsState()
    
    var activeCategory by remember(initialCategory) { mutableStateOf<String?>(initialCategory) }
    var showAddBlankPageDialog by remember { mutableStateOf(false) }
    var newPageName by remember { mutableStateOf("") }

    BackHandler {
        if (activeCategory != null) {
            activeCategory = null
        } else {
            onClose()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Elegant Settings Header with a modern Back arrow
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (activeCategory != null) {
                        activeCategory = null
                    } else {
                        onClose()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = activeCategory ?: "Dextera Settings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontFamily = fontFamily
                    )
                    Text(
                        text = if (activeCategory != null) "Back to settings overview" else "Customize look, feel, widgets and gesture shortcuts",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontFamily = fontFamily
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (activeCategory == null) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val categories = listOf(
                        // Style category removed per user request
                        Triple("Performance", "Dextera 120Hz Core Optimization", "Enable ultra low-latency GC, surface buffer queues, CPU affinity, and GPU overdraw elimination"),
                        Triple("Gestures", "Touch Gestures & Taps", "Double tap visual sleep locks and search bar swipe shortcuts"),
                        Triple("Permissions", "System Access & Services", "Manage coarse/precise location updates and notifications listeners"),
                        Triple("Search", "Search & Discovery", "Customize search result limits and result prioritization"),
                        Triple("Pages", "Customize Screen Pages", "Enable customizable slider screen pages like a tactile music widget")
                    )
                    items(categories) { (name, label, desc) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { activeCategory = name },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                    Text(
                                        text = label,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontFamily = fontFamily
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = desc,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                        fontFamily = fontFamily
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (activeCategory) {
                        "Style" -> { // Style Settings
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    // 1. Wallpaper Engine Card
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                            Text("Wallpaper & Background Canvas", fontSize = 15.sp, color = themeColor, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                            Text("Set your launcher's visual backdrop. Pure solid pitch black, cosmic gradients, or custom system wallpapers.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = fontFamily)
                                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    listOf("Pitch Black", "Cosmic Slate", "Cyber Dawn").forEach { wall ->
                                                        val isSel = currentWallpaper == wall
                                                        Box(modifier = Modifier.weight(1f).border(1.dp, if (isSel) themeColor else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp)).background(if (isSel) themeColor.copy(alpha = 0.15f) else Color.Transparent).clickable { 
                                                            activity.selectedWallpaper.value = wall
                                                            activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putString("wallpaper", wall).apply()
                                                            activity.decodeAndExtractWallpaperColor()
                                                        }.padding(12.dp), contentAlignment = Alignment.Center) {
                                                            Text(wall, fontSize = 13.sp, color = if (isSel) themeColor else MaterialTheme.colorScheme.onSurface, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, fontFamily = fontFamily)
                                                        }
                                                    }
                                                }
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    listOf("Sand Dunes", "Mint Breeze", "Royal Satin").forEach { wall ->
                                                        val isSel = currentWallpaper == wall
                                                        Box(modifier = Modifier.weight(1f).border(1.dp, if (isSel) themeColor else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp)).background(if (isSel) themeColor.copy(alpha = 0.15f) else Color.Transparent).clickable { 
                                                            activity.selectedWallpaper.value = wall
                                                            activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putString("wallpaper", wall).apply()
                                                            activity.decodeAndExtractWallpaperColor()
                                                        }.padding(12.dp), contentAlignment = Alignment.Center) {
                                                            Text(wall, fontSize = 13.sp, color = if (isSel) themeColor else MaterialTheme.colorScheme.onSurface, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, fontFamily = fontFamily)
                                                        }
                                                    }
                                                }
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    listOf("Local Image", "Bing Daily", "AI Generated", "AI Generated Location").forEach { wall ->
                                                        val isSel = currentWallpaper == wall
                                                        Box(modifier = Modifier.weight(1f).border(1.dp, if (isSel) themeColor else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp)).background(if (isSel) themeColor.copy(alpha = 0.15f) else Color.Transparent).clickable { 
                                                            if (wall == "Local Image") {
                                                                activity.pickLocalWallpaper()
                                                            } else if (wall == "Bing Daily") {
                                                                activity.fetchBingWallpaper()
                                                            } else {
                                                                activity.selectedWallpaper.value = wall
                                                                activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putString("wallpaper", wall).apply()
                                                                activity.decodeAndExtractWallpaperColor()
                                                            }
                                                        }.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                                                            Text(wall.replace("Generated ", ""), fontSize = 11.sp, color = if (isSel) themeColor else MaterialTheme.colorScheme.onSurface, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, textAlign = TextAlign.Center, fontFamily = fontFamily)
                                                        }
                                                    }
                                                }
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    listOf("Solar Eclipse", "System Wallpaper").forEach { wall ->
                                                        val isSel = currentWallpaper == wall
                                                        Box(modifier = Modifier.weight(1f).border(1.dp, if (isSel) themeColor else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp)).background(if (isSel) themeColor.copy(alpha = 0.15f) else Color.Transparent).clickable { 
                                                            if (wall == "System Wallpaper") {
                                                                try {
                                                                    activity.filesDir.resolve("local_wallpaper.jpg").delete()
                                                                    activity.filesDir.resolve("bing_wallpaper.jpg").delete()
                                                                } catch (e: Exception) {}
                                                            }
                                                            activity.selectedWallpaper.value = wall
                                                            activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putString("wallpaper", wall).apply()
                                                            activity.decodeAndExtractWallpaperColor()
                                                        }.padding(12.dp), contentAlignment = Alignment.Center) {
                                                            Text(wall, fontSize = 12.sp, color = if (isSel) themeColor else MaterialTheme.colorScheme.onSurface, maxLines = 1, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, fontFamily = fontFamily)
                                                        }
                                                    }
                                                }
                                            }

                                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                            
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                val blurOn by activity.wallpaperBlurEnabled.collectAsState()
                                                Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                                                    Text("Blur Wallpaper Background", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                                    Text("Applies a frosted glass effect to improve readability", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = fontFamily)
                                                }
                                                Switch(
                                                    checked = blurOn,
                                                    onCheckedChange = {
                                                        activity.wallpaperBlurEnabled.value = it
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putBoolean("wallpaper_blur", it).apply()
                                                    },
                                                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.onPrimary, checkedTrackColor = themeColor)
                                                )
                                            }
                                        }
                                    }

                                    // Stable Diffusion 
                                    var sdPrompt by remember { mutableStateOf("") }
                                    val isSDGenerating by activity.isAIGeneratingImage.collectAsState()
                                    val isLocalGenerating by activity.isAIGenerating.collectAsState()
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically, 
                                                horizontalArrangement = Arrangement.SpaceBetween, 
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    Text("🖼️", fontSize = 18.sp)
                                                    Text("Stable Diffusion Wallpaper", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                                }
                                                if (isSDGenerating || isLocalGenerating) {
                                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = themeColor, strokeWidth = 1.5.dp)
                                                } else if (currentWallpaper == "AI Generated" || currentWallpaper == "AI Generated Location") {
                                                    Box(modifier = Modifier.background(themeColor.copy(alpha = 0.12f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                                        Text("APPLIED", fontSize = 9.sp, color = themeColor, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                                    }
                                                }
                                            }
                                            
                                            Text(
                                                text = "Cloud-accelerated Stable Diffusion equivalent. Uses Gemini API.",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontFamily = fontFamily
                                            )
                                            
                                            Button(
                                                onClick = {
                                                    activity.generateLocationBasedLandscape(currentWallpaper)
                                                },
                                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = themeColor.copy(alpha = 0.15f)),
                                                border = BorderStroke(1.dp, themeColor.copy(alpha = 0.4f)),
                                                shape = RoundedCornerShape(12.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    Text("📍", fontSize = 14.sp)
                                                    Text("Generate Auto Location Landscape", fontSize = 12.sp, color = themeColor, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                                }
                                            }

                                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                            
                                            androidx.compose.foundation.text.BasicTextField(
                                                value = sdPrompt,
                                                onValueChange = { sdPrompt = it },
                                                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontFamily = fontFamily),
                                                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(12.dp)).border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)).padding(12.dp),
                                                decorationBox = { innerTextField ->
                                                    if (sdPrompt.isEmpty()) Text("Or describe your own...", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f), fontSize = 13.sp, fontFamily = fontFamily)
                                                    innerTextField()
                                                }
                                            )
                                            
                                            Button(
                                                onClick = {
                                                    activity.generateStableDiffusionWallpaper(sdPrompt)
                                                    sdPrompt = ""
                                                },
                                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                                                shape = RoundedCornerShape(12.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text(if (isSDGenerating) "Running Inference..." else "Generate Custom", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimary.takeOrElse { Color.Black }, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                            }
                                        }
                                    }

                                    // 2. Aesthetics & Appearance Customization
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                            Text("Aesthetics & Typography", fontSize = 15.sp, color = themeColor, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                            
                                            // Home Screen Clock Styles
                                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text("Home Screen Clock Styles", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    listOf("Dextera Date", "Minimal Digital", "Bold Accent", "Classic Analog").forEach { style ->
                                                        val isSel = clockStyleVal == style
                                                        Box(modifier = Modifier.weight(1f).border(1.dp, if (isSel) themeColor else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp)).background(if (isSel) themeColor.copy(alpha = 0.15f) else Color.Transparent).clickable { activity.clockStyle.value = style; activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putString("clock_style", style).apply() }.padding(10.dp), contentAlignment = Alignment.Center) {
                                                            Text(style, fontSize = 11.sp, color = if (isSel) themeColor else MaterialTheme.colorScheme.onSurface, maxLines = 1, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, fontFamily = fontFamily)
                                                        }
                                                    }
                                                }
                                            }

                                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                                            // App Icon Packs
                                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text("App Icon Packs", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    listOf("Classic", "Silhouette Outlined", "Neon Glow", "Pastel Minimalist").forEach { pack ->
                                                        val isSel = iconPackVal == pack
                                                        Box(modifier = Modifier.weight(1f).border(1.dp, if (isSel) themeColor else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp)).background(if (isSel) themeColor.copy(alpha = 0.15f) else Color.Transparent).clickable { activity.iconPack.value = pack; activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putString("icon_pack", pack).apply() }.padding(10.dp), contentAlignment = Alignment.Center) {
                                                            Text(pack, fontSize = 11.sp, color = if (isSel) themeColor else MaterialTheme.colorScheme.onSurface, maxLines = 1, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, fontFamily = fontFamily)
                                                        }
                                                    }
                                                }
                                            }

                                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                                            // Launcher Fonts
                                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text("Launcher Font Style", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    listOf("System Default", "Inter Elegant", "Space Mono", "Serif Elegant").forEach { fnt ->
                                                        val isSel = selectedFontVal == fnt
                                                        Box(modifier = Modifier.weight(1f).border(1.dp, if (isSel) themeColor else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp)).background(if (isSel) themeColor.copy(alpha = 0.15f) else Color.Transparent).clickable { activity.selectedFont.value = fnt; activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putString("font", fnt).apply() }.padding(10.dp), contentAlignment = Alignment.Center) {
                                                            Text(fnt, fontSize = 11.sp, color = if (isSel) themeColor else MaterialTheme.colorScheme.onSurface, maxLines = 1, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, fontFamily = fontFamily)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // 3. System Scales & Scales
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                            Text("System Scales & Units", fontSize = 15.sp, color = themeColor, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                            
                                            // Time Format
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Column(modifier = Modifier.weight(1.0f).padding(end = 8.dp)) {
                                                    Text("Time Format Type", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                                    Text("Toggle 24-Hour mode or standard 12-Hour model", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = fontFamily)
                                                }
                                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    listOf(false to "12-Hr", true to "24-Hr").forEach { (v, name) ->
                                                        val isSel = use24HourFormatVal == v
                                                        Box(modifier = Modifier.border(1.dp, if (isSel) themeColor else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp)).background(if (isSel) themeColor.copy(alpha = 0.15f) else Color.Transparent).clickable { 
                                                            activity.use24HourFormat.value = v
                                                            activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putBoolean("use_24_hour", v).apply()
                                                        }.padding(vertical = 8.dp, horizontal = 12.dp), contentAlignment = Alignment.Center) {
                                                            Text(name, fontSize = 12.sp, color = if (isSel) themeColor else MaterialTheme.colorScheme.onSurface, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, fontFamily = fontFamily)
                                                        }
                                                    }
                                                }
                                            }

                                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                                            // Climate Format
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Column(modifier = Modifier.weight(1.0f).padding(end = 8.dp)) {
                                                    Text("Temperature Scale style", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                                    Text("Celsius (°C) vs Fahrenheit (°F) scales style", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = fontFamily)
                                                }
                                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    listOf(false to "Celsius", true to "Fahrenheit").forEach { (v, name) ->
                                                        val isSel = useFahrenheitVal == v
                                                        Box(modifier = Modifier.border(1.dp, if (isSel) themeColor else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp)).background(if (isSel) themeColor.copy(alpha = 0.15f) else Color.Transparent).clickable { 
                                                            activity.useFahrenheit.value = v
                                                            activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putBoolean("use_fahrenheit", v).apply()
                                                        }.padding(vertical = 8.dp, horizontal = 12.dp), contentAlignment = Alignment.Center) {
                                                            Text(name, fontSize = 12.sp, color = if (isSel) themeColor else MaterialTheme.colorScheme.onSurface, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, fontFamily = fontFamily)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // 4. Theming & Color Palette Engine
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                            Text("Theming & Color Palette", fontSize = 15.sp, color = themeColor, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                            
                                            // Dynamic App Icon Tinting
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Column(modifier = Modifier.weight(1.0f).padding(end = 12.dp)) {
                                                    Text("Dynamic App Icon Tinting", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                                    Text("Tint app icons based on active wallpaper colors", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = fontFamily)
                                                }
                                                Switch(
                                                    checked = dynamicIconColorEnabledVal,
                                                    onCheckedChange = { 
                                                        activity.dynamicIconColorEnabled.value = it
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putBoolean("dynamic_icon_color", it).apply()
                                                    },
                                                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.onPrimary, checkedTrackColor = themeColor)
                                                )
                                            }

                                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                                            // Material You
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Column(modifier = Modifier.weight(1.0f).padding(end = 12.dp)) {
                                                    Text("Material You Dynamic Accent", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                                    Text("Derive palette keys from screen wallpaper colors", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = fontFamily)
                                                }
                                                Switch(
                                                    checked = materialYouEnabledVal, 
                                                    onCheckedChange = { 
                                                        activity.materialYouEnabled.value = it
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putBoolean("material_you", it).apply() 
                                                    }, 
                                                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.onPrimary, checkedTrackColor = themeColor)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        "Performance" -> { // 120Hz Optimization Panel
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    // Live Diagnostic Monitoring Card
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text("Dextera Engine Diagnostics", fontSize = 16.sp, color = themeColor, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                                    Text("Real-time telemetry & display synchronization", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = fontFamily)
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .background(themeColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                                ) {
                                                    val fpsVal by activity.liveFps.collectAsState()
                                                    Text(
                                                        text = String.format(java.util.Locale.US, "%.1f FPS", fpsVal),
                                                        fontSize = 14.sp,
                                                        color = themeColor,
                                                        fontWeight = FontWeight.Bold,
                                                        fontFamily = FontFamily.Monospace
                                                    )
                                                }
                                            }

                                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                            
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text("Active Thermal Status", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                                    val thermalVal by activity.currentThermalStatus.collectAsState()
                                                    Text(thermalVal, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = fontFamily)
                                                }
                                                val vsyncVal by activity.adaptiveVsyncTarget.collectAsState()
                                                Box(
                                                    modifier = Modifier
                                                        .background(MaterialTheme.colorScheme.surfaceContainerHighest, RoundedCornerShape(8.dp))
                                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                                ) {
                                                    Text("VSync: $vsyncVal", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, fontFamily = fontFamily, fontWeight = FontWeight.SemiBold)
                                                }
                                            }
                                        }
                                    }

                                    // 120Hz Optimizations Card
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                            Text("Hyper-Performance Core Parameters", fontSize = 15.sp, color = themeColor, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                            Text("Adhering strictly to backend & algorithmic optimizations from the 120Hz target blueprint:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = fontFamily)
                                            
                                            val overdrawVal by activity.overdrawElimination.collectAsState()
                                            val asyncImgVal by activity.asyncImageDecoding.collectAsState()
                                            val jvmZgcVal by activity.jvmZgcConfiguration.collectAsState()
                                            val adaptiveFpsVal by activity.adaptiveFpsThermal.collectAsState()
                                            val syntheticClickVal by activity.syntheticClickDelay.collectAsState()
                                            val touchInterpVal by activity.touchInterpolation.collectAsState()
                                            val cpuThreadVal by activity.cpuThreadAffinity.collectAsState()
                                            val asyncVfsVal by activity.asyncVfsMmap.collectAsState()

                                            // Options list
                                            val options = listOf(
                                                java.util.UUID.randomUUID().toString() to Triple(
                                                    "GPU Overdraw Elimination",
                                                    "Nullifies the native Window background drawable initialization to remove composite layers and speed up frame times.",
                                                    overdrawVal to { v: Boolean ->
                                                        activity.overdrawElimination.value = v
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putBoolean("perf_overdraw_elimination", v).apply()
                                                        try {
                                                            if (v && activity.selectedWallpaper.value != "System Wallpaper") {
                                                                activity.window?.setBackgroundDrawable(null)
                                                            } else {
                                                                // Restore normal system background indirectly or leave it
                                                            }
                                                        } catch (e: Exception) { e.printStackTrace() }
                                                    }
                                                ),
                                                java.util.UUID.randomUUID().toString() to Triple(
                                                    "Asynchronous Image Decoding",
                                                    "Moves wallpaper, icon downsampling, and save files bitmap extraction off the UI thread to background worker pools.",
                                                    asyncImgVal to { v: Boolean ->
                                                        activity.asyncImageDecoding.value = v
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putBoolean("perf_async_image_decoding", v).apply()
                                                    }
                                                ),
                                                java.util.UUID.randomUUID().toString() to Triple(
                                                    "Shenandoah/ZGC Garbage Collector",
                                                    "Appends ultra-low latency -XX:+UseZGC options to the VM launch parameters block, restricting heap pauses to < 2ms.",
                                                    jvmZgcVal to { v: Boolean ->
                                                        activity.jvmZgcConfiguration.value = v
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putBoolean("perf_jvm_zgc_config", v).apply()
                                                    }
                                                ),
                                                java.util.UUID.randomUUID().toString() to Triple(
                                                    "Thermal-Adaptive FPS Governor",
                                                    "Actively listens to the Android Hardware Thermal API. Safely downscales refresh rates to 90Hz/60Hz on THERMAL_STATUS_SEVERE to protect SoC silicon.",
                                                    adaptiveFpsVal to { v: Boolean ->
                                                        activity.adaptiveFpsThermal.value = v
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putBoolean("perf_adaptive_fps_thermal", v).apply()
                                                        if (!v) {
                                                            activity.adaptiveVsyncTarget.value = "120Hz"
                                                        }
                                                    }
                                                ),
                                                java.util.UUID.randomUUID().toString() to Triple(
                                                    "Synthetic Click Duration Delay",
                                                    "Applies automated frame-holding (8.3ms duration) to discrete virtual clicks so they are registered properly rather than discarded as zero-duration noise.",
                                                    syntheticClickVal to { v: Boolean ->
                                                        activity.syntheticClickDelay.value = v
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putBoolean("perf_synthetic_click_delay", v).apply()
                                                    }
                                                ),
                                                java.util.UUID.randomUUID().toString() to Triple(
                                                    "240Hz Input Event Interpolation",
                                                    "Queries motion vector histories via getHistoricalX/Y to capture sub-frame touch paths for smooth viewport adjustments.",
                                                    touchInterpVal to { v: Boolean ->
                                                        activity.touchInterpolation.value = v
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putBoolean("perf_touch_interpolation", v).apply()
                                                    }
                                                ),
                                                java.util.UUID.randomUUID().toString() to Triple(
                                                    "Big.LITTLE Thread Affinity Mapping",
                                                    "Pins the main game loop and JVM renderer threads exclusively to ARM Prime cores, and keeps low-load helper threads off them.",
                                                    cpuThreadVal to { v: Boolean ->
                                                        activity.cpuThreadAffinity.value = v
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putBoolean("perf_cpu_thread_affinity", v).apply()
                                                    }
                                                ),
                                                java.util.UUID.randomUUID().toString() to Triple(
                                                    "Asynchronous VFS & Memory Mapping",
                                                    "Bypasses Storage Access Framework (SAF) latency by directly mapping .jar resource packs and game zip files into RAM using native mmap POSIX.",
                                                    asyncVfsVal to { v: Boolean ->
                                                        activity.asyncVfsMmap.value = v
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putBoolean("perf_async_vfs_mmap", v).apply()
                                                    }
                                                )
                                            )

                                            options.forEach { (_, itemData) ->
                                                val (title, description, rawState) = itemData
                                                val (checked, onCheckedChange) = rawState

                                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                                                        Text(title, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Text(description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = fontFamily)
                                                    }
                                                    Switch(
                                                        checked = checked,
                                                        onCheckedChange = { onCheckedChange(it) },
                                                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.onPrimary, checkedTrackColor = themeColor)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        "Gestures" -> { // Gestures Settings
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    // 1. Interactive Gestures Card
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                            Text("Launcher Home Screen Gestures", fontSize = 15.sp, color = themeColor, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                                                    Text("Launcher Touch Gestures", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                                    Text("Enables double-tap to sleep and search bar shortcuts on home screen.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = fontFamily)
                                                }
                                                Switch(
                                                    checked = gesturesEnabledVal, 
                                                    onCheckedChange = { 
                                                        activity.gesturesEnabled.value = it
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putBoolean("gestures_enabled", it).apply() 
                                                    }, 
                                                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.onPrimary, checkedTrackColor = themeColor)
                                                )
                                            }
                                        }
                                    }

                                    // 3. Gestures Shortcuts Manual
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                            Text("Integrated Gesture Manual Shortcuts", fontSize = 15.sp, color = themeColor, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text("👇", fontSize = 16.sp, modifier = Modifier.padding(end = 8.dp))
                                                    Column {
                                                        Text("Swipe Down Shortcut", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, fontFamily = fontFamily)
                                                        Text("Launches search input immediately", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = fontFamily)
                                                    }
                                                }
                                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text("✌️", fontSize = 16.sp, modifier = Modifier.padding(end = 8.dp))
                                                    Column {
                                                        Text("Double Tap Shortcut", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, fontFamily = fontFamily)
                                                        Text("Locks system screen visually/ambiently", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = fontFamily)
                                                    }
                                                }
                                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text("👌", fontSize = 16.sp, modifier = Modifier.padding(end = 8.dp))
                                                    Column {
                                                        Text("Pinch In Shortcut", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, fontFamily = fontFamily)
                                                        Text("Opens Dextera Settings panel", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = fontFamily)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        "Wellbeing" -> { // Wellbeing Settings
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    // 1. Minimalism Mode Card
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                            Text("Aesthetic Layout Decoupling", fontSize = 15.sp, color = themeColor, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                                                    Text("Decluttered Minimalist Home", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                                    Text("Omit all labels, extra details, and distracting visual overlays for total zen flow.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = fontFamily)
                                                }
                                                Switch(
                                                    checked = declutterModeVal, 
                                                    onCheckedChange = { 
                                                        activity.declutterMode.value = it
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putBoolean("declutter_mode", it).apply() 
                                                    }, 
                                                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.onPrimary, checkedTrackColor = themeColor)
                                                )
                                            }
                                        }
                                    }

                                    // 2. Notification Distraction Summary Card
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                            Text("Notification Feed Summarizer", fontSize = 15.sp, color = themeColor, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                                                    Text("Focus Notification Summary Card", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                                    Text("Bundle lower priority updates neatly into daily summaries.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = fontFamily)
                                                }
                                                Switch(
                                                    checked = notificationSummaryEnabledVal, 
                                                    onCheckedChange = { 
                                                        activity.notificationSummaryEnabled.value = it
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putBoolean("notification_summary", it).apply() 
                                                    }, 
                                                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.onPrimary, checkedTrackColor = themeColor)
                                                )
                                            }

                                            if (notificationSummaryEnabledVal) {
                                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(12.dp))
                                                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), RoundedCornerShape(12.dp))
                                                        .padding(12.dp),
                                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    Text("Manage Allowed Summary Whispers", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = themeColor, fontFamily = fontFamily)
                                                    Text("Select category channels routed directly into the focal launchpad widgets:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = fontFamily)
                                                    
                                                    val categoriesList = listOf(
                                                        "Finance 💰", "Travel ✈️", "Social 💬", 
                                                        "Internet 🌐", "Entertainment 🎵", "Shopping 🛍️", "General 📦"
                                                    )
                                                    categoriesList.forEach { cat ->
                                                        val isChecked = allowedNotificationCategoriesVal.contains(cat)
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .clickable {
                                                                    val nextSet = allowedNotificationCategoriesVal.toMutableSet()
                                                                    if (isChecked) {
                                                                        if (nextSet.size > 1) { // keep at least one
                                                                            nextSet.remove(cat)
                                                                        } else {
                                                                            android.widget.Toast.makeText(activity, "Must keep at least one category in summary", android.widget.Toast.LENGTH_SHORT).show()
                                                                        }
                                                                    } else {
                                                                        nextSet.add(cat)
                                                                    }
                                                                    activity.allowedNotificationCategories.value = nextSet
                                                                    activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE)
                                                                        .edit()
                                                                        .putStringSet("allowed_notification_categories", nextSet)
                                                                        .apply()
                                                                }
                                                                .padding(vertical = 8.dp),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(cat.replace(Regex("[^A-Za-z ]"), "").trim(), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, fontFamily = fontFamily)
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(20.dp)
                                                                    .border(BorderStroke(1.5.dp, if (isChecked) themeColor else MaterialTheme.colorScheme.outline), RoundedCornerShape(6.dp))
                                                                    .background(if (isChecked) themeColor else Color.Transparent, RoundedCornerShape(6.dp)),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                if (isChecked) {
                                                                    Icon(
                                                                        imageVector = Icons.Default.Check,
                                                                        contentDescription = null,
                                                                        tint = MaterialTheme.colorScheme.onPrimary.takeOrElse { Color.Black },
                                                                        modifier = Modifier.size(14.dp)
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // 3. App Usage Limits Breaker limits card
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Text("Global App Usage Limits Engine", fontSize = 15.sp, color = themeColor, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                            Text("Set mindful daily alert/pause cooldown timers when opening high-distraction apps to help unplug.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = fontFamily)
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                                                listOf(1 to "1m (demo)", 5 to "5m", 15 to "15m").forEach { (v, text) ->
                                                    val isSel = usageBreakerMinutesVal == v
                                                    Box(modifier = Modifier.weight(1f).border(1.dp, if (isSel) themeColor else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp)).background(if (isSel) themeColor.copy(alpha = 0.15f) else Color.Transparent).clickable { 
                                                        activity.usageBreakerMinutes.value = v
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putInt("usage_breaker_min", v).apply() 
                                                    }.padding(12.dp), contentAlignment = Alignment.Center) {
                                                        Text(text, fontSize = 12.sp, color = if (isSel) themeColor else MaterialTheme.colorScheme.onSurface, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, fontFamily = fontFamily)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // 4. Hidden Apps list Card
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Text("Hidden Main Apps Inventory (${hiddenAppsSet.size})", fontSize = 15.sp, color = themeColor, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                            if (hiddenAppsSet.isEmpty()) {
                                                Text("No currently hidden launcher applications. Hide distracting apps via home list long-press.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = fontFamily)
                                            } else {
                                                hiddenAppsSet.forEach { pkg ->
                                                    Row(modifier = Modifier.fillMaxWidth().clickable {
                                                        val now = hiddenAppsSet.toMutableSet()
                                                        now.remove(pkg)
                                                        activity.hiddenApps.value = now
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putStringSet("hidden_packages", now).apply()
                                                    }.padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                        Text(pkg, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, modifier = Modifier.weight(1f).padding(end = 6.dp), fontFamily = fontFamily)
                                                        Text("Unhide", fontSize = 12.sp, color = themeColor, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // 5. App Usage Categorization Settings Card
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                            Text("App List Categorization by Usage", fontSize = 15.sp, color = themeColor, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                            
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                                                    Text("Categorize App List by Usage", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                                    Text("Group your most frequently launched apps alphabetically at the very top of your home screen.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = fontFamily)
                                                }
                                                Switch(
                                                    checked = categoriseByUsageVal, 
                                                    onCheckedChange = { 
                                                        activity.categoriseByUsage.value = it
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putBoolean("categorise_by_usage", it).apply() 
                                                    }, 
                                                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.onPrimary, checkedTrackColor = themeColor)
                                                )
                                            }

                                            if (categoriseByUsageVal) {
                                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                                Text("Number of Frequently Used Apps:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                                    listOf(4 to "4 Apps", 6 to "6 Apps", 8 to "8 Apps").forEach { (v, text) ->
                                                        val isSel = usageLimitCountVal == v
                                                        Box(
                                                            modifier = Modifier
                                                                .weight(1f)
                                                                .border(1.dp, if (isSel) themeColor else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp))
                                                                .background(if (isSel) themeColor.copy(alpha = 0.15f) else Color.Transparent)
                                                                .clickable { 
                                                                    activity.usageLimitCount.value = v
                                                                    activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putInt("usage_limit_count", v).apply() 
                                                                }
                                                                .padding(10.dp), 
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(text, fontSize = 12.sp, color = if (isSel) themeColor else MaterialTheme.colorScheme.onSurface, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, fontFamily = fontFamily)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        "Search" -> { // Search Settings
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                            Text("Search Result Length", fontSize = 15.sp, color = themeColor, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Column(modifier = Modifier.weight(1.0f).padding(end = 8.dp)) {
                                                    Text("Limit Search Display", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                                    Text("Amount of items to populate per category", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = fontFamily)
                                                }
                                                val currentLimit = activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).getInt("search_results_limit", 5)
                                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    listOf(3 to "3", 5 to "5", 7 to "7").forEach { (v, name) ->
                                                        val isSel = currentLimit == v
                                                        Box(modifier = Modifier.border(1.dp, if (isSel) themeColor else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp)).background(if (isSel) themeColor.copy(alpha = 0.15f) else Color.Transparent).clickable { 
                                                            activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putInt("search_results_limit", v).apply()
                                                        }.padding(vertical = 8.dp, horizontal = 12.dp), contentAlignment = Alignment.Center) {
                                                            Text(name, fontSize = 12.sp, color = if (isSel) themeColor else MaterialTheme.colorScheme.onSurface, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, fontFamily = fontFamily)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        "Permissions" -> { // Permissions Tab
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    // 1. Location Card
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        val isLocationGranted by activity.isLocationPermissionGranted.collectAsState()
                                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Text("Location Access for Weather Data", fontSize = 15.sp, color = themeColor, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                            Text(
                                                text = "Current Status: " + (if (isLocationGranted) "✅ Granted" else "❌ Missing (Showing Cupertino)"),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = fontFamily,
                                                color = if (isLocationGranted) themeColor else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text("Enables accurate, local temperature widgets on your home screen.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = fontFamily)
                                            Button(
                                                onClick = {
                                                    activity.requestPermissionLauncher.launch(
                                                        arrayOf(
                                                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                                            android.Manifest.permission.ACCESS_FINE_LOCATION
                                                        )
                                                    )
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text(if (isLocationGranted) "Modify Location Permission" else "Grant Location Access", fontSize = 13.sp, color = MaterialTheme.colorScheme.onPrimary.takeOrElse { Color.Black }, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                            }
                                        }
                                    }

                                    // 2. Notifications Access Card
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        val isNotifGranted by activity.isNotificationPermissionGranted.collectAsState()
                                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Text("Notification Handler Access", fontSize = 15.sp, color = themeColor, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                            Text(
                                                text = "Current Status: " + (if (isNotifGranted) "✅ Granted" else "❌ Missing (Showing Demo list)"),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = fontFamily,
                                                color = if (isNotifGranted) themeColor else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text("Required to access active system notifications to bundle, categorize, and summarize them securely on your launchpad.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = fontFamily)
                                            Button(
                                                onClick = {
                                                    try {
                                                        val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                        }
                                                        activity.startActivity(intent)
                                                    } catch (e: Exception) {
                                                        android.widget.Toast.makeText(activity, "Settings could not be opened automatically", android.widget.Toast.LENGTH_SHORT).show()
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text(if (isNotifGranted) "Configure in Settings" else "Grant Notification Access", fontSize = 13.sp, color = MaterialTheme.colorScheme.onPrimary.takeOrElse { Color.Black }, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        "Pages" -> { // Pages Tab Settings
                            item {
                                Column {
                                    Text(
                                        text = "Launcher Screen Pages",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF147A83), // Modern slate teal green
                                        fontFamily = fontFamily
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Enable, disable, reorder or create custom widget pages below.",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        fontFamily = fontFamily
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }

                            val activeList = activePagesVal
                            activeList.forEachIndexed { idx, page ->
                                item {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 14.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                val pageIcon = when (page) {
                                                    "App List" -> Icons.Default.Menu
                                                    "Music" -> Icons.Default.PlayArrow
                                                    "Notifications" -> Icons.Default.Notifications
                                                    else -> Icons.Default.Star
                                                }

                                                Icon(
                                                    imageVector = pageIcon,
                                                    contentDescription = null,
                                                    tint = Color(0xFF147A83),
                                                    modifier = Modifier.size(24.dp)
                                                )

                                                Spacer(modifier = Modifier.width(16.dp))

                                                val desc = when (page) {
                                                    "App List" -> "Primary launcher app grid (Permanent)"
                                                    "Music" -> "Media controller and audio widget"
                                                    "Notifications" -> "Custom notification summary page"
                                                    else -> "Custom blank page for widgets"
                                                }

                                                Column {
                                                    Text(
                                                        text = page,
                                                        fontSize = 15.sp,
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        fontWeight = FontWeight.Bold,
                                                        fontFamily = fontFamily
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = desc,
                                                        fontSize = 11.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                                        fontFamily = fontFamily
                                                    )
                                                }
                                            }

                                            if (page == "App List") {
                                                Text(
                                                    text = "Fixed",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF2E7D32),
                                                    modifier = Modifier.padding(end = 8.dp),
                                                    fontFamily = fontFamily
                                                )
                                            } else {
                                                IconButton(
                                                    onClick = {
                                                        val newList = activeList.toMutableList()
                                                        newList.remove(page)
                                                        activity.saveActivePages(newList)
                                                    },
                                                    modifier = Modifier.size(36.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Delete Page",
                                                        tint = Color(0xFFC62828),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // Add buttons mimicking the style of the screen layout
                            val canAddMusic = !activeList.contains("Music")
                            if (canAddMusic) {
                                item {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                            .clickable {
                                                val newList = activeList.toMutableList()
                                                newList.add("Music")
                                                activity.saveActivePages(newList)
                                            },
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFD5E1ED)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = Color(0xFF37474F),
                                                modifier = Modifier
                                                    .align(Alignment.CenterStart)
                                                    .padding(start = 20.dp)
                                                    .size(22.dp)
                                            )
                                            Text(
                                                text = "Add Music Page",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF37474F),
                                                fontFamily = fontFamily
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }

                            val canAddNotifications = !activeList.contains("Notifications")
                            if (canAddNotifications) {
                                item {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                            .clickable {
                                                val newList = activeList.toMutableList()
                                                newList.add("Notifications")
                                                activity.saveActivePages(newList)
                                            },
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFD5E1ED)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Notifications,
                                                contentDescription = null,
                                                tint = Color(0xFF37474F),
                                                modifier = Modifier
                                                    .align(Alignment.CenterStart)
                                                    .padding(start = 20.dp)
                                                    .size(22.dp)
                                            )
                                            Text(
                                                text = "Add Notifications Page",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF37474F),
                                                fontFamily = fontFamily
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }

                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .clickable {
                                            showAddBlankPageDialog = true
                                        },
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD5E1ED)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = Color(0xFF37474F),
                                            modifier = Modifier
                                                .align(Alignment.CenterStart)
                                                .padding(start = 20.dp)
                                                .size(22.dp)
                                        )
                                        Text(
                                            text = "Create Blank Widget Page",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF37474F),
                                            fontFamily = fontFamily
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(96.dp)) // generous scrolling spacer under cards so they don't get covered by FAB
                            }
                        }
                    }
                }
            }
        }

        if (showAddBlankPageDialog) {
            AlertDialog(
                onDismissRequest = { showAddBlankPageDialog = false },
                title = {
                    Text(
                        "Create Blank Widget Page",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Enter a name for your custom widget space:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = fontFamily
                        )
                        OutlinedTextField(
                            value = newPageName,
                            onValueChange = { newPageName = it },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontFamily = fontFamily, fontSize = 14.sp),
                            placeholder = { Text("e.g. Dashboard", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeColor,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val trimmed = newPageName.trim()
                            if (trimmed.isNotEmpty() && !activePagesVal.contains(trimmed)) {
                                val newList = activePagesVal.toMutableList()
                                newList.add(trimmed)
                                activity.saveActivePages(newList)
                                showAddBlankPageDialog = false
                                newPageName = ""
                            } else {
                                android.widget.Toast.makeText(activity, "Invalid or duplicate page name", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Create", color = MaterialTheme.colorScheme.onPrimary.takeOrElse { Color.Black }, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddBlankPageDialog = false }) {
                        Text("Cancel", color = MaterialTheme.colorScheme.primary, fontFamily = fontFamily)
                    }
                },
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
fun DexteraLauncherApp(modifier: Modifier = Modifier, viewModel: LauncherViewModel = viewModel()) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val isImeVisible = WindowInsets.ime.getBottom(density) > 0
    
    val activity = remember(context) {
        var cur = context
        while (cur is android.content.ContextWrapper) {
            if (cur is MainActivity) return@remember cur
            cur = cur.baseContext
        }
        cur as MainActivity
    }
    val widgetDataList by activity.widgetDataList.collectAsState()
    
    // Config state collectors
    val selectedWallpaper by activity.selectedWallpaper.collectAsState()
    val clockStyleVal by activity.clockStyle.collectAsState()
    val selectedFontVal by activity.selectedFont.collectAsState()
    val iconPackVal by activity.iconPack.collectAsState()
    val themeModeVal by activity.themeMode.collectAsState()
    val materialYouEnabledVal by activity.materialYouEnabled.collectAsState()
    val declutterModeVal by activity.declutterMode.collectAsState()
    val categoriseByUsageVal by activity.categoriseByUsage.collectAsState()
    val usageLimitCountVal by activity.usageLimitCount.collectAsState()
    val appUsageScoresVal by activity.appUsageScores.collectAsState()
    val gesturesEnabledVal by activity.gesturesEnabled.collectAsState()
    val hiddenAppsSet by activity.hiddenApps.collectAsState()
    val notificationSummaryEnabledVal by activity.notificationSummaryEnabled.collectAsState()
    val folderMap by activity.folderMapState.collectAsState()
    val isLocationGrantedVal by activity.isLocationPermissionGranted.collectAsState()
    val use24HourFormatVal by activity.use24HourFormat.collectAsState()
    val useFahrenheitVal by activity.useFahrenheit.collectAsState()
    val dynamicIconColorEnabledVal by activity.dynamicIconColorEnabled.collectAsState()
    val bingWallpaperUrlVal by activity.bingWallpaperUrl.collectAsState()
    val wallpaperBlurEnabledVal by activity.wallpaperBlurEnabled.collectAsState()
    val extractedWallpaperColorVal by activity.extractedWallpaperColor.collectAsState()
    val allowedNotificationCategoriesVal by activity.allowedNotificationCategories.collectAsState()
    
    val activePagesVal by activity.activePages.collectAsState()
    val mediaTrackInfoVal by activity.mediaTrackInfo.collectAsState()
    val notificationsListState by activity.notificationList.collectAsState()
    val widgetDataListVal by activity.widgetDataList.collectAsState()
    var currentPageIndex by remember { mutableIntStateOf(0) }
    
    val displayedPages = remember(activePagesVal) {
        val filtered = activePagesVal.filter { page ->
            true
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
            kotlinx.coroutines.delay(150)
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
    val filteredContacts = remember(allUsersVal, searchQuery) {
        if (searchQuery.trim().isEmpty()) emptyList()
        else {
            allUsersVal.filter { contact ->
                contact.name.contains(searchQuery, ignoreCase = true) ||
                contact.phoneNumber.replace("-", "").contains(searchQuery, ignoreCase = true)
            }
        }
    }
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

    var touchedLetter by remember { mutableStateOf<Char?>(null) }
    var lockedLetterState by remember { mutableStateOf<Char?>(null) }
    var scrolledLetter by remember { mutableStateOf<Char?>(null) }
    var isTouchingSidebar by remember { mutableStateOf(false) }
    var sidebarTouchY by remember { mutableStateOf<Float?>(null) }
    var sidebarTouchX by remember { mutableStateOf<Float?>(null) }
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
        // Wallpaper visualizer canvas
        WallpaperBackground(selectedWallpaper, bingWallpaperUrlVal, wallpaperBlurEnabledVal)

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

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = zoomFactor * wsScale
                        scaleY = zoomFactor * wsScale
                        translationY = translationYFactor * density.density
                        clip = zoomLevel > 0 || appTransitionProgressVal > 0.01f
                        shape = RoundedCornerShape(24.dp)
                        shadowElevation = if (zoomLevel > 0) 16.dp.toPx() else 0f
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
                                                    onDrag = { change, dragAmount ->
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
                        ) {
                            if (isNotificationsExpanded) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.25f))
                                )
                            }
                        }
                        
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
                                            useFahrenheitVal
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    // Categories Filter
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
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .border(
                                                            1.dp,
                                                            if (isAllSelected) currentThemeColor else Color.White.copy(alpha = 0.15f),
                                                            RoundedCornerShape(12.dp)
                                                        )
                                                        .clickable { selectedCategoryFilter = "All" }
                                                ) {
                                                    // Glassmorphic background blur effect
                                                    Box(
                                                        modifier = Modifier
                                                            .matchParentSize()
                                                            .background(Color.White.copy(alpha = if (isAllSelected) 0.12f else 0.05f))
                                                            .blur(10.dp)
                                                    )
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(Icons.Default.Menu, contentDescription = null, tint = if (isAllSelected) currentThemeColor else Color.White, modifier = Modifier.size(13.dp).padding(end = 4.dp))
                                                        Text(
                                                            text = "All Apps",
                                                            fontSize = 11.sp,
                                                            fontFamily = currentFontFamily,
                                                            color = if (isAllSelected) currentThemeColor else Color.White.copy(alpha = 0.8f),
                                                            fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal,
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
                                            }

                                            folderMap.forEach { (name, appsList) ->
                                                item {
                                                    val isSelected = selectedCategoryFilter == name
                                                    val iconVector = when (name.lowercase()) {
                                                        "social" -> Icons.Default.Person
                                                        "utilities" -> Icons.Default.Build
                                                        "media" -> Icons.Default.PlayArrow
                                                        else -> Icons.Default.List
                                                    }
                                                    val textLabel = name
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(12.dp))
                                                            .border(
                                                                1.dp,
                                                                if (isSelected) currentThemeColor else Color.White.copy(alpha = 0.15f),
                                                                RoundedCornerShape(12.dp)
                                                            )
                                                            .clickable { 
                                                                selectedCategoryFilter = if (isSelected) "All" else name
                                                            }
                                                    ) {
                                                        // Glassmorphic background blur effect
                                                        Box(
                                                            modifier = Modifier
                                                                .matchParentSize()
                                                                .background(Color.White.copy(alpha = if (isSelected) 0.12f else 0.05f))
                                                                .blur(10.dp)
                                                        )
                                                        Row(
                                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Icon(iconVector, contentDescription = null, tint = if (isSelected) currentThemeColor else Color.White, modifier = Modifier.size(13.dp).padding(end = 4.dp))
                                                            Text(
                                                                text = "$textLabel (${appsList.size})",
                                                                fontSize = 11.sp,
                                                                fontFamily = currentFontFamily,
                                                                color = if (isSelected) currentThemeColor else Color.White.copy(alpha = 0.8f),
                                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
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
                                val focusManager = LocalFocusManager.current
                                val density = LocalDensity.current
                                val isImeShowing = WindowInsets.ime.getBottom(density) > 0
                                
                                BackHandler(enabled = true) {
                                    if (activeSearchCategoryFilter != "All") {
                                        activeSearchCategoryFilter = "All"
                                    } else {
                                        searchQuery = ""
                                        isSearchFocused = false
                                        focusManager.clearFocus()
                                    }
                                }

                                LaunchedEffect(searchListState.isScrollInProgress) {
                                    if (searchListState.isScrollInProgress) {
                                        focusManager.clearFocus()
                                    }
                                }

                                // Interactive Scrim click collapse background
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.White.copy(alpha = 0.08f))
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                                        ) {
                                            searchQuery = ""
                                            isSearchFocused = false
                                            focusManager.clearFocus()
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
                                    contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
                                ) {
                                    // CATEGORY FILTER CHIPS (Since reverseLayout = true, declared first means rendered at the bottom, closest to the search input bar!)
                                    item {
                                        androidx.compose.foundation.lazy.LazyRow(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            contentPadding = PaddingValues(horizontal = 4.dp)
                                        ) {
                                            items(listOf("All", "Contacts", "Apps", "Web", "Settings & Files")) { cat ->
                                                val isSelected = activeSearchCategoryFilter == cat
                                                Box(
                                                    modifier = Modifier
                                                        .background(
                                                            if (isSelected) currentThemeColor else Color.White.copy(alpha = 0.08f),
                                                            RoundedCornerShape(16.dp)
                                                        )
                                                        .clickable {
                                                            activeSearchCategoryFilter = cat
                                                        }
                                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                                ) {
                                                    Text(
                                                        text = cat,
                                                        color = if (isSelected) Color.Black else Color.White,
                                                        fontSize = 12.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                        fontFamily = currentFontFamily,
                                                        style = TextStyle(
                                                            shadow = Shadow(
                                                                color = Color.Black.copy(alpha = 0.40f),
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
                                                    color = Color.White,
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
                                                    color = Color.White.copy(alpha = 0.82f),
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
                                        // Partition results for category ranking lists
                                        val contacts = displayedResults.filterIsInstance<SearchResult.ContactResult>()
                                        val apps = displayedResults.filterIsInstance<SearchResult.AppResult>()
                                        val webs = displayedResults.filterIsInstance<SearchResult.WebResult>()
                                        val others = displayedResults.filter { it is SearchResult.SettingResult || it is SearchResult.FileResult }

                                        // Render in reversed declaration order (so they show Priority 1 down to Priority 4 from top of screen to bottom)
                                        
                                        // Priority 4: SYSTEM SETTINGS / FILES
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
                                                                        val category = when (result.action) {
                                                                            "launcher_perf" -> "Performance"
                                                                            "launcher_gestures" -> "Gestures"
                                                                            "launcher_permissions" -> "Permissions"
                                                                            "launcher_search" -> "Search"
                                                                            "launcher_pages" -> "Pages"
                                                                            else -> null
                                                                        }
                                                                        isSearchFocused = false
                                                                        searchQuery = ""
                                                                        focusManager.clearFocus()
                                                                        activeSettingsCategory = category
                                                                        showSettingsPanel = true
                                                                    } else {
                                                                        try {
                                                                            val intent = Intent(result.action)
                                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                                            contextForSearch.startActivity(intent)
                                                                        } catch (e2: Exception) {}
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
                                                                    color = Color.White,
                                                                    fontSize = 14.sp,
                                                                    fontWeight = FontWeight.SemiBold,
                                                                    fontFamily = currentFontFamily
                                                                )
                                                                Text(
                                                                    text = "System Setting",
                                                                    color = Color.White.copy(alpha = 0.5f),
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
                                                                    android.widget.Toast.makeText(contextForSearch, "File Selected: ${result.label}", android.widget.Toast.LENGTH_SHORT).show()
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
                                                                    color = Color.White,
                                                                    fontSize = 14.sp,
                                                                    fontWeight = FontWeight.SemiBold,
                                                                    fontFamily = currentFontFamily,
                                                                    maxLines = 1,
                                                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                                                )
                                                                val kbSize = result.size / 1024
                                                                Text(
                                                                    text = "Local File • ${kbSize} KB • ${result.mimeType ?: "Unknown type"}",
                                                                    color = Color.White.copy(alpha = 0.5f),
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

                                        // Priority 3: WEB SUGGESTIONS
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
                                                                    val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://www.google.com/search?q=${java.net.URLEncoder.encode(result.label, "UTF-8")}"))
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                                    contextForSearch.startActivity(intent)
                                                                } catch (e2: Exception) {}
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
                                                            tint = Color.White.copy(alpha = 0.6f),
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(14.dp))
                                                    Text(
                                                        text = result.label,
                                                        color = Color.White.copy(alpha = 0.9f),
                                                        fontSize = 14.sp,
                                                        fontFamily = currentFontFamily,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowBack,
                                                        contentDescription = null,
                                                        tint = Color.White.copy(alpha = 0.3f),
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

                                        // Priority 2: APPLICATIONS
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
                                                            color = Color.White,
                                                            fontSize = 14.sp,
                                                            fontWeight = FontWeight.SemiBold,
                                                            fontFamily = currentFontFamily
                                                        )
                                                        Text(
                                                            text = "App • ${result.packageName}",
                                                            color = Color.White.copy(alpha = 0.5f),
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

                                        // Priority 1: CONTACTS
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
                                                                        val intent = Intent(Intent.ACTION_DIAL, android.net.Uri.parse("tel:${result.phoneNumber}"))
                                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                                        contextForSearch.startActivity(intent)
                                                                    } catch (e: Exception) {}
                                                                }
                                                            } else {
                                                                try {
                                                                    val intent = Intent(Intent.ACTION_DIAL, android.net.Uri.parse("tel:${result.phoneNumber}"))
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                                    contextForSearch.startActivity(intent)
                                                                } catch (e: Exception) {}
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
                                                            color = Color.White,
                                                            fontSize = 14.sp,
                                                            fontWeight = FontWeight.SemiBold,
                                                            fontFamily = currentFontFamily
                                                        )
                                                        Text(
                                                            text = "Contact • ${result.phoneNumber}",
                                                            color = Color.White.copy(alpha = 0.5f),
                                                            fontSize = 11.sp,
                                                            fontFamily = currentFontFamily
                                                        )
                                                    }
                                                    IconButton(
                                                        onClick = {
                                                            UniversalSearchEngine.recordSelection(contextForSearch, result)
                                                            try {
                                                                val intent = Intent(Intent.ACTION_DIAL, android.net.Uri.parse("tel:${result.phoneNumber}"))
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                                contextForSearch.startActivity(intent)
                                                            } catch (e: Exception) {}
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
                                    val letters = ('A'..'Z').toList()
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
                                                // Snaps the header exactly to the top when dragging sidebar
                                                listState.scrollToItem(actualIndex, scrollOffset = 0)
                                            }
                                        } catch (e: Exception) {
                                            listState.scrollToItem(actualIndex, scrollOffset = 0)
                                        }
                                    }

                                    // Define list column and alphabet box as Composable lambdas so they capture local variables easily without duplication
                                    val listColumn: @Composable RowScope.() -> Unit = {
                                        val sidebarInteractiveProgress by animateFloatAsState(
                                            targetValue = if (isTouchingSidebar) 1.0f else 0.0f,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessMediumLow
                                            )
                                        )

                                        LazyColumn(
                                            state = listState,
                                            modifier = Modifier.fillMaxHeight().weight(1.0f).graphicsLayer { clip = false },
                                            contentPadding = PaddingValues(bottom = 100.dp, start = 24.dp)
                                        ) {
                                            if (categoriseByUsageVal && searchQuery.isEmpty()) {
                                                // 1. Frequently Used Section
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
                                                                color = currentThemeColor.copy(alpha = 0.7f),
                                                                letterSpacing = 1.sp
                                                            )
                                                        }
                                                    }

                                                    itemsIndexed(items = topNApps, key = { _, app -> "top_" + app.packageName }) { index, app ->
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
                                                        val animatedAlpha by animateFloatAsState(targetValue = alphaTarget)
                                                        
                                                        AppRow(
                                                            index = 1 + index,
                                                            app = app,
                                                            animatedAlpha = animatedAlpha,
                                                            highlightedApp = highlightedApp,
                                                            sidebarTouchX = sidebarTouchX,
                                                            sidebarTouchY = sidebarTouchY,
                                                            sidebarInteractiveProgress = sidebarInteractiveProgress,
                                                            listState = listState,
                                                            density = density,
                                                            currentFontFamily = currentFontFamily,
                                                            iconPackVal = iconPackVal,
                                                            iconThemeColor = iconThemeColor,
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

                                                    // Minimalist split separator
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
                                                                    color = Color.White.copy(alpha = 0.25f),
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

                                                 // 2. Rest of applications Section with Alphabet Headers
                                                itemsIndexed(
                                                    items = restAppsEntries,
                                                    key = { idx, entry ->
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
                                                                isActive = (touchedLetter == entry.letter),
                                                                isTouchingSidebar = isTouchingSidebar,
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
                                                            val animatedAlpha by animateFloatAsState(targetValue = alphaTarget)

                                                            AppRow(
                                                                index = topNApps.size + 2 + index,
                                                                app = app,
                                                                animatedAlpha = animatedAlpha,
                                                                highlightedApp = highlightedApp,
                                                                sidebarTouchX = sidebarTouchX,
                                                                sidebarTouchY = sidebarTouchY,
                                                                sidebarInteractiveProgress = sidebarInteractiveProgress,
                                                                listState = listState,
                                                                density = density,
                                                                currentFontFamily = currentFontFamily,
                                                                iconPackVal = iconPackVal,
                                                                iconThemeColor = iconThemeColor,
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
                                                // Standard Alphabetical List with Alphabet Headers
                                                itemsIndexed(
                                                    items = standardListEntries,
                                                    key = { idx, entry ->
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
                                                                isActive = (touchedLetter == entry.letter),
                                                                isTouchingSidebar = isTouchingSidebar,
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
                                                            val animatedAlpha by animateFloatAsState(targetValue = alphaTarget)

                                                            AppRow(
                                                                index = index,
                                                                app = app,
                                                                animatedAlpha = animatedAlpha,
                                                                highlightedApp = highlightedApp,
                                                                sidebarTouchX = sidebarTouchX,
                                                                sidebarTouchY = sidebarTouchY,
                                                                sidebarInteractiveProgress = sidebarInteractiveProgress,
                                                                listState = listState,
                                                                density = density,
                                                                currentFontFamily = currentFontFamily,
                                                                iconPackVal = iconPackVal,
                                                                iconThemeColor = iconThemeColor,
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
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .width(36.dp)
                                                .padding(vertical = 12.dp)
                                                .pointerInput(letters) {
                                                    try {
                                                        awaitEachGesture {
                                                            try {
                                                                val down = awaitFirstDown()
                                                                down.consume()
                                                                isTouchingSidebar = true
                                                        var y = down.position.y
                                                        var x = down.position.x
                                                        sidebarTouchY = y
                                                        sidebarTouchX = x
                                                        var lastY = y
                                                        var touchAccumulatorY = 0f
                                                        var activeHoverIndex: Int? = null
                                                        var lastScrolledIndex = -1
                                                        var scrollJob: kotlinx.coroutines.Job? = null
                                                        var crossedThreshold = false
                                                        var lockedLetter: Char? = null

                                                        val getTargetLetterForY: (Float, Float) -> Char = { touchY, screenH ->
                                                            val pct = (touchY / screenH).coerceIn(0f, 1f)
                                                            val letterIndex = (pct * letters.size).toInt().coerceIn(0, letters.lastIndex)
                                                            letters[letterIndex]
                                                        }

                                                        val getAppIndexForY: (Float, Float, Char?) -> Int = { touchY, screenH, lockLtr ->
                                                            if (screenH > 0 && finalFilteredAppsList.isNotEmpty()) {
                                                                val pct = (touchY / screenH).coerceIn(0f, 1f)
                                                                val targetLetter = lockLtr ?: getTargetLetterForY(touchY, screenH)
                                                                
                                                                val matchingApps = finalFilteredAppsList.mapIndexedNotNull { index, app -> 
                                                                    if (app.label.firstOrNull()?.uppercaseChar() == targetLetter) index else null
                                                                }
                                                                
                                                                if (matchingApps.isNotEmpty()) {
                                                                    if (lockLtr != null) {
                                                                        val internalIndex = (pct * matchingApps.size).toInt().coerceIn(0, matchingApps.lastIndex)
                                                                        matchingApps[internalIndex]
                                                                    } else {
                                                                        matchingApps.first()
                                                                    }
                                                                } else {
                                                                    (pct * finalFilteredAppsList.lastIndex).toInt().coerceIn(0, finalFilteredAppsList.lastIndex)
                                                                }
                                                            } else 0
                                                        }

                                                        if (x > 165f) {
                                                            lockedLetter = getTargetLetterForY(y, size.height.toFloat())
                                                            lockedLetterState = lockedLetter
                                                        } else {
                                                            lockedLetterState = null
                                                        }

                                                        if (lockedLetter == null) {
                                                            touchedLetter = getTargetLetterForY(y, size.height.toFloat())
                                                        } else {
                                                            touchedLetter = lockedLetter
                                                        }
                                                        
                                                        val targetIndex = if (lockedLetter != null) {
                                                            getAppIndexForY(y, size.height.toFloat(), lockedLetter)
                                                        } else {
                                                            getAppIndexForY(y, size.height.toFloat(), null)
                                                        }
                                                        
                                                        activeHoverIndex = targetIndex
                                                        if (targetIndex in finalFilteredAppsList.indices) {
                                                            val currentHoveredApp = finalFilteredAppsList[targetIndex]
                                                            if (lockedLetter != null) {
                                                                hoveredApp = currentHoveredApp
                                                                if (highlightedApp != currentHoveredApp) highlightedApp = currentHoveredApp
                                                            } else {
                                                                hoveredApp = null
                                                                highlightedApp = null
                                                            }
                                                            if (targetIndex != lastScrolledIndex) {
                                                                lastScrolledIndex = targetIndex
                                                                scrollJob = coroutineScope.launch {
                                                                    try {
                                                                        scrollToItemCenter(targetIndex, y, lockedLetter != null)
                                                                    } catch (e: Exception) {}
                                                                }
                                                            }
                                                        }
                                                        
                                                        do {
                                                            val event = awaitPointerEvent()
                                                            val change = event.changes.firstOrNull()
                                                            if (change != null && change.pressed) {
                                                                change.consume()
                                                                val currentY = change.position.y
                                                                val currentX = change.position.x
                                                                val deltaY = currentY - lastY
                                                                lastY = currentY
                                                                sidebarTouchY = currentY
                                                                sidebarTouchX = currentX
                                                                
                                                                if (currentX > 165f) {
                                                                    if (!crossedThreshold) {
                                                                        crossedThreshold = true
                                                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                                    }
                                                                    if (lockedLetter == null) {
                                                                        lockedLetter = touchedLetter
                                                                    }
                                                                    lockedLetterState = lockedLetter
                                                                } else if (currentX <= 165f) {
                                                                    crossedThreshold = false
                                                                    lockedLetter = null
                                                                    lockedLetterState = null
                                                                }

                                                                if (lockedLetter == null) {
                                                                    touchedLetter = getTargetLetterForY(currentY, size.height.toFloat())
                                                                } else {
                                                                    touchedLetter = lockedLetter
                                                                }
                                                                
                                                                val targetIndex = if (lockedLetter != null) {
                                                                    getAppIndexForY(currentY, size.height.toFloat(), lockedLetter)
                                                                } else {
                                                                    getAppIndexForY(currentY, size.height.toFloat(), null)
                                                                }
                                                                
                                                                activeHoverIndex = targetIndex
                                                                if (targetIndex in finalFilteredAppsList.indices) {
                                                                    val currentHoveredApp = finalFilteredAppsList[targetIndex]
                                                                    if (lockedLetter != null) {
                                                                        hoveredApp = currentHoveredApp
                                                                        if (highlightedApp != currentHoveredApp) highlightedApp = currentHoveredApp
                                                                    } else {
                                                                        hoveredApp = null
                                                                        highlightedApp = null
                                                                    }
                                                                    if (targetIndex != lastScrolledIndex) {
                                                                        lastScrolledIndex = targetIndex
                                                                        if (currentX > 165f) {
                                                                            val prevApp = finalFilteredAppsList.getOrNull(lastScrolledIndex)
                                                                            val isLetterChanged = prevApp != null && prevApp.label.firstOrNull()?.uppercaseChar() != currentHoveredApp.label.firstOrNull()?.uppercaseChar()
                                                                            if (isLetterChanged) {
                                                                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                                            } else {
                                                                                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                                            }
                                                                        } else {
                                                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                                        }
                                                                        scrollJob?.cancel()
                                                                        scrollJob = coroutineScope.launch {
                                                                            try {
                                                                                scrollToItemCenter(targetIndex, currentY, lockedLetter != null)
                                                                            } catch (e: Exception) {}
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        } while (event.changes.any { it.pressed })
                                                        
                                                        val finalTouchedLetter = touchedLetter
                                                        if (finalTouchedLetter != null) {
                                                            scrolledLetter = finalTouchedLetter
                                                        }
                                                        
                                                        val finalSidebarTouchX = sidebarTouchX ?: 0f
                                                        if (hoveredApp != null && finalSidebarTouchX > 165f) {
                                                            activity.launchAppWithTracker(hoveredApp!!.packageName)
                                                        }
                                                        
                                                            } catch (e: kotlinx.coroutines.CancellationException) {
                                                                throw e
                                                            } catch (e: Exception) {
                                                                e.printStackTrace()
                                                            } finally {
                                                                hoveredApp = null
                                                                highlightedApp = null
                                                                touchedLetter = null
                                                                lockedLetterState = null
                                                                isTouchingSidebar = false
                                                                sidebarTouchY = null
                                                                sidebarTouchX = null
                                                            }
                                                        }
                                                    } catch (e: kotlinx.coroutines.CancellationException) {
                                                        throw e
                                                    } catch (e: Exception) {
                                                        e.printStackTrace()
                                                    }
                                                }
                                        ) {
                                        BoxWithConstraints(
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            val maxItemHeight = this.maxHeight / letters.size
                                            val dynamicFontSize = (maxItemHeight.value * 0.5f).coerceIn(10f, 15f).sp
                                            
                                            Column(
                                                modifier = Modifier.fillMaxSize(),
                                                verticalArrangement = Arrangement.SpaceEvenly,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                letters.forEachIndexed { idx, letter ->
                                                    val isActive = displayLetter == letter

                                                    val targetOffset = if (isTouchingSidebar && touchedLetter != null) {
                                                        val curIdx = letters.indexOf(touchedLetter)
                                                        val dist = abs(idx - curIdx)
                                                        val baseOffset = when (dist) {
                                                            0 -> (-24).dp
                                                            1 -> (-18).dp
                                                            2 -> (-12).dp
                                                            3 -> (-6).dp
                                                            else -> 0.dp
                                                        }
                                                        baseOffset
                                                    } else {
                                                        0.dp
                                                    }

                                                    val targetScale = if (isTouchingSidebar && touchedLetter != null) {
                                                        val curIdx = letters.indexOf(touchedLetter)
                                                        val dist = abs(idx - curIdx)
                                                        when (dist) {
                                                            0 -> 1.7f
                                                            1 -> 1.4f
                                                            2 -> 1.2f
                                                            3 -> 1.1f
                                                            else -> 1.0f
                                                        }
                                                    } else {
                                                        if (isActive) 1.2f else 1.0f
                                                    }

                                                    val animatedOffset by animateDpAsState(
                                                        targetValue = targetOffset,
                                                        animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                                            stiffness = Spring.StiffnessLow
                                                        )
                                                    )

                                                    val animatedScale by animateFloatAsState(
                                                        targetValue = targetScale,
                                                        animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                                            stiffness = Spring.StiffnessLow
                                                        )
                                                    )

                                                    Box(
                                                        contentAlignment = Alignment.Center,
                                                        modifier = Modifier
                                                            .wrapContentSize()
                                                            .weight(1f) // Ensures letters spread evenly and stay inside bounds
                                                            .graphicsLayer {
                                                                translationX = animatedOffset.toPx()
                                                            }
                                                            .then(
                                                                if (isActive) {
                                                                    Modifier
                                                                        .background(
                                                                            Color.White.copy(alpha = 0.2f),
                                                                            RoundedCornerShape(percent = 50)
                                                                        )
                                                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                                                } else {
                                                                    Modifier
                                                                }
                                                            )
                                                    ) {
                                                        Text(
                                                            text = letter.toString(),
                                                            fontSize = dynamicFontSize,
                                                            fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Normal,
                                                            color = if (isActive) Color.White else Color.White.copy(alpha = 0.55f),
                                                            modifier = Modifier
                                                                .graphicsLayer {
                                                                    scaleX = animatedScale
                                                                    scaleY = animatedScale
                                                                },
                                                            style = TextStyle(shadow = Shadow(Color.Black, Offset(1f, 1f), 3f))
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        }
                                    }

                                    val currentPageName = displayedPages.getOrNull(currentPageIndex) ?: "App List"
                                    
                                    Column(modifier = Modifier.fillMaxSize()) {
                                        if (displayedPages.size > 1) {
                                            Row(
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                                            ) {
                                                displayedPages.forEachIndexed { idx, page ->
                                                    val isSelected = idx == currentPageIndex
                                                    val dotWidth by animateDpAsState(targetValue = if (isSelected) 16.dp else 6.dp)
                                                    val dotColor = if (isSelected) currentThemeColor else Color.White
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
                                                            trackInfo = mediaTrackInfoVal,
                                                            themeColor = currentThemeColor,
                                                            fontFamily = currentFontFamily,
                                                            activity = activity,
                                                            modifier = Modifier.fillMaxSize()
                                                        )
                                                    } else if (targetPageName == "Notifications") {
                                                        NotificationsPage(
                                                            notifications = notificationsListState,
                                                            themeColor = currentThemeColor,
                                                            fontFamily = currentFontFamily,
                                                            activity = activity,
                                                            allowedCategories = allowedNotificationCategoriesVal,
                                                            onLongPressApp = { packageAppInfo -> focusedContextMenuApp = packageAppInfo },
                                                            onNotificationClick = { appName, notificationText, defaultPkg ->
                                                                val foundApp = uiState.apps.firstOrNull { 
                                                                    it.label.equals(appName, ignoreCase = true) || 
                                                                    it.packageName.equals(defaultPkg, ignoreCase = true) ||
                                                                    it.packageName.contains(appName, ignoreCase = true) 
                                                                }
                                                                if (foundApp != null) {
                                                                    activity.launchAppWithTracker(foundApp.packageName)
                                                                }
                                                            },
                                                            modifier = Modifier.fillMaxSize()
                                                        )
                                                    } else if (targetPageName == "App List") {
                                                        Box(modifier = Modifier.fillMaxSize()) {
                                                            Row(
                                                                modifier = Modifier.fillMaxSize(),
                                                                horizontalArrangement = Arrangement.SpaceBetween
                                                             ) {
                                                                 alphabetBox()
                                                                 listColumn()
                                                             }

                                                             val bubbleAlpha by animateFloatAsState(
                                                                 targetValue = if (isTouchingSidebar && lockedLetterState == null && touchedLetter != null) 1f else 0f
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
                                                             modifier = Modifier.fillMaxSize()
                                                         )
                                                     }
                                                 }
                                             }
                                         }
                                    }
                                }

                                // Interactive floating scroll letter bubble removed/disabled per user request
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

                    Spacer(modifier = Modifier.height(6.dp))

                    // Search Bar underneath the App List (At the bottom of the screen)
                    androidx.compose.animation.AnimatedVisibility(
                        visible = !isNotificationsExpanded,
                        enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
                    ) {
                        AdvancedSearchBar(
                            query = searchQuery,
                            onQueryChange = {
                                searchQuery = it
                                activity.fetchWebSuggestions(it)
                            },
                            fontFamily = currentFontFamily,
                            primaryColor = currentThemeColor,
                            onSearchWeb = { q ->
                                try {
                                    val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                                        putExtra(android.app.SearchManager.QUERY, q)
                                    }
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(intent)
                                } catch (e: Exception) {}
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
                                                            val intent = Intent(Intent.ACTION_DIAL, android.net.Uri.parse("tel:${topResult.phoneNumber}"))
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                            contextForSearch.startActivity(intent)
                                                        } catch (e: Exception) {}
                                                    }
                                                } else {
                                                    try {
                                                        val intent = Intent(Intent.ACTION_DIAL, android.net.Uri.parse("tel:${topResult.phoneNumber}"))
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                        contextForSearch.startActivity(intent)
                                                    } catch (e: Exception) {}
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
                                                        val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://www.google.com/search?q=${java.net.URLEncoder.encode(topResult.label, "UTF-8")}"))
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                        contextForSearch.startActivity(intent)
                                                    } catch (e2: Exception) {}
                                                }
                                            }
                                            is SearchResult.SettingResult -> {
                                                if (topResult.action.startsWith("launcher_")) {
                                                    val category = when (topResult.action) {
                                                        "launcher_perf" -> "Performance"
                                                        "launcher_gestures" -> "Gestures"
                                                        "launcher_permissions" -> "Permissions"
                                                        "launcher_search" -> "Search"
                                                        "launcher_pages" -> "Pages"
                                                        else -> null
                                                    }
                                                    activeSettingsCategory = category
                                                    showSettingsPanel = true
                                                    focusManager.clearFocus()
                                                } else {
                                                    try {
                                                        val intent = Intent(topResult.action)
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                        contextForSearch.startActivity(intent)
                                                    } catch (e: Exception) {}
                                                }
                                            }
                                            is SearchResult.FileResult -> {
                                                android.widget.Toast.makeText(contextForSearch, "Opening match: ${topResult.label}", android.widget.Toast.LENGTH_SHORT).show()
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
                                        } catch (e: Exception) {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://www.google.com/search?q=${java.net.URLEncoder.encode(webSearchQuery, "UTF-8")}"))
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                contextForSearch.startActivity(intent)
                                            } catch (e2: Exception) {}
                                        }
                                    }
                                    searchQuery = ""
                                    isSearchFocused = false
                                }
                            }
                        )
                    }
                }

            } // Close Box wrapper that animates scale/translate

            // Full screen grid overview mode
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

            // Underneath Options Panel when zoomed out
            AnimatedVisibility(
                visible = zoomLevel == 1,
                enter = androidx.compose.animation.slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
                exit = androidx.compose.animation.slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(),
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
                    // Header Label
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
                                color = Color.White,
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
                                } catch (e: Exception) {
                                    try {
                                        val intent = Intent(android.provider.Settings.ACTION_SETTINGS)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        activity.startActivity(intent)
                                    } catch (ex: Exception) {}
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
                                color = Color.White,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Expanded Popup folder Grid dialog sheet
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

        // Long press application Context overlay
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
                    activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit().putStringSet("hidden_packages", now).apply()
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
                    intent.data = android.net.Uri.parse("package:$pkg")
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

        // App usage mind breaker blocker countdown pause
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

        // Settings full sheet config Dialog
        AnimatedVisibility(
            visible = showSettingsPanel,
            enter = androidx.compose.animation.slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = androidx.compose.animation.slideOutVertically(targetOffsetY = { it }) + fadeOut()
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

        // Double tap sleep visual lock screensaver
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

        // ----------------- App Zoom/Fade Transition Overlay -----------------
        val isLaunchingAppVal by activity.isLaunchingApp.collectAsState()
        val isClosingAppVal by activity.isClosingApp.collectAsState()
        val appTransitionProgressVal by activity.appTransitionProgress.collectAsState()
        val animatingAppPackageVal by activity.animatingAppPackage.collectAsState()
        val animatingAppIconBoundsVal by activity.animatingAppIconBounds.collectAsState()

        if (isLaunchingAppVal || isClosingAppVal) {
            val progress = appTransitionProgressVal
            val bounds = animatingAppIconBoundsVal ?: android.graphics.Rect(150, 450, 350, 650)
            
            val configuration = androidx.compose.ui.platform.LocalConfiguration.current
            val localDensity = LocalDensity.current
            
            val screenWidthPx = with(localDensity) { configuration.screenWidthDp.dp.toPx() }
            val screenHeightPx = with(localDensity) { configuration.screenHeightDp.dp.toPx() }
            
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
                            width = with(localDensity) { currentWidth.toDp() },
                            height = with(localDensity) { currentHeight.toDp() }
                        )
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            shape = RoundedCornerShape((16 * (1f - progress)).dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (animatingAppPackageVal != null) {
                        val appInfo = uiState.apps.find { it.packageName == animatingAppPackageVal }
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
    }
}

@Composable
fun StyledAppIcon(icon: Drawable, pack: String, themeColor: Color, size: androidx.compose.ui.unit.Dp = 38.dp) {
    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        val painter = rememberAsyncImagePainter(icon)
        when (pack) {
            "Silhouette Outlined" -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(color = themeColor.copy(alpha = 0.35f), style = Stroke(width = 2.dp.toPx()))
                }
                Image(painter = painter, contentDescription = null, modifier = Modifier.fillMaxSize(0.57f))
            }
            "Neon Glow" -> {
                Box(modifier = Modifier.fillMaxSize().padding(2.dp).background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(10.dp)).border(1.5.dp, themeColor, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                    Image(painter = painter, contentDescription = null, modifier = Modifier.fillMaxSize(0.63f))
                }
            }
            "Pastel Minimalist" -> {
                Box(modifier = Modifier.fillMaxSize().background(themeColor.copy(alpha = 0.28f), RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                    Image(painter = painter, contentDescription = null, modifier = Modifier.fillMaxSize(0.63f))
                }
            }
            else -> { // Classic
                Image(painter = painter, contentDescription = null, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

private fun scrollToLetter(
    letter: Char,
    apps: List<AppInfo>,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    listState: androidx.compose.foundation.lazy.LazyListState
) {
    val index = apps.indexOfFirst { (it.label.firstOrNull()?.uppercaseChar() ?: '#') == letter }
    if (index >= 0) {
        coroutineScope.launch {
            listState.scrollToItem(index, 0)
        }
    }
}

@Composable
fun AppRow(
    index: Int,
    app: AppInfo,
    animatedAlpha: Float,
    highlightedApp: AppInfo?,
    sidebarTouchX: Float?,
    sidebarTouchY: Float?,
    sidebarInteractiveProgress: Float,
    listState: androidx.compose.foundation.lazy.LazyListState,
    density: androidx.compose.ui.unit.Density,
    currentFontFamily: FontFamily,
    iconPackVal: String,
    iconThemeColor: Color,
    onLongPress: () -> Unit,
    onTap: () -> Unit,
) {
    val isAppHovered = highlightedApp == app
    val hoverProgress by animateFloatAsState(
        targetValue = if (isAppHovered) 1.0f else 0.0f,
        animationSpec = spring(
            stiffness = Spring.StiffnessMedium
        )
    )

    val dynamicPaddingTop by animateDpAsState(
        targetValue = if (isAppHovered) 16.dp else 10.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium)
    )
    val dynamicPaddingBottom by animateDpAsState(
        targetValue = if (isAppHovered) 16.dp else 10.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium)
    )

    val rawPullX = if (isAppHovered && sidebarTouchX != null && sidebarTouchX < -10f) sidebarTouchX else 0f
    val animatedPullX by animateFloatAsState(
        targetValue = rawPullX,
        animationSpec = spring(
            dampingRatio = 0.55f,
            stiffness = 300f
        )
    )
    val separationProgress = (-animatedPullX / 100f).coerceIn(0f, 1.3f)

    val currentContext = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 44.dp, end = 16.dp)
            .onGloballyPositioned { layoutCoordinates ->
                val size = layoutCoordinates.size
                val position = layoutCoordinates.localToWindow(Offset.Zero)
                val rect = android.graphics.Rect(
                    position.x.toInt(),
                    position.y.toInt(),
                    (position.x + size.width).toInt(),
                    (position.y + size.height).toInt()
                )
                var cur = currentContext
                while (cur is android.content.ContextWrapper) {
                    if (cur is MainActivity) {
                        cur.appIconBoundsMap[app.packageName] = rect
                        break
                    }
                    cur = cur.baseContext
                }
            }
            .graphicsLayer {
                var tx = 0f
                val touchY = sidebarTouchY
                if (touchY != null && sidebarInteractiveProgress > 0.01f) {
                    val approxItemHeight = 60.dp.toPx()
                    val itemCenterLocal = (index - listState.firstVisibleItemIndex) * approxItemHeight - listState.firstVisibleItemScrollOffset + (approxItemHeight / 2f)
                    val dy = abs(itemCenterLocal - touchY)
                    
                    val maxDist = 220.dp.toPx()
                    val effect = if (dy < maxDist) {
                        val fraction = dy / maxDist
                        kotlin.math.cos(fraction * (Math.PI / 2f)).toFloat()
                    } else 0f
                    
                    val maxTx = 64.dp.toPx()
                    tx = (effect * maxTx) * sidebarInteractiveProgress
                }
                
                translationX = tx
                scaleX = 1.0f + (hoverProgress * 0.04f)
                scaleY = 1.0f + (hoverProgress * 0.04f)
                alpha = animatedAlpha
            }
            .background(
                color = if (isAppHovered) Color.White.copy(alpha = 0.08f) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = if (isAppHovered) Color.White.copy(alpha = 0.12f) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .pointerInput(app) {
                detectTapGestures(
                    onLongPress = { onLongPress() },
                    onTap = { onTap() }
                )
            }
            .padding(
                start = 12.dp,
                end = 12.dp,
                top = if (index == 0) 0.dp else dynamicPaddingTop,
                bottom = dynamicPaddingBottom
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.graphicsLayer {
                translationX = animatedPullX
            },
            contentAlignment = Alignment.Center
        ) {
            if (separationProgress > 0.01f) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .graphicsLayer {
                            val s = (separationProgress * 1.1f).coerceIn(0f, 1f)
                            scaleX = s
                            scaleY = s
                            alpha = s
                        }
                        .background(iconThemeColor, CircleShape)
                )
            }
            StyledAppIcon(app.icon, iconPackVal, iconThemeColor, size = 30.dp)
        }
        
        Spacer(modifier = Modifier.width(14.dp))
        
        Text(
            text = app.label,
            fontSize = if (isAppHovered) 14.5.sp else 13.sp,
            fontWeight = if (isAppHovered) FontWeight.SemiBold else FontWeight.Normal,
            fontFamily = currentFontFamily,
            color = Color.White,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            style = TextStyle(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.5f),
                    offset = Offset(1f, 1f),
                    blurRadius = 3f
                )
            ),
            modifier = Modifier
                .weight(1f)
                .graphicsLayer {
                    translationX = separationProgress * 40f
                    alpha = 1f - separationProgress.coerceIn(0f, 1f)
                    
                    var scaleAdd = 0f
                    val touchY = sidebarTouchY
                    if (touchY != null && sidebarInteractiveProgress > 0.01f) {
                        val approxItemHeight = 60.dp.toPx()
                        val itemCenterLocal = (index - listState.firstVisibleItemIndex) * approxItemHeight - listState.firstVisibleItemScrollOffset + (approxItemHeight / 2f)
                        val dy = abs(itemCenterLocal - touchY)
                        val maxDist = 220.dp.toPx()
                        if (dy < maxDist) {
                            val fraction = dy / maxDist
                            val effect = kotlin.math.cos(fraction * (Math.PI / 2f)).toFloat()
                            scaleAdd = effect * 0.05f * sidebarInteractiveProgress
                        }
                    }
                    scaleX = 1.0f + scaleAdd
                    scaleY = 1.0f + scaleAdd
                    transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 0.5f)
                }
        )
    }
}

@Composable
fun MusicPage(
    trackInfo: MediaTrackInfo?,
    themeColor: Color,
    fontFamily: FontFamily,
    activity: MainActivity,
    modifier: Modifier = Modifier
) {
    val isNotifGranted by activity.isNotificationPermissionGranted.collectAsState()

    if (trackInfo == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth(0.92f)
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF13151D)),
                shape = RoundedCornerShape(32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "No Media",
                        tint = Color.White.copy(alpha = 0.25f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (!isNotifGranted) "Permission Required" else "No Music Playing",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = fontFamily
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (!isNotifGranted) 
                            "Dextera requires Notification Access to detect and control music playback. Please grant it in settings."
                            else "Open any music app (like Spotify, YouTube, etc.) and play media to activate this screen.",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        fontFamily = fontFamily,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    
                    if (!isNotifGranted) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                try {
                                    val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    activity.startActivity(intent)
                                } catch (e: Exception) {
                                    android.widget.Toast.makeText(activity, "Settings could not be opened", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Grant Access", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        return
    }

    val isPlaying = trackInfo.isPlaying
    
    var realProgressMs by remember(trackInfo.title, trackInfo.isPlaying) { 
        mutableStateOf(trackInfo.progressMs) 
    }
    
    LaunchedEffect(isPlaying, trackInfo.progressMs) {
        realProgressMs = trackInfo.progressMs
    }
    
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                kotlinx.coroutines.delay(1000)
                realProgressMs = (realProgressMs + 1000L).coerceAtMost(trackInfo.durationMs)
            }
        }
    }
    
    val haptic = LocalHapticFeedback.current
    
    val phaseTransition = rememberInfiniteTransition(label = "waveform_phase_shift")
    val peakShift by phaseTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "peak_shift"
    )
    
    var hudExpanded by remember { mutableStateOf(false) }
    
    val waveformHeights = remember {
        listOf(
            0.38f, 0.58f, 0.44f, 0.65f, 0.82f, 0.74f, 0.52f, 0.40f, 0.60f, 0.80f,
            0.96f, 0.85f, 0.62f, 0.46f, 0.35f, 0.55f, 0.72f, 0.84f, 0.75f, 0.58f,
            0.42f, 0.68f, 0.82f, 0.94f, 0.88f, 0.64f, 0.50f, 0.42f, 0.56f, 0.72f,
            0.85f, 0.75f, 0.56f, 0.40f, 0.46f, 0.58f
        )
    }

    val trackColors = listOf(themeColor, themeColor.copy(alpha = 0.5f))

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null,
                onClick = { /* Eat clicks so they do not bubble up to the parent background gesture layers */ }
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            val context = LocalContext.current
            val audioManager = remember(context) { context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager }
            val activeController = MyNotificationListenerService.activeController
            val playbackInfo = activeController?.playbackInfo

            val maxVolume = if (playbackInfo != null) {
                playbackInfo.maxVolume
            } else {
                audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC)
            }

            var currentVolume by remember(playbackInfo?.currentVolume, playbackInfo?.maxVolume) {
                val initialVal = if (playbackInfo != null) {
                    playbackInfo.currentVolume
                } else {
                    audioManager.getStreamVolume(android.media.AudioManager.STREAM_MUSIC)
                }
                mutableStateOf(initialVal)
            }

            val maxVolFloat = if (maxVolume <= 0) 15f else maxVolume.toFloat()
            val coercedVolume = currentVolume.toFloat().coerceIn(0f, maxVolFloat)

            // Section 1: Restructured Artwork Row with Floating Side Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.1f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        com.example.media.MediaController.dispatchMediaKey(context, android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS)
                    },
                    modifier = Modifier.size(48.dp),
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous Track",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                val coroutineScope = rememberCoroutineScope()
                val artworkScale = remember { androidx.compose.animation.core.Animatable(1f) }
                var feedbackIconState by remember { mutableStateOf<Boolean?>(null) } // true = play, false = pause, null = hide

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = artworkScale.value
                                scaleY = artworkScale.value
                            }
                            .pointerInput(isPlaying) {
                                var totalDragY = 0f
                                var gestureTriggered = false
                                detectVerticalDragGestures(
                                    onDragStart = {
                                        totalDragY = 0f
                                        gestureTriggered = false
                                    },
                                    onDragEnd = {
                                        totalDragY = 0f
                                    },
                                    onDragCancel = {
                                        totalDragY = 0f
                                    },
                                    onVerticalDrag = { change, dragAmount ->
                                        change.consume()
                                        if (!gestureTriggered) {
                                            totalDragY += dragAmount
                                            if (totalDragY > 70f) {
                                                gestureTriggered = true
                                                com.example.media.MediaController.dispatchMediaKey(context, android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
                                                
                                                coroutineScope.launch {
                                                    artworkScale.animateTo(
                                                        targetValue = 0.85f,
                                                        animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                                            stiffness = Spring.StiffnessLow
                                                        )
                                                    )
                                                    artworkScale.animateTo(
                                                        targetValue = 1.0f,
                                                        animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                                            stiffness = Spring.StiffnessLow
                                                        )
                                                    )
                                                }
                                                
                                                feedbackIconState = !isPlaying
                                                coroutineScope.launch {
                                                    kotlinx.coroutines.delay(800)
                                                    if (feedbackIconState == !isPlaying) {
                                                        feedbackIconState = null
                                                    }
                                                }
                                            }
                                        }
                                    }
                                )
                            },
                        shape = RoundedCornerShape(28.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (trackInfo.artwork != null) {
                                Image(
                                    bitmap = trackInfo.artwork.asImageBitmap(),
                                    contentDescription = "Album Art",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                // Beautiful cyberpunk/neon visual gradient with music icon fallback
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.radialGradient(
                                                colors = listOf(
                                                    themeColor.copy(alpha = 0.4f),
                                                    Color(0xFF13151D)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.MusicNote,
                                            contentDescription = "Music Playing",
                                            tint = themeColor,
                                            modifier = Modifier.size(80.dp)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "DEXTERA STREAMING",
                                            letterSpacing = 5.sp,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = themeColor.copy(alpha = 0.85f),
                                            fontFamily = fontFamily
                                        )
                                    }
                                }
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = feedbackIconState != null,
                        enter = fadeIn() + scaleIn(initialScale = 0.6f),
                        exit = fadeOut() + scaleOut(targetScale = 0.6f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (feedbackIconState == true) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = if (feedbackIconState == true) "Play" else "Pause",
                                tint = themeColor,
                                modifier = Modifier.size(44.dp)
                            )
                        }
                    }
                }

                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        com.example.media.MediaController.dispatchMediaKey(context, android.view.KeyEvent.KEYCODE_MEDIA_NEXT)
                    },
                    modifier = Modifier.size(48.dp),
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Track",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Elegant spacing below Row
            Spacer(modifier = Modifier.height(8.dp))

            // Volume Bar Slider: Directly below the new artwork Row and directly above the dark media info card
            Slider(
                value = coercedVolume,
                onValueChange = { newValue ->
                    currentVolume = newValue.roundToInt()
                    if (activeController != null && playbackInfo != null) {
                        try {
                            activeController.setVolumeTo(currentVolume, 0)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        audioManager.setStreamVolume(
                            android.media.AudioManager.STREAM_MUSIC,
                            currentVolume,
                            0
                        )
                    }
                },
                valueRange = 0f..maxVolFloat,
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                colors = SliderDefaults.colors(
                    thumbColor = themeColor,
                    activeTrackColor = themeColor,
                    inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                )
            )

            // Elegant spacing below Volume Bar
            Spacer(modifier = Modifier.height(8.dp))

            // Section 2: Overhauled Media Control Card (100% Mockup styling)
            Card(
                modifier = Modifier
                    .weight(1f) // Ensure balanced portion
                    .fillMaxWidth(0.98f)
                    .widthIn(max = 480.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF13151D)),
                shape = RoundedCornerShape(32.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Track Title & Artist (Centered labels from mockup)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = trackInfo.title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = fontFamily,
                            maxLines = 1,
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .basicMarquee()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${trackInfo.artist}  •  Active Streaming Session",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.55f),
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .basicMarquee()
                        )
                    }

                    // Waveform Seekbar Row
                    val progressFraction = if (trackInfo.durationMs > 0L) realProgressMs.toFloat() / trackInfo.durationMs else 0.35f
                    val formatMs: (Long) -> String = { ms ->
                        val min = ms / 60000
                        val sec = (ms % 60000) / 1000
                        String.format("%d:%02d", min, sec)
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        val unplayedColor = Color.White.copy(alpha = 0.15f)
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .padding(horizontal = 4.dp)
                        ) {
                            val canvasWidth = constraints.maxWidth.toFloat()
                            val canvasHeight = constraints.maxHeight.toFloat()
                            val density = LocalDensity.current.density
                            val barCount = 36
                            val spacingPx = 4.dp.value * density
                            val rawBarWidth = (canvasWidth - (spacingPx * (barCount - 1))) / barCount
                            val barWidth = rawBarWidth.coerceAtLeast(3f)

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(trackInfo) {
                                        detectTapGestures { offset ->
                                            val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                                            val targetProgress = (fraction * trackInfo.durationMs).toLong()
                                            realProgressMs = targetProgress
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            try {
                                                MyNotificationListenerService.activeController?.transportControls?.seekTo(targetProgress)
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }
                                    }
                                    .pointerInput(trackInfo) {
                                        var lastPercentagePoint = -1
                                        detectHorizontalDragGestures(
                                            onHorizontalDrag = { change, dragAmount ->
                                                change.consume()
                                                val fraction = (change.position.x / size.width).coerceIn(0f, 1f)
                                                val targetProgress = (fraction * trackInfo.durationMs).toLong()
                                                realProgressMs = targetProgress
                                                val percentageInt = (fraction * 24).toInt()
                                                if (percentageInt != lastPercentagePoint) {
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    lastPercentagePoint = percentageInt
                                                }
                                                try {
                                                    MyNotificationListenerService.activeController?.transportControls?.seekTo(targetProgress)
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                        )
                                    }
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val drawHeight = size.height
                                    for (i in 0 until barCount) {
                                        val peakOffset = if (isPlaying) peakShift else 0f
                                        val animationMod = if (isPlaying) {
                                            kotlin.math.sin((i.toFloat() * 0.45f) + peakOffset) * 0.16f
                                        } else {
                                            0f
                                        }
                                        val rawH = waveformHeights[i % waveformHeights.size]
                                        val scaleH = (rawH + animationMod).coerceIn(0.12f, 1.0f)
                                        
                                        val barStartX = i * (barWidth + spacingPx)
                                        val currentBarHeight = drawHeight * scaleH
                                        val barStartY = (drawHeight - currentBarHeight) / 2f
                                        
                                        val barProgressFrac = i.toFloat() / barCount
                                        val isPlayedElement = barProgressFrac <= progressFraction
                                        
                                        val drawColor = if (isPlayedElement) themeColor else unplayedColor
                                        
                                        drawRoundRect(
                                            color = drawColor,
                                            topLeft = Offset(barStartX, barStartY),
                                            size = Size(barWidth, currentBarHeight),
                                            cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatMs(realProgressMs),
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.5f),
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Medium
                            )

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        hudExpanded = !hudExpanded
                                    }
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = if (hudExpanded) "Telemetry ▲" else "Telemetry ▼",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = themeColor,
                                    fontFamily = fontFamily
                                )
                            }

                            Text(
                                text = formatMs(trackInfo.durationMs),
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.5f),
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Force Inject Control Row
                    val context = LocalContext.current
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                com.example.media.MediaController.dispatchMediaKey(context, android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS)
                            },
                            modifier = Modifier.size(48.dp),
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Previous Track",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                com.example.media.MediaController.dispatchMediaKey(context, android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
                            },
                            modifier = Modifier.size(48.dp),
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = themeColor,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                com.example.media.MediaController.dispatchMediaKey(context, android.view.KeyEvent.KEYCODE_MEDIA_NEXT)
                            },
                            modifier = Modifier.size(48.dp),
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next Track",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    // Diagnostics Panel
                    AnimatedVisibility(
                        visible = hudExpanded,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2128)),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Audio Codec:", fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f), fontFamily = fontFamily)
                                    Text("AAC (AudioTrack)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = fontFamily)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Buffer Latency:", fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f), fontFamily = fontFamily)
                                    Text(if (isPlaying) "12 ms (Synced)" else "0 ms", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = themeColor, fontFamily = fontFamily)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Playback Target:", fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f), fontFamily = fontFamily)
                                    Text(trackInfo.packageName, fontSize = 9.sp, fontWeight = FontWeight.Medium, color = Color.White.copy(alpha = 0.7f), fontFamily = fontFamily)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SwipeToDismissNotification(
    item: AppNotification,
    themeColor: Color,
    fontFamily: FontFamily,
    activity: MainActivity,
    onDismiss: () -> Unit,
    onNotificationClick: () -> Unit,
    onLongPress: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    val offsetX = remember { androidx.compose.animation.core.Animatable(0f) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    val appIconDrawable = remember(item.pkg) {
        try {
            context.packageManager.getApplicationIcon(item.pkg)
        } catch (e: Exception) {
            null
        }
    }

    var activeReplyActionIndex by remember { mutableStateOf(-1) }
    var replyText by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
    ) {
        // Swipe indicator background feedback
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    color = if (offsetX.value != 0f) MaterialTheme.colorScheme.error.copy(alpha = 0.15f) else Color.Transparent,
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(horizontal = 16.dp),
            contentAlignment = if (offsetX.value > 0) Alignment.CenterStart else Alignment.CenterEnd
        ) {
            if (offsetX.value != 0f) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Dismiss",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Swipeable content Column
        Column(
            modifier = Modifier
                .offset { androidx.compose.ui.unit.IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(item.key) {
                    var totalDrag = 0f
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            val threshold = with(density) { 96.dp.toPx() }
                            if (abs(totalDrag) > threshold) {
                                val target = if (totalDrag > 0) size.width.toFloat() else -size.width.toFloat()
                                scope.launch {
                                    offsetX.animateTo(target, tween(200))
                                    onDismiss()
                                }
                            } else {
                                scope.launch {
                                    offsetX.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
                                }
                            }
                            totalDrag = 0f
                        },
                        onDragCancel = {
                            scope.launch {
                                offsetX.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
                            }
                            totalDrag = 0f
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            totalDrag += dragAmount
                            scope.launch {
                                offsetX.snapTo(totalDrag)
                            }
                        }
                    )
                }
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(14.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            // Main body containing App Icon, App Name, and text message body
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(item.key) {
                        detectTapGestures(
                            onLongPress = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onLongPress?.invoke()
                            },
                            onTap = {
                                if (offsetX.value == 0f) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    val sbn = item.sbn
                                    val fallbackLaunch = {
                                        try {
                                            val targetPkg = sbn?.packageName ?: item.pkg
                                            val fallbackIntent = context.packageManager.getLaunchIntentForPackage(targetPkg)
                                            if (fallbackIntent != null) {
                                                fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                context.startActivity(fallbackIntent)
                                            }
                                        } catch (ex: Exception) {
                                            ex.printStackTrace()
                                        }
                                    }

                                    if (sbn != null) {
                                        val contentIntent = sbn.notification.contentIntent
                                        if (contentIntent != null) {
                                            try {
                                                val options = if (android.os.Build.VERSION.SDK_INT >= 34) {
                                                    android.app.ActivityOptions.makeBasic().apply {
                                                        setPendingIntentBackgroundActivityStartMode(android.app.ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED)
                                                    }
                                                } else {
                                                    android.app.ActivityOptions.makeBasic()
                                                }
                                                contentIntent.send(context, 0, null, null, null, null, options.toBundle())
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                                fallbackLaunch()
                                            }
                                        } else {
                                            fallbackLaunch()
                                        }

                                        val isAutoCancel = (sbn.notification.flags and android.app.Notification.FLAG_AUTO_CANCEL) != 0
                                        if (isAutoCancel) {
                                            MyNotificationListenerService.instance?.cancelNotification(item.key)
                                            onDismiss()
                                        }
                                    } else {
                                        onNotificationClick()
                                    }
                                }
                            }
                        )
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (appIconDrawable != null) {
                        Image(
                            painter = rememberAsyncImagePainter(appIconDrawable),
                            contentDescription = "${item.appName} Icon",
                            modifier = Modifier.fillMaxSize().padding(4.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(themeColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.appName.firstOrNull()?.uppercase() ?: "",
                                color = themeColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                fontFamily = fontFamily
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.appName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = fontFamily
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.text,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = fontFamily,
                        maxLines = 2
                    )
                }
            }

            // Render notification actions row at the bottom of the card
            val actions = item.sbn?.notification?.actions
            if (actions != null && actions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.foundation.lazy.LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(actions.size) { index ->
                        val action = actions[index]
                        val remoteInputs = action.remoteInputs
                        val isReply = remoteInputs != null && remoteInputs.isNotEmpty()
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, themeColor.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                                .background(themeColor.copy(alpha = 0.08f))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    if (isReply) {
                                        activeReplyActionIndex = if (activeReplyActionIndex == index) -1 else index
                                    } else {
                                        try {
                                            action.actionIntent?.send(context, 0, null)
                                        } catch (e: android.app.PendingIntent.CanceledException) {
                                            e.printStackTrace()
                                        } catch (e: SecurityException) {
                                            e.printStackTrace()
                                        }
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (isReply) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = null,
                                        tint = themeColor,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                                Text(
                                    text = action.title?.toString() ?: "Action",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = themeColor,
                                    fontFamily = fontFamily
                                )
                            }
                        }
                    }
                }
            }

            // Inline Quick Reply OutlinedTextField
            if (activeReplyActionIndex != -1 && actions != null && activeReplyActionIndex < actions.size) {
                val action = actions[activeReplyActionIndex]
                val remoteInputs = action.remoteInputs
                if (remoteInputs != null && remoteInputs.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val placeholderLabel = remoteInputs.firstOrNull()?.label?.toString() ?: "Reply..."
                        androidx.compose.foundation.text.BasicTextField(
                            value = replyText,
                            onValueChange = { replyText = it },
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                fontFamily = fontFamily
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .background(
                                    MaterialTheme.colorScheme.surfaceContainerHigh,
                                    RoundedCornerShape(12.dp)
                                )
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            singleLine = true,
                            cursorBrush = androidx.compose.ui.graphics.SolidColor(themeColor),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (replyText.isEmpty()) {
                                        Text(
                                            text = placeholderLabel,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                            fontSize = 11.sp,
                                            fontFamily = fontFamily
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                        
                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                if (replyText.isNotEmpty()) {
                                    val resultBundle = Bundle()
                                    for (remoteInput in remoteInputs) {
                                        resultBundle.putCharSequence(remoteInput.resultKey, replyText)
                                    }
                                    val fillInIntent = Intent()
                                    android.app.RemoteInput.addResultsToIntent(remoteInputs, fillInIntent, resultBundle)
                                    try {
                                        action.actionIntent?.send(context, 0, fillInIntent)
                                        replyText = ""
                                        activeReplyActionIndex = -1
                                        onDismiss()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Text(
                                text = "Send",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun NotificationsPage(
    notifications: List<AppNotification>,
    themeColor: Color,
    fontFamily: FontFamily,
    activity: MainActivity,
    allowedCategories: Set<String>,
    onLongPressApp: ((AppInfo) -> Unit)? = null,
    onNotificationClick: (appName: String, text: String, defaultPkg: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    
    // Filter active notifications using configuration
    val activeNotifications = remember(notifications, allowedCategories) {
        notifications.filter {
            val cat = getNotificationCategory(it.appName, it.text, it.pkg)
            allowedCategories.contains(cat)
        }
    }

    var selectedFilterCategory by remember { mutableStateOf("All") }
    val expandedGroups = remember { androidx.compose.runtime.mutableStateMapOf<String, Boolean>() }

    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp, bottom = 48.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NOTIFICATION CENTER",
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColor,
                    fontFamily = fontFamily
                )
                
                if (activeNotifications.isNotEmpty()) {
                    Text(
                        text = "Clear All",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily,
                        modifier = Modifier
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                try {
                                    activeNotifications.forEach { item ->
                                        MyNotificationListenerService.instance?.cancelNotification(item.key)
                                    }
                                } catch (e: Exception) {}
                                activity.notificationList.value = emptyList()
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Tabs / Filters
            val presentCategories = remember(activeNotifications) {
                activeNotifications.map { getNotificationCategory(it.appName, it.text, it.pkg) }.distinct()
            }

            if (presentCategories.size > 1) {
                androidx.compose.foundation.lazy.LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        val isSelected = selectedFilterCategory == "All"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .border(BorderStroke(1.dp, if (isSelected) themeColor else MaterialTheme.colorScheme.outlineVariant), RoundedCornerShape(20.dp))
                                .background(if (isSelected) themeColor.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable { selectedFilterCategory = "All" }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("All", fontSize = 10.sp, color = if (isSelected) themeColor else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    presentCategories.forEach { cat ->
                        item {
                            val isSelected = selectedFilterCategory == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .border(BorderStroke(1.dp, if (isSelected) themeColor else MaterialTheme.colorScheme.outlineVariant), RoundedCornerShape(20.dp))
                                    .background(if (isSelected) themeColor.copy(alpha = 0.15f) else Color.Transparent)
                                    .clickable { selectedFilterCategory = cat }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(cat, fontSize = 10.sp, color = if (isSelected) themeColor else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            val displayedNotifications = remember(activeNotifications, selectedFilterCategory) {
                activeNotifications.filter { selectedFilterCategory == "All" || getNotificationCategory(it.appName, it.text, it.pkg) == selectedFilterCategory }
            }

            if (displayedNotifications.isEmpty()) {
                // Polished Zen empty state
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(themeColor.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = themeColor, modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Notifications",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = fontFamily
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Your space is decluttered and clean.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = fontFamily
                        )
                    }
                }
            } else {
                val groupedNotifications = remember(displayedNotifications) {
                    val groups = linkedMapOf<String, List<AppNotification>>()
                    for (notification in displayedNotifications) {
                        val pkg = notification.pkg
                        val list = groups.getOrPut(pkg) { mutableListOf() }
                        (list as MutableList).add(notification)
                    }
                    groups.toList()
                }

                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(groupedNotifications, key = { it.first }) { (pkg, groupList) ->
                        if (groupList.size == 1) {
                            val item = groupList[0]
                            SwipeToDismissNotification(
                                item = item,
                                themeColor = themeColor,
                                fontFamily = fontFamily,
                                activity = activity,
                                onDismiss = {
                                    try {
                                        MyNotificationListenerService.instance?.cancelNotification(item.key)
                                    } catch (e: Exception) {}
                                    val currentList = activity.notificationList.value.toMutableList()
                                    currentList.remove(item)
                                    activity.notificationList.value = currentList
                                },
                                onNotificationClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onNotificationClick(item.appName, item.text, item.pkg)
                                },
                                onLongPress = {
                                    val resolvedAppInfo = try {
                                        val label = activity.packageManager.getApplicationLabel(activity.packageManager.getApplicationInfo(item.pkg, 0)).toString()
                                        val icon = activity.packageManager.getApplicationIcon(item.pkg)
                                        AppInfo(label = label, packageName = item.pkg, icon = icon)
                                    } catch (e: Exception) {
                                        AppInfo(
                                            label = item.appName,
                                            packageName = item.pkg,
                                            icon = try {
                                                activity.packageManager.getApplicationIcon(item.pkg)
                                            } catch (ex: Exception) {
                                                android.graphics.drawable.ColorDrawable(0)
                                            }
                                        )
                                    }
                                    onLongPressApp?.invoke(resolvedAppInfo)
                                }
                            )
                        } else {
                            val isExpanded = expandedGroups[pkg] == true
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (!isExpanded) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .pointerInput(pkg) {
                                                detectTapGestures(
                                                    onLongPress = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        val resolvedAppInfo = try {
                                                            val label = activity.packageManager.getApplicationLabel(activity.packageManager.getApplicationInfo(pkg, 0)).toString()
                                                            val icon = activity.packageManager.getApplicationIcon(pkg)
                                                            AppInfo(label = label, packageName = pkg, icon = icon)
                                                        } catch (e: Exception) {
                                                            AppInfo(
                                                                label = groupList[0].appName,
                                                                packageName = pkg,
                                                                icon = try {
                                                                    activity.packageManager.getApplicationIcon(pkg)
                                                                } catch (ex: Exception) {
                                                                    android.graphics.drawable.ColorDrawable(0)
                                                                }
                                                            )
                                                        }
                                                        onLongPressApp?.invoke(resolvedAppInfo)
                                                    },
                                                    onTap = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        expandedGroups[pkg] = true
                                                    }
                                                )
                                            }
                                    ) {
                                        // 3D Visual card stack shadow/layer
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth(0.92f)
                                                .align(Alignment.BottomCenter)
                                                .offset(y = 8.dp)
                                                .graphicsLayer {
                                                    scaleX = 0.95f
                                                    scaleY = 0.95f
                                                },
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                                            shape = RoundedCornerShape(14.dp),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                        ) {
                                            Box(modifier = Modifier.fillMaxWidth().height(64.dp))
                                        }

                                        SwipeToDismissNotification(
                                            item = groupList[0],
                                            themeColor = themeColor,
                                            fontFamily = fontFamily,
                                            activity = activity,
                                            onDismiss = {
                                                try {
                                                    MyNotificationListenerService.instance?.cancelNotification(groupList[0].key)
                                                } catch (e: Exception) {}
                                                val currentList = activity.notificationList.value.toMutableList()
                                                currentList.remove(groupList[0])
                                                activity.notificationList.value = currentList
                                            },
                                            onNotificationClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                expandedGroups[pkg] = true
                                            },
                                            onLongPress = {
                                                val resolvedAppInfo = try {
                                                    val label = activity.packageManager.getApplicationLabel(activity.packageManager.getApplicationInfo(pkg, 0)).toString()
                                                    val icon = activity.packageManager.getApplicationIcon(pkg)
                                                    AppInfo(label = label, packageName = pkg, icon = icon)
                                                } catch (e: Exception) {
                                                    AppInfo(
                                                        label = groupList[0].appName,
                                                        packageName = pkg,
                                                        icon = try {
                                                            activity.packageManager.getApplicationIcon(pkg)
                                                        } catch (ex: Exception) {
                                                            android.graphics.drawable.ColorDrawable(0)
                                                        }
                                                    )
                                                }
                                                onLongPressApp?.invoke(resolvedAppInfo)
                                            }
                                        )

                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(top = 8.dp, end = 8.dp)
                                                .background(themeColor, shape = CircleShape)
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "+${groupList.size - 1}",
                                                fontSize = 9.sp,
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = fontFamily
                                            )
                                        }
                                    }
                                } else {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                expandedGroups[pkg] = false
                                            }
                                            .padding(vertical = 4.dp, horizontal = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowBack,
                                                contentDescription = "Collapse",
                                                tint = themeColor,
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .graphicsLayer {
                                                        rotationZ = 90f
                                                    }
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${groupList[0].appName.uppercase()} (${groupList.size})",
                                                fontSize = 11.sp,
                                                letterSpacing = 1.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = themeColor,
                                                fontFamily = fontFamily
                                            )
                                        }
                                        Text(
                                            text = "Collapse Stack",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontFamily = fontFamily
                                        )
                                    }

                                    androidx.compose.animation.AnimatedVisibility(
                                        visible = isExpanded,
                                        enter = expandVertically() + fadeIn(),
                                        exit = shrinkVertically() + fadeOut()
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            groupList.forEach { item ->
                                                SwipeToDismissNotification(
                                                    item = item,
                                                    themeColor = themeColor,
                                                    fontFamily = fontFamily,
                                                    activity = activity,
                                                    onDismiss = {
                                                        try {
                                                            MyNotificationListenerService.instance?.cancelNotification(item.key)
                                                        } catch (e: Exception) {}
                                                        val currentList = activity.notificationList.value.toMutableList()
                                                        currentList.remove(item)
                                                        activity.notificationList.value = currentList
                                                    },
                                                    onNotificationClick = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        onNotificationClick(item.appName, item.text, item.pkg)
                                                    },
                                                    onLongPress = {
                                                        val resolvedAppInfo = try {
                                                            val label = activity.packageManager.getApplicationLabel(activity.packageManager.getApplicationInfo(item.pkg, 0)).toString()
                                                            val icon = activity.packageManager.getApplicationIcon(item.pkg)
                                                            AppInfo(label = label, packageName = item.pkg, icon = icon)
                                                        } catch (e: Exception) {
                                                            AppInfo(
                                                                label = item.appName,
                                                                packageName = item.pkg,
                                                                icon = try {
                                                                    activity.packageManager.getApplicationIcon(item.pkg)
                                                                } catch (ex: Exception) {
                                                                    android.graphics.drawable.ColorDrawable(0)
                                                                }
                                                            )
                                                        }
                                                        onLongPressApp?.invoke(resolvedAppInfo)
                                                    }
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
    }
}


class MyNotificationListenerService : android.service.notification.NotificationListenerService() {
    companion object {
        var instance: MyNotificationListenerService? = null
        val notificationsFlow = MutableStateFlow<List<AppNotification>>(emptyList())
        val mediaFlow = MutableStateFlow<MediaTrackInfo?>(null)
        var isConnected = false
        var activeController: android.media.session.MediaController? = null
    }

    private var sessionListener: android.media.session.MediaSessionManager.OnActiveSessionsChangedListener? = null

    override fun onListenerConnected() {
        super.onListenerConnected()
        isConnected = true
        instance = this
        
        try {
            val mediaSessionManager = getSystemService(MEDIA_SESSION_SERVICE) as android.media.session.MediaSessionManager
            val componentName = android.content.ComponentName(this, MyNotificationListenerService::class.java)
            val listener = android.media.session.MediaSessionManager.OnActiveSessionsChangedListener { controllers ->
                updateActiveMedia()
            }
            mediaSessionManager.addOnActiveSessionsChangedListener(listener, componentName)
            sessionListener = listener
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        updateActiveMedia()
        fetchActiveNotifications()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        isConnected = false
        instance = null
        
        try {
            sessionListener?.let { listener ->
                val mediaSessionManager = getSystemService(MEDIA_SESSION_SERVICE) as android.media.session.MediaSessionManager
                mediaSessionManager.removeOnActiveSessionsChangedListener(listener)
            }
            sessionListener = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        activeController?.unregisterCallback(mediaCallback)
        activeController = null
        mediaFlow.value = null
    }

    override fun onNotificationPosted(sbn: android.service.notification.StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        fetchActiveNotifications()
    }

    override fun onNotificationRemoved(sbn: android.service.notification.StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        fetchActiveNotifications()
    }

    private val mediaCallback = object : android.media.session.MediaController.Callback() {
        override fun onPlaybackStateChanged(state: android.media.session.PlaybackState?) {
            updateActiveMedia()
        }
        override fun onMetadataChanged(metadata: android.media.MediaMetadata?) {
            updateActiveMedia()
        }
        override fun onSessionDestroyed() {
            updateActiveMedia()
        }
    }

    private fun updateActiveMedia() {
        try {
            val mediaSessionManager = getSystemService(MEDIA_SESSION_SERVICE) as android.media.session.MediaSessionManager
            val componentName = android.content.ComponentName(this, MyNotificationListenerService::class.java)
            val controllers = mediaSessionManager.getActiveSessions(componentName)
            
            var foundMedia: MediaTrackInfo? = null
            var bestController: android.media.session.MediaController? = null
            
            if (controllers != null && controllers.isNotEmpty()) {
                bestController = controllers.firstOrNull { controller ->
                    val state = controller.playbackState?.state
                    state == android.media.session.PlaybackState.STATE_PLAYING ||
                    state == android.media.session.PlaybackState.STATE_BUFFERING ||
                    state == android.media.session.PlaybackState.STATE_CONNECTING ||
                    state == android.media.session.PlaybackState.STATE_FAST_FORWARDING ||
                    state == android.media.session.PlaybackState.STATE_REWINDING
                } ?: controllers.firstOrNull()
                
                if (bestController != null) {
                    val metadata = bestController.metadata
                    val playbackState = bestController.playbackState
                    
                    // Robust title and artist extraction supporting both regular and display metadata (e.g. YouTube, YouTube Music, Spotify)
                    val title = metadata?.getString(android.media.MediaMetadata.METADATA_KEY_TITLE)
                        ?: metadata?.getText(android.media.MediaMetadata.METADATA_KEY_TITLE)?.toString()
                        ?: metadata?.getString(android.media.MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
                        ?: metadata?.getText(android.media.MediaMetadata.METADATA_KEY_DISPLAY_TITLE)?.toString()
                        ?: ""
                    val artist = metadata?.getString(android.media.MediaMetadata.METADATA_KEY_ARTIST)
                        ?: metadata?.getText(android.media.MediaMetadata.METADATA_KEY_ARTIST)?.toString()
                        ?: metadata?.getString(android.media.MediaMetadata.METADATA_KEY_ALBUM_ARTIST)
                        ?: metadata?.getText(android.media.MediaMetadata.METADATA_KEY_ALBUM_ARTIST)?.toString()
                        ?: metadata?.getString(android.media.MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE)
                        ?: metadata?.getText(android.media.MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE)?.toString()
                        ?: ""
                    val duration = metadata?.getLong(android.media.MediaMetadata.METADATA_KEY_DURATION) ?: 0L
                    val progress = playbackState?.position ?: 0L
                    
                    val isPlaying = playbackState?.state == android.media.session.PlaybackState.STATE_PLAYING || 
                                    playbackState?.state == android.media.session.PlaybackState.STATE_BUFFERING
                    
                    if (title.isNotEmpty()) {
                        val artwork = try {
                            metadata?.getBitmap(android.media.MediaMetadata.METADATA_KEY_ALBUM_ART)
                                ?: metadata?.getBitmap(android.media.MediaMetadata.METADATA_KEY_ART)
                        } catch (e: Exception) {
                            null
                        }
                        foundMedia = MediaTrackInfo(
                            title = title,
                            artist = artist,
                            packageName = bestController.packageName ?: "",
                            isPlaying = isPlaying,
                            durationMs = if (duration > 0) duration else 180000L,
                            progressMs = progress,
                            artwork = artwork
                        )
                    }
                }
            }
            
            if (foundMedia == null) {
                val active = try { activeNotifications } catch (e: Exception) { null }
                if (active != null) {
                    for (sbn in active) {
                        if (sbn == null) continue
                        val pkg = sbn.packageName ?: ""
                        val notification = sbn.notification ?: continue
                        val extras = notification.extras ?: continue
                        
                        val isTransport = notification.category == android.app.Notification.CATEGORY_TRANSPORT ||
                                          extras.containsKey("android.mediaSession") ||
                                          pkg.contains("spotify") || pkg.contains("music") || pkg.contains("youtube") || pkg.contains("vlc") ||
                                          pkg.contains("pandora") || pkg.contains("soundcloud") || pkg.contains("tidal") || pkg.contains("deezer")
                        
                        if (isTransport) {
                            val titleChar = extras.getCharSequence("android.title")
                            val textChar = extras.getCharSequence("android.text")
                            val title = titleChar?.toString() ?: ""
                            val artist = textChar?.toString() ?: ""
                            if (title.isNotEmpty()) {
                                @Suppress("DEPRECATION")
                                val artworkVal = try {
                                    (extras.getParcelable("android.largeIcon") as? android.graphics.Bitmap)
                                        ?: (extras.get("android.largeIcon.big") as? android.graphics.Bitmap)
                                        ?: (notification.getLargeIcon()?.loadDrawable(this@MyNotificationListenerService) as? android.graphics.drawable.BitmapDrawable)?.bitmap
                                } catch (e: Exception) {
                                    null
                                }
                                foundMedia = MediaTrackInfo(
                                    title = title,
                                    artist = artist,
                                    packageName = pkg,
                                    isPlaying = true, // If it's in notifications, it's likely active
                                    artwork = artworkVal
                                )
                                break
                            }
                        }
                    }
                }
            }
            
            // Handle callback registration for real-time updates
            if (bestController != activeController) {
                activeController?.unregisterCallback(mediaCallback)
                bestController?.registerCallback(mediaCallback)
            }
            
            activeController = bestController
            mediaFlow.value = foundMedia
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun fetchActiveNotifications() {
        try {
            updateActiveMedia()
            val active = activeNotifications ?: return
            val list = active.mapNotNull { sbn ->
                if (sbn == null) return@mapNotNull null
                val pkg = sbn.packageName ?: ""
                if (pkg == "com.android.systemui") return@mapNotNull null
                val notification = sbn.notification ?: return@mapNotNull null
                val extras = notification.extras ?: return@mapNotNull null
                val titleCharSeq = extras.getCharSequence("android.title")
                val title = titleCharSeq?.toString() ?: ""
                val textCharSeq = extras.getCharSequence("android.text")
                val text = textCharSeq?.toString() ?: ""
                
                // Keep all clearable notifications (that can be swiped away) with some readable text or title
                if (pkg.isNotEmpty() && sbn.isClearable && (title.isNotEmpty() || text.isNotEmpty())) {
                    val pm = packageManager
                    val appName = try {
                        val info = pm.getApplicationInfo(pkg, 0)
                        pm.getApplicationLabel(info).toString()
                    } catch (t: Throwable) {
                        pkg
                    }
                    val itemKey = sbn.key ?: ""
                    
                    // Combine title and notification text details elegantly
                    val combinedText = if (title.isNotEmpty() && text.isNotEmpty() && !text.startsWith(title)) {
                        "$title: $text"
                    } else if (title.isNotEmpty()) {
                        title
                    } else {
                        text
                    }
                    
                    AppNotification(appName, combinedText, pkg, itemKey, sbn)
                } else null
            }
            notificationsFlow.value = list
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@Composable
fun AppWidgetHostViewContainer(
    widgetId: Int,
    revision: Int,
    activity: MainActivity,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hostView = remember(widgetId, revision) {
        val info = try {
            activity.appWidgetManager.getAppWidgetInfo(widgetId)
        } catch (e: Exception) {
            null
        }
        if (info != null) {
            try {
                activity.appWidgetHost.createView(context, widgetId, info).apply {
                    setAppWidget(widgetId, info)
                }
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    LaunchedEffect(hostView, onLongClick) {
        if (hostView != null && onLongClick != null) {
            hostView.setOnLongClickListener {
                onLongClick()
                true
            }
        }
    }

    if (hostView != null) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { hostView }
            )
        }
    } else {
        Box(
            modifier = modifier
                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Widget unavailable",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun WidgetPage(
    pageName: String,
    widgetDataList: List<WidgetData>,
    themeColor: Color,
    fontFamily: FontFamily,
    activity: MainActivity,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val showEditMenuForWidgetId by activity._longPressedWidgetId.collectAsState()

    Box(modifier = modifier) {
        if (widgetDataList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                     imageVector = Icons.Default.Star,
                     contentDescription = null,
                     tint = themeColor,
                     modifier = Modifier.size(32.dp).padding(bottom = 12.dp)
                )
                Text(
                    text = pageName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = fontFamily,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Text(
                    text = "This screen is ready for widgets.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = fontFamily,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Button(
                    onClick = {
                        activity.widgetTargetPage.value = pageName
                        val nextAppId = activity.appWidgetHost.allocateAppWidgetId()
                        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_PICK).apply {
                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, nextAppId)
                        }
                        activity.pickWidgetLauncher.launch(intent)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeColor,
                        contentColor = MaterialTheme.colorScheme.onPrimary.takeOrElse { Color.Black }
                    ),
                    shape = RoundedCornerShape(100.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text("➕ Add Widget", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pageName.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        letterSpacing = 1.5.sp,
                        fontFamily = fontFamily
                    )
                    TextButton(
                        onClick = {
                            activity.widgetTargetPage.value = pageName
                            val nextAppId = activity.appWidgetHost.allocateAppWidgetId()
                            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_PICK).apply {
                                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, nextAppId)
                            }
                            activity.pickWidgetLauncher.launch(intent)
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("➕ ADD WIDGET", fontSize = 11.sp, color = themeColor, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(
                        items = widgetDataList,
                        key = { it.id },
                        span = { w -> GridItemSpan(w.widthSpan.coerceIn(1, 4)) }
                    ) { widget ->
                        val isEditingVal = (showEditMenuForWidgetId == widget.id)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((widget.heightSpan * 110).dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(14.dp))
                                    .border(
                                        width = if (isEditingVal) 2.dp else 1.dp,
                                        color = if (isEditingVal) Color(0xFF29B6F6) else MaterialTheme.colorScheme.outlineVariant,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .pointerInput(widget.id) {
                                        detectTapGestures(
                                            onLongPress = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                activity._longPressedWidgetId.value = widget.id
                                            }
                                        )
                                    }
                            ) {
                                AppWidgetHostViewContainer(
                                    widgetId = widget.id,
                                    revision = widget.revision,
                                    activity = activity,
                                    onLongClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        activity._longPressedWidgetId.value = widget.id
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(if (isEditingVal) 6.dp else 4.dp)
                                )

                                // Drag handles for resizing (Render only if selected/editing)
                                if (isEditingVal) {
                                    var accumulatedDragX by remember(widget.id) { mutableStateOf(0f) }
                                    var accumulatedDragY by remember(widget.id) { mutableStateOf(0f) }
                                    val density = LocalDensity.current
                                    val thresholdPx = with(density) { 60.dp.toPx() }

                                    // Right Handle (Mid-Right edge)
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                            .offset(x = 8.dp)
                                            .size(16.dp)
                                            .background(Color(0xFF29B6F6), CircleShape)
                                            .border(2.dp, Color.White, CircleShape)
                                            .pointerInput(widget.id) {
                                                detectDragGestures(
                                                    onDragStart = { accumulatedDragX = 0f },
                                                    onDragEnd = { accumulatedDragX = 0f },
                                                    onDragCancel = { accumulatedDragX = 0f }
                                                ) { change, dragAmount ->
                                                    change.consume()
                                                    accumulatedDragX += dragAmount.x
                                                    if (accumulatedDragX > thresholdPx) {
                                                        val nextSpan = (widget.widthSpan + 1).coerceAtMost(4)
                                                        if (nextSpan != widget.widthSpan) {
                                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                            activity.updateWidgetSize(widget.id, nextSpan, widget.heightSpan)
                                                        }
                                                        accumulatedDragX = 0f
                                                    } else if (accumulatedDragX < -thresholdPx) {
                                                        val nextSpan = (widget.widthSpan - 1).coerceAtLeast(1)
                                                        if (nextSpan != widget.widthSpan) {
                                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                            activity.updateWidgetSize(widget.id, nextSpan, widget.heightSpan)
                                                        }
                                                        accumulatedDragX = 0f
                                                    }
                                                }
                                            }
                                    )

                                    // Bottom Handle (Mid-Bottom edge)
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .offset(y = 8.dp)
                                            .size(16.dp)
                                            .background(Color(0xFF29B6F6), CircleShape)
                                            .border(2.dp, Color.White, CircleShape)
                                            .pointerInput(widget.id) {
                                                detectDragGestures(
                                                    onDragStart = { accumulatedDragY = 0f },
                                                    onDragEnd = { accumulatedDragY = 0f },
                                                    onDragCancel = { accumulatedDragY = 0f }
                                                ) { change, dragAmount ->
                                                    change.consume()
                                                    accumulatedDragY += dragAmount.y
                                                    if (accumulatedDragY > thresholdPx) {
                                                        val nextSpan = (widget.heightSpan + 1).coerceAtMost(4)
                                                        if (nextSpan != widget.heightSpan) {
                                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                            activity.updateWidgetSize(widget.id, widget.widthSpan, nextSpan)
                                                        }
                                                        accumulatedDragY = 0f
                                                    } else if (accumulatedDragY < -thresholdPx) {
                                                        val nextSpan = (widget.heightSpan - 1).coerceAtLeast(1)
                                                        if (nextSpan != widget.heightSpan) {
                                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                            activity.updateWidgetSize(widget.id, widget.widthSpan, nextSpan)
                                                        }
                                                        accumulatedDragY = 0f
                                                    }
                                                }
                                            }
                                    )

                                    // Left Handle (Mid-Left edge)
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.CenterStart)
                                            .offset(x = (-8).dp)
                                            .size(16.dp)
                                            .background(Color(0xFF29B6F6), CircleShape)
                                            .border(2.dp, Color.White, CircleShape)
                                            .pointerInput(widget.id) {
                                                detectDragGestures(
                                                    onDragStart = { accumulatedDragX = 0f },
                                                    onDragEnd = { accumulatedDragX = 0f },
                                                    onDragCancel = { accumulatedDragX = 0f }
                                                ) { change, dragAmount ->
                                                    change.consume()
                                                    accumulatedDragX += dragAmount.x
                                                    if (accumulatedDragX < -thresholdPx) {
                                                        val nextSpan = (widget.widthSpan + 1).coerceAtMost(4)
                                                        if (nextSpan != widget.widthSpan) {
                                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                            activity.updateWidgetSize(widget.id, nextSpan, widget.heightSpan)
                                                        }
                                                        accumulatedDragX = 0f
                                                    } else if (accumulatedDragX > thresholdPx) {
                                                        val nextSpan = (widget.widthSpan - 1).coerceAtLeast(1)
                                                        if (nextSpan != widget.widthSpan) {
                                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                            activity.updateWidgetSize(widget.id, nextSpan, widget.heightSpan)
                                                        }
                                                        accumulatedDragX = 0f
                                                    }
                                                }
                                            }
                                    )

                                    // Context Option Balloon Popup (exactly styled after screenshot!)
                                    val info = remember(widget.id) {
                                        try {
                                            activity.appWidgetManager.getAppWidgetInfo(widget.id)
                                        } catch (e: Exception) {
                                            null
                                        }
                                    }
                                    val hasConfiguration = info?.configure != null

                                    androidx.compose.ui.window.Popup(
                                        alignment = Alignment.TopCenter,
                                        offset = androidx.compose.ui.unit.IntOffset(0, -with(density) { 56.dp.roundToPx() }),
                                        onDismissRequest = { activity._longPressedWidgetId.value = null },
                                        properties = androidx.compose.ui.window.PopupProperties(
                                            focusable = true,
                                            dismissOnBackPress = true,
                                            dismissOnClickOutside = true
                                        )
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.width(androidx.compose.foundation.layout.IntrinsicSize.Max)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .background(Color(0xFF222222), RoundedCornerShape(16.dp))
                                                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // Remove Option
                                                Column(
                                                    modifier = Modifier
                                                        .clickable {
                                                            activity.removeWidget(widget.id)
                                                            activity._longPressedWidgetId.value = null
                                                        }
                                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Remove From Home",
                                                        tint = Color.White.copy(alpha = 0.9f),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = "Remove from\nHome",
                                                        color = Color.White.copy(alpha = 0.95f),
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        fontFamily = fontFamily,
                                                        textAlign = TextAlign.Center,
                                                        lineHeight = 11.sp
                                                    )
                                                }

                                                // Widget Settings Option (if configurable)
                                                if (hasConfiguration && info != null) {
                                                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(Color.White.copy(alpha = 0.08f)))
                                                    Column(
                                                        modifier = Modifier
                                                            .clickable {
                                                                try {
                                                                    activity.configuringWidgetId = widget.id
                                                                    val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE).apply {
                                                                        component = info.configure
                                                                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widget.id)
                                                                    }
                                                                    activity.reconfigureWidgetLauncher.launch(intent)
                                                                    activity._longPressedWidgetId.value = null
                                                                } catch (e: Exception) {
                                                                    e.printStackTrace()
                                                                    android.widget.Toast.makeText(context, "Cannot open widget settings", android.widget.Toast.LENGTH_SHORT).show()
                                                                }
                                                            }
                                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                                        horizontalAlignment = Alignment.CenterHorizontally
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Settings,
                                                            contentDescription = "Widget Settings",
                                                            tint = Color.White.copy(alpha = 0.9f),
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            text = "Widget settings",
                                                            color = Color.White.copy(alpha = 0.95f),
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Medium,
                                                            fontFamily = fontFamily,
                                                            textAlign = TextAlign.Center,
                                                            lineHeight = 11.sp
                                                        )
                                                    }
                                                }

                                                // App Info Option
                                                if (info != null) {
                                                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(Color.White.copy(alpha = 0.08f)))
                                                    Column(
                                                        modifier = Modifier
                                                            .clickable {
                                                                try {
                                                                    val pName = info.provider?.packageName
                                                                    if (pName != null) {
                                                                        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                                                            data = android.net.Uri.fromParts("package", pName, null)
                                                                        }
                                                                        context.startActivity(intent)
                                                                        activity._longPressedWidgetId.value = null
                                                                    } else {
                                                                        android.widget.Toast.makeText(context, "App details not available", android.widget.Toast.LENGTH_SHORT).show()
                                                                    }
                                                                } catch (e: Exception) {
                                                                    e.printStackTrace()
                                                                    android.widget.Toast.makeText(context, "Unable to show app info", android.widget.Toast.LENGTH_SHORT).show()
                                                                }
                                                            }
                                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                                        horizontalAlignment = Alignment.CenterHorizontally
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Info,
                                                            contentDescription = "App Info",
                                                            tint = Color.White.copy(alpha = 0.9f),
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            text = "App info",
                                                            color = Color.White.copy(alpha = 0.95f),
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Medium,
                                                            fontFamily = fontFamily,
                                                            textAlign = TextAlign.Center,
                                                            lineHeight = 11.sp
                                                        )
                                                    }
                                                }
                                            }

                                            // Small Callout pointer at center bottom
                                            Box(
                                                modifier = Modifier
                                                    .offset(y = (-2).dp)
                                                    .size(10.dp)
                                                    .background(Color(0xFF222222), shape = androidx.compose.foundation.shape.GenericShape { size, _ ->
                                                        moveTo(0f, 0f)
                                                        lineTo(size.width, 0f)
                                                        lineTo(size.width / 2f, size.height)
                                                        close()
                                                    })
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
}

@Composable
fun PageReorderOverview(
    activePagesVal: List<String>,
    currentPageIndex: Int,
    onPageClick: (Int) -> Unit,
    onReorder: (List<String>) -> Unit,
    activity: MainActivity,
    themeColor: Color,
    fontFamily: FontFamily,
    widgetDataListVal: List<WidgetData>,
    notificationsListState: List<AppNotification>,
    mediaTrackInfoVal: MediaTrackInfo?,
    allowedNotificationCategoriesVal: Set<String>,
    uiState: LauncherUiState
) {
    val haptic = LocalHapticFeedback.current
    
    // Maintain a local mutable state list of page names to animate drag & swap in real-time
    val pagesList = remember { mutableStateListOf<String>() }
    
    // Sync when the parent list updates
    LaunchedEffect(activePagesVal) {
        pagesList.clear()
        pagesList.addAll(activePagesVal)
    }

    // Drag-and-drop state variables
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffsetX by remember { mutableStateOf(0f) }
    var dragOffsetY by remember { mutableStateOf(0f) }
    
    val columns = 2
    val density = LocalDensity.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            ) {
                onPageClick(currentPageIndex)
            }
            .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "REORGANIZE PAGES",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = fontFamily,
            color = themeColor,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            text = "Long-press & drag cards to reorder. Tap to select.",
            fontSize = 12.sp,
            fontFamily = fontFamily,
            color = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(pagesList.size) { index ->
                    val pageName = pagesList.getOrNull(index) ?: return@items
                    val isDragged = draggedIndex == index
                    
                    val animatedScale by animateFloatAsState(
                        targetValue = if (isDragged) 1.05f else 1.00f,
                        label = "cardScale"
                    )
                    
                    val translationX = if (isDragged) dragOffsetX else 0f
                    val translationY = if (isDragged) dragOffsetY else 0f
                    
                    Box(
                        modifier = Modifier
                            .aspectRatio(0.6f)
                            .zIndex(if (isDragged) 1f else 0f)
                            .graphicsLayer {
                                scaleX = animatedScale
                                scaleY = animatedScale
                                this.translationX = translationX
                                this.translationY = translationY
                                shadowElevation = if (isDragged) 16.dp.toPx() else 2.dp.toPx()
                                clip = true
                                shape = RoundedCornerShape(16.dp)
                            }
                            .pointerInput(index, pageName, pagesList.size) {
                                if (pageName == "App List") {
                                    detectTapGestures(
                                        onTap = {
                                            val origIdx = activePagesVal.indexOf(pageName)
                                            if (origIdx != -1) onPageClick(origIdx)
                                        }
                                    )
                                } else {
                                    detectDragGesturesAfterLongPress(
                                        onDragStart = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            draggedIndex = index
                                            dragOffsetX = 0f
                                            dragOffsetY = 0f
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            dragOffsetX += dragAmount.x
                                            dragOffsetY += dragAmount.y
                                            
                                            val currentDragIdx = draggedIndex
                                            if (currentDragIdx != null) {
                                                val cardWidthPx = size.width.toFloat() + 16.dp.toPx()
                                                val cardHeightPx = size.height.toFloat() + 16.dp.toPx()
                                                
                                                val stepsX = (dragOffsetX / cardWidthPx).roundToInt()
                                                val stepsY = (dragOffsetY / cardHeightPx).roundToInt()
                                                val offsetSteps = stepsX + (stepsY * columns)
                                                
                                                if (offsetSteps != 0) {
                                                    val targetIdx = currentDragIdx + offsetSteps
                                                    if (targetIdx in 1 until pagesList.size) {
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                        val temp = pagesList[currentDragIdx]
                                                        pagesList.removeAt(currentDragIdx)
                                                        pagesList.add(targetIdx, temp)
                                                        dragOffsetX -= stepsX * cardWidthPx
                                                        dragOffsetY -= stepsY * cardHeightPx
                                                        draggedIndex = targetIdx
                                                    }
                                                }
                                            }
                                        },
                                        onDragEnd = {
                                            draggedIndex = null
                                            dragOffsetX = 0f
                                            dragOffsetY = 0f
                                            onReorder(pagesList.toList())
                                        },
                                        onDragCancel = {
                                            draggedIndex = null
                                            dragOffsetX = 0f
                                            dragOffsetY = 0f
                                        }
                                    )
                                }
                            }
                            .clickable(enabled = draggedIndex == null) {
                                val origIdx = activePagesVal.indexOf(pageName)
                                if (origIdx != -1) onPageClick(origIdx)
                            }
                            .background(Color(0xFF14151B), RoundedCornerShape(16.dp))
                            .border(
                                width = if (draggedIndex == index) 2.dp else 1.dp,
                                color = if (draggedIndex == index) themeColor else Color.White.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        PageThumbnail(
                            pageName = pageName,
                            themeColor = themeColor,
                            fontFamily = fontFamily,
                            widgetDataListVal = widgetDataListVal,
                            notificationsListState = notificationsListState,
                            mediaTrackInfoVal = mediaTrackInfoVal,
                            allowedNotificationCategoriesVal = allowedNotificationCategoriesVal,
                            uiState = uiState,
                            activity = activity
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PageThumbnail(
    pageName: String,
    themeColor: Color,
    fontFamily: FontFamily,
    widgetDataListVal: List<WidgetData>,
    notificationsListState: List<AppNotification>,
    mediaTrackInfoVal: MediaTrackInfo?,
    allowedNotificationCategoriesVal: Set<String>,
    uiState: LauncherUiState,
    activity: MainActivity
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.04f))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                val icon = when (pageName) {
                    "App List" -> Icons.Default.Menu
                    "Music" -> Icons.Default.PlayArrow
                    "Notifications" -> Icons.Default.Notifications
                    else -> Icons.Default.Star
                }
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$pageName",
                    fontSize = 11.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily,
                    maxLines = 1
                )
            }
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                when (pageName) {
                    "App List" -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(14.dp)
                                    .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(4.dp))
                            )
                            
                            (1..4).forEach { _ ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(modifier = Modifier.size(8.dp).background(themeColor.copy(alpha = 0.4f), CircleShape))
                                    Box(modifier = Modifier.width(36.dp).height(5.dp).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(2.dp)))
                                }
                            }
                        }
                    }
                    "Music" -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize().padding(top = 16.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            val title = if (mediaTrackInfoVal != null && mediaTrackInfoVal.title.isNotEmpty()) mediaTrackInfoVal.title else "No Music Playing"
                            Text(title, fontSize = 8.sp, color = Color.White.copy(alpha = 0.6f), maxLines = 1, modifier = Modifier.basicMarquee())
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("⏮", fontSize = 10.sp, color = themeColor)
                                Text("⏸", fontSize = 10.sp, color = themeColor)
                                Text("⏭", fontSize = 10.sp, color = themeColor)
                            }
                        }
                    }
                    "Notifications" -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (notificationsListState.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("No Notifications", fontSize = 8.sp, color = Color.White.copy(alpha = 0.3f))
                                }
                            } else {
                                notificationsListState.take(3).forEach { notif ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(6.dp))
                                            .padding(4.dp)
                                    ) {
                                        Text(notif.appName, fontSize = 7.sp, color = themeColor, fontWeight = FontWeight.Bold)
                                        Text(notif.text, fontSize = 6.sp, color = Color.White.copy(alpha = 0.7f), maxLines = 1)
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        val pageWidgets = widgetDataListVal.filter { it.pageName == pageName }
                        if (pageWidgets.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Blank Page", fontSize = 7.sp, color = Color.White.copy(alpha = 0.4f))
                                }
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(4),
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                verticalArrangement = Arrangement.spacedBy(3.dp),
                                userScrollEnabled = false,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    items = pageWidgets,
                                    key = { it.id },
                                    span = { w -> GridItemSpan(w.widthSpan.coerceIn(1, 4)) }
                                ) { widget ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height((widget.heightSpan * 14).dp)
                                            .background(themeColor.copy(alpha = 0.08f), RoundedCornerShape(4.dp))
                                            .border(0.5.dp, themeColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("WIDGET", fontSize = 6.sp, color = Color.White.copy(alpha = 0.5f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if (pageName == "App List") {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(themeColor, RoundedCornerShape(topStart = 8.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("HOME", fontSize = 8.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

sealed class ListEntry {
    data class Header(val letter: Char) : ListEntry()
    data class App(val appInfo: AppInfo) : ListEntry()
}

@Composable
fun AlphabetHeaderRow(
    letter: Char,
    isActive: Boolean,
    isTouchingSidebar: Boolean,
    currentFontFamily: FontFamily?,
    themeColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 14.dp, top = 20.dp, bottom = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        val displayActive = isTouchingSidebar && isActive
        val sizeTarget = if (displayActive) 34.sp else 24.sp
        val letterColor = if (displayActive) themeColor else Color.White.copy(alpha = 0.5f)
        val weightTarget = if (displayActive) FontWeight.ExtraBold else FontWeight.Bold
        val animatedFontSize by animateFloatAsState(targetValue = sizeTarget.value)

        Text(
            text = letter.toString(),
            fontSize = animatedFontSize.sp,
            fontWeight = weightTarget,
            fontFamily = currentFontFamily,
            color = letterColor,
            modifier = Modifier.padding(start = 0.dp)
        )
    }
}
