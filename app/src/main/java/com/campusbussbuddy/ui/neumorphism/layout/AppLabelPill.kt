package com.campusbussbuddy.ui.neumorphism.layout

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusbussbuddy.ui.theme.*

@Composable
fun AppLabelPill(
    icon: Int,
    title: String = "Campus Bus Buddy",
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.78f)
            .neumorphic(cornerRadius = 28.dp, elevation = 6.dp, blur = 12.dp)
            .background(NeumorphSurface, RoundedCornerShape(28.dp))
            .bounceClick { onClick() }
            .padding(start = 22.dp, end = 5.dp, top = 5.dp, bottom = 5.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text          = title,
            fontSize      = 14.sp,
            fontWeight    = FontWeight.SemiBold,
            color         = NeumorphTextSecondary,
            letterSpacing = 0.sp
        )

        Box(
            modifier = Modifier
                .size(46.dp)
                .neumorphic(cornerRadius = 23.dp, elevation = 4.dp, blur = 8.dp)
                .background(NeumorphSurface, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState   = icon,
                transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(160)) },
                label         = "pill_role_icon"
            ) { icon ->
                Icon(
                    painter            = painterResource(id = icon),
                    contentDescription = null,
                    tint               = NeumorphTextSecondary,
                    modifier           = Modifier.size(22.dp)
                )
            }
        }
    }
}
