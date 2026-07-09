package com.sendafrica.messeji.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sendafrica.messeji.data.AppSettings
import com.sendafrica.messeji.data.repository.MessageRepository
import com.sendafrica.messeji.data.sms.SmsMessage
import com.sendafrica.messeji.util.ContactResolver
import com.sendafrica.messeji.util.TimeFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = true,
    val isSending: Boolean = false,
    val error: String? = null,
    val contactName: String = "",
    val address: String = "",
    val selectedSim: Int = 0
)

data class ChatMessage(
    val id: Long,
    val body: String,
    val date: Long,
    val isSent: Boolean,
    val status: Int,
    val timeFormatted: String
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: MessageRepository,
    private val appSettings: AppSettings,
    private val contactResolver: ContactResolver
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var threadId: Long = 0

    fun loadThread(threadId: Long, address: String, name: String) {
        this.threadId = threadId
        _uiState.value = _uiState.value.copy(
            address = address,
            contactName = name
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                repository.markThreadRead(threadId)
                val messages = repository.getThreadMessages(threadId)
                val prefs = appSettings.preferences
                _uiState.value = _uiState.value.copy(
                    messages = messages.map { it.toChatMessage() },
                    isLoading = false,
                    selectedSim = prefs.value.defaultSimSlot
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Imeshindwa kupakia meseji"
                )
            }
        }
    }

    fun updateInput(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSending = true)
            val success = repository.sendMessage(
                address = _uiState.value.address,
                body = text,
                simSlot = _uiState.value.selectedSim
            )
            if (success) {
                _uiState.value = _uiState.value.copy(
                    inputText = "",
                    isSending = false
                )
                refreshMessages()
            } else {
                _uiState.value = _uiState.value.copy(isSending = false)
            }
        }
    }

    fun setSimSlot(slot: Int) {
        _uiState.value = _uiState.value.copy(selectedSim = slot)
    }

    fun refreshMessages() {
        viewModelScope.launch {
            try {
                val messages = repository.getThreadMessages(threadId)
                _uiState.value = _uiState.value.copy(
                    messages = messages.map { it.toChatMessage() }
                )
            } catch (_: Exception) { }
        }
    }

    fun deleteMessage(messageId: Long) {
        viewModelScope.launch {
            repository.deleteMessage(messageId)
            refreshMessages()
        }
    }

    private fun SmsMessage.toChatMessage(): ChatMessage {
        return ChatMessage(
            id = id,
            body = body ?: "",
            date = date,
            isSent = type == 2,
            status = status,
            timeFormatted = TimeFormatter.formatMessageTime(date)
        )
    }
}
