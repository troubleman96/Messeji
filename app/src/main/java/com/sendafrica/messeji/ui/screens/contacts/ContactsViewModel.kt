package com.sendafrica.messeji.ui.screens.contacts

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

data class ContactsUiState(
    val contacts: List<ContactInfo> = emptyList(),
    val searchQuery: String = ""
)

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val contactResolver: ContactResolver
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactsUiState())
    val uiState: StateFlow<ContactsUiState> = _uiState.asStateFlow()

    init {
        loadContacts()
    }

    private fun loadContacts() {
        viewModelScope.launch {
            val contacts = contactResolver.getAllContacts()
            _uiState.value = _uiState.value.copy(contacts = contacts)
        }
    }

    fun updateSearch(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        if (query.isBlank()) {
            loadContacts()
        } else {
            viewModelScope.launch {
                val results = contactResolver.searchContacts(query)
                _uiState.value = _uiState.value.copy(contacts = results)
            }
        }
    }
}
