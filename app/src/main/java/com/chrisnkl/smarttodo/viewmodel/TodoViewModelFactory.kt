package com.chrisnkl.smarttodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chrisnkl.smarttodo.data.InsightRepository

class TodoViewModelFactory(
    private val repository: InsightRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TodoViewModel(repository) as T
    }

}