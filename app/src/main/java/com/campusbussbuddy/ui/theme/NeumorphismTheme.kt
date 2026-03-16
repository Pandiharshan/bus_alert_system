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

val NeumorphBgPrimary = Color(0xFFECECEC)        // #ECECEC - Primary Background
val NeumorphSurface = Color(0xFFE6E6E6)          // #E6E6E6 - Secondary Surface
val NeumorphTextPrimary = Color(0xFF1F1F1F)      // #1F1F1F - Text Primary
val NeumorphTextSecondary = Color(0xFF6E6E6E)    // #6E6E6E - Text Secondary

// Accents
val NeumorphAccentPrimary = Color(0xFF6D28D9)    // #6D28D9 - Primary Accent
val NeumorphAccentHover = Color(0xFF7C3AED)      // #7C3AED - Hover Accent
val NeumorphAccentGlow = Color(0x596D28D9)       // rgba(109,40,217,0.35) - Glow Accent

// Shadows
val NeumorphLightShadow = Color(0xFFFFFFFF)      // #FFFFFF
val NeumorphDarkShadow = Color(0xFFCFCFCF)       // #CFCFCF

// Radii
val NeumorphCardRadius = 24.dp
val NeumorphButtonRadius = 30.dp
val NeumorphInputRadius = 28.dp

// ─── NEUMORPHIC MODIFIERS ───────────────────────────────────────────────────

/**
 * Creates a raised neumorphic shadow effect.
 * box-shadow: 10px 10px 20px #CFCFCF, -10px -10px 20px #FFFFFF
 */
fun Modifier.neumorphic(
    cornerRadius: Dp = NeumorphCardRadius,
    lightShadowColor: Color = NeumorphLightShadow,
    darkShadowColor: Color = NeumorphDarkShadow,
    elevation: Dp = 10.dp,
    blur: Dp = 20.dp
) = this.drawBehind {
    val cornerRadiusPx = cornerRadius.toPx()
    val elevationPx = elevation.toPx()
    val blurPx = blur.toPx()

    // Draw Dark Shadow (Bottom Right)
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

        // Draw Light Shadow (Top Left)
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
 * Creates an inset neumorphic shadow effect.
 * inset: 6px 6px 12px #CFCFCF, inset -6px -6px 12px #FFFFFF
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

    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.isAntiAlias = true
        frameworkPaint.color = Color.Transparent.toArgb()
        
        // Dark Inner Shadow (Top Left)
        canvas.save()
        val pathDark = Path().apply {
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    left = 0f, top = 0f, right = size.width, bottom = size.height,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadiusPx, cornerRadiusPx)
                )
            )
        }
        canvas.clipPath(pathDark)

        val shadowPaintDark = Paint().apply {
            color = darkShadowColor
            style = PaintingStyle.Stroke
            strokeWidth = blurPx + elevationPx
        }
        shadowPaintDark.asFrameworkPaint().apply {
            maskFilter = android.graphics.BlurMaskFilter(blurPx, android.graphics.BlurMaskFilter.Blur.NORMAL)
        }

        // Draw slightly offset border to create inset effect
        canvas.drawPath(
            Path().apply {
                addRoundRect(
                    androidx.compose.ui.geometry.RoundRect(
                        left = -elevationPx, top = -elevationPx, right = size.width + elevationPx, bottom = size.height + elevationPx,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadiusPx, cornerRadiusPx)
                    )
                )
            },
            shadowPaintDark
        )
        canvas.restore()

        // Light Inner Shadow (Bottom Right)
        canvas.save()
        canvas.clipPath(pathDark)

        val shadowPaintLight = Paint().apply {
            color = lightShadowColor
            style = PaintingStyle.Stroke
            strokeWidth = blurPx + elevationPx
        }
        shadowPaintLight.asFrameworkPaint().apply {
            maskFilter = android.graphics.BlurMaskFilter(blurPx, android.graphics.BlurMaskFilter.Blur.NORMAL)
        }

        canvas.drawPath(
            Path().apply {
                addRoundRect(
                    androidx.compose.ui.geometry.RoundRect(
                        left = elevationPx, top = elevationPx, right = size.width - elevationPx, bottom = size.height - elevationPx,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadiusPx, cornerRadiusPx)
                    )
                )
            },
            shadowPaintLight
        )
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
