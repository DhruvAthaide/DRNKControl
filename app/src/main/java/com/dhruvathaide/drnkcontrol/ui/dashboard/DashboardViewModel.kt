package com.dhruvathaide.drnkcontrol.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: com.dhruvathaide.drnkcontrol.domain.MessageRepository,
    private val geofenceManager: com.dhruvathaide.drnkcontrol.utils.GeofenceManager
) : ViewModel() {

    private val _isSafetyModeActive = MutableStateFlow(false)
    val isSafetyModeActive: StateFlow<Boolean> = _isSafetyModeActive.asStateFlow()

    private val _navigationEvent = kotlinx.coroutines.flow.MutableSharedFlow<DashboardNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    sealed class DashboardNavigationEvent {
        object StartChallenges : DashboardNavigationEvent()
    }

    init {
        viewModelScope.launch {
            repository.getSettings().collect { settings ->
                _isSafetyModeActive.value = settings?.isSafetyModeActive ?: false
            }
        }
    }

    fun toggleSafetyMode() {
        viewModelScope.launch {
            val isCurrentlyActive = _isSafetyModeActive.value
            if (isCurrentlyActive) {
                // User wants to UNLOCK. Trigger Challenges.
                _navigationEvent.emit(DashboardNavigationEvent.StartChallenges)
            } else {
                // User wants to LOCK.
                repository.updateSafetyMode(true)
                
                // Example: Add a geofence for a common nightlife spot or current location
                // Here we use placeholder coordinates for demonstration.
                geofenceManager.addGeofence(
                    id = "NightlifeArea",
                    lat = 18.5204, // Placeholder: Central Pune area
                    lng = 73.8567,
                    radius = 500f
                )
            }
        }
    }
}
