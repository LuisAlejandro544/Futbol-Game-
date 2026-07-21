package com.example.engine

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object GameSettings {
    private val _isAbbreviationEnabled = MutableStateFlow(false)
    val isAbbreviationEnabled: StateFlow<Boolean> = _isAbbreviationEnabled

    private val _currencySymbol = MutableStateFlow("$")
    val currencySymbol: StateFlow<String> = _currencySymbol

    fun initialize(context: Context) {
        val prefs = context.applicationContext.getSharedPreferences("game_settings_prefs", Context.MODE_PRIVATE)
        _isAbbreviationEnabled.value = prefs.getBoolean("abbreviate_money", false)
        _currencySymbol.value = prefs.getString("currency_symbol", "$") ?: "$"
    }

    fun setAbbreviationEnabled(context: Context, enabled: Boolean) {
        _isAbbreviationEnabled.value = enabled
        val prefs = context.applicationContext.getSharedPreferences("game_settings_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("abbreviate_money", enabled).apply()
    }

    fun setCurrencySymbol(context: Context, symbol: String) {
        _currencySymbol.value = symbol
        val prefs = context.applicationContext.getSharedPreferences("game_settings_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("currency_symbol", symbol).apply()
    }

    fun formatMoney(value: Long): String {
        val symbol = _currencySymbol.value
        val abbreviate = _isAbbreviationEnabled.value
        
        return if (abbreviate) {
            when {
                value >= 1_000_000_000L -> {
                    val b = value.toDouble() / 1_000_000_000.0
                    String.format("%s%.1fB", symbol, b)
                }
                value >= 1_000_000L -> {
                    val m = value.toDouble() / 1_000_000.0
                    String.format("%s%.1fM", symbol, m)
                }
                value >= 1_000L -> {
                    val k = value.toDouble() / 1_000.0
                    String.format("%s%.1fK", symbol, k)
                }
                else -> {
                    "$symbol$value"
                }
            }
        } else {
            "$symbol" + String.format("%,d", value)
        }
    }
}
