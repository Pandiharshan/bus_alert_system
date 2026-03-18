package com.campusbussbuddy.ui.neumorphism.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.campusbussbuddy.ui.theme.*

/**
 * General-purpose raised neumorphic card container.
 * Wraps any content with neumorphic outer shadows and rounded corners.
 *
 * Usage:
 *   NeumorphismCard {
 *       Text("Hello")
 *   }
 *
 *   NeumorphismCard(widthFraction = 0.94f, contentPadding = PaddingValues(24.dp)) {
 *       ...
 *   }
 */
@Composable
fun NeumorphismCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = NeumorphCardRadius,
    elevation: Dp = 8.dp,
    blur: Dp = 18.dp,
    widthFraction: Float = 1f,
    showBorder: Boolean = false,
    contentAlignment: Alignment = Alignment.TopStart,
    contentPadding: PaddingValues = PaddingValues(24.dp),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth(widthFraction)
            .neumorphic(cornerRadius = cornerRadius, elevation = elevation, blur = blur)
            .background(NeumorphSurface, RoundedCornerShape(cornerRadius))
            .then(
                if (showBorder) {
                    Modifier.border(
                        width = 1.5.dp,
                        brush = Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.0f  to Color.White.copy(alpha = 0.70f),
                                0.50f to Color.White.copy(alpha = 0.20f),
                                1.0f  to NeumorphAccentPrimary.copy(alpha = 0.80f)
                            )
                        ),
                        shape = RoundedCornerShape(cornerRadius)
                    )
                } else Modifier
            )
            .padding(contentPadding),
        contentAlignment = contentAlignment,
        content = content
    )
}
