package com.dhruvathaide.drnkcontrol.ui.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhruvathaide.drnkcontrol.domain.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengeViewModel @Inject constructor(
    private val repository: MessageRepository
) : ViewModel() {

    fun completeFinalChallenge(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.updateSafetyMode(false)
            onComplete()
        }
    }
}
