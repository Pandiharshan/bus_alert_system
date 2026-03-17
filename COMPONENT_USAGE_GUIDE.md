# Component Library Usage Guide

Quick reference for using reusable components in Campus Bus Buddy.

## Import Statement
```kotlin
import com.campusbussbuddy.ui.components.*
```

---

## Common Patterns

### 1. Screen Layout with Top Bar
```kotlin
@Composable
fun MyScreen(onBackClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar
        TopBarLayout(
            title = "Screen Title",
            subtitle = "Subtitle",
            onBackClick = onBackClick,
            trailingContent = {
                ActionButtonCircle(
                    iconRes = R.drawable.ic_settings,
                    contentDescription = "Settings",
                    onClick = { /* Handle click */ }
                )
            }
        )
        
        // Content
        Column(modifier = Modifier.padding(20.dp)) {
            // Your content here
        }
    }
}
```

### 2. Card with Content
```kotlin
GlassCardContainer {
    SectionTitle("Card Title")
    Spacer(modifier = Modifier.height(12.dp))
    Text("Card content goes here")
}
```

### 3. Stats Display
```kotlin
Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(16.dp)
) {
    StatCard(
        value = "24",
        label = "BOARDED HERE",
        valueColor = Color(0xFF4CAF50),
        modifier = Modifier.weight(1f)
    )
    
    StatCard(
        value = "42",
        label = "TOTAL ONBOARD",
        valueColor = Color(0xFF2A2A2A),
        modifier = Modifier.weight(1f)
    )
}
```

### 4. Tab Switching
```kotlin
var selectedTab by remember { mutableStateOf("CURRENT") }

AnimatedTabSelector(
    tabs = listOf("PAST", "CURRENT", "UPCOMING"),
    selectedTab = selectedTab,
    onTabSelected = { selectedTab = it }
)
```

### 5. Progress Tracking
```kotlin
Column {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SectionSubtitle("ATTENDANCE GOAL")
        SectionSubtitle("80% REACHED")
    }
    
    Spacer(modifier = Modifier.height(8.dp))
    
    RoundedProgressBar(progress = 0.8f)
}
```

### 6. Action Button
```kotlin
PrimaryActionButton(
    text = "Complete Trip",
    onClick = { /* Handle click */ },
    iconRes = R.drawable.ic_check_circle
)
```

### 7. Bottom Navigation
```kotlin
var selectedNav by remember { mutableStateOf("home") }

BottomNavBar {
    BottomNavItem(
        icon = R.drawable.ic_home,
        label = "Home",
        isSelected = selectedNav == "home",
        onClick = { selectedNav = "home" }
    )
    
    BottomNavItem(
        icon = R.drawable.ic_settings,
        label = "Settings",
        isSelected = selectedNav == "settings",
        onClick = { selectedNav = "settings" }
    )
}
```

### 8. List Items
```kotlin
Column(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
) {
    repeat(5) { index ->
        GlassListCard(
            onClick = { /* Handle click */ }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_bus),
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text("Item $index", fontWeight = FontWeight.Bold)
                Text("Subtitle", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
```

### 9. Status Display
```kotlin
Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
) {
    StatusIndicator(
        text = "ACTIVE STOP",
        dotColor = Color(0xFF4CAF50)
    )
    
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(Color(0xFF4CAF50), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_pin_drop),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}
```

### 10. Info Display
```kotlin
Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    InfoRow(
        iconRes = R.drawable.ic_directions_bus_vector,
        label = "BUS NUMBER",
        value = "42-B"
    )
    
    InfoRow(
        iconRes = R.drawable.ic_my_location,
        label = "CURRENT STOP",
        value = "North Campus Terminal"
    )
    
    InfoRow(
        iconRes = R.drawable.ic_calendar_today,
        label = "DEPARTURE",
        value = "2:30 PM"
    )
}
```

---

## Component Reference

### GlassCardContainer
```kotlin
GlassCardContainer(
    modifier = Modifier.fillMaxWidth(),
    onClick = null,  // Optional click handler
    content = { /* Column content */ }
)
```

### BackButtonCircle
```kotlin
BackButtonCircle(
    onBackClick = { /* Navigate back */ },
    modifier = Modifier
)
```

### ActionButtonCircle
```kotlin
ActionButtonCircle(
    iconRes = R.drawable.ic_settings,
    contentDescription = "Settings",
    onClick = { /* Handle click */ },
    modifier = Modifier,
    iconTint = Color(0xFF4CAF50)
)
```

### StatCard
```kotlin
StatCard(
    value = "42",
    label = "TOTAL ITEMS",
    modifier = Modifier.weight(1f),
    valueColor = Color(0xFF2A2A2A),
    backgroundColor = Color.White.copy(alpha = 0.4f)
)
```

### SectionTitle
```kotlin
SectionTitle(
    text = "Title",
    modifier = Modifier,
    fontSize = 18,
    color = Color(0xFF1A1A1A)
)
```

### SectionSubtitle
```kotlin
SectionSubtitle(
    text = "SUBTITLE",
    modifier = Modifier,
    color = Color(0xFF4A5F5F)
)
```

### AnimatedTabSelector
```kotlin
AnimatedTabSelector(
    tabs = listOf("TAB1", "TAB2", "TAB3"),
    selectedTab = selectedTab,
    onTabSelected = { selectedTab = it },
    modifier = Modifier.fillMaxWidth()
)
```

### RoundedProgressBar
```kotlin
RoundedProgressBar(
    progress = 0.75f,  // 0.0 to 1.0
    modifier = Modifier.fillMaxWidth(),
    backgroundColor = Color.White.copy(alpha = 0.3f),
    progressColor = Color(0xFF4CAF50),
    height = 8
)
```

### StatusIndicator
```kotlin
StatusIndicator(
    text = "ACTIVE STOP",
    dotColor = Color(0xFF4CAF50),
    textColor = Color(0xFF4A5F5F),
    modifier = Modifier
)
```

### PrimaryActionButton
```kotlin
PrimaryActionButton(
    text = "Action",
    onClick = { /* Handle click */ },
    modifier = Modifier.fillMaxWidth(),
    iconRes = R.drawable.ic_check_circle,
    backgroundColor = Color(0xFF4CAF50),
    contentColor = Color.White
)
```

### IconContainer
```kotlin
IconContainer(
    iconRes = R.drawable.ic_qr_code,
    contentDescription = "QR Code",
    modifier = Modifier,
    size = 60,
    iconSize = 32,
    backgroundColor = Color.White.copy(alpha = 0.8f),
    iconTint = Color(0xFF2A2A2A),
    cornerRadius = 12
)
```

### TextBadge
```kotlin
TextBadge(
    text = "SCAN ACTIVE",
    modifier = Modifier,
    backgroundColor = Color(0xFF4CAF50),
    textColor = Color.White,
    fontSize = 10
)
```

### TopBarLayout
```kotlin
TopBarLayout(
    title = "Screen Title",
    onBackClick = { /* Navigate back */ },
    modifier = Modifier.fillMaxWidth(),
    subtitle = "Optional Subtitle",
    trailingContent = { /* Optional trailing widget */ }
)
```

### BottomNavBar
```kotlin
BottomNavBar(
    modifier = Modifier.fillMaxWidth()
) {
    // Add BottomNavItem children
}
```

### BottomNavItem
```kotlin
BottomNavItem(
    icon = R.drawable.ic_home,
    label = "Home",
    onClick = { /* Handle click */ },
    modifier = Modifier,
    isSelected = true
)
```

### GlassListCard
```kotlin
GlassListCard(
    modifier = Modifier.fillMaxWidth(),
    onClick = { /* Handle click */ }
) {
    // Row content
}
```

### InfoRow
```kotlin
InfoRow(
    iconRes = R.drawable.ic_directions_bus_vector,
    label = "BUS NUMBER",
    value = "42-B",
    modifier = Modifier.fillMaxWidth()
)
```

### AlertMessageBox
```kotlin
AlertMessageBox(
    message = "This is an alert message",
    modifier = Modifier.fillMaxWidth()
)
```

---

## Design System Colors

```kotlin
// Backgrounds
Background:     Color(0xFFE6E6E6)
Surface:        Color(0xFFE6E6E6)

// Text
TextPrimary:    Color(0xFF1A1A1A)
TextSecondary:  Color(0xFF4A5F5F)

// Accents
AccentGreen:    Color(0xFF4CAF50)
AccentPurple:   Color(0xFF8A5CFF)

// Shadows
LightShadow:    Color(0xFFFFFFFF)
DarkShadow:     Color(0xFF808080)
```

---

## Tips & Best Practices

1. **Always use components** - Don't create custom cards/buttons, use the library
2. **Consistent spacing** - Use 12dp, 16dp, 20dp, 24dp for spacing
3. **Proper hierarchy** - Use SectionTitle for main titles, SectionSubtitle for labels
4. **Ripple feedback** - All clickable components have ripple effects
5. **Glass styling** - Use GlassCardContainer for all card sections
6. **Color consistency** - Use design system colors, not custom colors
7. **Icon sizing** - Use 20dp for small icons, 24dp for medium, 32dp for large
8. **Padding** - Cards have 20dp internal padding by default
9. **Shadows** - All components have consistent shadow styling
10. **Animations** - Use bounceClick for button interactions

---

## Common Mistakes to Avoid

❌ Creating custom cards instead of using `GlassCardContainer`
❌ Using different colors than the design system
❌ Inconsistent spacing and padding
❌ Not using `SectionTitle` and `SectionSubtitle`
❌ Creating custom buttons instead of `PrimaryActionButton`
❌ Forgetting to add click handlers to interactive components
❌ Using different shadow values
❌ Not using `AnimatedTabSelector` for tabs
❌ Creating custom progress bars instead of `RoundedProgressBar`
❌ Inconsistent icon sizes

---

## Questions?

Refer to `TripSupervisorScreen.kt` for a complete example of how to use all components together.
