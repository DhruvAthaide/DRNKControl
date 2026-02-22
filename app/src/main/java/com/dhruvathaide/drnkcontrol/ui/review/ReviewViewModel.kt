package com.dhruvathaide.drnkcontrol.ui.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhruvathaide.drnkcontrol.data.GhostMessageEntity
import com.dhruvathaide.drnkcontrol.domain.SafetyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val repository: SafetyRepository
) : ViewModel() {

    val pendingMessages: Flow<List<GhostMessageEntity>> = repository.getPendingMessages()

    fun sendMessage(message: GhostMessageEntity) {
        viewModelScope.launch {
            // In a real app, logic to actually dispatch the intent to send via original app goes here
            // For now, we mark as sent in DB
            repository.updateMessageStatus(message.id, "SENT")
        }
    }

    fun deleteMessage(id: Long) {
        viewModelScope.launch {
            repository.updateMessageStatus(id, "BURNED")
        }
    }

    fun emergencyWipe() {
        viewModelScope.launch {
            // Real app would trigger auto-reply logic here before clearing
            repository.clearQueue()
        }
    }
}
