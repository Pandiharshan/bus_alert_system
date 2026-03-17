package com.campusbussbuddy.ui.neumorphism.layout

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusbussbuddy.ui.theme.NeumorphTextPrimary
import com.campusbussbuddy.ui.theme.NeumorphTextSecondary

/**
 * Neumorphic-styled header section with a title and optional subtitle.
 * Designed for use at the top of any screen.
 *
 * Usage:
 *   NeumorphismHeader(
 *       title    = "Welcome, Admin",
 *       subtitle = "Dashboard Overview"
 *   )
 */
@Composable
fun NeumorphismHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String = "",
    titleFontSize: Float = 30f,
    subtitleFontSize: Float = 13f
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text          = title,
            fontSize      = titleFontSize.sp,
            fontWeight    = FontWeight.ExtraBold,
            color         = NeumorphTextPrimary,
            textAlign     = TextAlign.Center,
            letterSpacing = (-0.8).sp
        )

        if (subtitle.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text       = subtitle,
                fontSize   = subtitleFontSize.sp,
                fontWeight = FontWeight.Normal,
                color      = NeumorphTextSecondary,
                textAlign  = TextAlign.Center,
                lineHeight = 19.sp
            )
        }
    }
}
