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

// ─── Design Tokens ───────────────────────────────────────────────────────────
private val BgTop        = Color(0xFF6BBFB0)   // light teal-green (top)
private val BgBottom     = Color(0xFF3A8A85)   // deeper teal (bottom)
private val CardBg       = Color(0xFFFFFFFF).copy(alpha = 0.18f)
private val CardBorder   = Color(0xFFFFFFFF).copy(alpha = 0.35f)
private val FieldBg      = Color(0xFFF0F4F3).copy(alpha = 0.88f)
private val FieldText    = Color(0xFF2E2E2E)
private val FieldHint    = Color(0xFF9AACAA)
private val FieldIcon    = Color(0xFF7DBFB0)
private val BtnGreen     = Color(0xFF4CAF7D)
private val BtnText      = Color(0xFFFFFFFF)
private val TitleColor   = Color(0xFF1A1A1A)
private val SubtitleColor= Color(0xFF4A6460)
private val DividerColor = Color(0xFFB0CFCC)
private val SwitchLabel  = Color(0xFFB0CFCC)
private val BottomLabel  = Color(0xFFCCE8E4)
private val ForgotColor  = Color(0xFF2A9D8F)

@Composable
fun UnifiedLoginScreen(
    onStudentLoginSuccess: () -> Unit,
    onDriverLoginSuccess: () -> Unit,
    onAdminLoginSuccess: () -> Unit
) {
    var selectedRole by remember { mutableStateOf(LoginRole.STUDENT) }
    var showPrivacyDialog  by remember { mutableStateOf(false) }
    var showSupportDialog  by remember { mutableStateOf(false) }
    var showAppInfoDialog  by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(colors = listOf(BgTop, BgBottom))
            )
    ) {
        MainContent(
            selectedRole        = selectedRole,
            onRoleChange        = { selectedRole = it },
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
    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            // ── App Label Pill ───────────────────────────────────────────────
            AppLabelPill(onAppInfoClick)

            Spacer(modifier = Modifier.height((-14).dp)) // overlap pill over card

            // ── Login Card ───────────────────────────────────────────────────
            DynamicLoginCard(
                selectedRole          = selectedRole,
                onRoleChange          = onRoleChange,
                onStudentLoginSuccess = onStudentLoginSuccess,
                onDriverLoginSuccess  = onDriverLoginSuccess,
                onAdminLoginSuccess   = onAdminLoginSuccess
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── Bottom Nav Pills ──────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomPill("PRIVACY")  { onPrivacyPolicyClick() }
                BottomPill("SUPPORT")  { onSupportClick() }
                BottomPill("ABOUT")    { onAppInfoClick() }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ─── App Label Pill ───────────────────────────────────────────────────────────
@Composable
private fun AppLabelPill(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .shadow(4.dp, RoundedCornerShape(50), spotColor = Color.Black.copy(0.08f))
            .background(CardBg, RoundedCornerShape(50))
            .border(1.dp, CardBorder, RoundedCornerShape(50))
            .clip(RoundedCornerShape(50))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true, color = Color.White.copy(0.3f))
            ) { onClick() }
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_directions_bus_vector),
            contentDescription = null,
            tint = TitleColor,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = "Campus Bus Buddy",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TitleColor,
            letterSpacing = 0.2.sp
        )
    }
}

// ─── Bottom Pill Button ───────────────────────────────────────────────────────
@Composable
private fun BottomPill(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .shadow(2.dp, RoundedCornerShape(50), spotColor = Color.Black.copy(0.06f))
            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(50))
            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(50))
            .clip(RoundedCornerShape(50))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true, color = Color.White.copy(0.25f))
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.W600,
            color = BottomLabel,
            letterSpacing = 1.sp
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
                icon           = R.drawable.studentlogin,
                iconBackground = Color(0xFF2C2C2C),
                title          = "Student Login",
                subtitle       = "Welcome back! Please enter your campus credentials to track your bus.",
                usernameLabel  = "Student ID or Email",
                buttonText     = "Sign In",
                isImage        = true
            )
            LoginRole.DRIVER  -> RoleData(
                icon           = R.drawable.driver_login,
                iconBackground = Color(0xFF1A1A1A),
                title          = "Driver Login",
                subtitle       = "Access your bus route and operations dashboard.",
                usernameLabel  = "Driver Username",
                buttonText     = "Sign In",
                isImage        = true
            )
            LoginRole.ADMIN   -> RoleData(
                icon           = R.drawable.admin,
                iconBackground = Color(0xFF0D0D0D),
                title          = "Admin Login",
                subtitle       = "Manage routes, drivers and student records.",
                usernameLabel  = "Admin Username",
                buttonText     = "Sign In",
                isImage        = true
            )
        }
    }

    // ── Card Shell ────────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation    = 12.dp,
                shape        = RoundedCornerShape(28.dp),
                ambientColor = Color.Black.copy(alpha = 0.10f),
                spotColor    = Color.Black.copy(alpha = 0.10f)
            )
            .background(CardBg, RoundedCornerShape(28.dp))
            .border(1.5.dp, CardBorder, RoundedCornerShape(28.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 28.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Profile Icon ─────────────────────────────────────────────────
            AnimatedContent(
                targetState = roleData,
                transitionSpec = {
                    fadeIn(tween(300)) + scaleIn(initialScale = 0.85f, animationSpec = tween(300)) togetherWith
                    fadeOut(tween(200)) + scaleOut(targetScale = 0.85f, animationSpec = tween(200))
                },
                label = "role_icon"
            ) { data ->
                Box(
                    modifier = Modifier
                        .size(82.dp)
                        .shadow(6.dp, CircleShape, spotColor = Color.Black.copy(0.12f))
                        .background(Color.White.copy(alpha = 0.30f), CircleShape)
                        .border(2.dp, Color.White.copy(alpha = 0.50f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter        = painterResource(id = data.icon),
                        contentDescription = data.title,
                        modifier       = Modifier
                            .size(66.dp)
                            .clip(CircleShape),
                        contentScale   = ContentScale.Crop,
                        alignment      = BiasAlignment(0f, -0.25f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ── Title + Subtitle ─────────────────────────────────────────────
            AnimatedContent(
                targetState = roleData,
                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                label = "role_text"
            ) { data ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text       = data.title,
                        fontSize   = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color      = TitleColor,
                        textAlign  = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text       = data.subtitle,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color      = SubtitleColor,
                        textAlign  = TextAlign.Center,
                        lineHeight = 19.sp,
                        modifier   = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // ── Username Field ───────────────────────────────────────────────
            FlatInputField(
                value          = username,
                onValueChange  = { username = it; errorMessage = null },
                placeholder    = roleData.usernameLabel,
                leadingIconRes = R.drawable.ic_person,
                enabled        = !isLoading
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ── Password Field ───────────────────────────────────────────────
            FlatInputField(
                value                = password,
                onValueChange        = { password = it; errorMessage = null },
                placeholder          = "Password",
                leadingIconRes       = R.drawable.ic_visibility_off,
                trailingIconRes      = if (isPasswordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off,
                onTrailingIconClick  = { isPasswordVisible = !isPasswordVisible },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardType         = KeyboardType.Password,
                enabled              = !isLoading
            )

            // ── Forgot Password (Student only) ────────────────────────────────
            if (selectedRole == LoginRole.STUDENT) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text       = "Forgot Password?",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.W500,
                    color      = ForgotColor,
                    modifier   = Modifier
                        .align(Alignment.End)
                        .clickable { }
                )
            }

            // ── Error Message ─────────────────────────────────────────────────
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

            Spacer(modifier = Modifier.height(20.dp))

            // ── Sign In Button ────────────────────────────────────────────────
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
                    .height(52.dp),
                shape    = RoundedCornerShape(26.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = BtnGreen,
                    disabledContainerColor = BtnGreen.copy(alpha = 0.6f)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 2.dp),
                enabled  = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(20.dp),
                        color       = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text       = "Sign In",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = BtnText
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter       = painterResource(id = R.drawable.ic_chevron_right),
                        contentDescription = null,
                        tint          = Color.White,
                        modifier      = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // ── Divider + Switch Role ─────────────────────────────────────────
            Divider(color = DividerColor.copy(alpha = 0.5f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text          = "SWITCH ROLE",
                fontSize      = 11.sp,
                fontWeight    = FontWeight.W600,
                color         = SwitchLabel,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RoleIcon(
                    icon       = R.drawable.ic_directions_bus_vector,
                    isSelected = selectedRole == LoginRole.DRIVER,
                    onClick    = { onRoleChange(LoginRole.DRIVER) }
                )
                RoleIcon(
                    icon       = R.drawable.studentlogin,
                    isSelected = selectedRole == LoginRole.STUDENT,
                    onClick    = { onRoleChange(LoginRole.STUDENT) },
                    isImage    = true
                )
                RoleIcon(
                    icon       = R.drawable.ic_person,
                    isSelected = selectedRole == LoginRole.ADMIN,
                    onClick    = { onRoleChange(LoginRole.ADMIN) }
                )
            }
        }
    }
}

// ─── Flat Input Field ─────────────────────────────────────────────────────────
@Composable
private fun FlatInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIconRes: Int,
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
                color      = FieldHint,
                fontSize   = 14.sp,
                fontWeight = FontWeight.W400
            )
        },
        leadingIcon = {
            Icon(
                painter           = painterResource(id = leadingIconRes),
                contentDescription = null,
                tint              = FieldIcon,
                modifier          = Modifier.size(20.dp)
            )
        },
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
            .height(54.dp),
        shape  = RoundedCornerShape(27.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor     = FieldIcon.copy(alpha = 0.6f),
            unfocusedBorderColor   = Color.Transparent,
            focusedContainerColor  = FieldBg,
            unfocusedContainerColor= FieldBg,
            focusedTextColor       = FieldText,
            unfocusedTextColor     = FieldText,
            cursorColor            = FieldIcon
        ),
        textStyle = androidx.compose.ui.text.TextStyle(
            fontSize      = 14.sp,
            fontWeight    = FontWeight.W500,
            color         = FieldText,
            letterSpacing = 0.2.sp
        ),
        singleLine = true,
        enabled    = enabled
    )
}

// ─── Role Icon ────────────────────────────────────────────────────────────────
@Composable
private fun RoleIcon(
    icon: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    isImage: Boolean = false
) {
    val scale by animateFloatAsState(
        targetValue  = if (isSelected) 1.12f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label        = "scale"
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .scale(scale)
            .shadow(
                elevation    = if (isSelected) 6.dp else 1.dp,
                shape        = CircleShape,
                ambientColor = if (isSelected) BtnGreen.copy(alpha = 0.25f) else Color.Black.copy(0.06f),
                spotColor    = if (isSelected) BtnGreen.copy(alpha = 0.25f) else Color.Black.copy(0.06f)
            )
            .background(
                if (isSelected) Color.White.copy(alpha = 0.45f) else Color.White.copy(alpha = 0.20f),
                CircleShape
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color.White.copy(alpha = 0.75f) else Color.White.copy(alpha = 0.30f),
                shape = CircleShape
            )
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = rememberRipple(bounded = true, color = Color.White.copy(0.3f))
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isImage) {
            Image(
                painter           = painterResource(id = icon),
                contentDescription = null,
                modifier          = Modifier.size(26.dp),
                contentScale      = ContentScale.Fit
            )
        } else {
            Icon(
                painter           = painterResource(id = icon),
                contentDescription = null,
                tint              = if (isSelected) TitleColor else Color(0xFF5A7A77),
                modifier          = Modifier.size(24.dp)
            )
        }
    }
}

// ─── Data holder ─────────────────────────────────────────────────────────────
private data class RoleData(
    val icon: Int,
    val iconBackground: Color,
    val title: String,
    val subtitle: String,
    val usernameLabel: String,
    val buttonText: String,
    val isImage: Boolean = false
)

// ─── Privacy Policy Dialog ────────────────────────────────────────────────────
@Composable
private fun PrivacyPolicyDialog(onDismiss: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val alpha by animateFloatAsState(if (visible) 1f else 0f, tween(300), label = "a")
    val scale by animateFloatAsState(if (visible) 1f else 0.85f, tween(300), label = "s")

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
                        fontSize = 14.sp, color = Color(0xFF555555), textAlign = TextAlign.Center, lineHeight = 22.sp
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { visible = false; onDismiss() },
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
    val scale by animateFloatAsState(if (visible) 1f else 0.85f, tween(300), label = "s")

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
            modifier = Modifier.size(38.dp).background(BtnGreen.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(iconRes), null, tint = BtnGreen, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title,    fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TitleColor)
            Text(subtitle, fontSize = 13.sp, color = Color(0xFF777777))
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
    val scale by animateFloatAsState(if (visible) 1f else 0.85f, tween(300), label = "s")

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
                        fontSize = 14.sp, color = Color(0xFF555555), textAlign = TextAlign.Start,
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
                                Text(feature, fontSize = 13.sp, color = Color(0xFF555555))
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