package com.example.frontend_kotlin.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontend_kotlin.ui.components.GoldButton
import com.example.frontend_kotlin.ui.components.OutlineGoldButton
import com.example.frontend_kotlin.ui.theme.*

@Composable
fun HomeClientScreen(
    userName: String,
    onNewBooking: () -> Unit,
    onViewAppointments: () -> Unit,
    onViewReports: (() -> Unit)? = null,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Carbon)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

        // ── Cabeçalho ─────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Olá, ${userName.split(" ").first()} 👋",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "O que vamos fazer hoje?",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            // FIX 4: apenas um ícone de logout no header
            IconButton(onClick = onLogout) {
                Icon(
                    imageVector = Icons.Outlined.Logout,
                    contentDescription = "Sair",
                    tint = OnSurface2
                )
            }
        }

        Spacer(Modifier.height(40.dp))

        // ── Card agendamento ──────────────────────────────────────────────────
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Surface2),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("✂", style = MaterialTheme.typography.displayLarge)
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Agendar horário",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Escolha o serviço, barbeiro e horário ideal para você",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(20.dp))
                GoldButton(text = "Novo Agendamento", onClick = onNewBooking)
            }
        }

        Spacer(Modifier.height(16.dp))

        if (onViewReports != null) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface2),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.BarChart,
                            contentDescription = null,
                            tint = Gold,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Relatorios", style = MaterialTheme.typography.titleLarge)
                            Text("Desempenho e faturamento", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    IconButton(onClick = onViewReports) {
                        Icon(
                            imageVector = Icons.Outlined.ChevronRight,
                            contentDescription = "Ver relatorios",
                            tint = Gold
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }

        // ── Card histórico ────────────────────────────────────────────────────
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Surface2),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = Gold,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Meus agendamentos", style = MaterialTheme.typography.titleLarge)
                        Text("Veja seu histórico", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                IconButton(onClick = onViewAppointments) {
                    Icon(
                        imageVector = Icons.Outlined.ChevronRight,
                        contentDescription = "Ver agendamentos",
                        tint = Gold
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))
    }
}
