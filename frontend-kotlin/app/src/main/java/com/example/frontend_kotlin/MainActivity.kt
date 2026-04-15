package com.example.frontend_kotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.frontend_kotlin.ui.theme.BarbeariaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BarbeariaTheme {
                // O NavController gerencia a troca de telas
                val navController = rememberNavController()

                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    // Definição das rotas do App
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            TelaLogin(onLoginSucesso = { tipoUsuario ->
                                if (tipoUsuario == "admin") {
                                    navController.navigate("dashboard_admin")
                                } else {
                                    navController.navigate("dashboard_cliente")
                                }
                            })
                        }
                        composable("dashboard_cliente") { TelaCliente() }
                        composable("dashboard_admin") { TelaAdmin() }
                    }
                }
            }
        }
    }
}

// --- TELA DE LOGIN ---
@Composable
fun TelaLogin(onLoginSucesso: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Barbearia Premium", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-mail") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // Lógica temporária: se o e-mail for 'admin', vai para tela de admin
                if (email == "admin") onLoginSucesso("admin")
                else onLoginSucesso("cliente")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar")
        }
    }
}

// --- TELA DO CLIENTE (Nível 1) ---
@Composable
fun TelaCliente() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Bem-vindo, Cliente!", style = MaterialTheme.typography.headlineMedium)
        Text("Aqui você poderá ver os serviços e agendar horários.")

        Spacer(modifier = Modifier.height(20.dp))

        // Exemplo de Card de Serviço (Simulando o catálogo)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Corte Masculino", style = MaterialTheme.typography.titleLarge)
                Text("R$ 45,00 - 30 min")
                Button(onClick = { /* Abrir seletor de data */ }) {
                    Text("Agendar")
                }
            }
        }
    }
}

// --- TELA DO BARBEIRO/ADMIN (Nível 2) ---
@Composable
fun TelaAdmin() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Painel do Barbeiro", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Text("Visualize a agenda e relatórios gerenciais.")

        Spacer(modifier = Modifier.height(20.dp))

        Text("Próximos Agendamentos:", style = MaterialTheme.typography.titleMedium)

        // Simulação de lista de agenda
        ListItem(
            headlineContent = { Text("João Silva - 14:00") },
            supportingContent = { Text("Serviço: Barba") },
            trailingContent = { Text("Pendente") }
        )
        Divider()
    }
}