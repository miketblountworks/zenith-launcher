package com.example.ui.pages

import android.view.KeyEvent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainActivity
import com.example.media.MediaController
import com.example.service.MyNotificationListenerService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPage(
    themeColor: Color,
    fontFamily: FontFamily,
    activity: MainActivity,
    modifier: Modifier = Modifier,
    contentColor: Color = Color.White,
    shadowColor: Color = Color.Black.copy(alpha = 0.6f)
) {
    val mediaTrackInfoVal by activity.mediaTrackInfo.collectAsState()
    val trackInfo = mediaTrackInfoVal
    val isNotifGranted by activity.isNotificationPermissionGranted.collectAsState()

    if (trackInfo == null) {
        // Precise Empty State
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Card(
                modifier = Modifier.fillMaxWidth(0.92f).padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(32.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.MusicNote, null, tint = contentColor.copy(alpha = 0.25f), modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = if (!isNotifGranted) "Permission Required" else "No Music Playing",
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, color = contentColor, fontFamily = fontFamily,
                        style = TextStyle(shadow = Shadow(shadowColor, Offset(1f, 2f), 4f))
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (!isNotifGranted) "Dextera requires Notification Access to detect music. Please grant it in settings."
                        else "Open any music app and play media to activate this screen.",
                        fontSize = 13.sp, color = contentColor.copy(alpha = 0.6f), fontFamily = fontFamily, textAlign = TextAlign.Center,
                        style = TextStyle(shadow = Shadow(shadowColor, Offset(1f, 1f), 2f))
                    )
                }
            }
        }
        return
    }

    val isPlaying = trackInfo.isPlaying
    var realProgressMs by remember(trackInfo.title, trackInfo.isPlaying) { mutableLongStateOf(trackInfo.progressMs) }
    
    LaunchedEffect(isPlaying, trackInfo.progressMs) { realProgressMs = trackInfo.progressMs }
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                delay(1000)
                realProgressMs = (realProgressMs + 1000L).coerceAtMost(trackInfo.durationMs)
            }
        }
    }

    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val artworkScale = remember { Animatable(1f) }

    Box(
        modifier = modifier.fillMaxSize().clickable(
            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
            indication = null, onClick = {}
        ),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Artwork with Integrated Metadata Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .aspectRatio(1.6f)
                    .graphicsLayer { scaleX = artworkScale.value; scaleY = artworkScale.value }
                    .pointerInput(isPlaying) {
                        detectTapGestures(
                            onTap = {
                                MediaController.dispatchMediaKey(activity, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
                                coroutineScope.launch {
                                    artworkScale.animateTo(0.95f, tween(100))
                                    artworkScale.animateTo(1.0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                }
                            },
                            onLongPress = { haptic.performHapticFeedback(HapticFeedbackType.LongPress) }
                        )
                    }
                    .pointerInput(Unit) {
                        var totalDragX = 0f
                        var triggered = false
                        detectHorizontalDragGestures(
                            onDragStart = { totalDragX = 0f; triggered = false },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                if (!triggered) {
                                    totalDragX += dragAmount
                                    if (abs(totalDragX) > 80f) {
                                        triggered = true
                                        if (totalDragX > 0) MediaController.dispatchMediaKey(activity, KeyEvent.KEYCODE_MEDIA_NEXT)
                                        else MediaController.dispatchMediaKey(activity, KeyEvent.KEYCODE_MEDIA_PREVIOUS)
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        coroutineScope.launch {
                                            artworkScale.animateTo(0.92f, tween(100))
                                            artworkScale.animateTo(1.0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                        }
                                    }
                                }
                            }
                        )
                    }
            ) {
                Card(
                    modifier = Modifier.fillMaxSize().shadow(12.dp, RoundedCornerShape(32.dp)),
                    shape = RoundedCornerShape(32.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (trackInfo.artwork != null) {
                            Image(bitmap = trackInfo.artwork.asImageBitmap(), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        } else {
                            Box(Modifier.fillMaxSize().background(Brush.radialGradient(listOf(themeColor.copy(0.4f), Color(0xFF13151D)))), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.MusicNote, null, tint = themeColor, modifier = Modifier.size(80.dp))
                            }
                        }
                        
                        // Metadata Overlay Scrim
                        Box(
                            modifier = Modifier.fillMaxSize().background(
                                Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.7f)), startY = 300f)
                            )
                        )
                        
                        // Metadata Text Overlay (Strict Mockup Alignment)
                        Column(
                            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = trackInfo.title,
                                fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White,
                                maxLines = 1, textAlign = TextAlign.Center, modifier = Modifier.basicMarquee()
                            )
                            Text(
                                text = trackInfo.artist,
                                fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White.copy(0.8f),
                                maxLines = 1, textAlign = TextAlign.Center, modifier = Modifier.basicMarquee()
                            )
                        }
                    }
                }
            }

            // 2. Playback Control Row (Strict Component Placement)
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { MediaController.dispatchMediaKey(activity, KeyEvent.KEYCODE_MEDIA_PREVIOUS) }) {
                    Icon(Icons.Default.SkipPrevious, null, tint = contentColor, modifier = Modifier.size(32.dp))
                }

                // Glowy Center Play Button
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(themeColor.copy(alpha = 0.2f), CircleShape)
                        .shadow(16.dp, CircleShape, spotColor = themeColor, ambientColor = themeColor)
                        .clickable { MediaController.dispatchMediaKey(activity, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) },
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.size(54.dp).background(themeColor, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }

                IconButton(onClick = { MediaController.dispatchMediaKey(activity, KeyEvent.KEYCODE_MEDIA_NEXT) }) {
                    Icon(Icons.Default.SkipNext, null, tint = contentColor, modifier = Modifier.size(32.dp))
                }
            }

            // 3. Custom Seek Bar (Strict Visual Reference)
            Column(modifier = Modifier.fillMaxWidth(0.92f)) {
                val progressFrac = if (trackInfo.durationMs > 0) realProgressMs.toFloat() / trackInfo.durationMs else 0f
                
                Box(modifier = Modifier.fillMaxWidth().height(48.dp), contentAlignment = Alignment.Center) {
                    // Track
                    Canvas(modifier = Modifier.fillMaxWidth().height(4.dp)) {
                        drawRoundRect(Color.White.copy(0.15f), size = size, cornerRadius = CornerRadius(2.dp.toPx()))
                        drawRoundRect(themeColor, size = size.copy(width = size.width * progressFrac), cornerRadius = CornerRadius(2.dp.toPx()))
                    }
                    
                    // Spherical Thumb Overlay
                    Slider(
                        value = progressFrac,
                        onValueChange = { 
                            val target = (it * trackInfo.durationMs).toLong()
                            MyNotificationListenerService.activeController?.transportControls?.seekTo(target)
                        },
                        thumb = {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        Brush.radialGradient(listOf(Color.White, themeColor, themeColor.copy(0.8f))),
                                        CircleShape
                                    )
                                    .shadow(6.dp, CircleShape)
                            )
                        },
                        colors = SliderDefaults.colors(thumbColor = Color.Transparent, activeTrackColor = Color.Transparent, inactiveTrackColor = Color.Transparent)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    val formatMs: (Long) -> String = { ms -> String.format(Locale.getDefault(), "%02d:%02d", ms/60000, (ms%60000)/1000) }
                    Text(formatMs(realProgressMs), fontSize = 11.sp, color = contentColor.copy(0.6f), fontFamily = fontFamily, style = TextStyle(shadow = Shadow(shadowColor, Offset(1f, 1f), 2f)))
                    Text(formatMs(trackInfo.durationMs), fontSize = 11.sp, color = contentColor.copy(0.6f), fontFamily = fontFamily, style = TextStyle(shadow = Shadow(shadowColor, Offset(1f, 1f), 2f)))
                }
            }

            // 4. Session & Waveform Card (Strict Bottom Placement)
            Card(
                modifier = Modifier.fillMaxWidth(0.98f).height(120.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.45f))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val sessionTitle = remember(trackInfo.packageName) {
                            try {
                                activity.packageManager.getApplicationLabel(activity.packageManager.getApplicationInfo(trackInfo.packageName, 0)).toString()
                            } catch (_: Exception) {
                                "Media Session"
                            }
                        }
                        Text(sessionTitle, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = fontFamily)
                        Text("• Active Streaming Session", fontSize = 12.sp, color = Color.White.copy(0.6f), fontFamily = fontFamily)
                    }
                    
                    // Animated Waveform
                    WaveformVisualizer(themeColor, isPlaying)
                }
            }
        }
    }
}

@Composable
fun WaveformVisualizer(color: Color, isPlaying: Boolean) {
    val phaseTransition = rememberInfiniteTransition(label = "waveform")
    val phase by phaseTransition.animateFloat(0f, 2f * PI.toFloat(), infiniteRepeatable(tween(1500, easing = LinearEasing)), label = "phase")
    
    val barCount = 40
    val heights = remember { List(barCount) { 0.3f + Random.nextFloat() * 0.6f } }
    
    Box(Modifier.fillMaxWidth().height(40.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val barWidth = (size.width / barCount) * 0.6f
            val spacing = (size.width / barCount) * 0.4f
            for (i in 0 until barCount) {
                val animMod = if (isPlaying) cos(i * 0.5f + phase) * 0.2f else 0f
                val h = (heights[i] + animMod).coerceIn(0.1f, 1.0f) * size.height
                val x = i * (barWidth + spacing)
                val y = (size.height - h) / 2f
                drawRoundRect(color, Offset(x, y), Size(barWidth, h), CornerRadius(barWidth/2))
            }
        }
    }
}
