package com.example.frontend_kotlin.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.example.frontend_kotlin.ui.components.*
import com.example.frontend_kotlin.ui.theme.*

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: (role: String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    var name            by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var phone           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPass        by remember { mutableStateOf(false) }
    var showConfirm     by remember { mutableStateOf(false) }

    // FIX: consumeSuccess() após navegar, igual ao LoginScreen
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onRegisterSuccess(state.role ?: "Client")
            viewModel.consumeSuccess()
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
            Spacer(Modifier.height(52.dp))

            BarbeariaHeader()

            Spacer(Modifier.height(32.dp))

            Text("CRIAR CONTA", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            Text("Preencha seus dados para começar", style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(28.dp))

            state.error?.let { err ->
                ErrorBanner(message = err)
                Spacer(Modifier.height(16.dp))
            }

            BarberTextField(
                value = name,
                onValueChange = { name = it; viewModel.clearError() },
                label = "Nome completo",
                leadingIcon = Icons.Outlined.Person,
                keyboardOptions = KeyboardOptions(
                    keyboardType   = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words
                )
            )

            Spacer(Modifier.height(14.dp))

            BarberTextField(
                value = email,
                onValueChange = { email = it; viewModel.clearError() },
                label = "E-mail",
                leadingIcon = Icons.Outlined.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(14.dp))

            BarberTextField(
                value = phone,
                onValueChange = { phone = it; viewModel.clearError() },
                label = "Telefone / WhatsApp",
                leadingIcon = Icons.Outlined.Phone,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(Modifier.height(14.dp))

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
                            contentDescription = null,
                            tint = OnSurface2
                        )
                    }
                }
            )

            Spacer(Modifier.height(14.dp))

            BarberTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; viewModel.clearError() },
                label = "Confirmar senha",
                leadingIcon = Icons.Outlined.LockOpen,
                visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError      = confirmPassword.isNotEmpty() && confirmPassword != password,
                errorMessage = if (confirmPassword.isNotEmpty() && confirmPassword != password) "Senhas não coincidem" else null,
                trailingIcon = {
                    IconButton(onClick = { showConfirm = !showConfirm }) {
                        Icon(
                            imageVector = if (showConfirm) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                            contentDescription = null,
                            tint = OnSurface2
                        )
                    }
                }
            )

            Spacer(Modifier.height(28.dp))

            GoldButton(
                text    = "Criar conta",
                onClick = { viewModel.registerClient(name, email, phone, password, confirmPassword) },
                loading = state.isLoading
            )

            Spacer(Modifier.height(20.dp))

            Row {
                Text("Já tem uma conta? ", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text     = "Fazer login",
                    style    = MaterialTheme.typography.bodyMedium.copy(color = Gold),
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}