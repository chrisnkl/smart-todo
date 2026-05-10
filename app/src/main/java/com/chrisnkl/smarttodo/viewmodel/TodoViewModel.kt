package com.chrisnkl.smarttodo.viewmodel

import androidx.compose.runtime.currentComposer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrisnkl.smarttodo.data.InsightRepository
import com.chrisnkl.smarttodo.model.TaskItem
import com.chrisnkl.smarttodo.model.TodoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TodoViewModel (
    private val repository: InsightRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodoUiState())
    val uiState: StateFlow<TodoUiState> = _uiState

    fun onTaskTextChanged(newText: String) {
        _uiState.value = _uiState.value.copy(currentTaskText = newText)
    }

    fun addTask() {

        val text = _uiState.value.currentTaskText.trim()
        if (text.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val actions = repository.analyzeTask(text)

                val updatedTasks = _uiState.value.tasks + TaskItem(
                    text = text,
                    actions = actions
                )

                _uiState.value = _uiState.value.copy(
                    currentTaskText = "",
                    tasks = updatedTasks,
                    isLoading = false
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Unknown error"
                )
            }

        }


    }

    override fun onCleared() {
        super.onCleared()
        repository.close()
    }

}