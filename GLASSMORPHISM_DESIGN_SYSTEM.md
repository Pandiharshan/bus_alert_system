# 🪟 Glassmorphism Design System - Complete Implementation Guide

## 📋 Overview

This document describes the complete glassmorphism design system implemented across the Campus Bus Buddy app. All UI components now follow a unified glass aesthetic matching the reference UI.

---

## 🎨 Design Tokens

### Glass Layer Colors (Translucent - Never Solid)

```kotlin
GlassTokens.GlassBackground        // rgba(32,45,45,0.32)
GlassTokens.GlassBorder            // rgba(255,255,255,0.08)
GlassTokens.GlassHighlight         // rgba(255,255,255,0.06)
GlassTokens.GlassInputBg           // rgba(0,0,0,0.15)
GlassTokens.GlassInputBorder       // rgba(255,255,255,0.06)
GlassTokens.GlassInputFocusBorder  // rgba(255,255,255,0.18)
GlassTokens.GlassInputFocusBg      // rgba(255,255,255,0.06)
GlassTokens.GlassButtonBg          // rgba(255,255,255,0.08)
GlassTokens.GlassButtonHover       // rgba(255,255,255,0.14)
```

### Text & Icon Colors

```kotlin
GlassTokens.TextPrimary    // rgba(255,255,255,0.92)
GlassTokens.TextSecondary  // rgba(255,255,255,0.55)
GlassTokens.IconMuted      // rgba(255,255,255,0.65)
GlassTokens.IconActive     // rgba(255,255,255,0.70)
```

---

## 📐 Spacing System (Fixed 8dp Grid)

**STRICT RULE:** Only use these values. No random spacing.

```kotlin
GlassSpacing.Micro    = 4.dp   // micro gap
GlassSpacing.Icon     = 8.dp   // icon spacing
GlassSpacing.Inner    = 14.dp  // inner padding
GlassSpacing.Card     = 18.dp  // card padding
GlassSpacing.Section  = 22.dp  // section gap
```

---

## 🏔️ Depth System (Shadow Hierarchy)

Three depth levels for consistent z-axis layering:

```kotlin
// Level 1: Floating icons
GlassDepth.Level1Elevation = 14.dp
GlassDepth.Level1Alpha = 0.18f

// Level 2: Search bars / inputs
GlassDepth.Level2Elevation = 24.dp
GlassDepth.Level2Alpha = 0.22f

// Level 3: Main cards & forms
GlassDepth.Level3Elevation = 38.dp
GlassDepth.Level3Alpha = 0.28f
```

---

## ⏱️ Animation Timing

Consistent transitions across all interactions:

```kotlin
GlassAnimation.DurationFast = 120ms
GlassAnimation.DurationMedium = 220ms
GlassAnimation.EasingStandard = FastOutSlowInEasing
```

---

## 🧩 Core Components

### 1. GlassContainer

Base layered glass component with blur, gradient, and border.

```kotlin
GlassContainer(
    modifier = Modifier.fillMaxWidth(),
    blurRadius = 22f,
    cornerRadius = 18.dp,
    shadowElevation = GlassDepth.Level2Elevation
) {
    // Your content here
}
```

**Features:**
- Automatic blur layer
- Gradient light overlay
- Configurable shadow depth
- Consistent border styling

---

### 2. GlassInputField

Input field matching the search bar style from reference UI.

```kotlin
GlassInputField(
    value = username,
    onValueChange = { username = it },
    placeholder = "Username",
    leadingIcon = {
        Icon(
            painter = painterResource(R.drawable.ic_person),
            contentDescription = null,
            tint = GlassTokens.IconMuted,
            modifier = Modifier.size(18.dp)
        )
    }
)
```

**Features:**
- Animated focus state (220ms transition)
- Micro lift on focus (-1dp translateY)
- Icon wrapper with glass background
- Interpolated border/background colors

---

### 3. GlassIconWrapper

Consistent icon container matching sidebar style.

```kotlin
GlassIconWrapper(
    size = 28.dp,
    cornerRadius = 8.dp
) {
    Icon(
        painter = painterResource(R.drawable.ic_home),
        contentDescription = "Home",
        tint = GlassTokens.IconActive,
        modifier = Modifier.size(18.dp)
    )
}
```

**Rules:**
- Stroke-based icons only (no filled icons)
- Neutral tint (70% white opacity)
- Rounded glass capsule background

---

### 4. GlassButton

Premium glass button with press animation.

```kotlin
GlassButton(
    onClick = { /* action */ },
    modifier = Modifier.fillMaxWidth(),
    cornerRadius = 14.dp,
    height = 44.dp
) {
    Text(
        text = "Sign In",
        fontSize = 16.sp,
        fontWeight = FontWeight.W600,
        color = GlassTokens.TextPrimary
    )
}
```

**Features:**
- Scale animation on press (0.98x)
- Hover state with increased opacity
- Consistent shadow depth

---

### 5. GlassCard

Card component for forms and content sections.

```kotlin
GlassCard(
    modifier = Modifier.fillMaxWidth(),
    cornerRadius = 24.dp
) {
    // Column content with 18.dp padding
    Text("Card Title")
    Spacer(modifier = Modifier.height(GlassSpacing.Inner))
    Text("Card content")
}
```

---

## 🎯 Implementation Rules

### ✅ DO

1. **Use only glass tokens** for colors (never solid colors)
2. **Follow spacing grid** strictly (4dp, 8dp, 14dp, 18dp, 22dp)
3. **Apply consistent depth** based on component hierarchy
4. **Use stroke icons** with neutral tint
5. **Animate interactions** with standard timing (220ms)
6. **Layer structure**: Blur → Gradient → Border → Content

### ❌ DON'T

1. ❌ Use solid black cards
2. ❌ Use hard shadows
3. ❌ Use flat material surfaces
4. ❌ Use filled icons
5. ❌ Use random spacing values
6. ❌ Mix shadow styles between pages
7. ❌ Apply blur directly on content (use container)

---

## 🏗️ Layer Stacking Model

Every glass element has 4 visual layers:

```
Content Layer (text/icons/input)
    ↓
Glass Tint Layer
    ↓
Blur Layer
    ↓
Background Wallpaper
```

**Implementation:**
```kotlin
GlassContainer {  // Handles blur + gradient + border
    BlurLayer (absolute)
    GradientOverlay (absolute)
    ContentLayer (relative)
}
```

---

## 🎨 Visual Pipeline (Build Order)

When creating new screens, follow this exact order:

1. Define design tokens (colors, blur, shadow, animation)
2. Build GlassContainer with layered structure
3. Add gradient overlay + lighting
4. Create InputField components
5. Apply icon wrapper style
6. Add focus animation + border transition
7. Implement depth shadows
8. Add edge-light pseudo layer
9. Apply typography system
10. Tune contrast dynamically

---

## 📱 Component Usage Examples

### Login Form

```kotlin
GlassCard(
    modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp)
) {
    // Header
    GlassIconWrapper(size = 36.dp) {
        Icon(...)
    }
    
    Spacer(modifier = Modifier.height(GlassSpacing.Section))
    
    // Input fields
    GlassInputField(
        value = username,
        onValueChange = { username = it },
        placeholder = "Username",
        leadingIcon = { /* icon */ }
    )
    
    Spacer(modifier = Modifier.height(GlassSpacing.Inner))
    
    GlassInputField(
        value = password,
        onValueChange = { password = it },
        placeholder = "Password",
        visualTransformation = PasswordVisualTransformation()
    )
    
    Spacer(modifier = Modifier.height(GlassSpacing.Section))
    
    // Button
    GlassButton(
        onClick = { /* login */ },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Sign In")
    }
}
```

### Dashboard Card

```kotlin
GlassContainer(
    modifier = Modifier
        .fillMaxWidth()
        .padding(GlassSpacing.Card),
    cornerRadius = 16.dp,
    shadowElevation = GlassDepth.Level2Elevation
) {
    Column(modifier = Modifier.padding(GlassSpacing.Card)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            GlassIconWrapper {
                Icon(...)
            }
            Spacer(modifier = Modifier.width(GlassSpacing.Icon))
            Text(
                text = "Dashboard",
                color = GlassTokens.TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.W600
            )
        }
        
        Spacer(modifier = Modifier.height(GlassSpacing.Inner))
        
        Text(
            text = "Content here",
            color = GlassTokens.TextSecondary,
            fontSize = 14.sp
        )
    }
}
```

---

## 🔄 Migration Guide

### Migrating Existing Screens

1. **Replace solid backgrounds** with `GlassContainer`
2. **Replace TextField** with `GlassInputField`
3. **Replace Button** with `GlassButton`
4. **Wrap icons** in `GlassIconWrapper`
5. **Update spacing** to use `GlassSpacing` tokens
6. **Update colors** to use `GlassTokens`

### Before:
```kotlin
Card(
    colors = CardDefaults.cardColors(
        containerColor = Color.White
    )
) {
    TextField(...)
}
```

### After:
```kotlin
GlassCard {
    GlassInputField(...)
}
```

---

## 🎭 Interaction Details

### Focus State
- Border opacity: 0.06 → 0.18 (220ms)
- Background opacity: 0.15 → 0.06 (220ms)
- Micro lift: translateY(-1dp)

### Button Press
- Scale: 1.0 → 0.98 (120ms)
- No color change (maintains glass effect)

### Hover (Desktop)
- Background opacity: +0.06
- Transition: 220ms

---

## 🚀 Performance Optimization

1. **Cache gradient brushes** with `remember`
2. **Avoid recomposition** inside blur layers
3. **Use drawWithCache** for noise layers
4. **Apply blur only at container level** (not per element)

```kotlin
val glassGradient = remember {
    GlassTokens.glassLightGradient()
}
```

---

## ✅ Checklist for New Screens

- [ ] Uses `GlassContainer` for all cards/panels
- [ ] Uses `GlassInputField` for all inputs
- [ ] Uses `GlassButton` for all buttons
- [ ] Uses `GlassIconWrapper` for all icons
- [ ] Follows `GlassSpacing` grid (no random values)
- [ ] Uses `GlassTokens` colors (no solid colors)
- [ ] Applies correct depth level shadows
- [ ] Includes focus animations (220ms)
- [ ] Uses stroke icons only (no filled)
- [ ] Matches reference UI visual style

---

## 📚 Reference

All components are defined in:
```
app/src/main/java/com/campusbussbuddy/ui/theme/AppTheme.kt
```

**Key Objects:**
- `GlassTokens` - Color and gradient tokens
- `GlassSpacing` - Spacing system
- `GlassDepth` - Shadow hierarchy
- `GlassAnimation` - Timing constants

---

## 🎯 Final Notes

This glassmorphism system ensures:
- **Visual consistency** across all screens
- **Premium feel** with subtle animations
- **Performance** through optimized layering
- **Maintainability** with reusable components
- **Accessibility** with proper contrast ratios

Every screen must visually integrate with this glass system. No legacy UI allowed.
