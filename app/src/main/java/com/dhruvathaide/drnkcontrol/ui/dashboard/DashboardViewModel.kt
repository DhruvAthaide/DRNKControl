package com.dhruvathaide.drnkcontrol.ui.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhruvathaide.drnkcontrol.accessibility.GhostService
import com.dhruvathaide.drnkcontrol.domain.SafetyRepository
import com.dhruvathaide.drnkcontrol.utils.SafetyPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: SafetyRepository,
    private val geofenceManager: com.dhruvathaide.drnkcontrol.utils.GeofenceManager
) : ViewModel() {

    private val _isSafetyModeActive = MutableStateFlow(false)
    val isSafetyModeActive: StateFlow<Boolean> = _isSafetyModeActive.asStateFlow()

    private val _isServiceEnabled = MutableStateFlow(false)
    val isServiceEnabled: StateFlow<Boolean> = _isServiceEnabled.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<DashboardNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    sealed class DashboardNavigationEvent {
        object StartChallenges : DashboardNavigationEvent()
    }

    init {
        // Observe DB for safety mode state (drives UI) and also keep SharedPrefs in sync
        viewModelScope.launch {
            repository.getSettings().collect { settings ->
                val active = settings?.isSafetyModeActive ?: false
                _isSafetyModeActive.value = active
                // Keep SharedPrefs in sync for GhostService to read synchronously
                SafetyPrefs.setSafetyMode(context, active)
            }
        }
        checkServiceStatus()
    }

    fun checkServiceStatus() {
        _isServiceEnabled.value = GhostService.isEnabled(context)
    }

    fun toggleSafetyMode() {
        viewModelScope.launch {
            val isCurrentlyActive = _isSafetyModeActive.value
            if (isCurrentlyActive) {
                // User wants to UNLOCK — must pass challenges first
                _navigationEvent.emit(DashboardNavigationEvent.StartChallenges)
            } else {
                // User wants to LOCK — persist to both DB and SharedPrefs
                repository.updateSafetyMode(true)
                geofenceManager.addGeofence(
                    id = "NightlifeArea",
                    lat = 18.5204,
                    lng = 73.8567,
                    radius = 500f
                )
            }
        }
    }
}
