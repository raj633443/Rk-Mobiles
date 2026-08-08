package com.rkmobiles.app
import android.content.Context
class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("rk_lock", Context.MODE_PRIVATE)
    fun getPin() = prefs.getString("pin", "1234") ?: "1234"
    fun savePin(pin: String) { prefs.edit().putString("pin", pin).apply() }
}
