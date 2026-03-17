package com.campusbussbuddy.ui.neumorphism.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.campusbussbuddy.ui.theme.*

/**
 * General-purpose raised neumorphic card container.
 * Wraps any content with neumorphic outer shadows and rounded corners.
 *
 * Usage:
 *   NeumorphismCard {
 *       Text("Hello")
 *   }
 */
@Composable
fun NeumorphismCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = NeumorphCardRadius,
    elevation: Dp = 8.dp,
    blur: Dp = 18.dp,
    contentAlignment: Alignment = Alignment.TopStart,
    contentPadding: Dp = 24.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .neumorphic(cornerRadius = cornerRadius, elevation = elevation, blur = blur)
            .background(NeumorphSurface, RoundedCornerShape(cornerRadius))
            .padding(contentPadding),
        contentAlignment = contentAlignment,
        content = content
    )
}
