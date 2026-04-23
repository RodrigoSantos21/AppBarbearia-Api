package com.example.frontend_kotlin.session

import android.content.Context

object SessionManager {
    private const val PREFS         = "barbearia_prefs"
    private const val KEY_TOKEN     = "jwt_token"
    private const val KEY_USER_ID   = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_ROLE = "user_role"

    private fun prefs(ctx: Context) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun save(ctx: Context, token: String, userId: String, name: String, role: String) {
        // commit() é síncrono — garante que o dado está salvo antes de continuar
        prefs(ctx).edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_USER_ID, userId)
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_ROLE, role)
            .commit()
    }

    fun getToken(ctx: Context): String?    = prefs(ctx).getString(KEY_TOKEN, null)
    fun getUserId(ctx: Context): String?   = prefs(ctx).getString(KEY_USER_ID, null)
    fun getUserName(ctx: Context): String? = prefs(ctx).getString(KEY_USER_NAME, null)
    fun getRole(ctx: Context): String?     = prefs(ctx).getString(KEY_USER_ROLE, null)
    fun isLoggedIn(ctx: Context)           = getToken(ctx) != null

    // FIX: commit() síncrono garante que o clear() termina ANTES
    // do onSessionChanged() recalcular o startDestination
    fun clear(ctx: Context) {
        prefs(ctx).edit().clear().commit()
    }

    fun bearer(ctx: Context) = "Bearer ${getToken(ctx)}"
}