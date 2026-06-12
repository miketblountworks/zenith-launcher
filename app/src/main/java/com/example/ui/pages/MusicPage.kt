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
    val context = LocalContext.current
    val audioManager = remember(context) { context.getSystemService(android.content.Context.AUDIO_SERVICE) as android.media.AudioManager }
    val activeController = MyNotificationListenerService.activeController
    val playbackInfo = activeController?.playbackInfo

    val maxVolume = playbackInfo?.maxVolume ?: audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC)
    var currentVolume by remember(playbackInfo?.currentVolume) {
        mutableIntStateOf(playbackInfo?.currentVolume ?: audioManager.getStreamVolume(android.media.AudioManager.STREAM_MUSIC))
    }
    val maxVolFloat = if (maxVolume <= 0) 15f else maxVolume.toFloat()
    val coercedVolume = currentVolume.toFloat().coerceIn(0f, maxVolFloat)

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
            // 1. Artwork with Integrated Metadata Overlay & Gesture Controls
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .aspectRatio(1.6f)
                    .graphicsLayer { scaleX = artworkScale.value; scaleY = artworkScale.value }
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragEnd = { /* Reset animations if needed */ }
                        ) { change, dragAmount ->
                            change.consume()
                            // Swiping down (dragAmount > 0) toggles Play/Pause
                            if (dragAmount > 35f) {
                                MediaController.dispatchMediaKey(activity, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                coroutineScope.launch {
                                    artworkScale.animateTo(0.95f, tween(100))
                                    artworkScale.animateTo(1.0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                }
                            }
                        }
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
                                Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.75f)), startY = 200f)
                            )
                        )
                        
                        // Metadata Text Overlay
                        Column(
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp, start = 16.dp, end = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = trackInfo.title,
                                fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White,
                                maxLines = 1, textAlign = TextAlign.Center, modifier = Modifier.basicMarquee()
                            )
                            Text(
                                text = trackInfo.artist,
                                fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White.copy(0.75f),
                                maxLines = 1, textAlign = TextAlign.Center, modifier = Modifier.basicMarquee()
                            )
                        }
                    }
                }
            }

            // 2. Volume Bar (Mockup Style)
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.VolumeUp, null, tint = contentColor.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                Slider(
                    value = coercedVolume,
                    onValueChange = { newValue ->
                        currentVolume = newValue.roundToInt()
                        if (activeController != null && playbackInfo != null) {
                            try { activeController.setVolumeTo(currentVolume, 0) } catch (_: Exception) {}
                        } else {
                            audioManager.setStreamVolume(android.media.AudioManager.STREAM_MUSIC, currentVolume, 0)
                        }
                    },
                    valueRange = 0f..maxVolFloat,
                    modifier = Modifier.weight(1f).height(32.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = themeColor,
                        inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                    ),
                    thumb = { Box(modifier = Modifier.size(10.dp).background(Color.White, CircleShape)) }
                )
            }

            // 3. Thick Pill Seek Bar (Mockup Style)
            val progressFrac = if (trackInfo.durationMs > 0) realProgressMs.toFloat() / trackInfo.durationMs else 0.45f
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .height(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
                    .clickable { /* Future seek interaction */ },
                contentAlignment = Alignment.CenterStart
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressFrac)
                        .fillMaxHeight()
                        .background(Brush.horizontalGradient(listOf(themeColor, themeColor.copy(alpha = 0.8f))))
                )
            }

            // 4. Timestamps
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val formatMs: (Long) -> String = { ms -> String.format(Locale.getDefault(), "%d:%02d", ms/60000, (ms%60000)/1000) }
                    Text(formatMs(realProgressMs), fontSize = 12.sp, color = contentColor.copy(alpha = 0.9f), fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                    Text(formatMs(trackInfo.durationMs), fontSize = 12.sp, color = contentColor.copy(alpha = 0.9f), fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                }
            }
        }
    }
}
