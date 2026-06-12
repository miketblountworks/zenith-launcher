package com.example.ui.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toDrawable
import coil.compose.rememberAsyncImagePainter
import com.example.MainActivity
import com.example.model.AppInfo
import com.example.model.AppNotification
import com.example.service.MyNotificationListenerService
import com.example.ui.components.GroupedNotificationStack
import com.example.ui.components.SwipeToDismissNotification
import com.example.utils.getNotificationCategory

@Composable
fun NotificationsPage(
    notifications: List<AppNotification>,
    themeColor: Color,
    fontFamily: FontFamily,
    activity: MainActivity,
    allowedCategories: Set<String>,
    modifier: Modifier = Modifier,
    onLongPressApp: ((AppInfo) -> Unit)? = null,
    onNotificationClick: (appName: String, text: String, defaultPkg: String) -> Unit,
    contentColor: Color = Color.White
) {
    val haptic = LocalHapticFeedback.current
    
    val activeNotifications = remember(notifications, allowedCategories) {
        notifications.filter {
            val cat = getNotificationCategory(it.appName, it.text, it.pkg)
            allowedCategories.contains(cat)
        }
    }

    val expandedGroups = remember { mutableStateMapOf<String, Boolean>() }
    var expandedNotification by remember { mutableStateOf<AppNotification?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 16.dp)
    ) {
        // Main Container - Transparent
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp, vertical = 24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 12.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NOTIFICATION CENTER",
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontFamily = fontFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (activeNotifications.isNotEmpty()) {
                        Text(
                            text = "Clear All",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                            fontFamily = fontFamily,
                            modifier = Modifier
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    try {
                                        activeNotifications.forEach { item ->
                                            MyNotificationListenerService.instance?.cancelNotification(item.key)
                                        }
                                    } catch (_: Exception) {}
                                    activity.notificationList.value = emptyList()
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                if (activeNotifications.isEmpty()) {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(themeColor.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = null, tint = themeColor, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No Notifications",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = fontFamily,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                } else {
                    val groupedNotifications = remember(activeNotifications) {
                        val groups = linkedMapOf<String, List<AppNotification>>()
                        for (notification in activeNotifications) {
                            val pkg = notification.pkg
                            val list = groups.getOrPut(pkg) { mutableListOf() }
                            (list as MutableList).add(notification)
                        }
                        groups.toList()
                    }

                    val listSize = groupedNotifications.size
                    val listState = rememberLazyListState(initialFirstVisibleItemIndex = if (listSize > 0) (Int.MAX_VALUE / 2) - (Int.MAX_VALUE / 2 % listSize) else 0)
                    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
                    
                    var previousCycle by remember { mutableIntStateOf(if (listSize > 0) listState.firstVisibleItemIndex / listSize else 0) }

                    LaunchedEffect(listState.firstVisibleItemIndex) {
                        if (listSize > 0) {
                            val currentCycle = listState.firstVisibleItemIndex / listSize
                            if (currentCycle != previousCycle) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                previousCycle = currentCycle
                            }
                        }
                    }

                    LazyColumn(
                        state = listState,
                        flingBehavior = snapFlingBehavior,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                            .drawWithContent {
                                drawContent()
                                val topFadePx = 24.dp.toPx()
                                val bottomFadePx = 90.dp.toPx()
                                drawRect(
                                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                        0.0f to Color.Transparent,
                                        (topFadePx / size.height) to Color.Black,
                                        1f - (bottomFadePx / size.height) to Color.Black,
                                        1.0f to Color.Transparent
                                    ),
                                    blendMode = androidx.compose.ui.graphics.BlendMode.DstIn
                                )
                            },
                        contentPadding = PaddingValues(top = 32.dp, bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(count = if (listSize > 0) Int.MAX_VALUE else 0) { index ->
                            val (pkg, groupList) = groupedNotifications[index % listSize]
                            if (groupList.size == 1) {
                                val item = groupList[0]
                                SwipeToDismissNotification(
                                    item = item,
                                    themeColor = themeColor,
                                    fontFamily = fontFamily,
                                    activity = activity,
                                    onDismiss = {
                                        try {
                                            MyNotificationListenerService.instance?.cancelNotification(item.key)
                                        } catch (_: Exception) {}
                                        val currentList = activity.notificationList.value.toMutableList()
                                        currentList.remove(item)
                                        activity.notificationList.value = currentList
                                    },
                                    onNotificationClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onNotificationClick(item.appName, item.text, item.pkg)
                                    },
                                    onExpand = { expandedNotification = item },
                                    onLongPress = {
                                        val resolvedAppInfo = try {
                                            val label = activity.packageManager.getApplicationLabel(activity.packageManager.getApplicationInfo(item.pkg, 0)).toString()
                                            val icon = activity.packageManager.getApplicationIcon(item.pkg)
                                            AppInfo(label = label, packageName = item.pkg, icon = icon)
                                        } catch (_: Exception) {
                                            AppInfo(
                                                label = item.appName,
                                                packageName = item.pkg,
                                                icon = try {
                                                    activity.packageManager.getApplicationIcon(item.pkg)
                                                } catch (_: Exception) {
                                                    0.toDrawable()
                                                }
                                            )
                                        }
                                        onLongPressApp?.invoke(resolvedAppInfo)
                                    }
                                )
                            } else {
                                val isExpanded = expandedGroups[pkg] == true
                                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (!isExpanded) {
                                        GroupedNotificationStack(
                                            notifications = groupList,
                                            themeColor = themeColor,
                                            fontFamily = fontFamily,
                                            activity = activity,
                                            onLongPressApp = onLongPressApp,
                                            onNotificationClick = onNotificationClick,
                                            onExpandedClick = { expandedGroups[pkg] = true },
                                            onExpand = { expandedNotification = it }
                                        )
                                    } else {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    expandedGroups[pkg] = false
                                                }
                                                .padding(vertical = 8.dp, horizontal = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                    contentDescription = "Collapse",
                                                    tint = themeColor,
                                                    modifier = Modifier.size(18.dp).graphicsLayer { rotationZ = 90f }
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "${groupList[0].appName.uppercase()} (${groupList.size})",
                                                    fontSize = 12.sp,
                                                    letterSpacing = 1.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = themeColor,
                                                    fontFamily = fontFamily,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            Text(text = "Collapse", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, fontFamily = fontFamily)
                                        }

                                        AnimatedVisibility(
                                            visible = isExpanded,
                                            enter = expandVertically() + fadeIn(),
                                            exit = shrinkVertically() + fadeOut()
                                        ) {
                                            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                                groupList.forEach { item ->
                                                    SwipeToDismissNotification(
                                                        item = item,
                                                        themeColor = themeColor,
                                                        fontFamily = fontFamily,
                                                        activity = activity,
                                                        onDismiss = {
                                                            try {
                                                                MyNotificationListenerService.instance?.cancelNotification(item.key)
                                                            } catch (_: Exception) {}
                                                            val currentList = activity.notificationList.value.toMutableList()
                                                            currentList.remove(item)
                                                            activity.notificationList.value = currentList
                                                        },
                                                        onNotificationClick = {
                                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                            onNotificationClick(item.appName, item.text, item.pkg)
                                                        },
                                                        onExpand = { expandedNotification = item },
                                                        onLongPress = {
                                                            val resolvedAppInfo = try {
                                                                val label = activity.packageManager.getApplicationLabel(activity.packageManager.getApplicationInfo(item.pkg, 0)).toString()
                                                                val icon = activity.packageManager.getApplicationIcon(item.pkg)
                                                                AppInfo(label = label, packageName = item.pkg, icon = icon)
                                                            } catch (_: Exception) {
                                                                AppInfo(
                                                                    label = item.appName,
                                                                    packageName = item.pkg,
                                                                    icon = try {
                                                                        activity.packageManager.getApplicationIcon(item.pkg)
                                                                    } catch (_: Exception) {
                                                                        0.toDrawable()
                                                                    }
                                                                )
                                                            }
                                                            onLongPressApp?.invoke(resolvedAppInfo)
                                                        }
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

        // Integrated Search Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
                .fillMaxWidth()
                .height(64.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(32.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f), RoundedCornerShape(32.dp))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Search contacts, apps, web...",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    fontSize = 15.sp,
                    fontFamily = fontFamily,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Box(modifier = Modifier.size(36.dp).background(Color(0xFF9C27B0), CircleShape), contentAlignment = Alignment.Center) {
                    Text(text = "MT", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Expanded Notification Popup Overlay
        AnimatedVisibility(
            visible = expandedNotification != null,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { expandedNotification = null })
                    },
                contentAlignment = Alignment.BottomCenter
            ) {
                expandedNotification?.let { item ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                try {
                                    MyNotificationListenerService.instance?.cancelNotification(item.key)
                                } catch (_: Exception) {}
                                val currentList = activity.notificationList.value.toMutableList()
                                currentList.remove(item)
                                activity.notificationList.value = currentList
                                expandedNotification = null
                                true
                            } else {
                                false
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = { /* No background needed for expanded popup */ },
                        content = {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 100.dp, start = 12.dp, end = 12.dp)
                                    .wrapContentHeight()
                                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(28.dp)),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                                shape = RoundedCornerShape(28.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        val appIcon = try {
                                            activity.packageManager.getApplicationIcon(item.pkg)
                                        } catch (_: Exception) {
                                            null
                                        }
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.surfaceVariant),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (appIcon != null) {
                                                androidx.compose.foundation.Image(
                                                    painter = rememberAsyncImagePainter(appIcon),
                                                    contentDescription = null,
                                                    modifier = Modifier.fillMaxSize().padding(8.dp)
                                                )
                                            } else {
                                                Text(text = item.appName.firstOrNull()?.uppercase() ?: "", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(
                                            text = item.appName,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontFamily = fontFamily
                                        )
                                    }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .verticalScroll(rememberScrollState())
                                    ) {
                                        Text(
                                            text = item.text,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontFamily = fontFamily,
                                            lineHeight = 22.sp
                                        )
                                    }
                                    
                                    // Actions in Expanded View
                                    val actions = item.sbn?.notification?.actions
                                    if (actions != null && actions.isNotEmpty()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                                        ) {
                                            actions.forEach { action ->
                                                Text(
                                                    text = action.title?.toString() ?: "Action",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF9C27B0),
                                                    fontFamily = fontFamily,
                                                    modifier = Modifier.clickable {
                                                        try {
                                                            action.actionIntent?.send(activity, 0, null)
                                                            expandedNotification = null
                                                        } catch (_: Exception) {}
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
