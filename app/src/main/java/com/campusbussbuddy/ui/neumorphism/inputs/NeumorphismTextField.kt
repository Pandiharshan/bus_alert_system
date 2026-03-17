package com.campusbussbuddy.ui.neumorphism.inputs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.runtime.*
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusbussbuddy.ui.theme.*

/**
 * Neumorphic pressed-in text field with inner shadows.
 * Dark shadow top-left + light shadow bottom-right creates the "sunk into surface" illusion.
 *
 * Usage:
 *   NeumorphismTextField(
 *       value         = username,
 *       onValueChange = { username = it },
 *       placeholder   = "Username"
 *   )
 */
@Composable
fun NeumorphismTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    enabled: Boolean = true,
    cornerRadius: Dp = NeumorphInputRadius,
    height: Dp = 56.dp
) {
    // Slightly darker than NeumorphSurface to enhance the "sunk in" illusion
    val insetBg = Color(0xFFDCDCDC)

    var isFocused by remember { mutableStateOf(false) }

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
        modifier             = modifier
            .fillMaxWidth()
            .height(height)
            .onFocusChanged { isFocused = it.isFocused }
            .then(
                if (isFocused) {
                    Modifier.neumorphicInset(cornerRadius = cornerRadius, elevation = 5.dp, blur = 10.dp)
                } else {
                    Modifier.neumorphic(cornerRadius = cornerRadius, elevation = 5.dp, blur = 10.dp)
                }
            ),
        shape  = RoundedCornerShape(cornerRadius),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor      = Color.Transparent,
            unfocusedBorderColor    = Color.Transparent,
            disabledBorderColor     = Color.Transparent,
            focusedContainerColor   = insetBg,
            unfocusedContainerColor = NeumorphSurface,
            disabledContainerColor  = NeumorphSurface,
            focusedTextColor        = NeumorphTextPrimary,
            unfocusedTextColor      = NeumorphTextPrimary,
            cursorColor             = NeumorphAccentPrimary
        ),
        textStyle = TextStyle(
            fontSize      = 14.sp,
            fontWeight    = FontWeight.Medium,
            color         = NeumorphTextPrimary,
            letterSpacing = 0.2.sp
        ),
        singleLine = true,
        enabled    = enabled
    )
}
