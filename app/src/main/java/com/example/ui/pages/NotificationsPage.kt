package com.example.ui.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toDrawable
import com.example.MainActivity
import com.example.model.AppInfo
import com.example.model.AppNotification
import com.example.service.MyNotificationListenerService
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
    onNotificationClick: (appName: String, text: String, defaultPkg: String) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    
    // Filter active notifications using configuration
    val activeNotifications = remember(notifications, allowedCategories) {
        notifications.filter {
            val cat = getNotificationCategory(it.appName, it.text, it.pkg)
            allowedCategories.contains(cat)
        }
    }

    var selectedFilterCategory by remember { mutableStateOf("All") }
    val expandedGroups = remember { mutableStateMapOf<String, Boolean>() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, bottom = 48.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NOTIFICATION CENTER",
                        fontSize = 11.sp,
                        letterSpacing = 0.15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontFamily = fontFamily,
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.6f),
                                offset = Offset(1f, 2f),
                                blurRadius = 4f
                            )
                        )
                    )

                    if (activeNotifications.isNotEmpty()) {
                        Text(
                            text = "Clear All",
                            fontSize = 11.sp,
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

                // Tabs / Filters
                val presentCategories = remember(activeNotifications) {
                    activeNotifications.map { getNotificationCategory(it.appName, it.text, it.pkg) }.distinct()
                }

                if (presentCategories.size > 1) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            val isSelected = selectedFilterCategory == "All"
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedFilterCategory = "All" },
                                label = { Text("All", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                shape = RoundedCornerShape(20.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    selectedContainerColor = themeColor,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                border = null
                            )
                        }

                        presentCategories.forEach { cat ->
                            item {
                                val isSelected = selectedFilterCategory == cat
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedFilterCategory = cat },
                                    label = { Text(cat, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                    shape = RoundedCornerShape(20.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        selectedContainerColor = themeColor,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    border = null
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

            val displayedNotifications = remember(activeNotifications, selectedFilterCategory) {
                activeNotifications.filter { selectedFilterCategory == "All" || getNotificationCategory(it.appName, it.text, it.pkg) == selectedFilterCategory }
            }

            if (displayedNotifications.isEmpty()) {
                // Polished Zen empty state
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
                            fontFamily = fontFamily
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Your space is decluttered and clean.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = fontFamily
                        )
                    }
                }
            } else {
                val groupedNotifications = remember(displayedNotifications) {
                    val groups = linkedMapOf<String, List<AppNotification>>()
                    for (notification in displayedNotifications) {
                        val pkg = notification.pkg
                        val list = groups.getOrPut(pkg) { mutableListOf() }
                        (list as MutableList).add(notification)
                    }
                    groups.toList()
                }

                val listSize = groupedNotifications.size
                val middle = Int.MAX_VALUE / 2
                val startIndex = if (listSize > 0) middle - (middle % listSize) else 0
                val listState = rememberLazyListState(initialFirstVisibleItemIndex = startIndex)

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .graphicsLayer { alpha = 0.99f }
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
                    contentPadding = PaddingValues(top = 24.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (!isExpanded) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .pointerInput(pkg) {
                                                detectTapGestures(
                                                    onLongPress = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        val resolvedAppInfo = try {
                                                            val label = activity.packageManager.getApplicationLabel(activity.packageManager.getApplicationInfo(pkg, 0)).toString()
                                                            val icon = activity.packageManager.getApplicationIcon(pkg)
                                                            AppInfo(label = label, packageName = pkg, icon = icon)
                                                        } catch (_: Exception) {
                                                            AppInfo(
                                                                label = groupList[0].appName,
                                                                packageName = pkg,
                                                                icon = try {
                                                                activity.packageManager.getApplicationIcon(pkg)
                                                            } catch (_: Exception) {
                                                                0.toDrawable()
                                                            }
                                                            )
                                                        }
                                                        onLongPressApp?.invoke(resolvedAppInfo)
                                                    },
                                                    onTap = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        expandedGroups[pkg] = true
                                                    }
                                                )
                                            }
                                    ) {
                                        // 3D Visual card stack shadow/layer
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth(0.92f)
                                                .align(Alignment.BottomCenter)
                                                .offset(y = 8.dp)
                                                .graphicsLayer {
                                                    scaleX = 0.95f
                                                    scaleY = 0.95f
                                                },
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                                            shape = RoundedCornerShape(14.dp),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                        ) {
                                            Box(modifier = Modifier.fillMaxWidth().height(64.dp))
                                        }

                                        SwipeToDismissNotification(
                                            item = groupList[0],
                                            themeColor = themeColor,
                                            fontFamily = fontFamily,
                                            activity = activity,
                                            onDismiss = {
                                                try {
                                                    MyNotificationListenerService.instance?.cancelNotification(groupList[0].key)
                                                } catch (_: Exception) {}
                                                val currentList = activity.notificationList.value.toMutableList()
                                                currentList.remove(groupList[0])
                                                activity.notificationList.value = currentList
                                            },
                                            onNotificationClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                expandedGroups[pkg] = true
                                            },
                                            onLongPress = {
                                                val resolvedAppInfo = try {
                                                    val label = activity.packageManager.getApplicationLabel(activity.packageManager.getApplicationInfo(pkg, 0)).toString()
                                                    val icon = activity.packageManager.getApplicationIcon(pkg)
                                                    AppInfo(label = label, packageName = pkg, icon = icon)
                                                } catch (_: Exception) {
                                                    AppInfo(
                                                        label = groupList[0].appName,
                                                        packageName = pkg,
                                                        icon = try {
                                                            activity.packageManager.getApplicationIcon(pkg)
                                                        } catch (_: Exception) {
                                                            0.toDrawable()
                                                        }
                                                    )
                                                }
                                                onLongPressApp?.invoke(resolvedAppInfo)
                                            }
                                        )

                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(top = 8.dp, end = 8.dp)
                                                .background(themeColor, shape = CircleShape)
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "+${groupList.size - 1}",
                                                fontSize = 9.sp,
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = fontFamily
                                            )
                                        }
                                    }
                                } else {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                expandedGroups[pkg] = false
                                            }
                                            .padding(vertical = 4.dp, horizontal = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Collapse",
                                                tint = themeColor,
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .graphicsLayer {
                                                        rotationZ = 90f
                                                    }
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${groupList[0].appName.uppercase()} (${groupList.size})",
                                                fontSize = 11.sp,
                                                letterSpacing = 1.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = themeColor,
                                                fontFamily = fontFamily
                                            )
                                        }
                                        Text(
                                            text = "Collapse Stack",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontFamily = fontFamily
                                        )
                                    }

                                    AnimatedVisibility(
                                        visible = isExpanded,
                                        enter = expandVertically() + fadeIn(),
                                        exit = shrinkVertically() + fadeOut()
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
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
}
}
