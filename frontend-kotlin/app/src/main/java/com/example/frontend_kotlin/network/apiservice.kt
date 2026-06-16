package com.example.frontend_kotlin.network

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

// =============================================================================
// DTOs — espelham exatamente os tipos do C#
// =============================================================================

// ── Auth ──────────────────────────────────────────────────────────────────────

/** POST /api/auth/register  →  RegisterClientCommand */
data class RegisterClientRequest(
    val fullName: String,
    val email: String,
    val phone: String,
    val password: String,
    val confirmPassword: String
)

/** POST /api/auth/login  →  LoginCommand */
data class LoginRequest(
    val email: String,
    val password: String
)

/** Resposta do login (result.Value do LoginCommandHandler) */
data class AuthResponse(
    val accessToken: String,   // era "token", C# retorna "accessToken"
    val tokenType: String,
    val expiresIn: Int,
    val user: AuthUserDto
)

/** GET /api/auth/me */
data class AuthUserDto(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val role: String,
    val isActive: Boolean,
    val createdAt: String
)

/** GET /api/auth/barbers */
data class BarberDto(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String
)

/** PUT /api/auth/profile */
data class UpdateProfileRequest(
    val fullName: String,
    val phone: String
)

/** PUT /api/auth/password */
data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
    val confirmNewPassword: String
)

// ── Services ──────────────────────────────────────────────────────────────────

/** GET /api/services  →  ServiceDto do C# */
data class ServiceDto(
    val id: String,
    val name: String,
    val description: String?,
    val price: Double,
    val durationMinutes: Int,
    val isActive: Boolean
)

// ── Appointments ──────────────────────────────────────────────────────────────

/**
 * POST /api/appointments
 * Espelha: CreateAppointmentRequest(Guid BarberId, Guid ServiceId, DateTime ScheduledAt)
 * ScheduledAt: ISO 8601 — ex: "2026-04-20T14:30:00"
 */
data class CreateAppointmentRequest(
    val barberId: String,
    val serviceId: String,
    val scheduledAt: String
)

/** GET /api/appointments/my  e  GET /api/appointments/{id} */
data class AppointmentDto(
    val id: String,
    val clientId: String,
    val clientName: String,
    val barberId: String,
    val barberName: String,
    val serviceId: String,
    val serviceName: String,
    val servicePrice: Double,
    val serviceDurationMinutes: Int,
    val scheduledAt: String,
    val scheduledEnd: String,
    val status: String,
    val cancellationReason: String?,
    val createdAt: String,
    val updatedAt: String?
)


/** POST /api/appointments/{id}/cancel */
data class CancelRequest(val reason: String)

/** POST /api/appointments/{id}/reschedule */
data class RescheduleRequest(val newScheduledAt: String)

// =============================================================================
// Interface da API — mapeamento 1:1 com os Controllers C#
// =============================================================================

interface BarbeariaApi {

    // ── Auth ──────────────────────────────────────────────────────────────────

    /** POST /api/auth/register  [AllowAnonymous] */
    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterClientRequest
    ): Response<Unit>   // retorna 201 com { id } no body — ignoramos, só verificamos isSuccessful

    /** POST /api/auth/login  [AllowAnonymous] */
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    /** GET /api/auth/me  [Authorize] */
    @GET("auth/me")
    suspend fun getMe(
        @Header("Authorization") token: String
    ): Response<AuthUserDto>

    /** PUT /api/auth/profile  [Authorize] */
    @PUT("auth/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<Unit>

    /** PUT /api/auth/password  [Authorize] */
    @PUT("auth/password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Response<Unit>

    /** GET /api/auth/barbers  [Authorize] */
    @GET("auth/barbers")
    suspend fun getBarbers(
        @Header("Authorization") token: String
    ): Response<List<BarberDto>>

    // ── Services ──────────────────────────────────────────────────────────────

    /** GET /api/services?onlyActive=true  [Authorize] */
    @GET("services")
    suspend fun getServices(
        @Header("Authorization") token: String,
        @Query("onlyActive") onlyActive: Boolean = true
    ): Response<List<ServiceDto>>

    /** GET /api/services/{id}  [Authorize] */
    @GET("services/{id}")
    suspend fun getServiceById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<ServiceDto>

    // ── Appointments ──────────────────────────────────────────────────────────

    /**
     * POST /api/appointments  [Authorize]
     * O userId é extraído do JWT pelo C# — não precisa enviar no body
     */
    @POST("appointments")
    suspend fun createAppointment(
        @Header("Authorization") token: String,
        @Body request: CreateAppointmentRequest
    ): Response<Unit>   // 201 com { id }

    /** GET /api/appointments/my  [Authorize] */
    @GET("appointments/my")
    suspend fun getMyAppointments(
        @Header("Authorization") token: String
    ): Response<List<AppointmentDto>>

    /** GET /api/appointments/{id}  [Authorize] */
    @GET("appointments/{id}")
    suspend fun getAppointmentById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<AppointmentDto>

    /** POST /api/appointments/{id}/cancel  [Authorize] */
    @POST("appointments/{id}/cancel")
    suspend fun cancelAppointment(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: CancelRequest
    ): Response<Unit>

    /** POST /api/appointments/{id}/reschedule  [Authorize] */
    @POST("appointments/{id}/reschedule")
    suspend fun rescheduleAppointment(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: RescheduleRequest
    ): Response<Unit>

    // ── Barber/Admin only ─────────────────────────────────────────────────────

    /** GET /api/appointments/agenda?date=yyyy-MM-dd  [Authorize(Roles="Barber,Admin")] */
    @GET("appointments/agenda")
    suspend fun getMyAgenda(
        @Header("Authorization") token: String,
        @Query("date") date: String? = null   // "yyyy-MM-ddTHH:mm:ss" ou null = hoje
    ): Response<List<AppointmentDto>>

    /** POST /api/appointments/{id}/confirm  [Authorize(Roles="Barber,Admin")] */
    @POST("appointments/{id}/confirm")
    suspend fun confirmAppointment(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>

    /** POST /api/appointments/{id}/finish  [Authorize(Roles="Barber,Admin")] */
    @POST("appointments/{id}/finish")
    suspend fun finishAppointment(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>
}

object RetrofitClient {
    private const val BASE_URL = "http://192.168.0.39:5000/api/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY   // remova em produção
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: BarbeariaApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BarbeariaApi::class.java)
    }
}