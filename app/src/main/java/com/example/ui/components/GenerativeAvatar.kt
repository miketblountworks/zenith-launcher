package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

/**
 * A completely offline, natively drawn Generative Avatar component using Jetpack Compose Canvas.
 * Derives a deterministic design from a seed string (e.g., contact name).
 */
@Composable
fun GenerativeAvatar(seedName: String, modifier: Modifier = Modifier) {
    val seed = remember(seedName) { seedName.hashCode().toLong() }
    val random = remember(seed) { Random(seed) }

    // Premium modern color palette
    val palette = remember {
        listOf(
            Color(0xFF5C6BC0), // Soft Indigo
            Color(0xFF26A69A), // Modern Teal
            Color(0xFF78909C), // Slate
            Color(0xFFFFCA28), // Muted Amber
            Color(0xFFEF5350), // Terracotta
            Color(0xFF7E57C2), // Deep Purple
            Color(0xFF42A5F5), // Light Blue
            Color(0xFF66BB6A)  // Soft Green
        )
    }

    val backgroundColor = remember(random) { palette[random.nextInt(palette.size)] }
    val shapeColor1 = remember(random) { palette[random.nextInt(palette.size)] }
    val shapeColor2 = remember(random) { palette[random.nextInt(palette.size)] }

    Canvas(modifier = modifier.fillMaxSize()) {
        // Step 1: Solid background layer
        drawRect(color = backgroundColor)

        // Step 2: First geometric layer (Deterministic Circle)
        val radius = size.width * (0.3f + random.nextFloat() * 0.3f)
        val centerX = size.width * random.nextFloat()
        val centerY = size.height * random.nextFloat()
        drawCircle(
            color = shapeColor1,
            radius = radius,
            center = Offset(centerX, centerY),
            alpha = 0.6f
        )

        // Step 3: Second accent geometric layer (Rotated Rectangle with Overlay)
        val rectWidth = size.width * (0.4f + random.nextFloat() * 0.4f)
        val rectHeight = size.height * (0.2f + random.nextFloat() * 0.3f)
        val rectX = size.width * random.nextFloat()
        val rectY = size.height * random.nextFloat()
        val rotation = random.nextFloat() * 360f

        rotate(degrees = rotation, pivot = Offset(rectX, rectY)) {
            drawRect(
                color = shapeColor2,
                topLeft = Offset(rectX - rectWidth / 2, rectY - rectHeight / 2),
                size = Size(rectWidth, rectHeight),
                alpha = 0.4f,
                blendMode = BlendMode.Overlay
            )
        }
    }
}
