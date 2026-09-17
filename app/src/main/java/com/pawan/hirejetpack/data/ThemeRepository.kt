package com.pawan.hirejetpack.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * [ThemeRepository] — single source of truth for the app's theme preference.
 *
 * Staff note: In a production app, this would be backed by DataStore
 * or SharedPreferences to persist the setting across app restarts.
 * For this demo, it's an in-memory singleton.
 */
object ThemeRepository {
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }
}