package com.example.frontend_kotlin.ui.reports

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.frontend_kotlin.network.BarberWeeklyReportDto
import com.example.frontend_kotlin.network.DailyOccupancyDto
import com.example.frontend_kotlin.network.MonthlyRevenueReportDto
import com.example.frontend_kotlin.ui.theme.*

@Composable
fun ReportsScreen(
    token: String,
    userRole: String,
    onBack: () -> Unit,
    viewModel: ReportsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(token, userRole) {
        viewModel.setSession(token, userRole)
        viewModel.loadReports()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Carbon)
    ) {
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
                text = "Relatorios",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { viewModel.loadReports() }) {
                Icon(Icons.Outlined.Refresh, contentDescription = "Atualizar", tint = Gold)
            }
        }
        HorizontalDivider(color = Surface3)

        when {
            state.isLoading && state.weeklyReport == null && state.monthlyReport == null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Gold)
                }
            }

            state.weeklyReport == null && state.monthlyReport == null && state.error != null -> {
                Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Erro ao carregar", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(8.dp))
                        Text(state.error ?: "", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadReports() },
                            colors = ButtonDefaults.buttonColors(containerColor = Gold)
                        ) {
                            Text("Tentar novamente", color = Carbon)
                        }
                    }
                }
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (state.error != null) {
                        item {
                            ReportNotice(message = state.error ?: "")
                        }
                    }

                    state.weeklyReport?.let { weekly ->
                        item { WeeklyReportSection(weekly) }
                    }

                    state.monthlyReport?.let { monthly ->
                        item { MonthlyRevenueSection(monthly) }
                    }

                    if (!userRole.equals("Admin", ignoreCase = true)) {
                        item {
                            ReportNotice(
                                message = "Faturamento mensal disponivel apenas para administradores."
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeeklyReportSection(report: BarberWeeklyReportDto) {
    ReportCard {
        Text("Semana do barbeiro", style = MaterialTheme.typography.titleLarge)
        Text(
            "${formatDate(report.weekStart)} a ${formatDate(report.weekEnd)}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ReportMetricCard(
                label = "Receita",
                value = formatCurrency(report.totalRevenue),
                modifier = Modifier.weight(1f)
            )
            ReportMetricCard(
                label = "Conclusao",
                value = "${report.completionRate}%",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ReportMetricCard(
                label = "Finalizados",
                value = report.totalFinished.toString(),
                modifier = Modifier.weight(1f)
            )
            ReportMetricCard(
                label = "Cancelados",
                value = report.totalCancelled.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(18.dp))
        Text("Ocupacao diaria", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(10.dp))
        WeeklyStackedBarChart(report.dailyBreakdown)
        Spacer(Modifier.height(12.dp))
        ChartLegend()
    }
}

@Composable
private fun MonthlyRevenueSection(report: MonthlyRevenueReportDto) {
    ReportCard {
        Text("Faturamento mensal", style = MaterialTheme.typography.titleLarge)
        Text(report.monthName, style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ReportMetricCard(
                label = "Receita",
                value = formatCurrency(report.totalRevenue),
                modifier = Modifier.weight(1f)
            )
            ReportMetricCard(
                label = "Ticket medio",
                value = formatCurrency(report.averageTicket),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ReportMetricCard(
                label = "Atendimentos",
                value = report.totalAppointments.toString(),
                modifier = Modifier.weight(1f)
            )
            ReportMetricCard(
                label = "Servico estrela",
                value = report.starService?.serviceName ?: "-",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(18.dp))
        MonthlyRevenueChart(report)

        report.starService?.let { service ->
            Spacer(Modifier.height(12.dp))
            Text(
                "${service.timesPerformed} atendimentos em ${service.serviceName}",
                style = MaterialTheme.typography.bodyMedium.copy(color = OnSurface)
            )
        }
    }
}

@Composable
private fun WeeklyStackedBarChart(days: List<DailyOccupancyDto>) {
    if (days.isEmpty()) {
        EmptyChart("Sem dados na semana")
        return
    }

    val maxTotal = (days.maxOfOrNull { it.totalCount() } ?: 0).coerceAtLeast(1)

    Column {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .border(1.dp, Surface3, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            val gap = 10.dp.toPx()
            val barWidth = ((size.width - gap * (days.size + 1)) / days.size).coerceAtLeast(8.dp.toPx())
            val radius = CornerRadius(6.dp.toPx(), 6.dp.toPx())

            days.forEachIndexed { index, day ->
                val x = gap + index * (barWidth + gap)
                var bottom = size.height

                day.chartSegments().forEach { segment ->
                    if (segment.value > 0) {
                        val segmentHeight = size.height * (segment.value.toFloat() / maxTotal.toFloat())
                        drawRoundRect(
                            color = segment.color,
                            topLeft = Offset(x, bottom - segmentHeight),
                            size = Size(barWidth, segmentHeight),
                            cornerRadius = radius
                        )
                        bottom -= segmentHeight
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            days.forEach { day ->
                Text(
                    text = day.dayOfWeek.take(3),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun MonthlyRevenueChart(report: MonthlyRevenueReportDto) {
    val starRevenue = report.starService?.totalRevenue ?: 0.0
    val rows = listOf(
        "Receita total" to report.totalRevenue,
        "Servico estrela" to starRevenue,
        "Ticket medio" to report.averageTicket
    )
    val maxValue = rows.maxOf { it.second }.coerceAtLeast(1.0)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Comparativo do mes", style = MaterialTheme.typography.titleLarge)
        rows.forEachIndexed { index, row ->
            val color = when (index) {
                0 -> Gold
                1 -> Color(0xFF66BB6A)
                else -> Color(0xFF42A5F5)
            }
            HorizontalValueBar(
                label = row.first,
                value = formatCurrency(row.second),
                fraction = (row.second / maxValue).toFloat(),
                color = color
            )
        }
    }
}

@Composable
private fun HorizontalValueBar(
    label: String,
    value: String,
    fraction: Float,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = OnSurface,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .background(Surface3, RoundedCornerShape(8.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .background(color, RoundedCornerShape(8.dp))
            )
        }
    }
}

@Composable
private fun ReportMetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.border(1.dp, Surface3, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        color = Surface1
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(6.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Gold,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 2
            )
        }
    }
}

@Composable
private fun ReportCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface2),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Surface3, RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
private fun ReportNotice(message: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Gold.copy(alpha = 0.12f),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Gold.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
    ) {
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium.copy(color = OnSurface),
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
private fun ChartLegend() {
    val items = listOf(
        "Finalizados" to Gold,
        "Confirmados" to Color(0xFF66BB6A),
        "Pendentes" to Color(0xFFFFA726),
        "Cancelados" to ErrorRed
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEach { item ->
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(item.second, RoundedCornerShape(2.dp))
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(item.first, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyChart(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .border(1.dp, Surface3, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(message, style = MaterialTheme.typography.bodyMedium)
    }
}

private data class ChartSegment(
    val value: Int,
    val color: Color
)

private fun DailyOccupancyDto.totalCount(): Int =
    finishedCount + confirmedCount + pendingCount + cancelledCount

private fun DailyOccupancyDto.chartSegments(): List<ChartSegment> = listOf(
    ChartSegment(finishedCount, Gold),
    ChartSegment(confirmedCount, Color(0xFF66BB6A)),
    ChartSegment(pendingCount, Color(0xFFFFA726)),
    ChartSegment(cancelledCount, ErrorRed)
)

private fun formatDate(raw: String): String {
    return try {
        val (year, month, day) = raw.take(10).split("-")
        "$day/$month/$year"
    } catch (e: Exception) {
        raw
    }
}

private fun formatCurrency(value: Double): String {
    return "R$ %.2f".format(value).replace(".", ",")
}
