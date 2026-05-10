package com.chrisnkl.smarttodo.model

data class TaskItem(
    val text: String,
    val actions: List<TaskAction> = emptyList()
)
