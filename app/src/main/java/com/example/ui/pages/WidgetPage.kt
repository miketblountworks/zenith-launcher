package com.example.ui.pages

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.MainActivity
import com.example.model.WidgetData

@Composable
fun AppWidgetHostViewContainer(
    widgetId: Int,
    revision: Int,
    activity: MainActivity,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val hostView = remember(widgetId, revision) {
        val info = try {
            activity.appWidgetManager.getAppWidgetInfo(widgetId)
        } catch (_: Exception) {
            null
        }
        if (info != null) {
            try {
                activity.appWidgetHost.createView(context, widgetId, info).apply {
                    setAppWidget(widgetId, info)
                }
            } catch (_: Exception) {
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
                                    .background(Color.Transparent, RoundedCornerShape(14.dp))
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
                                        } catch (_: Exception) {
                                            null
                                        }
                                    }
                                    val hasConfiguration = info?.configure != null

                                    Popup(
                                        alignment = Alignment.TopCenter,
                                        offset = IntOffset(0, -with(density) { 56.dp.roundToPx() }),
                                        onDismissRequest = { activity._longPressedWidgetId.value = null },
                                        properties = PopupProperties(
                                            focusable = true,
                                            dismissOnBackPress = true,
                                            dismissOnClickOutside = true
                                        )
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.width(IntrinsicSize.Max)
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
                                                                    Toast.makeText(context, "Cannot open widget settings", Toast.LENGTH_SHORT).show()
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
                                                                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                                                            data = android.net.Uri.fromParts("package", pName, null)
                                                                        }
                                                                        context.startActivity(intent)
                                                                        activity._longPressedWidgetId.value = null
                                                                    } else {
                                                                        Toast.makeText(context, "App details not available", Toast.LENGTH_SHORT).show()
                                                                    }
                                                                } catch (e: Exception) {
                                                                    e.printStackTrace()
                                                                    Toast.makeText(context, "Unable to show app info", Toast.LENGTH_SHORT).show()
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
                                                    .background(Color(0xFF222222), shape = GenericShape { size, _ ->
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
