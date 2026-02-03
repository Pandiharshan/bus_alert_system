package com.campusbussbuddy.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Professional Color System - Material 3 Inspired
object AppColors {
    // Primary Colors
    val Primary = Color(0xFF1976D2)
    val PrimaryVariant = Color(0xFF1565C0)
    val PrimaryContainer = Color(0xFFE3F2FD)
    
    // Background Colors
    val Background = Color(0xFFFAFAFA)
    val Surface = Color.White
    val SurfaceVariant = Color(0xFFF5F5F5)
    
    // Text Colors
    val OnBackground = Color(0xFF1C1B1F)
    val OnSurface = Color(0xFF1C1B1F)
    val OnSurfaceVariant = Color(0xFF49454F)
    val OnPrimary = Color.White
    
    // Neutral Colors
    val Outline = Color(0xFFE0E0E0)
    val OutlineVariant = Color(0xFFF0F0F0)
    
    // Status Colors
    val Success = Color(0xFF4CAF50)
    val Warning = Color(0xFFFF9800)
    val Error = Color(0xFFF44336)
    val Info = Color(0xFF2196F3)
    
    // Semantic Colors
    val SuccessContainer = Color(0xFFE8F5E8)
    val WarningContainer = Color(0xFFFFF3E0)
    val ErrorContainer = Color(0xFFFFEBEE)
    val InfoContainer = Color(0xFFE3F2FD)
}

// Typography Scale - Material 3
object AppTypography {
    val DisplayLarge = 28.sp
    val DisplayMedium = 24.sp
    val DisplaySmall = 22.sp
    
    val HeadlineLarge = 20.sp
    val HeadlineMedium = 18.sp
    val HeadlineSmall = 16.sp
    
    val BodyLarge = 16.sp
    val BodyMedium = 14.sp
    val BodySmall = 12.sp
    
    val LabelLarge = 14.sp
    val LabelMedium = 12.sp
    val LabelSmall = 11.sp
}

// Spacing System - 8dp Grid
object AppSpacing {
    val XS = 4.dp
    val S = 8.dp
    val M = 16.dp
    val L = 24.dp
    val XL = 32.dp
    val XXL = 48.dp
}

// Corner Radius
object AppRadius {
    val XS = 4.dp
    val S = 8.dp
    val M = 12.dp
    val L = 16.dp
    val XL = 20.dp
}

// Elevation
object AppElevation {
    val None = 0.dp
    val XS = 1.dp
    val S = 2.dp
    val M = 4.dp
    val L = 8.dp
    val XL = 12.dp
}