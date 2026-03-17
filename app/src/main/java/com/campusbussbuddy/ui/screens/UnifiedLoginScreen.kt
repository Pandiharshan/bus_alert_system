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
import com.campusbussbuddy.ui.theme.*
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

// ─── Unified Login Screen ─────────────────────────────────────────────────────
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
            .background(NeumorphBgPrimary)
    ) {
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
        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier         = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            // Card pushed down 26dp so pill center aligns with card top edge
            Box(modifier = Modifier.padding(top = 26.dp)) {
                DynamicLoginCard(
                    selectedRole          = selectedRole,
                    onRoleChange          = onRoleChange,
                    onStudentLoginSuccess = onStudentLoginSuccess,
                    onDriverLoginSuccess  = onDriverLoginSuccess,
                    onAdminLoginSuccess   = onAdminLoginSuccess
                )
            }

            // Pill drawn last — overlaps card top edge, perfectly centered
            AppLabelPill(
                selectedRole = selectedRole,
                onClick      = onAppInfoClick
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            BottomPill("PRIVACY") { onPrivacyPolicyClick() }
            BottomPill("SUPPORT") { onSupportClick() }
            BottomPill("ABOUT")   { onAppInfoClick() }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ─── App Label Pill ───────────────────────────────────────────────────────────
@Composable
private fun AppLabelPill(
    selectedRole: LoginRole,
    onClick: () -> Unit
) {
    val roleIcon = when (selectedRole) {
        LoginRole.STUDENT -> R.drawable.ic_student
        LoginRole.DRIVER  -> R.drawable.ic_directions_bus_vector
        LoginRole.ADMIN   -> R.drawable.ic_admin_panel
    }

    Row(
        modifier = Modifier
            .fillMaxWidth(0.78f)
            .neumorphic(cornerRadius = 28.dp, elevation = 6.dp, blur = 12.dp)
            .background(NeumorphSurface, RoundedCornerShape(28.dp))
            .bounceClick { onClick() }
            .padding(start = 22.dp, end = 5.dp, top = 5.dp, bottom = 5.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text          = "Campus Bus Buddy",
            fontSize      = 14.sp,
            fontWeight    = FontWeight.SemiBold,
            color         = NeumorphTextSecondary,
            letterSpacing = 0.sp
        )

        // Circle stays static — only the icon inside crossfades (no ghosting)
        Box(
            modifier = Modifier
                .size(46.dp)
                .neumorphic(cornerRadius = 23.dp, elevation = 4.dp, blur = 8.dp)
                .background(NeumorphSurface, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState   = roleIcon,
                transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(160)) },
                label         = "pill_role_icon"
            ) { icon ->
                Icon(
                    painter            = painterResource(id = icon),
                    contentDescription = null,
                    tint               = NeumorphTextSecondary,
                    modifier           = Modifier.size(22.dp)
                )
            }
        }
    }
}

// ─── Bottom Pill Button ───────────────────────────────────────────────────────
@Composable
private fun BottomPill(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .neumorphic(cornerRadius = 20.dp, elevation = 4.dp, blur = 8.dp)
            .background(NeumorphSurface, RoundedCornerShape(20.dp))
            .bounceClick { onClick() }
            .padding(horizontal = 18.dp, vertical = 9.dp)
    ) {
        Text(
            text          = label,
            fontSize      = 10.sp,
            fontWeight    = FontWeight.Bold,
            color         = NeumorphTextSecondary,
            letterSpacing = 1.5.sp
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
                icon          = R.drawable.ic_student,
                title         = "Student Login",
                subtitle      = "Welcome to the Campus Bus Buddy",
                usernameLabel = "Username",
                isImage       = false
            )
            LoginRole.DRIVER  -> RoleData(
                icon          = R.drawable.driver_login,
                title         = "Driver Login",
                subtitle      = "Welcome back, Driver",
                usernameLabel = "Driver Username",
                isImage       = true
            )
            LoginRole.ADMIN   -> RoleData(
                icon          = R.drawable.admin_panel,
                title         = "Admin Login",
                subtitle      = "Admin Control Panel",
                usernameLabel = "Admin Username",
                isImage       = false
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth(0.94f)
            .neumorphic(cornerRadius = NeumorphCardRadius, elevation = 8.dp, blur = 18.dp)
            .background(NeumorphSurface, RoundedCornerShape(NeumorphCardRadius))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, bottom = 32.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Title + Subtitle ──────────────────────────────────────────────
            AnimatedContent(
                targetState  = roleData,
                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                label        = "role_title"
            ) { data ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier            = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text          = data.title,
                        fontSize      = 30.sp,
                        fontWeight    = FontWeight.ExtraBold,
                        color         = NeumorphTextPrimary,
                        textAlign     = TextAlign.Center,
                        letterSpacing = (-0.8).sp
                    )
                    if (data.subtitle.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text       = data.subtitle,
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color      = NeumorphTextSecondary,
                            textAlign  = TextAlign.Center,
                            lineHeight = 19.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Username Field ────────────────────────────────────────────────
            NeumorphicInputField(
                value         = username,
                onValueChange = { username = it; errorMessage = null },
                placeholder   = roleData.usernameLabel,
                leadingIcon   = {
                    Icon(
                        painter            = painterResource(id = R.drawable.ic_person),
                        contentDescription = null,
                        tint               = NeumorphTextSecondary,
                        modifier           = Modifier.size(20.dp)
                    )
                },
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ── Password Field ────────────────────────────────────────────────
            NeumorphicInputField(
                value         = password,
                onValueChange = { password = it; errorMessage = null },
                placeholder   = "Password",
                leadingIcon   = {
                    Icon(
                        painter            = painterResource(id = R.drawable.ic_lock),
                        contentDescription = null,
                        tint               = NeumorphTextSecondary,
                        modifier           = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            painter = painterResource(
                                id = if (isPasswordVisible) R.drawable.ic_visibility
                                     else R.drawable.ic_visibility_off
                            ),
                            contentDescription = null,
                            tint               = NeumorphTextSecondary,
                            modifier           = Modifier.size(20.dp)
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None
                                       else PasswordVisualTransformation(),
                keyboardOptions      = KeyboardOptions(keyboardType = KeyboardType.Password),
                enabled              = !isLoading
            )

            // ── Error Message ─────────────────────────────────────────────────
            AnimatedVisibility(visible = errorMessage != null) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text      = errorMessage ?: "",
                        fontSize  = 13.sp,
                        color     = Color(0xFFE53935),
                        textAlign = TextAlign.Center,
                        modifier  = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Sign In Button ────────────────────────────────────────────────
            SignInButton(
                isLoading = isLoading,
                onClick   = {
                    if (isLoading) return@SignInButton
                    if (username.isBlank() || password.isBlank()) {
                        errorMessage = "Please fill in all fields"
                        return@SignInButton
                    }
                    scope.launch {
                        isLoading    = true
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
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text       = "Switch Role",
                fontSize   = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color      = NeumorphTextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ── Role Selector ─────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .widthIn(min = 220.dp, max = 252.dp)
                    .height(64.dp)
                    .neumorphicInset(cornerRadius = 32.dp, elevation = 5.dp, blur = 10.dp)
                    .background(NeumorphSurface, RoundedCornerShape(32.dp))
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    RoleIcon(
                        icon       = R.drawable.ic_directions_bus_vector,
                        isSelected = selectedRole == LoginRole.DRIVER,
                        onClick    = { onRoleChange(LoginRole.DRIVER) }
                    )
                    RoleIcon(
                        icon       = R.drawable.ic_student,
                        isSelected = selectedRole == LoginRole.STUDENT,
                        onClick    = { onRoleChange(LoginRole.STUDENT) }
                    )
                    RoleIcon(
                        icon       = R.drawable.ic_admin_panel,
                        isSelected = selectedRole == LoginRole.ADMIN,
                        onClick    = { onRoleChange(LoginRole.ADMIN) }
                    )
                }
            }
        }
    }
}

// ─── Sign In Button ───────────────────────────────────────────────────────────
@Composable
private fun SignInButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f  to Color.Transparent,
                            0.45f to Color.Transparent,
                            0.72f to NeumorphAccentPrimary.copy(alpha = 0.30f),
                            1.0f  to NeumorphAccentPrimary.copy(alpha = 0.60f)
                        )
                    ),
                    shape = RoundedCornerShape(NeumorphButtonRadius)
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.TopCenter)
                .neumorphic(
                    cornerRadius    = NeumorphButtonRadius,
                    elevation       = 6.dp,
                    blur            = 12.dp,
                    darkShadowColor = NeumorphAccentGlow
                )
                .background(NeumorphSurface, RoundedCornerShape(NeumorphButtonRadius))
                .border(
                    width = 1.2.dp,
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f  to Color.White.copy(alpha = 0.90f),
                            0.50f to Color.White.copy(alpha = 0.40f),
                            1.0f  to NeumorphAccentPrimary.copy(alpha = 0.80f)
                        )
                    ),
                    shape = RoundedCornerShape(NeumorphButtonRadius)
                )
                .bounceClick { onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier    = Modifier.size(22.dp),
                    color       = NeumorphAccentPrimary,
                    strokeWidth = 2.5.dp
                )
            } else {
                Text(
                    text          = "Sign In",
                    fontSize      = 17.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = NeumorphTextPrimary,
                    letterSpacing = 0.2.sp
                )
            }
        }
    }
}

// ─── Neumorphic Input Field ───────────────────────────────────────────────────
// Pressed-in effect achieved by:
//   1. Box draws the slightly darker background (sunk-in illusion)
//   2. neumorphicInset draws inner shadows ON TOP via drawWithContent
//   3. TextField inside is fully transparent — shadows stay visible
@Composable
private fun NeumorphicInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    enabled: Boolean = true
) {
    val insetBg = Color(0xFFDADADA) // Slightly darker than surface (#E6E6E6)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(insetBg, RoundedCornerShape(NeumorphInputRadius))
            .neumorphicInset(cornerRadius = NeumorphInputRadius, elevation = 5.dp, blur = 10.dp)
    ) {
        OutlinedTextField(
            value                = value,
            onValueChange        = onValueChange,
            placeholder          = {
                Text(
                    text       = placeholder,
                    color      = NeumorphTextSecondary,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            leadingIcon          = leadingIcon,
            trailingIcon         = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions      = keyboardOptions,
            modifier             = Modifier.fillMaxSize(),
            shape  = RoundedCornerShape(NeumorphInputRadius),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = Color.Transparent,
                unfocusedBorderColor    = Color.Transparent,
                disabledBorderColor     = Color.Transparent,
                focusedContainerColor   = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor  = Color.Transparent,
                focusedTextColor        = NeumorphTextPrimary,
                unfocusedTextColor      = NeumorphTextPrimary,
                cursorColor             = NeumorphAccentPrimary
            ),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize      = 14.sp,
                fontWeight    = FontWeight.Medium,
                color         = NeumorphTextPrimary,
                letterSpacing = 0.2.sp
            ),
            singleLine = true,
            enabled    = enabled
        )
    }
}

// ─── Role Icon ────────────────────────────────────────────────────────────────
@Composable
private fun RoleIcon(
    icon: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    isImage: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .then(
                if (isSelected)
                    Modifier.neumorphicInset(cornerRadius = 24.dp, elevation = 4.dp, blur = 8.dp)
                else
                    Modifier.neumorphic(cornerRadius = 24.dp, elevation = 4.dp, blur = 8.dp)
            )
            .background(NeumorphSurface, CircleShape)
            .bounceClick { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isImage) {
            Image(
                painter            = painterResource(id = icon),
                contentDescription = null,
                modifier           = Modifier.size(25.dp),
                contentScale       = ContentScale.Fit,
                alpha              = if (isSelected) 1f else 0.55f
            )
        } else {
            Icon(
                painter            = painterResource(id = icon),
                contentDescription = null,
                tint               = if (isSelected) NeumorphAccentPrimary else NeumorphTextSecondary,
                modifier           = Modifier.size(25.dp)
            )
        }
    }
}

// ─── Data Holder ─────────────────────────────────────────────────────────────
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
                    modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("About Campus Buddy", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TitleColor, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "A smart bus attendance and tracking system for colleges \u2014 GPS-based live tracking, QR attendance, student absence planning, real-time stop management, and multi-role support for drivers, students, and admins.",
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