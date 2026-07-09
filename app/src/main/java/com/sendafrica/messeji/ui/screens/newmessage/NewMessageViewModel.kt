package com.sendafrica.messeji.ui.screens.newmessage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sendafrica.messeji.domain.ContactInfo
import com.sendafrica.messeji.util.ContactResolver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewMessageUiState(
    val searchQuery: String = "",
    val searchResults: List<ContactInfo> = emptyList(),
    val recipients: List<ContactInfo> = emptyList()
)

@HiltViewModel
class NewMessageViewModel @Inject constructor(
    private val contactResolver: ContactResolver
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewMessageUiState())
    val uiState: StateFlow<NewMessageUiState> = _uiState.asStateFlow()

    fun updateSearch(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        if (query.length >= 1) {
            viewModelScope.launch {
                val results = contactResolver.searchContacts(query)
                _uiState.value = _uiState.value.copy(searchResults = results)
            }
        } else {
            _uiState.value = _uiState.value.copy(searchResults = emptyList())
        }
    }

    fun selectRecipient(contact: ContactInfo) {
        val current = _uiState.value.recipients
        if (contact !in current) {
            _uiState.value = _uiState.value.copy(
                recipients = current + contact,
                searchQuery = "",
                searchResults = emptyList()
            )
        }
    }

    fun removeRecipient(contact: ContactInfo) {
        _uiState.value = _uiState.value.copy(
            recipients = _uiState.value.recipients - contact
        )
    }
}
