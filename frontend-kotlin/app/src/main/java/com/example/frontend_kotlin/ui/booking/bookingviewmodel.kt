package com.example.frontend_kotlin.ui.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_kotlin.network.*
import com.example.frontend_kotlin.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

data class BookingUiState(
    val services: List<ServiceDto>   = emptyList(),
    val barbers: List<BarberDto>     = emptyList(),
    val selectedService: ServiceDto? = null,
    val selectedBarber: BarberDto?   = null,
    val selectedDate: LocalDate?     = null,
    val selectedSlot: String?        = null,
    val availableSlots: List<String> = emptyList(),
    val isLoadingServices: Boolean   = false,
    val isLoadingBarbers: Boolean    = false,
    val isCreating: Boolean          = false,
    val error: String?               = null,
    val bookingSuccessId: String?    = null
)

class BookingViewModel : ViewModel() {

    private val api = RetrofitClient.api
    private var bearerToken: String = ""

    private val _state = MutableStateFlow(BookingUiState())
    val state: StateFlow<BookingUiState> = _state

    fun setToken(token: String) { bearerToken = token }

    // ── Carregamento ──────────────────────────────────────────────────────────

    fun loadServices() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingServices = true, error = null)
            runCatching { api.getServices(bearerToken) }
                .onSuccess { r ->
                    _state.value = _state.value.copy(
                        services          = if (r.isSuccessful) r.body() ?: emptyList() else emptyList(),
                        isLoadingServices = false,
                        error             = if (!r.isSuccessful) "Erro ao carregar serviços (${r.code()})" else null
                    )
                }
                .onFailure {
                    _state.value = _state.value.copy(isLoadingServices = false, error = "Sem conexão com o servidor")
                }
        }
    }

    fun loadBarbers() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingBarbers = true, error = null)
            runCatching { api.getBarbers(bearerToken) }
                .onSuccess { r ->
                    _state.value = _state.value.copy(
                        barbers          = if (r.isSuccessful) r.body() ?: emptyList() else emptyList(),
                        isLoadingBarbers = false,
                        error            = if (!r.isSuccessful) "Erro ao carregar barbeiros (${r.code()})" else null
                    )
                }
                .onFailure {
                    _state.value = _state.value.copy(isLoadingBarbers = false, error = "Sem conexão com o servidor")
                }
        }
    }

    // ── Seleções ──────────────────────────────────────────────────────────────

    fun selectService(service: ServiceDto) {
        _state.value = _state.value.copy(
            selectedService = service,
            selectedBarber  = null,
            selectedDate    = null,
            selectedSlot    = null,
            availableSlots  = emptyList()
        )
    }

    fun selectBarber(barber: BarberDto) {
        _state.value = _state.value.copy(
            selectedBarber = barber,
            selectedDate   = null,
            selectedSlot   = null,
            availableSlots = emptyList()
        )
    }

    fun selectDate(date: LocalDate) {
        val duration = _state.value.selectedService?.durationMinutes ?: 30
        _state.value = _state.value.copy(
            selectedDate   = date,
            selectedSlot   = null,
            // FIX 1: filtra slots que já passaram em UTC
            availableSlots = generateSlots(duration, date)
        )
    }

    fun selectSlot(slot: String) {
        _state.value = _state.value.copy(selectedSlot = slot)
    }

    // ── Voltar por step ───────────────────────────────────────────────────────

    fun clearServiceSelection() {
        _state.value = _state.value.copy(
            selectedService = null,
            selectedBarber  = null,
            selectedDate    = null,
            selectedSlot    = null,
            availableSlots  = emptyList(),
            error           = null
        )
    }

    fun clearBarberSelection() {
        _state.value = _state.value.copy(
            selectedBarber = null,
            selectedDate   = null,
            selectedSlot   = null,
            availableSlots = emptyList(),
            error          = null
        )
    }

    fun clearDateTimeSelection() {
        _state.value = _state.value.copy(
            selectedDate   = null,
            selectedSlot   = null,
            availableSlots = emptyList(),
            error          = null
        )
    }

    // ── Geração de slots filtrando passado em UTC ─────────────────────────────

    /**
     * Gera slots entre 08:00 e 18:00 com intervalo = duração do serviço.
     * FIX 1: Para hoje, remove slots cujo horário UTC já passou (margem de 5 min).
     */
    private fun generateSlots(durationMinutes: Int, date: LocalDate): List<String> {
        val step  = maxOf(durationMinutes, 30)
        val slots = mutableListOf<String>()
        var hour = 8; var minute = 0

        while (hour < 18) {
            slots.add("%02d:%02d".format(hour, minute))
            minute += step
            hour   += minute / 60
            minute %= 60
        }

        // Se a data selecionada é hoje, filtra slots que já passaram em UTC
        val today = LocalDate.now(ZoneId.systemDefault())
        if (date == today) {
            val nowUtc = LocalDateTime.now(ZoneOffset.UTC).plusMinutes(5) // margem de 5 min
            return slots.filter { slot ->
                val (h, m) = slot.split(":").map { it.toInt() }
                // Converte o slot (horário local) para UTC para comparar
                val slotUtc = LocalDateTime.of(date, LocalTime.of(h, m))
                    .atZone(ZoneId.systemDefault())
                    .withZoneSameInstant(ZoneOffset.UTC)
                    .toLocalDateTime()
                slotUtc.isAfter(nowUtc)
            }
        }

        return slots
    }

    // ── Confirmar agendamento ─────────────────────────────────────────────────

    fun confirmBooking() {
        val s       = _state.value
        val barber  = s.selectedBarber  ?: run { _state.value = s.copy(error = "Selecione um barbeiro"); return }
        val service = s.selectedService ?: run { _state.value = s.copy(error = "Selecione um serviço"); return }
        val date    = s.selectedDate    ?: run { _state.value = s.copy(error = "Selecione uma data"); return }
        val slot    = s.selectedSlot    ?: run { _state.value = s.copy(error = "Selecione um horário"); return }

        val (hour, minute) = slot.split(":").map { it.toInt() }

        // FIX 1: envia em UTC com sufixo Z — C# compara com DateTime.UtcNow corretamente
        val scheduledAt = LocalDateTime.of(date, LocalTime.of(hour, minute))
            .atZone(ZoneId.systemDefault())
            .withZoneSameInstant(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_INSTANT)  // ex: "2026-04-20T17:30:00Z"

        viewModelScope.launch {
            _state.value = _state.value.copy(isCreating = true, error = null)
            runCatching {
                api.createAppointment(
                    token   = bearerToken,
                    request = CreateAppointmentRequest(
                        barberId    = barber.id,
                        serviceId   = service.id,
                        scheduledAt = scheduledAt
                    )
                )
            }.onSuccess { r ->
                _state.value = if (r.isSuccessful) {
                    _state.value.copy(bookingSuccessId = "ok", isCreating = false)
                } else {
                    val errorBody = r.errorBody()?.string()
                    val msg = when (r.code()) {
                        409  -> "Horário já ocupado. Escolha outro."
                        400  -> if (!errorBody.isNullOrBlank()) errorBody else "Dados inválidos."
                        else -> "Erro ao confirmar agendamento (${r.code()})"
                    }
                    _state.value.copy(error = msg, isCreating = false)
                }
            }.onFailure {
                _state.value = _state.value.copy(error = "Sem conexão com o servidor", isCreating = false)
            }
        }
    }

    fun clearError()   { _state.value = _state.value.copy(error = null) }
    fun resetBooking() { _state.value = BookingUiState() }
}