package com.campusbussbuddy.ui.theme

import android.view.MotionEvent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ─── NEUMORPHISM DESIGN TOKEN SYSTEM ─────────────────────────────────────────

val NeumorphBgPrimary = Color(0xFFE6E6E6)        // Very light grey background
val NeumorphSurface = Color(0xFFE6E6E6)          // Same as background
val NeumorphTextPrimary = Color(0xFF111111)      // Near Black
val NeumorphTextSecondary = Color(0xFF6B7280)    // Gray-500

// Accents
val NeumorphAccentPrimary = Color(0xFF8A5CFF)    // Purple accent to match target
val NeumorphAccentHover = Color(0xFF7C3AED)      // Hover Accent
val NeumorphAccentGlow = Color(0x4D8A5CFF)       // Purple glow with more opacity

// Shadows — balanced for clean neumorphism
val NeumorphLightShadow = Color(0xFFF0F0F0)      // Slightly off-white (reduces corner brightness)
val NeumorphDarkShadow = Color(0xFFBEBEBE)       // Darker grey for depth contrast

// Radii
val NeumorphCardRadius = 24.dp
val NeumorphButtonRadius = 30.dp
val NeumorphInputRadius = 28.dp

// ─── NEUMORPHIC MODIFIERS ───────────────────────────────────────────────────

/**
 * Creates a raised neumorphic shadow effect with enhanced depth.
 * Enhanced shadows: light shadow (-8dp, -8dp) and dark shadow (8dp, 8dp) with higher blur
 */
fun Modifier.neumorphic(
    cornerRadius: Dp = NeumorphCardRadius,
    lightShadowColor: Color = NeumorphLightShadow,
    darkShadowColor: Color = NeumorphDarkShadow,
    elevation: Dp = 8.dp,
    blur: Dp = 16.dp
) = this.drawBehind {
    val cornerRadiusPx = cornerRadius.toPx()
    val elevationPx = elevation.toPx()
    val blurPx = blur.toPx()

    // Draw Dark Shadow (Bottom Right) - Enhanced offset and blur
    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = Color.Transparent.toArgb()
        frameworkPaint.setShadowLayer(
            blurPx,
            elevationPx,
            elevationPx,
            darkShadowColor.toArgb()
        )
        canvas.drawRoundRect(
            left = 0f,
            top = 0f,
            right = size.width,
            bottom = size.height,
            radiusX = cornerRadiusPx,
            radiusY = cornerRadiusPx,
            paint = paint
        )

        // Draw Light Shadow (Top Left) - Enhanced offset and blur
        frameworkPaint.setShadowLayer(
            blurPx,
            -elevationPx,
            -elevationPx,
            lightShadowColor.toArgb()
        )
        canvas.drawRoundRect(
            left = 0f,
            top = 0f,
            right = size.width,
            bottom = size.height,
            radiusX = cornerRadiusPx,
            radiusY = cornerRadiusPx,
            paint = paint
        )
    }
}

/**
 * Creates an inset (pressed-in) neumorphic shadow effect.
 * Uses canvas.translate to shift shadow drawing, producing clean inner shadows
 * when clipped to the element's rounded rect boundary.
 */
fun Modifier.neumorphicInset(
    cornerRadius: Dp = NeumorphInputRadius,
    lightShadowColor: Color = NeumorphLightShadow,
    darkShadowColor: Color = NeumorphDarkShadow,
    elevation: Dp = 6.dp,
    blur: Dp = 12.dp
) = this.drawWithContent {
    drawContent()

    val cornerRadiusPx = cornerRadius.toPx()
    val elevationPx = elevation.toPx()
    val blurPx = blur.toPx()

    val clipPath = Path().apply {
        addRoundRect(
            androidx.compose.ui.geometry.RoundRect(
                left = 0f, top = 0f,
                right = size.width, bottom = size.height,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadiusPx)
            )
        )
    }

    drawIntoCanvas { canvas ->
        // ── Dark Inner Shadow (Top-Left light source → shadow inside at top & left) ──
        val shadowPaintDark = Paint().apply {
            color = darkShadowColor.copy(alpha = 0.65f)
            style = PaintingStyle.Stroke
            strokeWidth = blurPx + elevationPx
        }
        shadowPaintDark.asFrameworkPaint().apply {
            maskFilter = android.graphics.BlurMaskFilter(blurPx, android.graphics.BlurMaskFilter.Blur.NORMAL)
        }

        canvas.save()
        canvas.clipPath(clipPath)
        canvas.translate(-elevationPx, -elevationPx)
        canvas.drawPath(clipPath, shadowPaintDark)
        canvas.restore()

        // ── Light Inner Shadow (Bottom-Right highlight edge) ──
        val shadowPaintLight = Paint().apply {
            color = lightShadowColor.copy(alpha = 0.5f)
            style = PaintingStyle.Stroke
            strokeWidth = blurPx + elevationPx
        }
        shadowPaintLight.asFrameworkPaint().apply {
            maskFilter = android.graphics.BlurMaskFilter(blurPx, android.graphics.BlurMaskFilter.Blur.NORMAL)
        }

        canvas.save()
        canvas.clipPath(clipPath)
        canvas.translate(elevationPx, elevationPx)
        canvas.drawPath(clipPath, shadowPaintLight)
        canvas.restore()
    }
}

/**
 * Adds a bounce micro-interaction on press.
 * scale(0.96) with 150ms ease.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.bounceClick(
    scaleDown: Float = 0.96f,
    onClick: () -> Unit
) = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = tween(durationMillis = 150), // 150ms ease
        label = "bounce"
    )

    this
        .scale(scale)
        .pointerInteropFilter { event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isPressed = true
                }
                MotionEvent.ACTION_UP -> {
                    isPressed = false
                    onClick()
                }
                MotionEvent.ACTION_CANCEL -> {
                    isPressed = false
                }
            }
            true
        }
}

/**
 * Creates a raised neumorphic shadow effect using a custom [Shape].
 */
fun Modifier.neumorphicShape(
    shape: androidx.compose.ui.graphics.Shape,
    lightShadowColor: Color = NeumorphLightShadow,
    darkShadowColor: Color = NeumorphDarkShadow,
    elevation: Dp = 10.dp,
    blur: Dp = 20.dp
) = this.drawBehind {
    val elevationPx = elevation.toPx()
    val blurPx = blur.toPx()
    val outline = shape.createOutline(size, layoutDirection, this)
    val path = when (outline) {
        is androidx.compose.ui.graphics.Outline.Generic -> outline.path
        is androidx.compose.ui.graphics.Outline.Rounded -> Path().apply { addRoundRect(outline.roundRect) }
        is androidx.compose.ui.graphics.Outline.Rectangle -> Path().apply { addRect(outline.rect) }
    }

    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = Color.Transparent.toArgb()

        frameworkPaint.setShadowLayer(
            blurPx,
            elevationPx,
            elevationPx,
            darkShadowColor.toArgb()
        )
        canvas.drawPath(path, paint)

        frameworkPaint.setShadowLayer(
            blurPx,
            -elevationPx,
            -elevationPx,
            lightShadowColor.toArgb()
        )
        canvas.drawPath(path, paint)
    }
}

/**
 * Creates an inset neumorphic shadow effect using a custom [Shape].
 */
fun Modifier.neumorphicInsetShape(
    shape: androidx.compose.ui.graphics.Shape,
    lightShadowColor: Color = NeumorphLightShadow,
    darkShadowColor: Color = NeumorphDarkShadow,
    elevation: Dp = 6.dp,
    blur: Dp = 12.dp
) = this.drawWithContent {
    drawContent()
    
    val elevationPx = elevation.toPx()
    val blurPx = blur.toPx()

    val outline = shape.createOutline(size, layoutDirection, this)
    val path = when (outline) {
        is androidx.compose.ui.graphics.Outline.Generic -> outline.path
        is androidx.compose.ui.graphics.Outline.Rounded -> Path().apply { addRoundRect(outline.roundRect) }
        is androidx.compose.ui.graphics.Outline.Rectangle -> Path().apply { addRect(outline.rect) }
    }

    drawIntoCanvas { canvas ->
        // Dark Inner Shadow (Top Left)
        canvas.save()
        canvas.clipPath(path)

        val shadowPaintDark = Paint().apply {
            color = darkShadowColor
            style = PaintingStyle.Stroke
            strokeWidth = blurPx + elevationPx
        }
        shadowPaintDark.asFrameworkPaint().apply {
            maskFilter = android.graphics.BlurMaskFilter(blurPx, android.graphics.BlurMaskFilter.Blur.NORMAL)
        }

        canvas.save()
        canvas.translate(-elevationPx, -elevationPx)
        canvas.drawPath(path, shadowPaintDark)
        canvas.restore()
        canvas.restore()

        // Light Inner Shadow (Bottom Right)
        canvas.save()
        canvas.clipPath(path)

        val shadowPaintLight = Paint().apply {
            color = lightShadowColor
            style = PaintingStyle.Stroke
            strokeWidth = blurPx + elevationPx
        }
        shadowPaintLight.asFrameworkPaint().apply {
            maskFilter = android.graphics.BlurMaskFilter(blurPx, android.graphics.BlurMaskFilter.Blur.NORMAL)
        }

        canvas.save()
        canvas.translate(elevationPx, elevationPx)
        canvas.drawPath(path, shadowPaintLight)
        canvas.restore()
        canvas.restore()
    }
}
