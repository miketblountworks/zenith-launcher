package com.example.ui.components

import android.content.ContextWrapper
import android.graphics.Rect
import android.graphics.drawable.Drawable
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    isActiveProvider: () -> Boolean,
    isTouchingSidebarProvider: () -> Boolean,
    currentFontFamily: FontFamily?,
    themeColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 14.dp, top = 20.dp, bottom = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Use graphicsLayer to animate scale/alpha based on providers to avoid recomposition
        Text(
            text = letter.toString(),
            fontSize = 24.sp,
            fontWeight = if (isActiveProvider()) FontWeight.ExtraBold else FontWeight.Bold,
            fontFamily = currentFontFamily,
            modifier = Modifier
                .padding(start = 0.dp)
                .graphicsLayer {
                    val isActive = isActiveProvider()
                    val isTouchingSidebar = isTouchingSidebarProvider()
                    val displayActive = isTouchingSidebar && isActive
                    
                    val scale = if (displayActive) 1.6f else 1.0f
                    scaleX = scale
                    scaleY = scale
                    alpha = if (displayActive) 1.0f else 0.65f
                },
            color = themeColor,
            style = TextStyle(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.6f),
                    offset = Offset(1f, 2f),
                    blurRadius = 4f
                )
            )
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
    alphaProvider: () -> Float,
    isAppHoveredProvider: () -> Boolean,
    sidebarTouchXProvider: () -> Float?,
    sidebarTouchYProvider: () -> Float?,
    sidebarInteractiveProgressProvider: () -> Float,
    listState: LazyListState,
    currentFontFamily: FontFamily,
    iconPackVal: String,
    iconThemeColor: Color,
    contentColor: Color = Color.White,   // adaptive text color for labels
    onLongPress: () -> Unit,
    onTap: () -> Unit,
) {
    val currentContext = LocalContext.current
    val density = LocalDensity.current
    
    // Performance: Pre-calculate constants
    val approxItemHeightPx = remember(density) { with(density) { 60.dp.toPx() } }
    val maxDistPx = remember(density) { with(density) { 220.dp.toPx() } }
    val maxTxPx = remember(density) { with(density) { 64.dp.toPx() } }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp) // Fixed height to avoid layout passes during zoom
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
                val isAppHovered = isAppHoveredProvider()
                val sidebarTouchX = sidebarTouchXProvider()
                val sidebarTouchY = sidebarTouchYProvider()
                val sidebarInteractiveProgress = sidebarInteractiveProgressProvider()
                val animatedAlpha = alphaProvider()

                // Consolidated fisheye math
                var tx = 0f
                var scaleFactor = 1.0f
                
                if (sidebarTouchY != null && sidebarInteractiveProgress > 0.01f) {
                    val itemCenterLocal = (index - listState.firstVisibleItemIndex) * approxItemHeightPx - listState.firstVisibleItemScrollOffset + (approxItemHeightPx / 2f)
                    val dy = abs(itemCenterLocal - sidebarTouchY)
                    
                    if (dy < maxDistPx) {
                        val fraction = dy / maxDistPx
                        val effect = cos(fraction * (PI / 2f)).toFloat()
                        tx = (effect * maxTxPx) * sidebarInteractiveProgress
                        scaleFactor = 1.0f + (effect * 0.15f * sidebarInteractiveProgress)
                    }
                }
                
                // Sidebar Pull effect
                val rawPullX = if (isAppHovered && sidebarTouchX != null && sidebarTouchX < -10f) sidebarTouchX else 0f
                // Note: ideally we'd provider-ize the animated pull X too, but let's stick to this for now
                
                translationX = tx + rawPullX
                scaleX = scaleFactor
                scaleY = scaleFactor
                alpha = animatedAlpha
            }
            .background(
                color = if (isAppHoveredProvider()) contentColor.copy(alpha = 0.09f) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .pointerInput(app) {
                detectTapGestures(
                    onLongPress = { onLongPress() },
                    onTap = { onTap() }
                )
            }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(30.dp),
            contentAlignment = Alignment.Center
        ) {
            // Internal icon scaling logic removed from dynamic layout to graphicsLayer where possible
            StyledAppIcon(app.icon, iconPackVal, iconThemeColor, size = 30.dp)
        }
        
        Spacer(modifier = Modifier.width(14.dp))
        
        Text(
            text = app.label,
            fontSize = 13.sp,
            fontWeight = if (isAppHoveredProvider()) FontWeight.SemiBold else FontWeight.Normal,
            fontFamily = currentFontFamily,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.6f),
                    offset = Offset(1f, 2f),
                    blurRadius = 4f
                )
            ),
            modifier = Modifier.weight(1f)
        )
    }
}
