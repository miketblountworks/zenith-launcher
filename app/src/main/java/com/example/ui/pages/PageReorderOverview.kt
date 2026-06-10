package com.example.ui.pages

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.MainActivity
import com.example.model.AppNotification
import com.example.model.LauncherUiState
import com.example.model.MediaTrackInfo
import com.example.model.WidgetData
import kotlin.math.roundToInt

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
    val _allowedNotificationCategoriesVal = allowedNotificationCategoriesVal // suppress unused
    val _uiState = uiState // suppress unused

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
                    text = pageName,
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
    
    val pagesList = remember { mutableStateListOf<String>() }
    
    LaunchedEffect(activePagesVal) {
        pagesList.clear()
        pagesList.addAll(activePagesVal)
    }

    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffsetX by remember { mutableStateOf(0f) }
    var dragOffsetY by remember { mutableStateOf(0f) }
    
    val columns = 2
    
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
                                shadowElevation = if (isDragged) 16.dp.toPx() else 0f
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
