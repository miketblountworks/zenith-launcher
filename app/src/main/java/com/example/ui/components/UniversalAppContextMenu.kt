package com.example.ui.components

import android.content.ContextWrapper
import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.example.MainActivity
import com.example.model.AppInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UniversalAppContextMenu(
    packageName: String,
    label: String,
    icon: Any?,
    folders: List<String>,
    hiddenList: Set<String>,
    isBreakerEnabled: Boolean,
    themeColor: Color,
    fontFamily: FontFamily,
    isVisible: Boolean,
    onClose: () -> Unit,
    onToggleHide: () -> Unit,
    onToggleBreaker: () -> Unit,
    onUninstall: () -> Unit,
    onAddToFolder: (String) -> Unit,
    selectedFolders: Set<String> = emptySet()
) {
    val _isBreakerEnabled = isBreakerEnabled // Suppress unused
    val _onToggleBreaker = onToggleBreaker // Suppress unused
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    // Background backdrop smooth fade matching isVisible state
    val bgAlpha by animateFloatAsState(
        targetValue = if (isVisible) 0.6f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "bgAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = bgAlpha))
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onClose() })
            },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(300)) + 
                    scaleIn(
                        initialScale = 0.8f, 
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ),
            exit = fadeOut(animationSpec = tween(250)) + 
                   scaleOut(targetScale = 0.8f, animationSpec = tween(250))
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { /* Prevent clicks inside dialog from closing it */ })
                    },
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = 8.dp,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header (App Icon + Title)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(icon),
                            contentDescription = label,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontFamily = fontFamily,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // List of Actions (Borderless, M3 ListItem style)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        // App Info Option
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = "App Info",
                                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = fontFamily)
                                )
                            },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "App Info",
                                    tint = themeColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = Color.Transparent,
                                headlineColor = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    try {
                                        val intent = Intent(
                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            "package:$packageName".toUri()
                                        ).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                        )

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f),
                            thickness = 1.dp
                        )

                        // Hide/Unhide Option
                        val isHidden = packageName in hiddenList
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = if (isHidden) "Unhide App" else "Hide App from Main List",
                                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = fontFamily)
                                )
                            },
                            leadingContent = {
                                Icon(
                                    imageVector = if (isHidden) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = themeColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = Color.Transparent,
                                headlineColor = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleHide() }
                        )

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f),
                            thickness = 1.dp
                        )

                        // Uninstall Option
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = "Uninstall App",
                                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = fontFamily),
                                    color = MaterialTheme.colorScheme.error
                                )
                            },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = Color.Transparent,
                                headlineColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onUninstall() }
                        )
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    // Folder Section
                    Text(
                        text = "Add to Pop-Up Folder Group:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontFamily = fontFamily,
                        modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 4.dp)
                    )

                    // Suggestion Chips / Filter Chips inside a FlowRow
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        folders.forEach { f ->
                            val isSelected = selectedFolders.contains(f)
                            SuggestionChip(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onAddToFolder(f)
                                },
                                label = {
                                    Text(
                                        text = f,
                                        style = MaterialTheme.typography.labelLarge.copy(fontFamily = fontFamily)
                                    )
                                },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (isSelected) themeColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    labelColor = if (isSelected) themeColor else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                border = null,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppContextMenuOverlay(
    app: AppInfo,
    folders: List<String>,
    hiddenList: Set<String>,
    isBreakerEnabled: Boolean,
    themeColor: Color,
    fontFamily: FontFamily,
    onClose: () -> Unit,
    onToggleHide: () -> Unit,
    onToggleBreaker: () -> Unit,
    onUninstall: () -> Unit,
    onAddToFolder: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isVisible by remember { mutableStateOf(false) }

    // Animate in on first composition / change of target app
    LaunchedEffect(app) {
        isVisible = true
    }

    // Helper function to animate out, then execute action
    val runExitAnimationThen = { action: () -> Unit ->
        isVisible = false
        coroutineScope.launch {
            delay(280) // Snappy and matches our fade/scale animation durations
            action()
        }
    }

    val context = LocalContext.current
    val activity = remember(context) {
        var cur = context
        while (cur is ContextWrapper) {
            if (cur is MainActivity) return@remember cur
            cur = cur.baseContext
        }
        null
    }
    val folderMap = activity?.folderMapState?.collectAsState()?.value ?: emptyMap()
    val selectedFolders = folders.filter { folderName ->
        app.packageName in (folderMap[folderName] ?: emptyList())
    }.toSet()

    UniversalAppContextMenu(
        packageName = app.packageName,
        label = app.label,
        icon = app.icon,
        folders = folders,
        hiddenList = hiddenList,
        isBreakerEnabled = isBreakerEnabled,
        themeColor = themeColor,
        fontFamily = fontFamily,
        isVisible = isVisible,
        onClose = { runExitAnimationThen(onClose) },
        onToggleHide = { runExitAnimationThen(onToggleHide) },
        onToggleBreaker = { runExitAnimationThen(onToggleBreaker) },
        onUninstall = { runExitAnimationThen(onUninstall) },
        onAddToFolder = { folder -> runExitAnimationThen { onAddToFolder(folder) } },
        selectedFolders = selectedFolders
    )
}
