package com.example.frontend_kotlin.ui.booking

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend_kotlin.network.BarberDto
import com.example.frontend_kotlin.network.ServiceDto
import com.example.frontend_kotlin.ui.components.*
import com.example.frontend_kotlin.ui.theme.*
import java.time.LocalDate
import java.time.format.TextStyle as JTextStyle
import java.util.Locale

@Composable
fun BookingScreen(
    viewModel: BookingViewModel,
    onBookingDone: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadServices()
        viewModel.loadBarbers()
    }

    LaunchedEffect(state.bookingSuccessId) {
        if (state.bookingSuccessId != null) onBookingDone()
    }

    val step = when {
        state.selectedService == null -> 1
        state.selectedBarber  == null -> 2
        state.selectedDate    == null || state.selectedSlot == null -> 3
        else -> 4
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Carbon)
    ) {
        TopBar(
            title = when (step) {
                1    -> "Escolha o Serviço"
                2    -> "Escolha o Barbeiro"
                3    -> "Data e Horário"
                else -> "Confirmar"
            },
            step = step,
            // FIX 2: cada step do botão voltar desfaz APENAS a seleção daquele step
            onBack = {
                when (step) {
                    1    -> onBack()  // step 1 = volta para home
                    2    -> viewModel.clearServiceSelection()   // volta para step 1
                    3    -> viewModel.clearBarberSelection()    // volta para step 2
                    else -> viewModel.clearDateTimeSelection()  // volta para step 3
                }
            }
        )

        state.error?.let { err ->
            Box(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                ErrorBanner(err)
            }
        }

        AnimatedContent(
            targetState = step,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut()
                } else {
                    slideInHorizontally { -it } + fadeIn() togetherWith
                            slideOutHorizontally { it } + fadeOut()
                }
            },
            label = "step_transition"
        ) { currentStep ->
            when (currentStep) {
                1 -> ServicesStep(
                    services  = state.services,
                    isLoading = state.isLoadingServices,
                    selected  = state.selectedService,
                    onSelect  = viewModel::selectService
                )
                2 -> BarbersStep(
                    barbers   = state.barbers,
                    isLoading = state.isLoadingBarbers,
                    selected  = state.selectedBarber,
                    onSelect  = viewModel::selectBarber
                )
                3 -> DateTimeStep(
                    selectedDate   = state.selectedDate,
                    selectedSlot   = state.selectedSlot,
                    availableSlots = state.availableSlots,
                    isLoadingSlots = false,
                    onSelectDate   = viewModel::selectDate,
                    onSelectSlot   = viewModel::selectSlot
                )
                else -> ConfirmStep(
                    state     = state,
                    loading   = state.isCreating,
                    onConfirm = viewModel::confirmBooking,
                    onEdit    = viewModel::clearDateTimeSelection
                )
            }
        }
    }
}

@Composable
private fun TopBar(title: String, step: Int, onBack: () -> Unit) {
    Column {
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
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$step / 4",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Surface3)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = step / 4f)
                    .height(2.dp)
                    .background(Brush.horizontalGradient(listOf(GoldDark, Gold)))
            )
        }
    }
}

@Composable
private fun ServicesStep(
    services: List<ServiceDto>,
    isLoading: Boolean,
    selected: ServiceDto?,
    onSelect: (ServiceDto) -> Unit
) {
    if (isLoading) { LoadingCenter(); return }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(services, key = { it.id }) { service ->
            ServiceCard(
                service    = service,
                isSelected = selected?.id == service.id,
                onClick    = { onSelect(service) }
            )
        }
    }
}

@Composable
private fun ServiceCard(service: ServiceDto, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isSelected) Gold else Surface3, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Surface3 else Surface2
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("✂", fontSize = 28.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                text = service.name,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 14.sp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(6.dp))
            Text("R$ %.2f".format(service.price), style = MaterialTheme.typography.labelMedium)
            Text("${service.durationMinutes} min", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun BarbersStep(
    barbers: List<BarberDto>,
    isLoading: Boolean,
    selected: BarberDto?,
    onSelect: (BarberDto) -> Unit
) {
    if (isLoading) { LoadingCenter(); return }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(barbers, key = { it.id }) { barber ->
            BarberCard(
                barber     = barber,
                isSelected = selected?.id == barber.id,
                onClick    = { onSelect(barber) }
            )
        }
    }
}

@Composable
private fun BarberCard(barber: BarberDto, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isSelected) Gold else Surface3, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Surface3 else Surface2)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(GoldDark, Surface1))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text  = barber.fullName.first().uppercaseChar().toString(),
                    style = MaterialTheme.typography.titleLarge.copy(color = Gold)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(barber.fullName, style = MaterialTheme.typography.titleLarge)
                Text("Barbeiro", style = MaterialTheme.typography.bodyMedium)
            }
            if (isSelected) {
                Spacer(Modifier.weight(1f))
                Icon(Icons.Outlined.CheckCircle, null, tint = Gold, modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Composable
private fun DateTimeStep(
    selectedDate: LocalDate?,
    selectedSlot: String?,
    availableSlots: List<String>,
    isLoadingSlots: Boolean,
    onSelectDate: (LocalDate) -> Unit,
    onSelectSlot: (String) -> Unit
) {
    val today = remember { LocalDate.now() }
    val dates = remember { (0..13).map { today.plusDays(it.toLong()) } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "SELECIONE A DATA",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 10.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(dates) { date ->
                DateChip(
                    date       = date,
                    isSelected = selectedDate == date,
                    onClick    = { onSelectDate(date) }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "HORÁRIOS DISPONÍVEIS",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(start = 16.dp, bottom = 10.dp)
        )

        when {
            selectedDate == null -> {
                Box(Modifier.fillMaxWidth().padding(24.dp), Alignment.Center) {
                    Text("Selecione uma data acima", style = MaterialTheme.typography.bodyMedium)
                }
            }
            isLoadingSlots -> LoadingCenter()
            availableSlots.isEmpty() -> {
                Box(Modifier.fillMaxWidth().padding(24.dp), Alignment.Center) {
                    Text("Nenhum horário disponível", style = MaterialTheme.typography.bodyMedium)
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) {
                    items(availableSlots) { slot ->
                        SlotChip(
                            time       = slot,
                            isSelected = selectedSlot == slot,
                            onClick    = { onSelectSlot(slot) }
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun DateChip(date: LocalDate, isSelected: Boolean, onClick: () -> Unit) {
    val dayName = date.dayOfWeek.getDisplayName(JTextStyle.SHORT, Locale("pt", "BR"))
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Gold else Surface2
        ),
        modifier = Modifier
            .width(60.dp)
            .border(1.dp, if (isSelected) GoldLight else Surface3, RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text  = dayName.uppercase(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 10.sp,
                    color    = if (isSelected) Carbon else OnSurface2
                )
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text  = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color      = if (isSelected) Carbon else OnSurface,
                    fontSize   = 18.sp
                )
            )
        }
    }
}

@Composable
private fun SlotChip(time: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Gold else Surface2
        ),
        modifier = Modifier.border(1.dp, if (isSelected) GoldLight else Surface3, RoundedCornerShape(8.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text  = time,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color      = if (isSelected) Carbon else OnSurface,
                    fontSize   = 13.sp
                )
            )
        }
    }
}

@Composable
private fun ConfirmStep(
    state: BookingUiState,
    loading: Boolean,
    onConfirm: () -> Unit,
    onEdit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        Text("RESUMO DO AGENDAMENTO", style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Surface2),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GoldDark.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                SummaryRow("Serviço",  state.selectedService?.name ?: "")
                SummaryRow("Barbeiro", state.selectedBarber?.fullName ?: "")
                SummaryRow("Data", state.selectedDate?.let {
                    val day   = it.dayOfMonth
                    val month = it.month.getDisplayName(JTextStyle.FULL, Locale("pt", "BR"))
                    val year  = it.year
                    "$day de $month de $year"
                } ?: "")
                SummaryRow("Horário", state.selectedSlot ?: "")
                HorizontalDivider(color = Surface3)
                SummaryRow(
                    label     = "Valor",
                    value     = "R$ %.2f".format(state.selectedService?.price ?: 0.0),
                    highlight = true
                )
                SummaryRow("Duração", "${state.selectedService?.durationMinutes ?: 0} min")
            }
        }

        Spacer(Modifier.height(32.dp))

        GoldButton(
            text    = "Confirmar Agendamento",
            onClick = onConfirm,
            loading = loading
        )
        Spacer(Modifier.height(12.dp))
        OutlineGoldButton(text = "Alterar data/horário", onClick = onEdit)
    }
}

@Composable
private fun SummaryRow(label: String, value: String, highlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text  = value,
            style = if (highlight)
                MaterialTheme.typography.titleLarge.copy(color = Gold)
            else
                MaterialTheme.typography.titleLarge.copy(fontSize = 14.sp)
        )
    }
}

@Composable
private fun LoadingCenter() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Gold)
    }
}