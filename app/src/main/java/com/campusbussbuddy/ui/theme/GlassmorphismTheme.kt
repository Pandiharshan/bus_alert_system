package com.campusbussbuddy.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── GLASSMORPHISM DESIGN TOKEN SYSTEM ───────────────────────────────────────

// 🎨 GLOBAL BACKGROUND GRADIENT - BRIGHT PREMIUM COLORS
val BackgroundTop = Color(0xFFE8F0EE)        // #E8F0EE - Slightly darker soft premium
val BackgroundBottom = Color(0xFFD4E5E1)     // #D4E5E1 - Slightly darker tinted white
val BackgroundOverlay = Color(0x1F2D6464)    // rgba(45,100,100,0.12) - Slightly more brand tint

// 🧊 GLASS SURFACE SYSTEM - UPDATED FOR BRIGHT THEME
val GlassCardFill = Color(0x59FFFFFF)        // rgba(255,255,255,0.35) - More transparent for glass effect
val GlassCardBorder = Color(0x66FFFFFF)      // rgba(255,255,255,0.4) - Slightly more visible border
val GlassCardRadius = 28.dp
val GlassCardBlur = 22.dp

// ⌨️ INPUT FIELD SYSTEM - BRIGHTER INPUTS
val InputFieldBackground = Color(0xA6FFFFFF) // rgba(255,255,255,0.65) - Semi-transparent input bg
val InputFieldHeight = 56.dp
val InputFieldRadius = 100.dp  // Pill shape
val InputIconTint = Color(0xFF475569)        // #475569 - Secondary text color

// 🟢 PRIMARY ACTION BUTTON - GREEN WITH UPDATED SHADOW
val PrimaryButtonColor = Color(0xD922C55E)   // rgba(34,197,94,0.85) - Green button
val PrimaryButtonShadow = Color(0x4022C55E)  // rgba(34,197,94,0.25) - Green shadow
val PrimaryButtonTextColor = Color(0xFFFFFFFF)
val PrimaryButtonRadius = 100.dp  // Pill shape

// 🔤 TYPOGRAPHY HIERARCHY - UPDATED FOR BRIGHT THEME
val TextPrimary = Color(0xFF0F172A)          // #0F172A - Primary text (darker)
val TextSecondary = Color(0xFF475569)        // #475569 - Secondary text
val TextHint = Color(0xFF94A3B8)             // #94A3B8 - Hint/placeholder text
val TextButton = Color(0xFFFFFFFF)           // Button text (unchanged)

// 👤 ICON & AVATAR SYSTEM
val AvatarSize = 120.dp
val AvatarBorder = Color(0x4DFFFFFF)         // #FFFFFF @ 30% alpha (unchanged)
val IconTint = Color(0xFF0F172A)             // #0F172A - Matches primary text
val SelectedIconTint = Color(0xFF2B6CEE)     // (unchanged)

// 🎨 ADDITIONAL COLOR ALIASES FOR UNIFIED LOGIN SCREEN
val BrandTeal = Color(0xFF2D6464)            // #2D6464 - Brand teal color
val TitleColor = TextPrimary                 // #0F172A
val SubtitleColor = TextSecondary            // #475569
val CardBg = GlassCardFill                   // rgba(255,255,255,0.65)
val CardBorder = GlassCardBorder             // rgba(255,255,255,0.25)
val BtnGreen = PrimaryButtonColor            // rgba(34,197,94,0.85)
val FieldBg = InputFieldBackground           // rgba(255,255,255,0.75)
val FieldBorder = Color(0x99FFFFFF)          // rgba(255,255,255,0.6)
val FieldText = TextPrimary                  // #0F172A
val FieldHint = TextHint                     // #94A3B8
val FieldIcon = TextSecondary                // #475569
val SwitchLabel = TextSecondary.copy(alpha = 0.6f)  // Secondary text @ 60%
val DividerColor = Color(0x66FFFFFF)         // rgba(255,255,255,0.4)

// 🎨 HEADER PILL COLORS
val HeaderPillBg = Color(0x66FFFFFF)         // rgba(255,255,255,0.4) - More transparent
val HeaderPillBorder = Color(0x99FFFFFF)     // rgba(255,255,255,0.6) - Visible border

// 📏 SPACING GRID (multiples of 8dp)
val OuterPadding = 18.dp
val CardInnerPadding = 24.dp
val InputGap = 16.dp
val ManagementCardHeight = 80.dp

// ─── GLASSMORPHISM COMPONENTS ───────────────────────────────────────────────

@Composable
fun GlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background gradient layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(BackgroundTop, BackgroundBottom)
                    )
                )
        )
        // Overlay tint layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundOverlay)
        )
        // Content
        content()
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    androidx.compose.material3.Card(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(GlassCardRadius),
                ambientColor = Color.Black.copy(alpha = 0.1f),
                spotColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(GlassCardRadius),
        colors = CardDefaults.cardColors(
            containerColor = GlassCardFill
        ),
        border = BorderStroke(1.5.dp, GlassCardBorder)
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(CardInnerPadding),
            content = content
        )
    }
}

@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(PrimaryButtonRadius),
                ambientColor = PrimaryButtonShadow,
                spotColor = PrimaryButtonShadow
            ),
        shape = RoundedCornerShape(PrimaryButtonRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryButtonColor,
            disabledContainerColor = PrimaryButtonColor.copy(alpha = 0.5f),
            contentColor = TextButton,
            disabledContentColor = TextButton.copy(alpha = 0.7f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        ),
        enabled = enabled,
        content = content
    )
}

@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
    singleLine: Boolean = true,
    enabled: Boolean = true
) {
    androidx.compose.material3.OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            androidx.compose.material3.Text(
                text = placeholder,
                color = TextHint,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal
            )
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        modifier = modifier
            .height(InputFieldHeight),
        shape = RoundedCornerShape(InputFieldRadius),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GlassCardBorder,
            unfocusedBorderColor = GlassCardBorder.copy(alpha = 0.7f),
            focusedContainerColor = InputFieldBackground,
            unfocusedContainerColor = InputFieldBackground,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = PrimaryButtonColor,
            disabledBorderColor = GlassCardBorder.copy(alpha = 0.5f),
            disabledContainerColor = InputFieldBackground.copy(alpha = 0.7f),
            disabledTextColor = TextSecondary
        ),
        textStyle = TextStyle(
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        ),
        singleLine = singleLine,
        enabled = enabled
    )
}

@Composable
fun GlassAvatar(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(AvatarSize)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                ambientColor = Color.Black.copy(alpha = 0.1f),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .border(
                width = 2.dp,
                color = AvatarBorder,
                shape = CircleShape
            )
            .background(GlassCardFill, CircleShape)
    ) {
        content()
    }
}