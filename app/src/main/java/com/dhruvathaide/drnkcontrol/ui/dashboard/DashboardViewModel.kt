package com.dhruvathaide.drnkcontrol.ui.dashboard

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor() : ViewModel() {

    private val _isSafetyModeActive = MutableStateFlow(false)
    val isSafetyModeActive: StateFlow<Boolean> = _isSafetyModeActive.asStateFlow()

    fun toggleSafetyMode() {
        val newState = !_isSafetyModeActive.value
        if (newState) {
            // TODO: Start AccessibilityService if not running, prompt for permissions
        } else {
            // TODO: Trigger Sobriety Gate to unlock
        }
        _isSafetyModeActive.value = newState
    }
}
