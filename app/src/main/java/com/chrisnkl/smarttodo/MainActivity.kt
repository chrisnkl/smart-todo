package com.chrisnkl.smarttodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.chrisnkl.smarttodo.data.InsightRepository
import com.chrisnkl.smarttodo.ui.theme.SmartTodoTheme
import com.chrisnkl.smarttodo.ui.TodoScreen
import com.chrisnkl.smarttodo.viewmodel.TodoViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = InsightRepository()
        val factory = TodoViewModelFactory(repository)

        enableEdgeToEdge()
        setContent {
            SmartTodoTheme {
                TodoScreen(factory = factory)
            }
        }
    }
}