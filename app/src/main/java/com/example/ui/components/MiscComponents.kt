package com.example.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.UserEntity
import com.example.model.AppInfo
import com.example.utils.evaluateMath
import kotlinx.coroutines.delay

@Composable
fun WallpaperBackground(wallpaper: String, bingWallpaperUrl: String, blurEnabled: Boolean = false) {
    val blurModifier = if (blurEnabled) Modifier.blur(16.dp, 16.dp) else Modifier
    Box(modifier = Modifier.fillMaxSize().then(blurModifier)) {
        val context = LocalContext.current

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
                            // Do not draw anything, let the system wallpaper show through
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
fun SwipableWidgetStack(fontFamily: FontFamily, primaryColor: Color) {
    var activeIdx by remember { mutableIntStateOf(0) }
    
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
    var timerSeconds by remember { mutableIntStateOf(5) }
    LaunchedEffect(Unit) {
        while (true) {
            if (timerSeconds > 0) {
                delay(1000)
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
                        delay(2000)
                        scale = 0.9f
                        delay(2000)
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
fun CategoryHeader(title: String, color: Color, fontFamily: FontFamily) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        fontFamily = fontFamily,
        letterSpacing = 1.5.sp,
        style = TextStyle(
            shadow = Shadow(
                color = Color.Black.copy(alpha = 0.6f),
                offset = Offset(1f, 2f),
                blurRadius = 4f
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun UserProfileScreen(user: UserEntity, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {} // Block touches
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
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
                    Text(
                        text = "$folderName Popup Folder", 
                        fontSize = 16.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = themeColor, 
                        fontFamily = fontFamily,
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.6f),
                                offset = Offset(1f, 2f),
                                blurRadius = 4f
                            )
                        )
                    )
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
                                    overflow = TextOverflow.Ellipsis,
                                    fontFamily = fontFamily,
                                    textAlign = TextAlign.Center,
                                    style = TextStyle(
                                        shadow = Shadow(
                                            color = Color.Black.copy(alpha = 0.6f),
                                            offset = Offset(1f, 2f),
                                            blurRadius = 4f
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
