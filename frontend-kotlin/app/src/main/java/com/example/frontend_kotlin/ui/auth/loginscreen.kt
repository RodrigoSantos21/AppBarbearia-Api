package com.example.frontend_kotlin.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.example.frontend_kotlin.ui.components.*
import com.example.frontend_kotlin.ui.theme.*

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: (role: String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }

    // Reage ao sucesso
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onLoginSuccess(state.role ?: "Client")
            viewModel.consumeSuccess()  // ← limpa o estado
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Carbon)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(72.dp))

            BarbeariaHeader(subtitle = "Estilo que fala por você")

            Spacer(Modifier.height(48.dp))

            Text(
                text = "ENTRAR",
                style = MaterialTheme.typography.titleLarge,
                color = OnSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Acesse sua conta",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(32.dp))

            // Erro
            state.error?.let { err ->
                ErrorBanner(message = err)
                Spacer(Modifier.height(16.dp))
            }

            BarberTextField(
                value = email,
                onValueChange = { email = it; viewModel.clearError() },
                label = "E-mail",
                leadingIcon = Icons.Outlined.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(16.dp))

            BarberTextField(
                value = password,
                onValueChange = { password = it; viewModel.clearError() },
                label = "Senha",
                leadingIcon = Icons.Outlined.Lock,
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector = if (showPass) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                            contentDescription = if (showPass) "Ocultar senha" else "Mostrar senha",
                            tint = OnSurface2
                        )
                    }
                }
            )

            Spacer(Modifier.height(28.dp))

            GoldButton(
                text = "Entrar",
                onClick = { viewModel.login(email, password) },
                loading = state.isLoading
            )

            Spacer(Modifier.height(24.dp))

            DividerWithText(text = "ou")

            Spacer(Modifier.height(24.dp))

            OutlineGoldButton(
                text = "Criar conta",
                onClick = onNavigateToRegister
            )

            Spacer(Modifier.height(32.dp))

            Row {
                Text("Ainda não tem conta? ", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "Cadastre-se",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Gold),
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}