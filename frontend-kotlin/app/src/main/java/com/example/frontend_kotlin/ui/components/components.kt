package com.example.frontend_kotlin.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.example.frontend_kotlin.ui.theme.*

// ─── Botão primário dourado ───────────────────────────────────────────────────
@Composable
fun GoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Gold,
            contentColor   = Carbon,
            disabledContainerColor = GoldDark.copy(alpha = 0.5f)
        )
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Carbon,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Carbon,
                    fontSize = TextUnit(
                        13f, TextUnitType.Sp
                    )
                )
            )
        }
    }
}

// ─── Botão outline ────────────────────────────────────────────────────────────
@Composable
fun OutlineGoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Gold),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Gold)
    ) {
        Text(text.uppercase(), style = MaterialTheme.typography.labelMedium)
    }
}

// ─── Campo de texto estilizado ────────────────────────────────────────────────
@Composable
fun BarberTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false,
    errorMessage: String? = null,
    singleLine: Boolean = true
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            leadingIcon = leadingIcon?.let { icon ->
                { Icon(imageVector = icon, contentDescription = null, tint = Gold.copy(alpha = 0.7f)) }
            },
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            isError = isError,
            singleLine = singleLine,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = Gold,
                unfocusedBorderColor    = Surface3,
                focusedLabelColor       = Gold,
                unfocusedLabelColor     = OnSurface2,
                cursorColor             = Gold,
                focusedTextColor        = OnSurface,
                unfocusedTextColor      = OnSurface,
                errorBorderColor        = ErrorRed,
                errorLabelColor         = ErrorRed
            )
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = ErrorRed,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}

// ─── Divisor com texto ────────────────────────────────────────────────────────
@Composable
fun DividerWithText(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = Surface3)
        Text(
            text = "  $text  ",
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurface2
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = Surface3)
    }
}

// ─── Indicador de erro global ─────────────────────────────────────────────────
@Composable
fun ErrorBanner(message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ErrorRed.copy(alpha = 0.15f))
            .border(1.dp, ErrorRed.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = message,
            color = ErrorRed,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// ─── Logo / Header da barbearia ───────────────────────────────────────────────
@Composable
fun BarbeariaHeader(subtitle: String = "") {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Ícone decorativo simples (substitua por Image() com asset real)
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.radialGradient(listOf(GoldDark, Carbon))
                )
                .border(1.dp, Gold.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("✂", style = MaterialTheme.typography.displayLarge.copy(fontSize = TextUnit(28f, TextUnitType.Sp)))
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "BARBEARIA",
            style = MaterialTheme.typography.displayLarge
        )
        if (subtitle.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}