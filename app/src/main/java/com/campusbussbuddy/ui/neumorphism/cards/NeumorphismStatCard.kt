package com.campusbussbuddy.ui.neumorphism.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusbussbuddy.ui.theme.*

/**
 * Neumorphic stat card showing a count value and a label.
 * Optionally displays an icon.
 *
 * Usage:
 *   NeumorphismStatCard(
 *       count = "42",
 *       label = "Total Students",
 *       iconRes = R.drawable.ic_student
 *   )
 */
@Composable
fun NeumorphismStatCard(
    count: String,
    label: String,
    modifier: Modifier = Modifier,
    iconRes: Int? = null,
    iconTint: Color = NeumorphAccentPrimary,
    cornerRadius: Dp = 20.dp
) {
    Box(
        modifier = modifier
            .neumorphic(cornerRadius = cornerRadius, elevation = 6.dp, blur = 14.dp)
            .background(NeumorphSurface, RoundedCornerShape(cornerRadius))
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (iconRes != null) {
                Icon(
                    painter            = painterResource(id = iconRes),
                    contentDescription = null,
                    tint               = iconTint,
                    modifier           = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text       = count,
                fontSize   = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = NeumorphTextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text       = label,
                fontSize   = 12.sp,
                fontWeight = FontWeight.Medium,
                color      = NeumorphTextSecondary,
                letterSpacing = 0.5.sp
            )
        }
    }
}
