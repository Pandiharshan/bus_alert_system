package com.campusbussbuddy.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusbussbuddy.ui.theme.*

// Premium App Card Component
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier.clip(RoundedCornerShape(AppRadius.ExtraLarge)),
            colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceAlpha),
            elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.Large)
        ) {
            Column(
                modifier = Modifier.padding(AppSpacing.Large),
                content = content
            )
        }
    } else {
        Card(
            modifier = modifier.clip(RoundedCornerShape(AppRadius.ExtraLarge)),
            colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceAlpha),
            elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.Large)
        ) {
            Column(
                modifier = Modifier.padding(AppSpacing.Large),
                content = content
            )
        }
    }
}

// Premium Gradient Button
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradient: List<Color> = AppColors.PrimaryGradient,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(AppRadius.Large)),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        enabled = enabled
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (enabled) gradient else listOf(Color.Gray, Color.Gray)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = AppTypography.HeadlineSmall,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextOnDark
            )
        }
    }
}

// Section Header with Subtitle
@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            fontSize = AppTypography.DisplaySmall,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextOnDark
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                fontSize = AppTypography.BodyLarge,
                color = AppColors.TextOnDarkSecondary,
                modifier = Modifier.padding(top = AppSpacing.ExtraSmall)
            )
        }
    }
}

// Premium Action Item
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradient: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppRadius.Large)),
        colors = CardDefaults.cardColors(containerColor = AppColors.CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.Medium)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.Large),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(AppRadius.Medium))
                    .background(brush = Brush.linearGradient(gradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(24.dp),
                    tint = AppColors.TextOnDark
                )
            }
            
            Spacer(modifier = Modifier.width(AppSpacing.Medium))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = AppTypography.HeadlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = AppTypography.BodyMedium,
                    color = AppColors.TextSecondary
                )
            }
            
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Navigate",
                tint = AppColors.TextSecondary
            )
        }
    }
}

// Stat Card Component
@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    gradient: List<Color>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clip(RoundedCornerShape(AppRadius.ExtraLarge)),
        colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceAlpha),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.Large)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppSpacing.Large),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = title,
                        fontSize = AppTypography.BodyMedium,
                        color = AppColors.TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = value,
                        fontSize = AppTypography.DisplaySmall,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(AppRadius.Medium))
                        .background(brush = Brush.linearGradient(gradient)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        modifier = Modifier.size(20.dp),
                        tint = AppColors.TextOnDark
                    )
                }
            }
        }
    }
}

// Animated Gradient Background
@Composable
fun AnimatedGradientBackground(
    colors: List<Color>,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = colors.mapIndexed { index, color ->
                        if (index % 2 == 0) {
                            color.copy(alpha = 0.8f + animatedOffset * 0.2f)
                        } else {
                            color.copy(alpha = 0.9f)
                        }
                    }
                )
            ),
        content = content
    )
}

// Premium Top Bar
@Composable
fun AppTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    onActionClick: (() -> Unit)? = null,
    actionIcon: ImageVector? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.Large, vertical = AppSpacing.Medium)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBackClick != null) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(AppRadius.Medium))
                    .background(AppColors.TextOnDark.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = AppColors.TextOnDark,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(AppSpacing.Medium))
        }
        
        Text(
            text = title,
            fontSize = AppTypography.DisplaySmall,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextOnDark,
            modifier = Modifier.weight(1f)
        )
        
        if (onActionClick != null && actionIcon != null) {
            IconButton(
                onClick = onActionClick,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(AppRadius.Medium))
                    .background(AppColors.TextOnDark.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = actionIcon,
                    contentDescription = "Action",
                    tint = AppColors.TextOnDark,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// Info Row Component
@Composable
fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.Small),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = AppTypography.BodyLarge,
            color = AppColors.TextSecondary
        )
        Text(
            text = value,
            fontSize = AppTypography.BodyLarge,
            fontWeight = FontWeight.Medium,
            color = AppColors.TextPrimary
        )
    }
}