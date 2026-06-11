package com.example.ui.components

import android.app.ActivityOptions
import android.app.Notification
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.MainActivity
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
    allowedCategories: Set<String> = setOf("Finance 💰", "Travel ✈️", "Social 💬", "Internet 🌐", "Entertainment 🎵", "Shopping 🛍️", "General 📦")
) {
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
                
                val presentCategories = remember(activeNotifications) {
                    activeNotifications.map { getNotificationCategory(it.appName, it.text, it.pkg) }.distinct()
                }

                if (presentCategories.size > 1) {
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
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
fun SwipeToDismissNotification(
    item: AppNotification,
    themeColor: Color,
    fontFamily: FontFamily,
    activity: MainActivity,
    onDismiss: () -> Unit,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongPress: (() -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    val offsetX = remember { androidx.compose.animation.core.Animatable(0f) }
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

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    color = if (offsetX.value != 0f) MaterialTheme.colorScheme.error.copy(alpha = 0.15f) else Color.Transparent,
                    shape = RoundedCornerShape(20.dp)
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
                .shadow(elevation = 10.dp, shape = RoundedCornerShape(20.dp))
                .defaultMinSize(minHeight = 84.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
                .padding(12.dp)
        ) {
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
                        androidx.compose.foundation.Image(
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
                    val softShadow = Shadow(color = Color.Black.copy(alpha = 0.6f), offset = Offset(1f, 2f), blurRadius = 4f)
                    Text(
                        text = item.appName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = fontFamily,
                        style = TextStyle(shadow = softShadow)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.text,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = fontFamily,
                        maxLines = 2,
                        style = TextStyle(shadow = softShadow)
                    )
                }
            }

            val actions = item.sbn?.notification?.actions
            if (actions != null && actions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
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
                                        } catch (_: Exception) {
                                            // ignore
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
                                        imageVector = Icons.AutoMirrored.Filled.Send,
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
                        BasicTextField(
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
                            cursorBrush = SolidColor(themeColor),
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
