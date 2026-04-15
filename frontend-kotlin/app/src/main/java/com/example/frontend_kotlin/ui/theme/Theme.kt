package com.example.frontend_kotlin.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Definição de cores (Estilo Barbearia: Marrom, Preto, Dourado)
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD4AF37), // Dourado
    secondary = Color(0xFF8B4513), // Marrom
    tertiary = Color(0xFF333333)   // Cinza Escuro
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF8B4513),   // Marrom
    secondary = Color(0xFFD4AF37), // Dourado
    tertiary = Color(0xFF000000),
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
)

@Composable
fun BarbeariaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Isso usa o arquivo Typography.kt que deve estar na mesma pasta
        content = content
    )
}