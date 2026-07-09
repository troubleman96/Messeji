package com.sendafrica.messeji.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sendafrica.messeji.data.repository.MessageRepository
import com.sendafrica.messeji.data.sms.SmsMessage
import com.sendafrica.messeji.util.ContactResolver
import com.sendafrica.messeji.util.TimeFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val messageResults: List<SearchResultMessage> = emptyList(),
    val contactResults: List<com.sendafrica.messeji.domain.ContactInfo> = emptyList(),
    val isSearching: Boolean = false
)

data class SearchResultMessage(
    val messageId: Long,
    val threadId: Long,
    val address: String,
    val contactName: String,
    val body: String,
    val date: Long,
    val timeFormatted: String
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MessageRepository,
    private val contactResolver: ContactResolver
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun updateQuery(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        searchJob?.cancel()

        if (query.length < 2) {
            _uiState.value = _uiState.value.copy(
                messageResults = emptyList(),
                contactResults = emptyList(),
                isSearching = false
            )
            return
        }

        searchJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true)
            delay(300)

            // Search contacts
            val contacts = contactResolver.searchContacts(query)
            _uiState.value = _uiState.value.copy(contactResults = contacts)

            // Search messages
            repository.searchMessages(query).collect { messages ->
                _uiState.value = _uiState.value.copy(
                    messageResults = messages.map {
                        val contact = contactResolver.resolveContact(it.address)
                        SearchResultMessage(
                            messageId = it.id,
                            threadId = it.threadId,
                            address = it.address,
                            contactName = contact?.name ?: it.address,
                            body = it.body ?: "",
                            date = it.date,
                            timeFormatted = TimeFormatter.formatRelative(it.date)
                        )
                    },
                    isSearching = false
                )
            }
        }
    }
}
