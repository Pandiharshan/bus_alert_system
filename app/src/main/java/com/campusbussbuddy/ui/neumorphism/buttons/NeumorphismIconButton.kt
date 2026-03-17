package com.campusbussbuddy.ui.neumorphism.buttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.campusbussbuddy.ui.theme.*

/**
 * Circular neumorphic icon button that toggles between raised (flat) and
 * pressed (inset) states.
 *
 * Usage:
 *   NeumorphismIconButton(
 *       iconRes    = R.drawable.ic_student,
 *       isSelected = true,
 *       onClick    = { ... }
 *   )
 */
@Composable
fun NeumorphismIconButton(
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isImage: Boolean = false,
    size: Dp = 48.dp,
    iconSize: Dp = 25.dp,
    selectedTint: Color = NeumorphAccentPrimary,
    unselectedTint: Color = NeumorphTextSecondary
) {
    val halfSize = size / 2

    Box(
        modifier = modifier
            .size(size)
            .then(
                if (isSelected)
                    Modifier.neumorphicInset(cornerRadius = halfSize, elevation = 4.dp, blur = 8.dp)
                else
                    Modifier.neumorphic(cornerRadius = halfSize, elevation = 4.dp, blur = 8.dp)
            )
            .background(NeumorphSurface, CircleShape)
            .bounceClick { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isImage) {
            Image(
                painter            = painterResource(id = iconRes),
                contentDescription = null,
                modifier           = Modifier.size(iconSize),
                contentScale       = ContentScale.Fit,
                alpha              = if (isSelected) 1f else 0.55f
            )
        } else {
            Icon(
                painter            = painterResource(id = iconRes),
                contentDescription = null,
                tint               = if (isSelected) selectedTint else unselectedTint,
                modifier           = Modifier.size(iconSize)
            )
        }
    }
}
