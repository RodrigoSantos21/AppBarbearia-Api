package com.example.frontend_kotlin.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.frontend_kotlin.session.SessionManager
import com.example.frontend_kotlin.ui.appointments.MyAppointmentsScreen
import com.example.frontend_kotlin.ui.auth.AuthViewModel
import com.example.frontend_kotlin.ui.auth.LoginScreen
import com.example.frontend_kotlin.ui.auth.RegisterScreen
import com.example.frontend_kotlin.ui.booking.BookingScreen
import com.example.frontend_kotlin.ui.booking.BookingSuccessScreen
import com.example.frontend_kotlin.ui.booking.BookingViewModel
import com.example.frontend_kotlin.ui.home.HomeClientScreen

object Routes {
    const val LOGIN           = "login"
    const val REGISTER        = "register"
    const val HOME_CLIENT     = "home_client"
    const val HOME_BARBER     = "home_barber"
    const val BOOKING         = "booking"
    const val BOOKING_SUCCESS = "booking_success"
    const val MY_APPOINTMENTS = "my_appointments"
}

@Composable
fun AppNavGraph() {
    val ctx = LocalContext.current

    // Estado de autenticação como mutableStateOf — muda aqui = recomposição imediata
    var isLoggedIn by remember { mutableStateOf(SessionManager.isLoggedIn(ctx)) }
    var userRole   by remember { mutableStateOf(SessionManager.getRole(ctx) ?: "") }

    if (!isLoggedIn) {
        // ── Grafo não autenticado ─────────────────────────────────────────────
        val authNavController = rememberNavController()
        val authViewModel: AuthViewModel = viewModel()

        NavHost(navController = authNavController, startDestination = Routes.LOGIN) {

            composable(Routes.LOGIN) {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = { role ->
                        authViewModel.lastAuthResult?.let { result ->
                            SessionManager.save(ctx, result.token, result.userId, result.name, result.role)
                        }
                        // Atualiza estado — troca para grafo autenticado imediatamente
                        userRole   = role
                        isLoggedIn = true
                    },
                    onNavigateToRegister = { authNavController.navigate(Routes.REGISTER) }
                )
            }

            composable(Routes.REGISTER) {
                RegisterScreen(
                    viewModel = authViewModel,
                    onRegisterSuccess = { role ->
                        authViewModel.lastAuthResult?.let { result ->
                            SessionManager.save(ctx, result.token, result.userId, result.name, result.role)
                        }
                        userRole   = role
                        isLoggedIn = true
                    },
                    onNavigateToLogin = { authNavController.popBackStack() }
                )
            }
        }

    } else {
        // ── Grafo autenticado ─────────────────────────────────────────────────
        val appNavController = rememberNavController()
        val bookingViewModel: BookingViewModel = viewModel()

        val onLogout: () -> Unit = {
            SessionManager.clear(ctx)
            bookingViewModel.resetBooking()
            // Atualiza estado — troca para grafo não autenticado imediatamente
            isLoggedIn = false
            userRole   = ""
        }

        val startDestination = if (userRole in listOf("Barber", "Admin"))
            Routes.HOME_BARBER else Routes.HOME_CLIENT

        NavHost(navController = appNavController, startDestination = startDestination) {

            composable(Routes.HOME_CLIENT) {
                val userName = SessionManager.getUserName(ctx) ?: "Cliente"
                HomeClientScreen(
                    userName           = userName,
                    onNewBooking       = {
                        bookingViewModel.resetBooking()
                        appNavController.navigate(Routes.BOOKING)
                    },
                    onViewAppointments = { appNavController.navigate(Routes.MY_APPOINTMENTS) },
                    onLogout           = onLogout
                )
            }

            composable(Routes.HOME_BARBER) {
                val userName = SessionManager.getUserName(ctx) ?: "Barbeiro"
                HomeClientScreen(
                    userName           = userName,
                    onNewBooking       = {
                        bookingViewModel.resetBooking()
                        appNavController.navigate(Routes.BOOKING)
                    },
                    onViewAppointments = { appNavController.navigate(Routes.MY_APPOINTMENTS) },
                    onLogout           = onLogout
                )
            }

            composable(Routes.BOOKING) {
                LaunchedEffect(Unit) {
                    bookingViewModel.setToken(SessionManager.bearer(ctx))
                }
                BookingScreen(
                    viewModel     = bookingViewModel,
                    onBookingDone = { appNavController.navigate(Routes.BOOKING_SUCCESS) },
                    onBack        = { appNavController.popBackStack() }
                )
            }

            composable(Routes.BOOKING_SUCCESS) {
                val state by bookingViewModel.state.collectAsState()
                BookingSuccessScreen(
                    serviceName = state.selectedService?.name ?: "",
                    barberName  = state.selectedBarber?.fullName ?: "",
                    date        = state.selectedDate?.toString() ?: "",
                    slot        = state.selectedSlot ?: "",
                    price       = state.selectedService?.price ?: 0.0,
                    onGoHome = {
                        bookingViewModel.resetBooking()
                        appNavController.navigate(Routes.HOME_CLIENT) {
                            popUpTo(Routes.HOME_CLIENT) { inclusive = true }
                        }
                    },
                    onViewAppointments = {
                        bookingViewModel.resetBooking()
                        appNavController.navigate(Routes.MY_APPOINTMENTS) {
                            popUpTo(Routes.HOME_CLIENT) { inclusive = false }
                        }
                    }
                )
            }

            composable(Routes.MY_APPOINTMENTS) {
                MyAppointmentsScreen(
                    token  = SessionManager.bearer(ctx),
                    onBack = { appNavController.popBackStack() }
                )
            }
        }
    }
}