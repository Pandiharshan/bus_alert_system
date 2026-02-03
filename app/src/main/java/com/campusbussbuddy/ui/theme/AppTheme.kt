package com.campusbussbuddy.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// SmartBus Dark Green Theme - Professional Bus Tracking System
object AppColors {
    // Primary Colors - Dark Green Theme
    val Primary = Color(0xFF00E676)  // Bright green accent
    val PrimaryDark = Color(0xFF00C853)  // Darker green
    val PrimaryContainer = Color(0xFF1B5E20)  // Dark green container
    
    // Background Colors - Dark Theme
    val Background = Color(0xFF0A1A0D)  // Very dark green-black
    val BackgroundDark = Color(0xFF0F1B11)  // Main dark background
    val Surface = Color(0xFF1A2E1D)  // Card surface
    val SurfaceVariant = Color(0xFF243529)  // Elevated surface
    val SurfaceDim = Color(0xFF162419)  // Dimmed surface
    
    // Text Colors - High Contrast
    val OnBackground = Color(0xFFFFFFFF)  // White text on dark
    val OnSurface = Color(0xFFFFFFFF)  // White text on surface
    val OnSurfaceVariant = Color(0xFFB8C8BC)  // Light green-gray text
    val OnPrimary = Color(0xFF000000)  // Black text on green
    val TextSecondary = Color(0xFF9CBAA8)  // Muted green-gray
    val TextTertiary = Color(0xFF6B7B6F)  // Darker muted text
    
    // Border & Outline Colors
    val Outline = Color(0xFF3B5445)  // Border color
    val OutlineVariant = Color(0xFF2A3F2E)  // Subtle border
    val Border = Color(0xFF3B5445)  // Standard border
    val BorderLight = Color(0xFF4A5F4E)  // Lighter border
    
    // Status Colors - Consistent with green theme
    val Success = Color(0xFF4CAF50)  // Green success
    val Warning = Color(0xFFFF9800)  // Orange warning
    val Error = Color(0xFFF44336)  // Red error
    val Info = Color(0xFF2196F3)  // Blue info
    
    // Status Container Colors
    val SuccessContainer = Color(0xFF1B4332)  // Dark green container
    val WarningContainer = Color(0xFF3D2914)  // Dark orange container
    val ErrorContainer = Color(0xFF3D1A16)  // Dark red container
    val InfoContainer = Color(0xFF1A2332)  // Dark blue container
    
    // Special UI Colors
    val CardBackground = Color(0xFF1A2E1D)  // Card background
    val CardElevated = Color(0xFF243529)  // Elevated card
    val ButtonBackground = Color(0xFF00E676)  // Button background
    val ButtonText = Color(0xFF000000)  // Button text
    val IconTint = Color(0xFFB8C8BC)  // Icon tint
    val IconActive = Color(0xFF00E676)  // Active icon
    
    // Map & Location Colors
    val LocationPin = Color(0xFF00E676)  // Location marker
    val RouteActive = Color(0xFF4CAF50)  // Active route
    val RouteInactive = Color(0xFF6B7B6F)  // Inactive route
    
    // Status Indicator Colors
    val StatusOnline = Color(0xFF4CAF50)  // Online status
    val StatusOffline = Color(0xFF9E9E9E)  // Offline status
    val StatusActive = Color(0xFF00E676)  // Active status
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