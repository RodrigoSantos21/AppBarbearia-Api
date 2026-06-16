package com.example.frontend_kotlin.ui.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_kotlin.network.LoginRequest
import com.example.frontend_kotlin.network.RegisterClientRequest
import com.example.frontend_kotlin.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val role: String? = null
)

data class AuthResult(
    val token: String,
    val userId: String,
    val name: String,
    val role: String
)

class AuthViewModel : ViewModel() {

    private val api = RetrofitClient.api

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state

    var lastAuthResult: AuthResult? = null
        private set

    // ── Login ─────────────────────────────────────────────────────────────────

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = _state.value.copy(error = "Preencha e-mail e senha")
            return
        }
        viewModelScope.launch {
            _state.value = AuthUiState(isLoading = true)
            runCatching {
                api.login(LoginRequest(email.trim(), password))
            }.onSuccess { response ->
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    lastAuthResult = AuthResult(
                        token  = body.accessToken,
                        userId = body.user.id,
                        name   = body.user.fullName,
                        role   = body.user.role
                    )
                    _state.value = AuthUiState(isSuccess = true, role = body.user.role)
                } else {
                    val msg = when (response.code()) {
                        401  -> "E-mail ou senha incorretos"
                        else -> "Erro ao fazer login (${response.code()})"
                    }
                    _state.value = AuthUiState(error = msg)
                }
            }.onFailure { e ->
                _state.value = AuthUiState(
                    error = if (e is ConnectException || e is SocketTimeoutException)
                        "Não foi possível conectar ao servidor.\nVerifique se o IP está correto e se o backend está rodando."
                    else
                        "Erro de rede: ${e.localizedMessage}"
                )
            }
        }
    }

    // ── Cadastro ──────────────────────────────────────────────────────────────

    fun registerClient(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String
    ) {
        when {
            fullName.isBlank() ->
            { _state.value = _state.value.copy(error = "Informe seu nome completo"); return }
            fullName.length > 150 ->
            { _state.value = _state.value.copy(error = "Nome muito longo (máx. 150 caracteres)"); return }
            email.isBlank() ->
            { _state.value = _state.value.copy(error = "Informe seu e-mail"); return }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
            { _state.value = _state.value.copy(error = "Formato de e-mail inválido"); return }
            phone.isBlank() ->
            { _state.value = _state.value.copy(error = "Informe seu telefone"); return }
            !Regex("""^\+?[\d\s\-()\d]{7,20}$""").matches(phone) ->
            { _state.value = _state.value.copy(error = "Telefone inválido. Use apenas números, espaços, +, -, ( )"); return }
            password.length < 8 ->
            { _state.value = _state.value.copy(error = "Senha deve ter pelo menos 8 caracteres"); return }
            !password.any { it.isUpperCase() } ->
            { _state.value = _state.value.copy(error = "Senha deve conter pelo menos uma letra maiúscula"); return }
            !password.any { it.isDigit() } ->
            { _state.value = _state.value.copy(error = "Senha deve conter pelo menos um número"); return }
            password != confirmPassword ->
            { _state.value = _state.value.copy(error = "As senhas não coincidem"); return }
        }
        viewModelScope.launch {
            _state.value = AuthUiState(isLoading = true)

            val registerResult = runCatching {
                api.register(
                    RegisterClientRequest(
                        fullName        = fullName.trim(),
                        email           = email.trim(),
                        phone           = phone.trim(),
                        password        = password,
                        confirmPassword = confirmPassword
                    )
                )
            }

            val registerResponse = registerResult.getOrNull()
            if (registerResponse == null || !registerResponse.isSuccessful) {
                val msg = when (registerResponse?.code()) {
                    409  -> "E-mail já cadastrado"
                    400  -> {
                        val errorBody = registerResponse.errorBody()?.string()
                        if (!errorBody.isNullOrBlank()) errorBody
                        else "Dados inválidos. Verifique os campos."
                    }
                    null -> "Não foi possível conectar ao servidor"
                    else -> "Erro ao criar conta (${registerResponse.code()})"
                }
                _state.value = AuthUiState(error = msg)
                return@launch
            }

            runCatching {
                api.login(LoginRequest(email.trim(), password))
            }.onSuccess { loginResponse ->
                if (loginResponse.isSuccessful && loginResponse.body() != null) {
                    val body = loginResponse.body()!!
                    lastAuthResult = AuthResult(
                        token  = body.accessToken,
                        userId = body.user.id,
                        name   = body.user.fullName,
                        role   = body.user.role
                    )
                    _state.value = AuthUiState(isSuccess = true, role = body.user.role)
                } else {
                    _state.value = AuthUiState(error = "Conta criada! Faça login para continuar.")
                }
            }.onFailure {
                _state.value = AuthUiState(error = "Conta criada! Faça login para continuar.")
            }
        }
    }

    // FIX: método na classe, não dentro de uma lambda
    fun consumeSuccess() {
        _state.value = AuthUiState() // reset completo — isSuccess volta para false
        lastAuthResult = null
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}