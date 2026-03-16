package com.campusbussbuddy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusbussbuddy.R

/**
 * Reusable glass card container with consistent styling
 */
@Composable
fun GlassCardContainer(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .let { cardModifier ->
                if (onClick != null) {
                    cardModifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(
                            bounded = true,
                            color = Color(0xFF6B9A92).copy(alpha = 0.2f)
                        )
                    ) { onClick() }
                } else cardModifier
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.25f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.30f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            content = content
        )
    }
}

/**
 * Circular back button with glass effect
 */
@Composable
fun BackButtonCircle(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .shadow(
                elevation = 4.dp,
                shape = CircleShape,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .background(
                Color.White.copy(alpha = 0.25f),
                CircleShape
            )
            .border(
                1.dp,
                Color.White.copy(alpha = 0.30f),
                CircleShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                    color = Color.White.copy(0.20f)
                )
            ) { onBackClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_left),
            contentDescription = "Back",
            tint = Color(0xFF2A2A2A),
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * Circular action button with glass effect
 */
@Composable
fun ActionButtonCircle(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = Color(0xFF4CAF50)
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .shadow(
                elevation = 4.dp,
                shape = CircleShape,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .background(
                Color.White.copy(alpha = 0.25f),
                CircleShape
            )
            .border(
                1.dp,
                Color.White.copy(alpha = 0.30f),
                CircleShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                    color = Color.White.copy(0.20f)
                )
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * Stat card showing a number and label
 */
@Composable
fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Color(0xFF2A2A2A),
    backgroundColor: Color = Color.White.copy(alpha = 0.4f)
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.50f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A5F5F),
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Section title with consistent styling
 */
@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: Int = 18,
    color: Color = Color(0xFF1A1A1A)
) {
    Text(
        text = text,
        fontSize = fontSize.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = modifier
    )
}

/**
 * Section subtitle with consistent styling
 */
@Composable
fun SectionSubtitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF4A5F5F)
) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        letterSpacing = 1.sp,
        modifier = modifier
    )
}

/**
 * Animated tab selector
 */
@Composable
fun AnimatedTabSelector(
    tabs: List<String>,
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.25f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.30f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            tabs.forEach { tab ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .background(
                            if (selectedTab == tab) Color.White.copy(alpha = 0.8f)
                            else Color.Transparent,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(
                                bounded = true,
                                color = Color(0xFF6B9A92).copy(alpha = 0.2f)
                            )
                        ) { onTabSelected(tab) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        fontSize = 12.sp,
                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == tab) Color(0xFF2A2A2A) else Color(0xFF4A5F5F),
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

/**
 * Rounded progress bar with consistent styling
 */
@Composable
fun RoundedProgressBar(
    progress: Float, // 0.0 to 1.0
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White.copy(alpha = 0.3f),
    progressColor: Color = Color(0xFF4CAF50),
    height: Int = 8
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .background(
                backgroundColor,
                RoundedCornerShape((height / 2).dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .background(
                    progressColor,
                    RoundedCornerShape((height / 2).dp)
                )
        )
    }
}

/**
 * Status indicator with dot and text
 */
@Composable
fun StatusIndicator(
    text: String,
    dotColor: Color = Color(0xFF4CAF50),
    textColor: Color = Color(0xFF4A5F5F),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    dotColor,
                    CircleShape
                )
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            letterSpacing = 1.sp
        )
    }
}

/**
 * Primary action button with consistent styling
 */
@Composable
fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconRes: Int? = null,
    backgroundColor: Color = Color(0xFF4CAF50),
    contentColor: Color = Color.White
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(28.dp)
            ),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (iconRes != null) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
            }
            
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Icon container with background
 */
@Composable
fun IconContainer(
    iconRes: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    size: Int = 60,
    iconSize: Int = 32,
    backgroundColor: Color = Color.White.copy(alpha = 0.8f),
    iconTint: Color = Color(0xFF2A2A2A),
    cornerRadius: Int = 12
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .background(
                backgroundColor,
                RoundedCornerShape(cornerRadius.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(iconSize.dp)
        )
    }
}

/**
 * Badge with text
 */
@Composable
fun TextBadge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF4CAF50),
    textColor: Color = Color.White,
    fontSize: Int = 10
) {
    Box(
        modifier = modifier
            .background(
                backgroundColor,
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            letterSpacing = 0.5.sp
        )
    }
}

/**
 * Reusable top bar layout with back button, centered title/subtitle, and optional trailing action
 */
@Composable
fun TopBarLayout(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color.Transparent)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Back Button
        BackButtonCircle(onBackClick = onBackClick)

        // Title Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SectionTitle(
                text = title,
                fontSize = 18
            )

            if (subtitle != null) {
                SectionSubtitle(text = subtitle)
            }
        }

        // Trailing content or placeholder for symmetry
        if (trailingContent != null) {
            trailingContent()
        } else {
            Spacer(modifier = Modifier.size(48.dp))
        }
    }
}

/**
 * Reusable bottom navigation bar with glass styling
 */
@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(35.dp),
                    ambientColor = Color.Black.copy(alpha = 0.08f),
                    spotColor = Color.Black.copy(alpha = 0.08f)
                ),
            shape = RoundedCornerShape(35.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.25f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Color.White.copy(alpha = 0.30f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}

/**
 * Reusable bottom navigation item
 */
@Composable
fun BottomNavItem(
    icon: Int,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = false,
                    radius = 24.dp,
                    color = Color(0xFF6B9A92).copy(alpha = 0.2f)
                )
            ) { onClick() }
            .padding(8.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = if (isSelected) Color(0xFF2A2A2A) else Color(0xFF4A5F5F),
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFF2A2A2A) else Color(0xFF4A5F5F),
            letterSpacing = 0.5.sp
        )
    }
}

/**
 * Glass card for list items (smaller padding, 20dp radius variant)
 */
@Composable
fun GlassListCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .let { cardModifier ->
                if (onClick != null) {
                    cardModifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(
                            bounded = true,
                            color = Color(0xFF6B9A92).copy(alpha = 0.2f)
                        )
                    ) { onClick() }
                } else cardModifier
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.25f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.30f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

/**
 * Reusable info row with icon, title label, and value text
 */
@Composable
fun InfoRow(
    iconRes: Int,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = Color(0xFF4A5F5F),
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A5F5F),
                letterSpacing = 1.sp
            )

            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A)
            )
        }
    }
}

/**
 * Glass alert/message box
 */
@Composable
fun AlertMessageBox(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Color.White.copy(alpha = 0.4f),
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = message,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF2A2A2A),
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
            lineHeight = 16.sp
        )
    }
}