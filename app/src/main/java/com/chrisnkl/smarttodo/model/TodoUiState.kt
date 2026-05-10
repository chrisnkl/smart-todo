package com.chrisnkl.smarttodo.model

data class TodoUiState(
    val currentTaskText: String = "",
    val tasks: List<TaskItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
}