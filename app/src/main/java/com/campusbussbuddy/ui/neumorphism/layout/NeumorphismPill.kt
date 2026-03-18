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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.runtime.*
import androidx.compose.ui.draw.scale
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import com.campusbussbuddy.ui.theme.*

/**
 * Small pill-shaped neumorphic label / button.
 *
 * Usage:
 *   NeumorphismPill(label = "PRIVACY") { onPrivacy() }
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NeumorphismPill(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconRes: Int? = null,
    cornerRadius: Dp = 20.dp
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "bounce"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .then(
                if (isPressed) Modifier.neumorphicInset(cornerRadius = cornerRadius, elevation = 4.dp, blur = 8.dp)
                else Modifier.neumorphic(cornerRadius = cornerRadius, elevation = 4.dp, blur = 8.dp)
            )
            .background(NeumorphSurface, RoundedCornerShape(cornerRadius))
            .pointerInteropFilter { event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> { isPressed = true }
                    android.view.MotionEvent.ACTION_UP -> { isPressed = false; onClick() }
                    android.view.MotionEvent.ACTION_CANCEL -> { isPressed = false }
                }
                true
            }
            .padding(horizontal = 14.dp, vertical = 9.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (iconRes != null) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = NeumorphTextSecondary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text          = label,
                fontSize      = 10.sp,
                fontWeight    = FontWeight.Bold,
                color         = NeumorphTextSecondary,
                letterSpacing = 1.5.sp,
                maxLines      = 1
            )
        }
    }
}
