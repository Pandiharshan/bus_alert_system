package com.campusbussbuddy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.campusbussbuddy.ui.theme.*

// Professional App Card
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        onClick = onClick ?: {},
        modifier = modifier.clip(RoundedCornerShape(AppRadius.M)),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.S),
        shape = RoundedCornerShape(AppRadius.M)
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.M),
            content = content
        )
    }
}

// Primary Button
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.Primary,
            contentColor = AppColors.OnPrimary
        ),
        shape = RoundedCornerShape(AppRadius.M)
    ) {
        Text(
            text = text,
            fontSize = AppTypography.LabelLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

// Secondary Button
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = AppColors.Primary
        ),
        border = ButtonDefaults.outlinedButtonBorder,
        shape = RoundedCornerShape(AppRadius.M)
    ) {
        Text(
            text = text,
            fontSize = AppTypography.LabelLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

// Top App Bar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    onNavigationClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = AppTypography.HeadlineMedium,
                fontWeight = FontWeight.Medium,
                color = AppColors.OnSurface
            )
        },
        navigationIcon = {
            if (onNavigationClick != null) {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = AppColors.OnSurface
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AppColors.Surface
        )
    )
}

// List Item
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListItem(
    title: String,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick ?: {},
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.XS),
        shape = RoundedCornerShape(AppRadius.S)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.M),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = AppColors.Primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(AppSpacing.M))
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = AppTypography.BodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.OnSurface
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = AppTypography.BodyMedium,
                        color = AppColors.OnSurfaceVariant
                    )
                }
            }
            
            if (trailingIcon != null) {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = AppColors.OnSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// Status Card
@Composable
fun StatusCard(
    title: String,
    value: String,
    icon: ImageVector,
    status: StatusType = StatusType.Default,
    modifier: Modifier = Modifier
) {
    val (containerColor, contentColor) = when (status) {
        StatusType.Success -> AppColors.SuccessContainer to AppColors.Success
        StatusType.Warning -> AppColors.WarningContainer to AppColors.Warning
        StatusType.Error -> AppColors.ErrorContainer to AppColors.Error
        StatusType.Info -> AppColors.InfoContainer to AppColors.Info
        StatusType.Default -> AppColors.SurfaceVariant to AppColors.Primary
    }
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.S),
        shape = RoundedCornerShape(AppRadius.M)
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.M)
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
                        color = AppColors.OnSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.XS))
                    Text(
                        text = value,
                        fontSize = AppTypography.DisplaySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.OnSurface
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = containerColor,
                            shape = RoundedCornerShape(AppRadius.S)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// Section Header
@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            fontSize = AppTypography.HeadlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.OnSurface
        )
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(AppSpacing.XS))
            Text(
                text = subtitle,
                fontSize = AppTypography.BodyMedium,
                color = AppColors.OnSurfaceVariant
            )
        }
    }
}

// Empty State
@Composable
fun EmptyState(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AppColors.OnSurfaceVariant,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(AppSpacing.M))
        Text(
            text = title,
            fontSize = AppTypography.HeadlineSmall,
            fontWeight = FontWeight.Medium,
            color = AppColors.OnSurface
        )
        Spacer(modifier = Modifier.height(AppSpacing.XS))
        Text(
            text = subtitle,
            fontSize = AppTypography.BodyMedium,
            color = AppColors.OnSurfaceVariant
        )
    }
}

enum class StatusType {
    Default, Success, Warning, Error, Info
}