package com.example.ui.settings

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.example.MainActivity

@Composable
fun SettingsPanel(onClose: () -> Unit, themeColor: Color, fontFamily: FontFamily, activity: MainActivity, initialCategory: String? = null) {
    val currentWallpaper by activity.selectedWallpaper.collectAsState()
    val clockStyleVal by activity.clockStyle.collectAsState()
    val selectedFontVal by activity.selectedFont.collectAsState()
    val iconPackVal by activity.iconPack.collectAsState()
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
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                                                            activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putString("wallpaper", wall) }
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
                                                            activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putString("wallpaper", wall) }
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
                                                                activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putString("wallpaper", wall) }
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
                                                                } catch (_: Exception) {}
                                                            }
                                                            activity.selectedWallpaper.value = wall
                                                            activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putString("wallpaper", wall) }
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
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putBoolean("wallpaper_blur", it) }
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
                                                        Box(modifier = Modifier.weight(1f).border(1.dp, if (isSel) themeColor else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp)).background(if (isSel) themeColor.copy(alpha = 0.15f) else Color.Transparent).clickable { activity.clockStyle.value = style; activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putString("clock_style", style) } }.padding(10.dp), contentAlignment = Alignment.Center) {
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
                                                        Box(modifier = Modifier.weight(1f).border(1.dp, if (isSel) themeColor else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp)).background(if (isSel) themeColor.copy(alpha = 0.15f) else Color.Transparent).clickable { activity.iconPack.value = pack; activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putString("icon_pack", pack) } }.padding(10.dp), contentAlignment = Alignment.Center) {
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
                                                        Box(modifier = Modifier.weight(1f).border(1.dp, if (isSel) themeColor else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp)).background(if (isSel) themeColor.copy(alpha = 0.15f) else Color.Transparent).clickable { activity.selectedFont.value = fnt; activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putString("font", fnt) } }.padding(10.dp), contentAlignment = Alignment.Center) {
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
                                                            activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putBoolean("use_24_hour", v) }
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
                                                            activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putBoolean("use_fahrenheit", v) }
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
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putBoolean("dynamic_icon_color", it) }
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
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putBoolean("material_you", it) } 
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
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putBoolean("perf_overdraw_elimination", v) }
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
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putBoolean("perf_async_image_decoding", v) }
                                                    }
                                                ),
                                                java.util.UUID.randomUUID().toString() to Triple(
                                                    "Shenandoah/ZGC Garbage Collector",
                                                    "Appends ultra-low latency -XX:+UseZGC options to the VM launch parameters block, restricting heap pauses to < 2ms.",
                                                    jvmZgcVal to { v: Boolean ->
                                                        activity.jvmZgcConfiguration.value = v
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putBoolean("perf_jvm_zgc_config", v) }
                                                    }
                                                ),
                                                java.util.UUID.randomUUID().toString() to Triple(
                                                    "Thermal-Adaptive FPS Governor",
                                                    "Actively listens to the Android Hardware Thermal API. Safely downscales refresh rates to 90Hz/60Hz on THERMAL_STATUS_SEVERE to protect SoC silicon.",
                                                    adaptiveFpsVal to { v: Boolean ->
                                                        activity.adaptiveFpsThermal.value = v
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putBoolean("perf_adaptive_fps_thermal", v) }
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
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putBoolean("perf_synthetic_click_delay", v) }
                                                    }
                                                ),
                                                java.util.UUID.randomUUID().toString() to Triple(
                                                    "240Hz Input Event Interpolation",
                                                    "Queries motion vector histories via getHistoricalX/Y to capture sub-frame touch paths for smooth viewport adjustments.",
                                                    touchInterpVal to { v: Boolean ->
                                                        activity.touchInterpolation.value = v
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putBoolean("perf_touch_interpolation", v) }
                                                    }
                                                ),
                                                java.util.UUID.randomUUID().toString() to Triple(
                                                    "Big.LITTLE Thread Affinity Mapping",
                                                    "Pins the main game loop and JVM renderer threads exclusively to ARM Prime cores, and keeps low-load helper threads off them.",
                                                    cpuThreadVal to { v: Boolean ->
                                                        activity.cpuThreadAffinity.value = v
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putBoolean("perf_cpu_thread_affinity", v) }
                                                    }
                                                ),
                                                java.util.UUID.randomUUID().toString() to Triple(
                                                    "Asynchronous VFS & Memory Mapping",
                                                    "Bypasses Storage Access Framework (SAF) latency by directly mapping .jar resource packs and game zip files into RAM using native mmap POSIX.",
                                                    asyncVfsVal to { v: Boolean ->
                                                        activity.asyncVfsMmap.value = v
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putBoolean("perf_async_vfs_mmap", v) }
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
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putBoolean("gestures_enabled", it) } 
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
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putBoolean("declutter_mode", it) } 
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
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putBoolean("notification_summary", it) } 
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
                                                                            Toast.makeText(activity, "Must keep at least one category in summary", Toast.LENGTH_SHORT).show()
                                                                        }
                                                                    } else {
                                                                        nextSet.add(cat)
                                                                    }
                                                                    activity.allowedNotificationCategories.value = nextSet
                                                                    activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE)
                                                                        .edit {
                                                                            putStringSet("allowed_notification_categories", nextSet)
                                                                        }
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
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putInt("usage_breaker_min", v) } 
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
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putStringSet("hidden_packages", now) }
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
                                                        activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putBoolean("categorise_by_usage", it) } 
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
                                                                    activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putInt("usage_limit_count", v) } 
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
                                                            activity.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE).edit { putInt("search_results_limit", v) }
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
                                                        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                        }
                                                        activity.startActivity(intent)
                                                    } catch (_: Exception) {
                                                        Toast.makeText(activity, "Settings could not be opened automatically", Toast.LENGTH_SHORT).show()
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

                                    // 3. Usage Access Card
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        val isUsageGranted = activity.hasUsageStatsPermission(activity)
                                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Text("Usage Statistics Access", fontSize = 15.sp, color = themeColor, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                                            Text(
                                                text = "Current Status: " + (if (isUsageGranted) "✅ Granted" else "❌ Missing"),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = fontFamily,
                                                color = if (isUsageGranted) themeColor else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text("Enables automatic categorization of your most used apps at the top of your list based on real usage patterns.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = fontFamily)
                                            Button(
                                                onClick = {
                                                    try {
                                                        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                        }
                                                        activity.startActivity(intent)
                                                    } catch (_: Exception) {
                                                        Toast.makeText(activity, "Settings could not be opened automatically", Toast.LENGTH_SHORT).show()
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text(if (isUsageGranted) "Configure in Settings" else "Grant Usage Access", fontSize = 13.sp, color = MaterialTheme.colorScheme.onPrimary.takeOrElse { Color.Black }, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
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
                            activeList.forEachIndexed { _, page ->
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
                                Toast.makeText(activity, "Invalid or duplicate page name", Toast.LENGTH_SHORT).show()
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
