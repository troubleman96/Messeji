package com.sendafrica.messeji.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sendafrica.messeji.data.AppSettings
import com.sendafrica.messeji.data.categorization.CategoryEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appSettings: AppSettings,
    private val categoryEngine: CategoryEngine
) : ViewModel() {

    private val _isLocked = MutableStateFlow(true)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    init {
        viewModelScope.launch {
            categoryEngine.loadBundledRules()
        }
    }

    fun unlock() {
        _isLocked.value = false
    }

    fun lock() {
        _isLocked.value = true
    }
}
