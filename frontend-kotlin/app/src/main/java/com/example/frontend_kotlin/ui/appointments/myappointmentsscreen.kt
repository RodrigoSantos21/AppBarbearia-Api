package com.example.frontend_kotlin.ui.appointments

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.frontend_kotlin.ui.theme.*

@Composable
fun MyAppointmentsScreen(
    token: String,
    onBack: () -> Unit,
    viewModel: MyAppointmentsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setToken(token)
        viewModel.loadAppointments()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Carbon)
    ) {
        // ── TopBar ────────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = Gold)
            }
            Text(
                text = "Meus Agendamentos",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
        }
        HorizontalDivider(color = Surface3)

        // ── Conteúdo ──────────────────────────────────────────────────────────
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Gold)
                }
            }
            state.error != null -> {
                Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Erro ao carregar", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(8.dp))
                        Text(state.error ?: "", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadAppointments() },
                            colors  = ButtonDefaults.buttonColors(containerColor = Gold)
                        ) {
                            Text("Tentar novamente", color = Carbon)
                        }
                    }
                }
            }
            state.appointments.isEmpty() -> {
                Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✂", fontSize = 48.sp)
                        Spacer(Modifier.height(16.dp))
                        Text("Nenhum agendamento", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Seus agendamentos aparecerão aqui",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.appointments, key = { it.id }) { appointment ->
                        AppointmentCard(appointment)
                    }
                }
            }
        }
    }
}

@Composable
private fun AppointmentCard(appointment: AppointmentItem) {
    Card(
        shape  = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface2),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Surface3, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // ── Header: serviço + status ──────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text  = appointment.serviceName,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                StatusBadge(appointment.status)
            }

            Spacer(Modifier.height(12.dp))

            // ── Detalhes ──────────────────────────────────────────────────────
            DetailRow(label = "Barbeiro", value = appointment.barberName)
            DetailRow(label = "Data/Hora", value = formatDateTime(appointment.scheduledAt))
            DetailRow(label = "Duração",   value = "${appointment.serviceDurationMinutes} min")
            DetailRow(
                label = "Valor",
                value = "R$ %.2f".format(appointment.servicePrice),
                highlight = true
            )

            // Motivo de cancelamento se houver
            if (!appointment.cancellationReason.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = Surface3)
                Spacer(Modifier.height(8.dp))
                Text(
                    text  = "Motivo: ${appointment.cancellationReason}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = ErrorRed)
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (label, color) = when (status.lowercase()) {
        "pending"   -> "Pendente"   to Color(0xFFFFA726)
        "confirmed" -> "Confirmado" to Color(0xFF66BB6A)
        "finished"  -> "Concluído"  to Gold
        "cancelled" -> "Cancelado"  to ErrorRed
        else        -> status       to OnSurface2
    }
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.15f),
        modifier = Modifier.border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.labelMedium.copy(
                color    = color,
                fontSize = 11.sp
            ),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String, highlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text  = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color      = if (highlight) Gold else OnSurface
            )
        )
    }
}

/** Formata "2026-04-20T14:30:00" → "20/04/2026 às 14:30" */
private fun formatDateTime(raw: String): String {
    return try {
        val clean = raw.take(16) // "2026-04-20T14:30"
        val (datePart, timePart) = clean.split("T")
        val (year, month, day)   = datePart.split("-")
        "$day/$month/$year às $timePart"
    } catch (e: Exception) {
        raw
    }
}