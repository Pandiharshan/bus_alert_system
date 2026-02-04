package com.campusbussbuddy.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Omnibus Light Theme - Based on UI Designs
object AppColors {
    // Primary Colors - Light Theme with Purple/Teal Accents
    val Primary = Color(0xFF9C88D4)  // Purple accent from designs
    val PrimaryDark = Color(0xFF7B68B8)  // Darker purple
    val PrimaryContainer = Color(0xFFE8E0F5)  // Light purple container
    val TealAccent = Color(0xFF7DD3C0)  // Teal accent for buttons
    val TealDark = Color(0xFF5BB5A2)  // Darker teal
    
    // Background Colors - Light Theme
    val Background = Color(0xFFF5F5F5)  // Light gray background
    val BackgroundLight = Color(0xFFFFFFFF)  // Pure white
    val BackgroundDark = Color(0xFF1A1A1A)  // Dark background for contrast
    val Surface = Color(0xFFFFFFFF)  // White card surface
    val SurfaceVariant = Color(0xFFF8F8F8)  // Light gray surface
    val SurfaceDim = Color(0xFFF0F0F0)  // Dimmed surface
    
    // Text Colors - Dark on Light
    val OnBackground = Color(0xFF1A1A1A)  // Dark text on light
    val OnSurface = Color(0xFF1A1A1A)  // Dark text on surface
    val OnSurfaceVariant = Color(0xFF666666)  // Gray text
    val OnPrimary = Color(0xFFFFFFFF)  // White text on purple
    val TextSecondary = Color(0xFF888888)  // Muted gray text
    val TextTertiary = Color(0xFFAAAAAA)  // Light gray text
    
    // Border & Outline Colors
    val Outline = Color(0xFFE0E0E0)  // Light border color
    val OutlineVariant = Color(0xFFF0F0F0)  // Subtle border
    val Border = Color(0xFFE0E0E0)  // Standard border
    val BorderLight = Color(0xFFF0F0F0)  // Lighter border
    
    // Status Colors
    val Success = Color(0xFF4CAF50)  // Green success
    val Warning = Color(0xFFFF9800)  // Orange warning
    val Error = Color(0xFFF44336)  // Red error
    val Info = Color(0xFF2196F3)  // Blue info
    val Online = Color(0xFF4CAF50)  // Online status green
    
    // Status Container Colors
    val SuccessContainer = Color(0xFFE8F5E8)  // Light green container
    val WarningContainer = Color(0xFFFFF3E0)  // Light orange container
    val ErrorContainer = Color(0xFFFFEBEE)  // Light red container
    val InfoContainer = Color(0xFFE3F2FD)  // Light blue container
    
    // Special UI Colors
    val CardBackground = Color(0xFFFFFFFF)  // White card background
    val CardElevated = Color(0xFFFFFFFF)  // White elevated card
    val ButtonBackground = Color(0xFF7DD3C0)  // Teal button background
    val ButtonText = Color(0xFFFFFFFF)  // White button text
    val IconTint = Color(0xFF666666)  // Gray icon tint
    val IconActive = Color(0xFF9C88D4)  // Purple active icon
    
    // Map & Location Colors
    val LocationPin = Color(0xFF9C88D4)  // Purple location marker
    val RouteActive = Color(0xFF4CAF50)  // Active route
    val RouteInactive = Color(0xFFAAAAAA)  // Inactive route
    
    // Status Indicator Colors
    val StatusOnline = Color(0xFF4CAF50)  // Online status
    val StatusOffline = Color(0xFF9E9E9E)  // Offline status
    val StatusActive = Color(0xFF9C88D4)  // Active status
    val StatusPending = Color(0xFFFF9800)  // Pending status
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