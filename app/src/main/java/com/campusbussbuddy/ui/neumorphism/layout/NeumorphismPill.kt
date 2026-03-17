package com.campusbussbuddy.ui.neumorphism.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusbussbuddy.ui.theme.*

/**
 * Small pill-shaped neumorphic label / button.
 *
 * Usage:
 *   NeumorphismPill(label = "PRIVACY") { onPrivacy() }
 */
@Composable
fun NeumorphismPill(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp
) {
    Box(
        modifier = modifier
            .neumorphic(cornerRadius = cornerRadius, elevation = 4.dp, blur = 8.dp)
            .background(NeumorphSurface, RoundedCornerShape(cornerRadius))
            .bounceClick { onClick() }
            .padding(horizontal = 18.dp, vertical = 9.dp)
    ) {
        Text(
            text          = label,
            fontSize      = 10.sp,
            fontWeight    = FontWeight.Bold,
            color         = NeumorphTextSecondary,
            letterSpacing = 1.5.sp
        )
    }
}
