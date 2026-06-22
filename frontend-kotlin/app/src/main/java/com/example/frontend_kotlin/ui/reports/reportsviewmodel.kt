package com.example.frontend_kotlin.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_kotlin.network.BarberWeeklyReportDto
import com.example.frontend_kotlin.network.MonthlyRevenueReportDto
import com.example.frontend_kotlin.network.RetrofitClient
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ReportsUiState(
    val isLoading: Boolean = false,
    val weeklyReport: BarberWeeklyReportDto? = null,
    val monthlyReport: MonthlyRevenueReportDto? = null,
    val error: String? = null
)

class ReportsViewModel : ViewModel() {

    private val api = RetrofitClient.api
    private var bearerToken: String = ""
    private var userRole: String = ""

    private val _state = MutableStateFlow(ReportsUiState())
    val state: StateFlow<ReportsUiState> = _state

    fun setSession(token: String, role: String) {
        bearerToken = token
        userRole = role
    }

    fun loadReports() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            runCatching {
                val weeklyDeferred = async { api.getWeeklyReport(bearerToken) }
                val monthlyDeferred = if (userRole.equals("Admin", ignoreCase = true)) {
                    async { api.getMonthlyRevenueReport(bearerToken) }
                } else {
                    null
                }

                weeklyDeferred.await() to monthlyDeferred?.await()
            }.onSuccess { (weeklyResponse, monthlyResponse) ->
                if (!weeklyResponse.isSuccessful) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Erro ao carregar relatorio semanal (${weeklyResponse.code()})"
                    )
                    return@onSuccess
                }

                if (monthlyResponse != null && !monthlyResponse.isSuccessful) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        weeklyReport = weeklyResponse.body(),
                        error = "Erro ao carregar faturamento mensal (${monthlyResponse.code()})"
                    )
                    return@onSuccess
                }

                _state.value = _state.value.copy(
                    isLoading = false,
                    weeklyReport = weeklyResponse.body(),
                    monthlyReport = monthlyResponse?.body()
                )
            }.onFailure {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Sem conexao com o servidor"
                )
            }
        }
    }
}
