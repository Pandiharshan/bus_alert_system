package com.campusbussbuddy.ui.neumorphism.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusbussbuddy.ui.theme.*

/**
 * Neumorphic raised button with optional glow, gradient border, bounce animation,
 * and built-in loading state.
 *
 * Usage:
 *   NeumorphismButton(text = "Sign In", onClick = { ... })
 *   NeumorphismButton(text = "Submit", isLoading = true, onClick = { })
 */
@Composable
fun NeumorphismButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    cornerRadius: Dp = NeumorphButtonRadius,
    showGlow: Boolean = true
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        // Glow layer behind the button
        if (showGlow) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.0f  to Color.Transparent,
                                0.45f to Color.Transparent,
                                0.72f to NeumorphAccentPrimary.copy(alpha = 0.30f),
                                1.0f  to NeumorphAccentPrimary.copy(alpha = 0.60f)
                            )
                        ),
                        shape = RoundedCornerShape(cornerRadius)
                    )
            )
        }

        // Raised button surface
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.TopCenter)
                .neumorphic(
                    cornerRadius    = cornerRadius,
                    elevation       = 6.dp,
                    blur            = 12.dp,
                    darkShadowColor = if (showGlow) NeumorphAccentGlow else NeumorphDarkShadow
                )
                .background(NeumorphSurface, RoundedCornerShape(cornerRadius))
                .border(
                    width = 1.2.dp,
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f  to Color.White.copy(alpha = 0.90f),
                            0.50f to Color.White.copy(alpha = 0.40f),
                            1.0f  to NeumorphAccentPrimary.copy(alpha = 0.80f)
                        )
                    ),
                    shape = RoundedCornerShape(cornerRadius)
                )
                .then(
                    if (enabled && !isLoading) Modifier.bounceClick { onClick() }
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier    = Modifier.size(22.dp),
                    color       = NeumorphAccentPrimary,
                    strokeWidth = 2.5.dp
                )
            } else {
                Text(
                    text          = text,
                    fontSize      = 17.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = if (enabled) NeumorphTextPrimary
                                    else NeumorphTextSecondary,
                    letterSpacing = 0.2.sp
                )
            }
        }
    }
}
