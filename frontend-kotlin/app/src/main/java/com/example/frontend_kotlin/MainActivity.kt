package com.example.frontend_kotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.frontend_kotlin.navigation.AppNavGraph
import com.example.frontend_kotlin.session.SessionManager
import com.example.frontend_kotlin.ui.theme.BarbeariaTheme
import com.example.frontend_kotlin.ui.theme.Carbon

class MainActivity : ComponentActivity() {

    private var sessionKey by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SessionManager.clear(this)

        enableEdgeToEdge()
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars     = false  // ícones brancos na status bar
            isAppearanceLightNavigationBars = false  // ícones brancos na nav bar
        }

        setContent {
            BarbeariaTheme {
                // Surface externo: fundo Carbon cobrindo as barras do sistema
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color    = Carbon
                ) {
                    // Surface interno: padding para o conteúdo não ficar sob as barras
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .systemBarsPadding()
                            .imePadding(),
                        color = Color.Transparent
                    ) {
                        AppNavGraph()
                    }
                }
            }
        }
    }
}