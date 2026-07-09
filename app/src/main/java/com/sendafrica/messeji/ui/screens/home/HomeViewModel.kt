package com.sendafrica.messeji.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sendafrica.messeji.data.repository.MessageRepository
import com.sendafrica.messeji.domain.ThreadInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val threads: List<ThreadInfo> = emptyList(),
    val selectedFilter: String = "zote",
    val isLoading: Boolean = true,
    val error: String? = null,
    val selectedThreadIds: Set<Long> = emptySet(),
    val isMultiSelectMode: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MessageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadThreads()
    }

    fun loadThreads() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val allThreads = repository.loadAllThreads()
                _uiState.value = _uiState.value.copy(
                    threads = allThreads,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Imeshindwa kupakia mazungumzo: ${e.message}"
                )
            }
        }
    }

    fun setFilter(filter: String) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
    }

    fun getFilteredThreads(): List<ThreadInfo> {
        val state = _uiState.value
        return when (state.selectedFilter) {
            "mtu_kwa_mtu" -> state.threads.filter { it.category == "person" }
            "pesa_na_otp" -> state.threads.filter { it.category == "money_otp" }
            "matangazo" -> state.threads.filter { it.category == "promo" }
            else -> state.threads
        }
    }

    fun enterMultiSelectMode() {
        _uiState.value = _uiState.value.copy(
            isMultiSelectMode = true,
            selectedThreadIds = emptySet()
        )
    }

    fun exitMultiSelectMode() {
        _uiState.value = _uiState.value.copy(
            isMultiSelectMode = false,
            selectedThreadIds = emptySet()
        )
    }

    fun toggleThreadSelection(threadId: Long) {
        val current = _uiState.value.selectedThreadIds
        _uiState.value = _uiState.value.copy(
            selectedThreadIds = if (threadId in current) {
                current - threadId
            } else {
                current + threadId
            }
        )
    }

    fun deleteSelectedThreads() {
        viewModelScope.launch {
            for (id in _uiState.value.selectedThreadIds) {
                repository.deleteThread(id)
            }
            exitMultiSelectMode()
            loadThreads()
        }
    }

    fun togglePinThread(threadId: Long, pinned: Boolean) {
        viewModelScope.launch {
            repository.togglePinThread(threadId, pinned)
            loadThreads()
        }
    }

    fun toggleMuteThread(threadId: Long, muted: Boolean) {
        viewModelScope.launch {
            repository.toggleMuteThread(threadId, muted)
            loadThreads()
        }
    }

    fun archiveThread(threadId: Long) {
        viewModelScope.launch {
            repository.archiveThread(threadId)
            loadThreads()
        }
    }

    fun blockNumber(number: String) {
        viewModelScope.launch {
            repository.blockNumber(number)
            loadThreads()
        }
    }
}
