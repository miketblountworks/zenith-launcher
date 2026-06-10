package com.example.ui.pages

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.KeyEvent
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainActivity
import com.example.media.MediaController
import com.example.service.MyNotificationListenerService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.PI
import kotlin.math.roundToInt

@Composable
fun MusicPage(
    themeColor: Color,
    fontFamily: FontFamily,
    activity: MainActivity,
    modifier: Modifier = Modifier
) {
    val mediaTrackInfoVal by activity.mediaTrackInfo.collectAsState()
    val trackInfo = mediaTrackInfoVal
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
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
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
                                    val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    activity.startActivity(intent)
                                } catch (_: Exception) {
                                    Toast.makeText(activity, "Settings could not be opened", Toast.LENGTH_SHORT).show()
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
        mutableLongStateOf(trackInfo.progressMs) 
    }
    
    LaunchedEffect(isPlaying, trackInfo.progressMs) {
        realProgressMs = trackInfo.progressMs
    }
    
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                delay(1000)
                realProgressMs = (realProgressMs + 1000L).coerceAtMost(trackInfo.durationMs)
            }
        }
    }
    
    val haptic = LocalHapticFeedback.current
    
    val phaseTransition = rememberInfiniteTransition(label = "waveform_phase_shift")
    val peakShift by phaseTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
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
                mutableIntStateOf(initialVal)
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
                        MediaController.dispatchMediaKey(context, KeyEvent.KEYCODE_MEDIA_PREVIOUS)
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
                val artworkScale = remember { Animatable(1f) }
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
                                                MediaController.dispatchMediaKey(context, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
                                                
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
                                                    delay(800)
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

                    Box {
                        androidx.compose.animation.AnimatedVisibility(
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
                }

                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        MediaController.dispatchMediaKey(context, KeyEvent.KEYCODE_MEDIA_NEXT)
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
                        } catch (_: Exception) {
                            // ignore
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
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
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
                        String.format(Locale.getDefault(), "%d:%02d", min, sec)
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        val unplayedColor = Color.White.copy(alpha = 0.15f)
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .padding(horizontal = 4.dp)
                        ) {
                            val localDensity = LocalDensity.current
                            val canvasWidth = constraints.maxWidth.toFloat()
                            val barCount = 36
                            val spacingPx = with(localDensity) { 4.dp.toPx() }
                            val rawBarWidth = (canvasWidth - (spacingPx * (barCount - 1))) / barCount
                            val barWidth = rawBarWidth.coerceAtLeast(3f)

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(trackInfo) {
                                        detectTapGestures { offset ->
                                            val fraction = (offset.x / this.size.width).coerceIn(0f, 1f)
                                            val targetProgress = (fraction * trackInfo.durationMs).toLong()
                                            realProgressMs = targetProgress
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            try {
                                                MyNotificationListenerService.activeController?.transportControls?.seekTo(targetProgress)
                                            } catch (_: Exception) {
                                                // ignore
                                            }
                                        }
                                    }
                                    .pointerInput(trackInfo) {
                                        var lastPercentagePoint = -1
                                        detectHorizontalDragGestures(
                                            onHorizontalDrag = { change, _ ->
                                                change.consume()
                                                val fraction = (change.position.x / this.size.width).coerceIn(0f, 1f)
                                                val targetProgress = (fraction * trackInfo.durationMs).toLong()
                                                realProgressMs = targetProgress
                                                val percentageInt = (fraction * 24).toInt()
                                                if (percentageInt != lastPercentagePoint) {
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    lastPercentagePoint = percentageInt
                                                }
                                                try {
                                                    MyNotificationListenerService.activeController?.transportControls?.seekTo(targetProgress)
                                                } catch (_: Exception) {
                                                    // ignore
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
                                MediaController.dispatchMediaKey(context, KeyEvent.KEYCODE_MEDIA_PREVIOUS)
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
                                MediaController.dispatchMediaKey(context, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
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
                                MediaController.dispatchMediaKey(context, KeyEvent.KEYCODE_MEDIA_NEXT)
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
                    androidx.compose.animation.AnimatedVisibility(
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
