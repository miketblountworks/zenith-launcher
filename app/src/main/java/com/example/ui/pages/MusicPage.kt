package com.example.ui.pages

import android.provider.Settings
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
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
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
    val textShadow = Shadow(color = Color.Black.copy(alpha = 0.8f), offset = Offset(2f, 2f), blurRadius = 8f)

    var feedbackIcon by remember { mutableStateOf<androidx.compose.ui.graphics.vector.ImageVector?>(null) }
    val scaleAnim = remember { Animatable(0f) }
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(feedbackIcon) {
        if (feedbackIcon != null) {
            launch {
                scaleAnim.snapTo(0.6f)
                scaleAnim.animateTo(1.4f, animationSpec = tween(400, easing = FastOutSlowInEasing))
            }
            launch {
                alphaAnim.snapTo(1f)
                alphaAnim.animateTo(0f, animationSpec = tween(400, delayMillis = 100))
                feedbackIcon = null
            }
        }
    }

    if (trackInfo == null) {
        Box(modifier = modifier.fillMaxSize().padding(bottom = 120.dp), contentAlignment = Alignment.Center) {
            Card(
                modifier = Modifier.fillMaxWidth(0.92f).padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(32.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.MusicNote, null, tint = Color.White.copy(alpha = 0.25f), modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = if (!isNotifGranted) "Permission Required" else "No Music Playing",
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = fontFamily,
                        style = TextStyle(shadow = textShadow)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (!isNotifGranted) "Dextera requires Notification Access to detect music. Please grant it in settings."
                        else "Open any music app and play media to activate this screen.",
                        fontSize = 13.sp, color = Color.White.copy(alpha = 0.6f), fontFamily = fontFamily, textAlign = TextAlign.Center,
                        style = TextStyle(shadow = textShadow)
                    )
                }
            }
        }
        return
    }

    val formatMs: (Long) -> String = { ms ->
        val seconds = (ms / 1000) % 60
        val minutes = (ms / (1000 * 60)) % 60
        String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
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
    val context = LocalContext.current
    val audioManager = remember { context.getSystemService(android.content.Context.AUDIO_SERVICE) as android.media.AudioManager }
    val activeController = MyNotificationListenerService.activeController

    val maxVolume = remember { audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC) }
    val currentSystemVolume = remember { audioManager.getStreamVolume(android.media.AudioManager.STREAM_MUSIC) }
    var sliderPosition by remember { mutableFloatStateOf(currentSystemVolume.toFloat() / maxVolume.toFloat().coerceAtLeast(1f)) }

    Box(
        modifier = modifier.fillMaxSize().padding(bottom = 120.dp).clickable(
            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
            indication = null, onClick = {}
        ),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Artwork with Integrated Metadata Overlay & Gated Gesture Controls
            var hasToggled by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .aspectRatio(1.6f)
                    .graphicsLayer { scaleX = artworkScale.value; scaleY = artworkScale.value }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                try {
                                    activeController?.sessionActivity?.send()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragStart = { hasToggled = false },
                            onDragEnd = { hasToggled = false },
                            onDragCancel = { hasToggled = false }
                        ) { change, dragAmount ->
                            change.consume()
                            if (dragAmount > 30f && !hasToggled) {
                                feedbackIcon = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow
                                MediaController.dispatchMediaKey(activity, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                coroutineScope.launch {
                                    artworkScale.animateTo(0.95f, tween(100))
                                    artworkScale.animateTo(1.0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                }
                                hasToggled = true
                            }
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                try {
                                    activeController?.sessionActivity?.send()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        var totalDragX = 0f
                        var triggered = false
                        detectHorizontalDragGestures(
                            onDragStart = { totalDragX = 0f; triggered = false },
                            onDragEnd = { totalDragX = 0f; triggered = false },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                if (!triggered) {
                                    totalDragX += dragAmount
                                    if (abs(totalDragX) > 80f) {
                                        triggered = true
                                        if (totalDragX > 0) {
                                            feedbackIcon = Icons.Rounded.SkipNext
                                            MediaController.dispatchMediaKey(activity, KeyEvent.KEYCODE_MEDIA_NEXT)
                                        } else {
                                            feedbackIcon = Icons.Rounded.SkipPrevious
                                            MediaController.dispatchMediaKey(activity, KeyEvent.KEYCODE_MEDIA_PREVIOUS)
                                        }
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
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(
                            elevation = 20.dp,
                            shape = RoundedCornerShape(32.dp),
                            ambientColor = Color.Black,
                            spotColor = Color.Black.copy(alpha = 0.5f)
                        ),
                    shape = RoundedCornerShape(32.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (trackInfo.artwork != null) {
                            Image(bitmap = trackInfo.artwork.asImageBitmap(), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        } else {
                            Box(Modifier.fillMaxSize().background(Brush.radialGradient(listOf(themeColor.copy(0.4f), Color(0xFF13151D)))), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.MusicNote, null, tint = themeColor, modifier = Modifier.size(80.dp))
                            }
                        }
                        
                        Box(
                            modifier = Modifier.fillMaxSize().background(
                                Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.75f)), startY = 200f)
                            )
                        )

                        // 1. Timestamp Overlay (Top-Left)
                        Text(
                            text = "${formatMs(realProgressMs)} / ${formatMs(trackInfo.durationMs)}",
                            modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium.copy(
                                shadow = Shadow(color = Color.Black.copy(alpha = 0.8f), offset = Offset(2f, 2f), blurRadius = 4f)
                            )
                        )

                        // 2. Simulated Visualizer (Top-Right)
                        Row(
                            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).height(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            val infiniteTransition = rememberInfiniteTransition(label = "visualizer")
                            
                            repeat(3) { index ->
                                val heightScale by if (isPlaying) {
                                    infiniteTransition.animateFloat(
                                        initialValue = 0.3f,
                                        targetValue = 1f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(
                                                durationMillis = when(index) {
                                                    0 -> 450
                                                    1 -> 300
                                                    else -> 600
                                                },
                                                easing = FastOutSlowInEasing
                                            ),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "bar_$index"
                                    )
                                } else {
                                    remember { mutableStateOf(0.3f) }
                                }
                                
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .fillMaxHeight(heightScale)
                                        .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(1.dp))
                                )
                            }
                        }

                        // Playback Action Visual Feedback
                        if (feedbackIcon != null) {
                            Icon(
                                imageVector = feedbackIcon!!,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(72.dp)
                                    .graphicsLayer {
                                        scaleX = scaleAnim.value
                                        scaleY = scaleAnim.value
                                        alpha = alphaAnim.value
                                    }
                                    .shadow(
                                        elevation = 8.dp,
                                        shape = CircleShape,
                                        ambientColor = Color.Black,
                                        spotColor = Color.Black
                                    )
                            )
                        }
                        
                        Column(
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp, start = 16.dp, end = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = trackInfo.title,
                                fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White,
                                style = TextStyle(shadow = textShadow),
                                maxLines = 1, textAlign = TextAlign.Center, modifier = Modifier.basicMarquee()
                            )
                            Text(
                                text = trackInfo.artist,
                                fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White.copy(0.75f),
                                style = TextStyle(shadow = textShadow),
                                maxLines = 1, textAlign = TextAlign.Center, modifier = Modifier.basicMarquee()
                            )
                        }
                    }
                }
            }

            // 2. Volume Control
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.VolumeUp, null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                Slider(
                    value = sliderPosition,
                    onValueChange = { newValue ->
                        sliderPosition = newValue
                        val hardwareVolume = (newValue * maxVolume).roundToInt()
                        audioManager.setStreamVolume(android.media.AudioManager.STREAM_MUSIC, hardwareVolume, 0)
                    },
                    valueRange = 0f..1f,
                    modifier = Modifier.weight(1f).height(32.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = themeColor,
                        inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatMs(realProgressMs),
                    style = MaterialTheme.typography.labelMedium.copy(shadow = textShadow),
                    color = Color.White.copy(alpha = 0.7f),
                    fontFamily = fontFamily
                )
                Text(
                    text = formatMs(trackInfo.durationMs),
                    style = MaterialTheme.typography.labelMedium.copy(shadow = textShadow),
                    color = Color.White.copy(alpha = 0.7f),
                    fontFamily = fontFamily
                )
            }

            // 4. Playback Seek Bar
            var sliderValue by remember(realProgressMs) { mutableFloatStateOf(realProgressMs.toFloat()) }
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                onValueChangeFinished = {
                    activeController?.transportControls?.seekTo(sliderValue.toLong())
                },
                valueRange = 0f..(trackInfo.durationMs.toFloat().coerceAtLeast(1f)),
                modifier = Modifier.fillMaxWidth(0.9f),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = themeColor,
                    inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                )
            )
        }
    }
}
