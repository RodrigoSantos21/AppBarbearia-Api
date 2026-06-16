package com.example.frontend_kotlin.ui.booking

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.example.frontend_kotlin.ui.components.GoldButton
import com.example.frontend_kotlin.ui.components.OutlineGoldButton
import com.example.frontend_kotlin.ui.theme.*

@Composable
fun BookingSuccessScreen(
    serviceName: String,
    barberName: String,
    date: String,
    slot: String,
    price: Double,
    onGoHome: () -> Unit,
    onViewAppointments: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.4f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessMedium
        ),
        label = "icon_scale"
    )

    LaunchedEffect(Unit) { visible = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Carbon)
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(GoldDark, Surface1)))
                .border(2.dp, Gold, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Check, null, tint = Gold, modifier = Modifier.size(48.dp))
        }

        Spacer(Modifier.height(28.dp))

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(600, delayMillis = 300)) + slideInVertically { it / 2 }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("AGENDADO!", style = MaterialTheme.typography.displayLarge)
                Spacer(Modifier.height(8.dp))
                Text("Seu horário está confirmado", style = MaterialTheme.typography.bodyMedium)

                Spacer(Modifier.height(32.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GoldDark.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                ) {
                    Column(
                        Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        InfoRow("Serviço",  serviceName)
                        InfoRow("Barbeiro", barberName)
                        InfoRow("Data",     date)
                        InfoRow("Horário",  slot)
                        HorizontalDivider(color = Surface3)
                        InfoRow("Total", "R$ %.2f".format(price), highlight = true)
                    }
                }

                Spacer(Modifier.height(32.dp))

                GoldButton(text = "Ver meus agendamentos", onClick = onViewAppointments)
                Spacer(Modifier.height(12.dp))
                OutlineGoldButton(text = "Ir para o início", onClick = onGoHome)
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, highlight: Boolean = false) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(
            value,
            style = if (highlight)
                MaterialTheme.typography.titleLarge.copy(color = Gold)
            else
                MaterialTheme.typography.titleLarge.copy(
                    fontSize = TextUnit(14f, TextUnitType.Sp)
                )
        )
    }
}