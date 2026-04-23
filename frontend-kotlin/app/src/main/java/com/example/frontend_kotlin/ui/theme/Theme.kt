package com.example.frontend_kotlin.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ─── Cores ───────────────────────────────────────────────────────────────────
val Gold        = Color(0xFFD4AF37)
val GoldLight   = Color(0xFFF0D060)
val GoldDark    = Color(0xFF9C7E1A)
val Carbon      = Color(0xFF0D0D0D)
val Surface1    = Color(0xFF161616)
val Surface2    = Color(0xFF1F1F1F)
val Surface3    = Color(0xFF2A2A2A)
val OnSurface   = Color(0xFFEEEEEE)
val OnSurface2  = Color(0xFF9E9E9E)
val ErrorRed    = Color(0xFFCF6679)

// ─── Tipografia ───────────────────────────────────────────────────────────────
// Usando fontes do sistema para não precisar adicionar assets manualmente.
// Substitua por fontes customizadas (ex: Playfair Display) se desejar.
val AppTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        letterSpacing = 1.sp,
        color = Gold
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        color = OnSurface
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = OnSurface2
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 1.2.sp,
        color = Gold
    )
)

// ─── ColorScheme ─────────────────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary            = Gold,
    onPrimary          = Carbon,
    primaryContainer   = GoldDark,
    secondary          = GoldLight,
    background         = Carbon,
    surface            = Surface1,
    surfaceVariant     = Surface2,
    onBackground       = OnSurface,
    onSurface          = OnSurface,
    onSurfaceVariant   = OnSurface2,
    error              = ErrorRed
)

// ─── Theme Composable ─────────────────────────────────────────────────────────
@Composable
fun BarbeariaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = AppTypography,
        content     = content
    )
}