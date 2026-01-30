package com.campusbussbuddy.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Color Palette - Pudgy Penguins Inspired
object AppColors {
    // Primary Gradients
    val PrimaryGradient = listOf(Color(0xFF667eea), Color(0xFF764ba2))
    val SecondaryGradient = listOf(Color(0xFF4facfe), Color(0xFF00f2fe))
    val AccentGradient = listOf(Color(0xFF43e97b), Color(0xFF38f9d7))
    val WarmGradient = listOf(Color(0xFFfa709a), Color(0xFFfee140))
    
    // Background Colors
    val BackgroundLight = Color(0xFFFAFAFA)
    val CardBackground = Color.White
    val SurfaceAlpha = Color.White.copy(alpha = 0.95f)
    
    // Text Colors
    val TextPrimary = Color(0xFF2D3748)
    val TextSecondary = Color(0xFF718096)
    val TextOnDark = Color.White
    val TextOnDarkSecondary = Color.White.copy(alpha = 0.8f)
    
    // Status Colors
    val Success = Color(0xFF38A169)
    val Warning = Color(0xFFD69E2E)
    val Error = Color(0xFFE53E3E)
    val Info = Color(0xFF3182CE)
}

// Typography Scale
object AppTypography {
    val DisplayLarge = 32.sp
    val DisplayMedium = 28.sp
    val DisplaySmall = 24.sp
    
    val HeadlineLarge = 22.sp
    val HeadlineMedium = 20.sp
    val HeadlineSmall = 18.sp
    
    val BodyLarge = 16.sp
    val BodyMedium = 14.sp
    val BodySmall = 12.sp
    
    val LabelLarge = 14.sp
    val LabelMedium = 12.sp
    val LabelSmall = 10.sp
}

// Spacing System
object AppSpacing {
    val ExtraSmall = 4.dp
    val Small = 8.dp
    val Medium = 16.dp
    val Large = 24.dp
    val ExtraLarge = 32.dp
    val Huge = 48.dp
}

// Corner Radius
object AppRadius {
    val Small = 8.dp
    val Medium = 12.dp
    val Large = 16.dp
    val ExtraLarge = 20.dp
    val Huge = 24.dp
}

// Elevation
object AppElevation {
    val Small = 4.dp
    val Medium = 8.dp
    val Large = 12.dp
    val ExtraLarge = 16.dp
}