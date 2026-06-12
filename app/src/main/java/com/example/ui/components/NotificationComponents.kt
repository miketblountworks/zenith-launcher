package com.example.ui.components

import android.app.ActivityOptions
import android.app.Notification
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import com.example.MainActivity
import com.example.model.AppInfo
import com.example.model.AppNotification
import com.example.service.MyNotificationListenerService
import com.example.utils.getNotificationCategory
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

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
        finishedListener = { _ ->
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
    allowedCategories: Set<String> = setOf("General 📦"),
    contentColor: Color = Color.White
) {
    val activeNotifications = remember(notifications) { notifications }

    Card(
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onExpandedChange(!isExpanded) }
                    .padding(vertical = 8.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    val count = activeNotifications.size
                    val titleText = if (count == 0) "All clear!" else "$count Bundled Notifications"
                    Text(
                        text = titleText,
                        fontSize = 14.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (activeNotifications.isNotEmpty()) {
                    Text(if (isExpanded) "Collapse" else "Expand", fontSize = 12.sp, color = primaryColor, fontWeight = FontWeight.Bold)
                }
            }
            if (isExpanded && activeNotifications.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    activeNotifications.forEach { item ->
                        SwipeToDeleteContainer(
                            onDismissed = { onDismissNotification(item) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .clickable { onNotificationClick(item.appName, item.text, item.pkg) }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(primaryColor.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item.appName.firstOrNull()?.uppercase() ?: "",
                                        color = primaryColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.appName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontFamily = fontFamily,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = item.text,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontFamily = fontFamily,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
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
fun GroupedNotificationStack(
    notifications: List<AppNotification>,
    themeColor: Color,
    fontFamily: FontFamily,
    activity: MainActivity,
    onLongPressApp: ((AppInfo) -> Unit)? = null,
    onNotificationClick: (appName: String, text: String, defaultPkg: String) -> Unit,
    onExpandedClick: (String) -> Unit,
    onExpand: (AppNotification) -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    var currentList by remember(notifications) { mutableStateOf(notifications) }
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val pkg = notifications.firstOrNull()?.pkg ?: ""

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // Calculate progress based on top card swipe (0f to 1f)
        val progress = (abs(offsetX.value) / 400f).coerceIn(0f, 1f)

        // Render up to 3 notifications in a stacked deck (Back to Front)
        currentList.take(3).withIndex().toList().reversed().forEach { (index, item) ->
            val isTopCard = index == 0
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        when (index) {
                            0 -> {
                                translationX = offsetX.value
                            }
                            1 -> {
                                scaleX = lerp(0.9f, 1f, progress)
                                scaleY = lerp(0.9f, 1f, progress)
                                translationY = lerp(40f, 0f, progress)
                                alpha = lerp(0.4f, 1f, progress)
                            }
                            else -> {
                                scaleX = 0.8f
                                scaleY = 0.8f
                                translationY = 80f
                                alpha = 0.0f
                            }
                        }
                    }
                    .zIndex(if (index == 0) 10f else if (index == 1) 5f else 1f)
                    .then(
                        if (isTopCard) {
                            Modifier.pointerInput(currentList) {
                                detectHorizontalDragGestures(
                                    onDragEnd = {
                                        scope.launch {
                                            if (abs(offsetX.value) > 200f) {
                                                val target = if (offsetX.value > 0) 1200f else -1200f
                                                offsetX.animateTo(target, tween(250))
                                                currentList = currentList.drop(1) + currentList.take(1)
                                                offsetX.snapTo(0f)
                                            } else {
                                                offsetX.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
                                            }
                                        }
                                    },
                                    onHorizontalDrag = { change, dragAmount ->
                                        change.consume()
                                        scope.launch { offsetX.snapTo(offsetX.value + dragAmount) }
                                    }
                                )
                            }
                        } else Modifier
                    )
            ) {
                NotificationItemCard(
                    item = item,
                    themeColor = themeColor,
                    fontFamily = fontFamily,
                    activity = activity,
                    onDismiss = null, // Exempt from dismissal
                    onNotificationClick = {
                        if (isTopCard) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onExpandedClick(pkg)
                        }
                    },
                    onExpand = { onExpand(item) },
                    onLongPress = {
                        if (isTopCard) {
                            val resolvedAppInfo = try {
                                val label = activity.packageManager.getApplicationLabel(activity.packageManager.getApplicationInfo(pkg, 0)).toString()
                                val icon = activity.packageManager.getApplicationIcon(pkg)
                                AppInfo(label = label, packageName = pkg, icon = icon)
                            } catch (_: Exception) {
                                AppInfo(
                                    label = item.appName,
                                    packageName = pkg,
                                    icon = try {
                                        activity.packageManager.getApplicationIcon(pkg)
                                    } catch (_: Exception) {
                                        activity.getDrawable(android.R.drawable.sym_def_app_icon)!!
                                    }
                                )
                            }
                            onLongPressApp?.invoke(resolvedAppInfo)
                        }
                    }
                )
            }
        }

        // Global Overflow Badge
        if (currentList.size > 1) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 12.dp)
                    .zIndex(20f)
                    .background(themeColor, shape = CircleShape)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "+${currentList.size - 1}",
                    fontSize = 10.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily
                )
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
    modifier: Modifier = Modifier,
    onLongPress: (() -> Unit)? = null,
    onExpand: () -> Unit = {}
) {
    NotificationItemCard(
        item = item,
        themeColor = themeColor,
        fontFamily = fontFamily,
        activity = activity,
        onDismiss = onDismiss,
        onNotificationClick = onNotificationClick,
        modifier = modifier,
        onLongPress = onLongPress,
        onExpand = onExpand
    )
}

@Composable
fun NotificationItemCard(
    item: AppNotification,
    themeColor: Color,
    fontFamily: FontFamily,
    activity: MainActivity,
    onDismiss: (() -> Unit)?,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongPress: (() -> Unit)? = null,
    onExpand: () -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    val appIconDrawable = remember(item.pkg) {
        try {
            context.packageManager.getApplicationIcon(item.pkg)
        } catch (_: Exception) {
            null
        }
    }

    var activeReplyActionIndex by remember { mutableIntStateOf(-1) }
    var replyText by remember { mutableStateOf("") }
    var hasTextOverflow by remember { mutableStateOf(false) }

    val actionTextColor = Color(0xFF9C27B0)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
    ) {
        // Dismiss Background (Only if onDismiss is provided)
        if (onDismiss != null) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        color = if (offsetX.value != 0f) MaterialTheme.colorScheme.error.copy(alpha = 0.25f) else Color.Transparent,
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(horizontal = 20.dp),
                contentAlignment = if (offsetX.value > 0) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                if (offsetX.value != 0f) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Dismiss",
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Foreground Card
        Column(
            modifier = Modifier
                .offset { androidx.compose.ui.unit.IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(item.key, onDismiss) {
                    if (onDismiss == null) return@pointerInput
                    var totalDragAmount = 0f
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            val threshold = with(density) { 96.dp.toPx() }
                            if (abs(totalDragAmount) > threshold) {
                                val target = if (totalDragAmount > 0) size.width.toFloat() else -size.width.toFloat()
                                scope.launch {
                                    offsetX.animateTo(target, tween(250))
                                    onDismiss()
                                }
                            } else {
                                scope.launch {
                                    offsetX.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
                                }
                            }
                            totalDragAmount = 0f
                        },
                        onDragCancel = {
                            scope.launch {
                                offsetX.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
                            }
                            totalDragAmount = 0f
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            totalDragAmount += dragAmount
                            scope.launch {
                                offsetX.snapTo(totalDragAmount)
                            }
                        }
                    )
                }
                .fillMaxWidth()
                .wrapContentHeight()
                .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(28.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f), RoundedCornerShape(28.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Main Content Row
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
                                                val options = if (Build.VERSION.SDK_INT >= 34) {
                                                    ActivityOptions.makeBasic().apply {
                                                        setPendingIntentBackgroundActivityStartMode(ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED)
                                                    }
                                                } else {
                                                    ActivityOptions.makeBasic()
                                                }
                                                contentIntent.send(context, 0, null, null, null, null, options.toBundle())
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                                fallbackLaunch()
                                            }
                                        } else {
                                            fallbackLaunch()
                                        }

                                        val isAutoCancel = (sbn.notification.flags and Notification.FLAG_AUTO_CANCEL) != 0
                                        if (isAutoCancel) {
                                            MyNotificationListenerService.instance?.cancelNotification(item.key)
                                            onDismiss?.invoke()
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
                // Circular App Icon
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (appIconDrawable != null) {
                        androidx.compose.foundation.Image(
                            painter = rememberAsyncImagePainter(appIconDrawable),
                            contentDescription = "${item.appName} Icon",
                            modifier = Modifier.fillMaxSize().padding(6.dp)
                        )
                    } else {
                        Text(
                            text = item.appName.firstOrNull()?.uppercase() ?: "",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            fontFamily = fontFamily
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Text content area
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(enabled = hasTextOverflow) { onExpand() }
                ) {
                    Text(
                        text = item.appName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = fontFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.text,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = fontFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp,
                        onTextLayout = { textLayoutResult ->
                            if (textLayoutResult.hasVisualOverflow) hasTextOverflow = true
                        }
                    )
                }
            }

            // Action Buttons Row - Using horizontalScroll to prevent clipping/wrapping
            val actions = item.sbn?.notification?.actions
            if (actions != null && actions.isNotEmpty()) {
                val actionsScrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 58.dp)
                        .horizontalScroll(actionsScrollState),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    actions.forEachIndexed { index, action ->
                        val remoteInputs = action.remoteInputs
                        val isReply = remoteInputs != null && remoteInputs.isNotEmpty()
                        
                        Text(
                            text = action.title?.toString() ?: "Action",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = actionTextColor,
                            fontFamily = fontFamily,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    if (isReply) {
                                        activeReplyActionIndex = if (activeReplyActionIndex == index) -1 else index
                                    } else {
                                        try {
                                            action.actionIntent?.send(context, 0, null)
                                        } catch (_: Exception) {}
                                    }
                                }
                        )
                    }
                }
            }

            // Inline Reply Field
            if (activeReplyActionIndex != -1 && actions != null && activeReplyActionIndex < actions.size) {
                val action = actions[activeReplyActionIndex]
                val remoteInputs = action.remoteInputs
                if (remoteInputs != null && remoteInputs.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val placeholderLabel = remoteInputs.firstOrNull()?.label?.toString() ?: "Reply..."
                        BasicTextField(
                            value = replyText,
                            onValueChange = { replyText = it },
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontFamily = fontFamily
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 44.dp)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    RoundedCornerShape(22.dp)
                                )
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                    RoundedCornerShape(22.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            singleLine = true,
                            cursorBrush = SolidColor(actionTextColor),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (replyText.isEmpty()) {
                                        Text(
                                            text = placeholderLabel,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                            fontSize = 13.sp,
                                            fontFamily = fontFamily,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                        
                        IconButton(
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
                                        onDismiss?.invoke()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .background(actionTextColor, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
