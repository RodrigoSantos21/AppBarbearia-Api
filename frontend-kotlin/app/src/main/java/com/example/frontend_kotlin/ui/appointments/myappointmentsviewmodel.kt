package com.example.frontend_kotlin.ui.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_kotlin.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AppointmentItem(
    val id: String,
    val clientName: String,
    val barberName: String,
    val serviceName: String,
    val servicePrice: Double,
    val serviceDurationMinutes: Int,
    val scheduledAt: String,
    val scheduledEnd: String,
    val status: String,
    val cancellationReason: String?
)

data class MyAppointmentsUiState(
    val isLoading: Boolean              = false,
    val appointments: List<AppointmentItem> = emptyList(),
    val error: String?                  = null
)

class MyAppointmentsViewModel : ViewModel() {

    private val api = RetrofitClient.api
    private var bearerToken: String = ""

    private val _state = MutableStateFlow(MyAppointmentsUiState())
    val state: StateFlow<MyAppointmentsUiState> = _state

    fun setToken(token: String) {
        bearerToken = token
    }

    fun loadAppointments() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            runCatching { api.getMyAppointments(bearerToken) }
                .onSuccess { r ->
                    if (r.isSuccessful) {
                        val items = r.body()?.map { dto ->
                            AppointmentItem(
                                id                    = dto.id,
                                clientName            = dto.clientName,
                                barberName            = dto.barberName,
                                serviceName           = dto.serviceName,
                                servicePrice          = dto.servicePrice,
                                serviceDurationMinutes = dto.serviceDurationMinutes,
                                scheduledAt           = dto.scheduledAt,
                                scheduledEnd          = dto.scheduledEnd,
                                status                = dto.status,
                                cancellationReason    = dto.cancellationReason
                            )
                        } ?: emptyList()
                        _state.value = _state.value.copy(
                            appointments = items,
                            isLoading    = false
                        )
                    } else {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error     = "Erro ao carregar agendamentos (${r.code()})"
                        )
                    }
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error     = "Sem conexão com o servidor"
                    )
                }
        }
    }
}