package com.campusbussbuddy.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.campusbussbuddy.R
import com.campusbussbuddy.firebase.*
import kotlinx.coroutines.launch

enum class LoginRole {
    STUDENT, DRIVER, ADMIN
}

// ─── Design Tokens (Exact HTML Sage Glass UI) ────────────────────────────────
private val BgTop        = Color(0xFF6BBFB0)                       // light teal-green (top)
private val BgBottom     = Color(0xFF3A8A85)                       // deeper teal (bottom)
private val TealOverlay  = Color(0xFF2d6464).copy(alpha = 0.40f)   // teal overlay 40%
private val CardBg       = Color(0xFFFFFFFF).copy(alpha = 0.22f)   // glass-card rgba(255,255,255,0.22)
private val CardBorder   = Color(0xFFFFFFFF).copy(alpha = 0.30f)   // white/30
private val FieldBg      = Color(0xFFFFFFFF).copy(alpha = 0.60f)   // glass-input rgba(255,255,255,0.6)
private val FieldBorder  = Color(0xFFFFFFFF).copy(alpha = 0.40f)   // white/40
private val FieldText    = Color(0xFF111111)                        // primary-text #111111
private val FieldHint    = Color(0xFF444444).copy(alpha = 0.70f)   // secondary-text 70%
private val FieldIcon    = Color(0xFF444444)                        // secondary-text for icons
private val BtnGreen     = Color(0xFF22C55E).copy(alpha = 0.85f)   // sage-green rgba(34,197,94,0.85)
private val BtnGlow      = Color(0xFF22C55E).copy(alpha = 0.40f)   // button glow shadow
private val BtnText      = Color(0xFFFFFFFF)
private val TitleColor   = Color(0xFF111111)                        // primary-text #111111
private val SubtitleColor= Color(0xFF444444)                        // secondary-text #444444
private val DividerColor = Color(0xFFFFFFFF).copy(alpha = 0.40f)   // white/40
private val SwitchLabel  = Color(0xFF444444).copy(alpha = 0.60f)   // secondary-text 60% opacity
private val ForgotColor  = Color(0xFF2d6464)                        // brand-teal #2d6464
private val BrandTeal    = Color(0xFF2d6464)                        // brand-teal #2d6464

@Composable
fun UnifiedLoginScreen(
    onStudentLoginSuccess: () -> Unit,
    onDriverLoginSuccess: () -> Unit,
    onAdminLoginSuccess: () -> Unit
) {
    var selectedRole      by remember { mutableStateOf(LoginRole.STUDENT) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }
    var showAppInfoDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(BgTop, BgBottom)))
    ) {
        // Campus overlay (rgba(45,100,100,0.4))
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TealOverlay)
        )

        MainContent(
            selectedRole          = selectedRole,
            onRoleChange          = { selectedRole = it },
            onStudentLoginSuccess = onStudentLoginSuccess,
            onDriverLoginSuccess  = onDriverLoginSuccess,
            onAdminLoginSuccess   = onAdminLoginSuccess,
            onPrivacyPolicyClick  = { showPrivacyDialog = true },
            onSupportClick        = { showSupportDialog = true },
            onAppInfoClick        = { showAppInfoDialog = true }
        )

        if (showPrivacyDialog) PrivacyPolicyDialog { showPrivacyDialog = false }
        if (showSupportDialog) SupportDialog       { showSupportDialog = false }
        if (showAppInfoDialog) AppInfoDialog        { showAppInfoDialog = false }
    }
}

// ─── Main Content ─────────────────────────────────────────────────────────────
// Header pill overlaps the card top edge — exactly like target image.
// HTML: `absolute top-8 left-1/2 -translate-x-1/2` sits at screen top-8,
// card starts below with space-y-8 from header inside flex column.
// In Compose: use Box so pill is drawn OVER the card at top-center.
@Composable
private fun MainContent(
    selectedRole: LoginRole,
    onRoleChange: (LoginRole) -> Unit,
    onStudentLoginSuccess: () -> Unit,
    onDriverLoginSuccess: () -> Unit,
    onAdminLoginSuccess: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onSupportClick: () -> Unit,
    onAppInfoClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // top-8 = 32dp from screen top before the pill
        Spacer(modifier = Modifier.height(32.dp))

        // ── Header pill + Card stacked via Box so pill overlaps card ──────────
        // Pill height ≈ 36dp. We want pill center at card top edge.
        // So pill needs to be raised by half its height = 18dp above card.
        Box(
            modifier            = Modifier.fillMaxWidth(),
            contentAlignment    = Alignment.TopCenter
        ) {
            // Card starts 18dp below (half-pill-height) so pill center = card top
            Box(modifier = Modifier.padding(top = 18.dp)) {
                DynamicLoginCard(
                    selectedRole          = selectedRole,
                    onRoleChange          = onRoleChange,
                    onStudentLoginSuccess = onStudentLoginSuccess,
                    onDriverLoginSuccess  = onDriverLoginSuccess,
                    onAdminLoginSuccess   = onAdminLoginSuccess
                )
            }

            // Pill drawn LAST = on top of card, perfectly centered
            AppLabelPill(onAppInfoClick)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Bottom Nav Pills ──────────────────────────────────────────────────
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            BottomPill("PRIVACY") { onPrivacyPolicyClick() }
            BottomPill("SUPPORT") { onSupportClick() }
            BottomPill("ABOUT")   { onAppInfoClick() }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ─── App Label Pill ───────────────────────────────────────────────────────────
// bg-white/10  backdrop-blur-md  border-white/30  rounded-full  px-5 py-2
// icon: brand-teal  text: font-bold tracking-tight primary-text
// NO shadow — floats with glass only
@Composable
private fun AppLabelPill(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .shadow(elevation = 0.dp, shape = RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.10f), RoundedCornerShape(50))  // bg-white/10
            .border(1.dp, Color.White.copy(alpha = 0.30f), RoundedCornerShape(50)) // border-white/30
            .clip(RoundedCornerShape(50))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = rememberRipple(bounded = true, color = Color.White.copy(0.20f))
            ) { onClick() }
            .padding(horizontal = 20.dp, vertical = 8.dp),   // px-5 py-2
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)   // space-x-2
    ) {
        Icon(
            painter           = painterResource(id = R.drawable.ic_directions_bus_vector),
            contentDescription = null,
            tint              = BrandTeal,          // text-brand-teal #2d6464
            modifier          = Modifier.size(18.dp)
        )
        Text(
            text          = "Campus Bus Buddy",
            fontSize      = 14.sp,
            fontWeight    = FontWeight.Bold,         // font-bold
            color         = TitleColor,              // primary-text #111111
            letterSpacing = (-0.3).sp               // tracking-tight
        )
    }
}

// ─── Bottom Pill Button ───────────────────────────────────────────────────────
// Target: glass-card style, uppercase tracking-widest, primary-text/80
@Composable
private fun BottomPill(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .shadow(elevation = 0.dp, shape = RoundedCornerShape(50))
            .background(CardBg, RoundedCornerShape(50))                  // glass-card bg
            .border(0.8.dp, CardBorder, RoundedCornerShape(50))         // glass-card border
            .clip(RoundedCornerShape(50))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = rememberRipple(bounded = true, color = Color.White.copy(0.25f))
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text          = label,
            fontSize      = 10.sp,
            fontWeight    = FontWeight.Bold,                             // font-bold
            color         = TitleColor.copy(alpha = 0.80f),             // primary-text/80
            letterSpacing = 1.5.sp                                       // tracking-widest
        )
    }
}

// ─── Dynamic Login Card ───────────────────────────────────────────────────────
@Composable
private fun DynamicLoginCard(
    selectedRole: LoginRole,
    onRoleChange: (LoginRole) -> Unit,
    onStudentLoginSuccess: () -> Unit,
    onDriverLoginSuccess: () -> Unit,
    onAdminLoginSuccess: () -> Unit
) {
    var username          by remember { mutableStateOf("") }
    var password          by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading         by remember { mutableStateOf(false) }
    var errorMessage      by remember { mutableStateOf<String?>(null) }
    val scope             = rememberCoroutineScope()

    LaunchedEffect(selectedRole) {
        username = ""; password = ""; isPasswordVisible = false; errorMessage = null
    }

    val roleData = remember(selectedRole) {
        when (selectedRole) {
            LoginRole.STUDENT -> RoleData(
                icon          = R.drawable.studentlogin,
                title         = "Student Login",
                subtitle      = "",  // removed caption
                usernameLabel = "Username",
                isImage       = true
            )
            LoginRole.DRIVER  -> RoleData(
                icon          = R.drawable.driver_login,
                title         = "Driver Login",
                subtitle      = "",  // removed caption
                usernameLabel = "Driver Username",
                isImage       = true
            )
            LoginRole.ADMIN   -> RoleData(
                icon          = R.drawable.admin,
                title         = "Admin Login",
                subtitle      = "",  // removed caption
                usernameLabel = "Admin Username",
                isImage       = true
            )
        }
    }

    // ── Card Shell: glass-card rgba(255,255,255,0.22), border white/30, blur 22px ─
    Box(
        modifier = Modifier
            .fillMaxWidth(0.92f)  // slightly wider card for better proportions
            .shadow(
                elevation    = 12.dp,  // softer shadow
                shape        = RoundedCornerShape(28.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor    = Color.Black.copy(alpha = 0.08f)
            )
            .background(CardBg, RoundedCornerShape(28.dp))  // rgba(255,255,255,0.22)
            .border(1.dp, CardBorder, RoundedCornerShape(28.dp))  // white/30
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 28.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Profile Icon Circle + Teal Dot Indicator ──────────────────────
            // Dot: -bottom-1 in Tailwind = overlaps circle bottom by 4px.
            // Use Box with contentAlignment BottomCenter + offset(y = 4.dp) on dot.
            AnimatedContent(
                targetState = roleData,
                transitionSpec = {
                    fadeIn(tween(300)) + scaleIn(initialScale = 0.85f, animationSpec = tween(300)) togetherWith
                    fadeOut(tween(200)) + scaleOut(targetScale = 0.85f, animationSpec = tween(200))
                },
                label = "role_icon"
            ) { data ->
                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier         = Modifier.wrapContentSize()
                ) {
                    // Glass circle
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .shadow(
                                8.dp, CircleShape,
                                ambientColor = Color.Black.copy(0.08f),
                                spotColor    = Color.Black.copy(0.08f)
                            )
                            .background(CardBg, CircleShape)
                            .border(1.5.dp, Color.White.copy(alpha = 0.50f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (data.isImage) {
                            Image(
                                painter           = painterResource(id = data.icon),
                                contentDescription = data.title,
                                modifier          = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape),
                                contentScale      = ContentScale.Crop,
                                alignment         = BiasAlignment(0f, -0.2f)
                            )
                        } else {
                            Icon(
                                painter           = painterResource(id = data.icon),
                                contentDescription = data.title,
                                tint              = BrandTeal,
                                modifier          = Modifier.size(44.dp)
                            )
                        }
                    }

                    // Teal dot: BottomCenter of the Box, offset 4dp down to overlap circle edge
                    // Matches HTML: `absolute -bottom-1 left-1/2 -translate-x-1/2`
                    Box(
                        modifier = Modifier
                            .offset(y = 4.dp)               // -bottom-1 = push 4dp outside circle
                            .size(8.dp)
                            .background(BrandTeal, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Title + Subtitle (text-3xl font-bold tracking-tight) ──────────
            AnimatedContent(
                targetState = roleData,
                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                label = "role_text"
            ) { data ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text          = data.title,
                        fontSize      = 28.sp,                          // text-3xl
                        fontWeight    = FontWeight.Bold,
                        color         = TitleColor,                     // #111111
                        textAlign     = TextAlign.Center,
                        letterSpacing = (-0.5).sp                       // tracking-tight
                    )
                    // Only show subtitle if not empty
                    if (data.subtitle.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text       = data.subtitle,
                            fontSize   = 13.sp,                             // text-sm
                            fontWeight = FontWeight.Medium,                 // font-medium
                            color      = SubtitleColor,                     // #444444
                            textAlign  = TextAlign.Center,
                            lineHeight = 19.sp,
                            modifier   = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Username Field ────────────────────────────────────────────────
            GlassInputField(
                value          = username,
                onValueChange  = { username = it; errorMessage = null },
                placeholder    = roleData.usernameLabel,
                leadingIconRes = R.drawable.ic_person,
                enabled        = !isLoading
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ── Password Field (NO leading icon, only trailing visibility toggle) ─
            GlassInputField(
                value                = password,
                onValueChange        = { password = it; errorMessage = null },
                placeholder          = "Password",
                leadingIconRes       = null,  // removed leading icon
                trailingIconRes      = if (isPasswordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off,
                onTrailingIconClick  = { isPasswordVisible = !isPasswordVisible },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardType         = KeyboardType.Password,
                enabled              = !isLoading
            )

            // ── Error Message ──────────────────────────────────────────────────
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text      = errorMessage!!,
                    fontSize  = 13.sp,
                    color     = Color(0xFFE53935),
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Sign In Button — glass-button-sage, NO chevron arrow ──────────
            Button(
                onClick = {
                    if (username.isBlank() || password.isBlank()) {
                        errorMessage = "Please fill in all fields"
                        return@Button
                    }
                    scope.launch {
                        isLoading = true
                        errorMessage = null
                        when (selectedRole) {
                            LoginRole.STUDENT -> when (val r = FirebaseManager.authenticateStudent(username.trim(), password)) {
                                is StudentAuthResult.Success -> { isLoading = false; onStudentLoginSuccess() }
                                is StudentAuthResult.Error   -> { isLoading = false; errorMessage = r.message }
                            }
                            LoginRole.DRIVER  -> when (val r = FirebaseManager.authenticateDriver(username.trim(), password)) {
                                is DriverAuthResult.Success  -> { isLoading = false; onDriverLoginSuccess() }
                                is DriverAuthResult.Error    -> { isLoading = false; errorMessage = r.message }
                            }
                            LoginRole.ADMIN   -> when (val r = FirebaseManager.authenticateAdmin(username.trim(), password)) {
                                is AuthResult.Success        -> { isLoading = false; onAdminLoginSuccess() }
                                is AuthResult.Error          -> { isLoading = false; errorMessage = r.message }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)                                       // h-14 = 56dp
                    .shadow(
                        elevation    = 0.dp,
                        shape        = RoundedCornerShape(27.dp),
                        ambientColor = BtnGlow,
                        spotColor    = BtnGlow
                    ),
                shape    = RoundedCornerShape(27.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = BtnGreen,                   // rgba(34,197,94,0.85)
                    disabledContainerColor = BtnGreen.copy(alpha = 0.50f)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(20.dp),
                        color       = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    // NO chevron — target has plain "Sign In" text only
                    Text(
                        text       = "Sign In",
                        fontSize   = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color      = BtnText
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Divider (very subtle, white/40) ───────────────────────────────
            Divider(color = DividerColor, thickness = 0.8.dp)

            Spacer(modifier = Modifier.height(16.dp))

            // ── Switch Role label (secondary-text 60%, uppercase tracking-widest)
            Text(
                text          = "Switch Role",
                fontSize      = 11.sp,
                fontWeight    = FontWeight.Bold,
                color         = SwitchLabel,
                letterSpacing = 1.5.sp                                  // tracking-widest
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ── Role Icons (max 240dp, justify-between) ────────────────────────
            Row(
                modifier              = Modifier.widthIn(max = 240.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // HTML order: student, driver, admin
                RoleIcon(
                    icon       = R.drawable.studentlogin,
                    isSelected = selectedRole == LoginRole.STUDENT,
                    onClick    = { onRoleChange(LoginRole.STUDENT) },
                    isImage    = true
                )
                RoleIcon(
                    icon       = R.drawable.ic_directions_bus_vector,
                    isSelected = selectedRole == LoginRole.DRIVER,
                    onClick    = { onRoleChange(LoginRole.DRIVER) }
                )
                RoleIcon(
                    icon       = R.drawable.ic_person,                 // admin icon
                    isSelected = selectedRole == LoginRole.ADMIN,
                    onClick    = { onRoleChange(LoginRole.ADMIN) }
                )
            }
        }
    }
}

// ─── Glass Input Field ────────────────────────────────────────────────────────
// glass-input: rgba(255,255,255,0.6), blur 10px, border-white/40, icons secondary-text
@Composable
private fun GlassInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIconRes: Int? = null,  // make optional
    trailingIconRes: Int? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        placeholder   = {
            Text(
                text       = placeholder,
                color      = FieldHint,                                 // secondary-text/70
                fontSize   = 14.sp,
                fontWeight = FontWeight.Medium
            )
        },
        leadingIcon = if (leadingIconRes != null) ({
            Icon(
                painter           = painterResource(id = leadingIconRes),
                contentDescription = null,
                tint              = FieldIcon,                          // secondary-text color
                modifier          = Modifier.size(20.dp)
            )
        }) else null,
        trailingIcon = if (trailingIconRes != null) ({
            IconButton(onClick = { onTrailingIconClick?.invoke() }) {
                Icon(
                    painter           = painterResource(id = trailingIconRes),
                    contentDescription = "toggle visibility",
                    tint              = FieldHint,
                    modifier          = Modifier.size(20.dp)
                )
            }
        }) else null,
        visualTransformation = visualTransformation,
        keyboardOptions      = KeyboardOptions(keyboardType = keyboardType),
        modifier             = Modifier
            .fillMaxWidth()
            .height(54.dp),                                             // h-14
        shape  = RoundedCornerShape(27.dp),                            // rounded-full
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor      = BrandTeal.copy(alpha = 0.30f),   // focus:ring-brand-teal/30
            unfocusedBorderColor    = FieldBorder,                     // white/40
            focusedContainerColor   = FieldBg,                         // rgba(255,255,255,0.6)
            unfocusedContainerColor = FieldBg,
            focusedTextColor        = FieldText,
            unfocusedTextColor      = FieldText,
            cursorColor             = BrandTeal
        ),
        textStyle = androidx.compose.ui.text.TextStyle(
            fontSize      = 14.sp,
            fontWeight    = FontWeight.Medium,
            color         = FieldText,
            letterSpacing = 0.2.sp
        ),
        singleLine = true,
        enabled    = enabled
    )
}

// ─── Role Icon ────────────────────────────────────────────────────────────────
// Target: glass-card circles, active = border-brand-teal/60 + bg-brand-teal/10
//         inactive = border-white/30, no thick white outlines
@Composable
private fun RoleIcon(
    icon: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    isImage: Boolean = false
) {
    val scale by animateFloatAsState(
        targetValue   = if (isSelected) 1.06f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label         = "scale"
    )

    Box(
        modifier = Modifier
            .size(52.dp)
            .scale(scale)
            .shadow(
                elevation    = if (isSelected) 4.dp else 1.dp,
                shape        = CircleShape,
                ambientColor = if (isSelected) BrandTeal.copy(alpha = 0.15f) else Color.Transparent,
                spotColor    = if (isSelected) BrandTeal.copy(alpha = 0.15f) else Color.Transparent
            )
            .background(
                // active: bg-brand-teal/10 | inactive: glass-card
                if (isSelected) BrandTeal.copy(alpha = 0.10f) else CardBg,
                CircleShape
            )
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) BrandTeal.copy(alpha = 0.60f)   // border-brand-teal/60
                        else Color.White.copy(alpha = 0.30f),            // border-white/30
                shape = CircleShape
            )
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = rememberRipple(bounded = true, color = BrandTeal.copy(0.20f))
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isImage) {
            Image(
                painter           = painterResource(id = icon),
                contentDescription = null,
                modifier          = Modifier.size(28.dp),
                contentScale      = ContentScale.Fit
            )
        } else {
            Icon(
                painter           = painterResource(id = icon),
                contentDescription = null,
                tint              = if (isSelected) BrandTeal else SubtitleColor,  // brand-teal active / secondary-text inactive
                modifier          = Modifier.size(24.dp)
            )
        }
    }
}

// ─── Data holder ─────────────────────────────────────────────────────────────
private data class RoleData(
    val icon: Int,
    val title: String,
    val subtitle: String,
    val usernameLabel: String,
    val isImage: Boolean = false
)

// ─── Privacy Policy Dialog ────────────────────────────────────────────────────
@Composable
private fun PrivacyPolicyDialog(onDismiss: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val alpha by animateFloatAsState(if (visible) 1f else 0f, tween(300), label = "a")
    val scale by animateFloatAsState(if (visible) 1f else 0.88f, tween(300), label = "s")

    Dialog(
        onDismissRequest = { visible = false; onDismiss() },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true, usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f * alpha))
                .clickable(remember { MutableInteractionSource() }, null) { visible = false; onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth().padding(32.dp).scale(scale).alpha(alpha)
                    .shadow(12.dp, RoundedCornerShape(24.dp))
                    .clickable(remember { MutableInteractionSource() }, null) { },
                shape  = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f))
            ) {
                Column(Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Privacy Policy", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TitleColor)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "This app is a student-developed project designed to help colleges manage bus tracking and attendance efficiently, and all data used is for educational and demonstration purposes only.",
                        fontSize = 14.sp, color = SubtitleColor, textAlign = TextAlign.Center, lineHeight = 22.sp
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick  = { visible = false; onDismiss() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape    = RoundedCornerShape(24.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = BtnGreen)
                    ) { Text("Got it", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White) }
                }
            }
        }
    }
}

// ─── Support Dialog ───────────────────────────────────────────────────────────
@Composable
private fun SupportDialog(onDismiss: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    LaunchedEffect(Unit) { visible = true }
    val alpha by animateFloatAsState(if (visible) 1f else 0f, tween(300), label = "a")
    val scale by animateFloatAsState(if (visible) 1f else 0.88f, tween(300), label = "s")

    Dialog(
        onDismissRequest = { visible = false; onDismiss() },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true, usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f * alpha))
                .clickable(remember { MutableInteractionSource() }, null) { visible = false; onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth().padding(32.dp).scale(scale).alpha(alpha)
                    .shadow(12.dp, RoundedCornerShape(24.dp))
                    .clickable(remember { MutableInteractionSource() }, null) { },
                shape  = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f))
            ) {
                Column(Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Support & Contact", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TitleColor)
                    Spacer(Modifier.height(16.dp))
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ContactItem(R.drawable.ic_share, "GitHub", "View source code") {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Pandiharshan/bus_alert_system")))
                        }
                        ContactItem(R.drawable.ic_person, "LinkedIn", "Connect professionally") {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/pandi-harshan-k-13962b2a5")))
                        }
                        ContactItem(R.drawable.ic_chat, "Email", "pandiharshanofficial@gmail.com") {
                            context.startActivity(Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:pandiharshanofficial@gmail.com")
                                putExtra(Intent.EXTRA_SUBJECT, "Campus Buddy App Support")
                            })
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick  = { visible = false; onDismiss() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape    = RoundedCornerShape(24.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = BtnGreen)
                    ) { Text("Close", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White) }
                }
            }
        }
    }
}

@Composable
private fun ContactItem(iconRes: Int, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(14.dp))
            .background(Color(0xFFF5F9F8), RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .clickable(remember { MutableInteractionSource() }, rememberRipple(bounded = true)) { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier         = Modifier.size(38.dp).background(BtnGreen.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(iconRes), null, tint = BtnGreen, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title,    fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TitleColor)
            Text(subtitle, fontSize = 13.sp, color = SubtitleColor)
        }
        Icon(painterResource(R.drawable.ic_chevron_right), null, tint = Color(0xFFAAAAAA), modifier = Modifier.size(18.dp))
    }
}

// ─── App Info Dialog ──────────────────────────────────────────────────────────
@Composable
private fun AppInfoDialog(onDismiss: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val alpha by animateFloatAsState(if (visible) 1f else 0f, tween(300), label = "a")
    val scale by animateFloatAsState(if (visible) 1f else 0.88f, tween(300), label = "s")

    Dialog(
        onDismissRequest = { visible = false; onDismiss() },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true, usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f * alpha))
                .clickable(remember { MutableInteractionSource() }, null) { visible = false; onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth().padding(32.dp).scale(scale).alpha(alpha)
                    .shadow(12.dp, RoundedCornerShape(24.dp))
                    .clickable(remember { MutableInteractionSource() }, null) { },
                shape  = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("About Campus Buddy", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TitleColor, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "A smart bus attendance and tracking system for colleges — GPS-based live tracking, QR attendance, student absence planning, real-time stop management, and multi-role support for drivers, students, and admins.",
                        fontSize = 14.sp, color = SubtitleColor, textAlign = TextAlign.Start,
                        lineHeight = 21.sp, modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            "Smart bus tracking with GPS",
                            "QR-based attendance system",
                            "Student absence planning",
                            "Real-time stop management",
                            "Driver and student role management"
                        ).forEach { feature ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(6.dp).background(BtnGreen, CircleShape))
                                Spacer(Modifier.width(10.dp))
                                Text(feature, fontSize = 13.sp, color = SubtitleColor)
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick  = { visible = false; onDismiss() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape    = RoundedCornerShape(24.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = BtnGreen)
                    ) { Text("Close", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White) }
                }
            }
        }
    }
}