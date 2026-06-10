package com.example.ui.components

import android.content.ContextWrapper
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.MainActivity
import com.example.model.AppInfo
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos

@Composable
fun AlphabetHeaderRow(
    letter: Char,
    isActive: Boolean,
    isTouchingSidebar: Boolean,
    currentFontFamily: FontFamily?,
    themeColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 14.dp, top = 20.dp, bottom = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        val displayActive = isTouchingSidebar && isActive
        val sizeTarget = if (displayActive) 34.sp else 24.sp
        val letterColor = if (displayActive) themeColor else Color.White.copy(alpha = 0.5f)
        val weightTarget = if (displayActive) FontWeight.ExtraBold else FontWeight.Bold
        val animatedFontSize by animateFloatAsState(targetValue = sizeTarget.value, label = "alphabet_font_size")

        Text(
            text = letter.toString(),
            fontSize = animatedFontSize.sp,
            fontWeight = weightTarget,
            fontFamily = currentFontFamily,
            color = letterColor,
            modifier = Modifier.padding(start = 0.dp)
        )
    }
}

@Composable
fun StyledAppIcon(icon: Drawable, pack: String, themeColor: Color, size: Dp = 38.dp) {
    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        val painter = rememberAsyncImagePainter(icon)
        when (pack) {
            "Silhouette Outlined" -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(color = themeColor.copy(alpha = 0.35f), style = Stroke(width = 2.dp.toPx()))
                }
                Image(painter = painter, contentDescription = null, modifier = Modifier.fillMaxSize(0.57f))
            }
            "Neon Glow" -> {
                Box(modifier = Modifier.fillMaxSize().padding(2.dp).background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(10.dp)).border(1.5.dp, themeColor, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                    Image(painter = painter, contentDescription = null, modifier = Modifier.fillMaxSize(0.63f))
                }
            }
            "Pastel Minimalist" -> {
                Box(modifier = Modifier.fillMaxSize().background(themeColor.copy(alpha = 0.28f), RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                    Image(painter = painter, contentDescription = null, modifier = Modifier.fillMaxSize(0.63f))
                }
            }
            else -> { // Classic
                Image(painter = painter, contentDescription = null, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun AppRow(
    index: Int,
    app: AppInfo,
    animatedAlpha: Float,
    highlightedApp: AppInfo?,
    sidebarTouchX: Float?,
    sidebarTouchY: Float?,
    sidebarInteractiveProgress: Float,
    listState: LazyListState,
    density: Density,
    currentFontFamily: FontFamily,
    iconPackVal: String,
    iconThemeColor: Color,
    onLongPress: () -> Unit,
    onTap: () -> Unit,
) {
    val _density = density // suppress unused
    val isAppHovered = highlightedApp == app
    val hoverProgress by animateFloatAsState(
        targetValue = if (isAppHovered) 1.0f else 0.0f,
        animationSpec = spring(
            stiffness = Spring.StiffnessMedium
        ),
        label = "app_hover_progress"
    )

    val dynamicPaddingTop by animateDpAsState(
        targetValue = if (isAppHovered) 16.dp else 10.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "dynamic_padding_top"
    )
    val dynamicPaddingBottom by animateDpAsState(
        targetValue = if (isAppHovered) 16.dp else 10.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "dynamic_padding_bottom"
    )

    val rawPullX = if (isAppHovered && sidebarTouchX != null && sidebarTouchX < -10f) sidebarTouchX else 0f
    val animatedPullX by animateFloatAsState(
        targetValue = rawPullX,
        animationSpec = spring(
            dampingRatio = 0.55f,
            stiffness = 300f
        ),
        label = "animated_pull_x"
    )
    val separationProgress = (-animatedPullX / 100f).coerceIn(0f, 1.3f)

    val currentContext = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 44.dp, end = 16.dp)
            .onGloballyPositioned { layoutCoordinates ->
                val size = layoutCoordinates.size
                val position = layoutCoordinates.localToWindow(Offset.Zero)
                val rect = Rect(
                    position.x.toInt(),
                    position.y.toInt(),
                    (position.x + size.width).toInt(),
                    (position.y + size.height).toInt()
                )
                var cur = currentContext
                while (cur is ContextWrapper) {
                    if (cur is MainActivity) {
                        cur.appIconBoundsMap[app.packageName] = rect
                        break
                    }
                    cur = cur.baseContext
                }
            }
            .graphicsLayer {
                var tx = 0f
                val touchY = sidebarTouchY
                if (touchY != null && sidebarInteractiveProgress > 0.01f) {
                    val approxItemHeight = 60.dp.toPx()
                    val itemCenterLocal = (index - listState.firstVisibleItemIndex) * approxItemHeight - listState.firstVisibleItemScrollOffset + (approxItemHeight / 2f)
                    val dy = abs(itemCenterLocal - touchY)
                    
                    val maxDist = 220.dp.toPx()
                    val effect = if (dy < maxDist) {
                        val fraction = dy / maxDist
                        cos(fraction * (PI / 2f)).toFloat()
                    } else 0f
                    
                    val maxTx = 64.dp.toPx()
                    tx = (effect * maxTx) * sidebarInteractiveProgress
                }
                
                translationX = tx
                scaleX = 1.0f + (hoverProgress * 0.04f)
                scaleY = 1.0f + (hoverProgress * 0.04f)
                alpha = animatedAlpha
            }
            .background(
                color = if (isAppHovered) Color.White.copy(alpha = 0.08f) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = if (isAppHovered) Color.White.copy(alpha = 0.12f) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .pointerInput(app) {
                detectTapGestures(
                    onLongPress = { onLongPress() },
                    onTap = { onTap() }
                )
            }
            .padding(
                start = 12.dp,
                end = 12.dp,
                top = if (index == 0) 0.dp else dynamicPaddingTop,
                bottom = dynamicPaddingBottom
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.graphicsLayer {
                translationX = animatedPullX
            },
            contentAlignment = Alignment.Center
        ) {
            if (separationProgress > 0.01f) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .graphicsLayer {
                            val s = (separationProgress * 1.1f).coerceIn(0f, 1f)
                            scaleX = s
                            scaleY = s
                            alpha = s
                        }
                        .background(iconThemeColor, CircleShape)
                )
            }
            StyledAppIcon(app.icon, iconPackVal, iconThemeColor, size = 30.dp)
        }
        
        Spacer(modifier = Modifier.width(14.dp))
        
        Text(
            text = app.label,
            fontSize = if (isAppHovered) 14.5.sp else 13.sp,
            fontWeight = if (isAppHovered) FontWeight.SemiBold else FontWeight.Normal,
            fontFamily = currentFontFamily,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.5f),
                    offset = Offset(1f, 1f),
                    blurRadius = 3f
                )
            ),
            modifier = Modifier
                .weight(1f)
                .graphicsLayer {
                    translationX = separationProgress * 40f
                    alpha = 1f - separationProgress.coerceIn(0f, 1f)
                    
                    var scaleAdd = 0f
                    val touchY = sidebarTouchY
                    if (touchY != null && sidebarInteractiveProgress > 0.01f) {
                        val approxItemHeight = 60.dp.toPx()
                        val itemCenterLocal = (index - listState.firstVisibleItemIndex) * approxItemHeight - listState.firstVisibleItemScrollOffset + (approxItemHeight / 2f)
                        val dy = abs(itemCenterLocal - touchY)
                        val maxDist = 220.dp.toPx()
                        if (dy < maxDist) {
                            val fraction = dy / maxDist
                            val effect = cos(fraction * (PI / 2f)).toFloat()
                            scaleAdd = effect * 0.05f * sidebarInteractiveProgress
                        }
                    }
                    scaleX = 1.0f + scaleAdd
                    scaleY = 1.0f + scaleAdd
                    transformOrigin = TransformOrigin(0f, 0.5f)
                }
        )
    }
}
