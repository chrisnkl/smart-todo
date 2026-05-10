package com.chrisnkl.smarttodo.ui.theme

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.chrisnkl.smarttodo.model.TaskActionType
import com.chrisnkl.smarttodo.model.TaskItem
import com.chrisnkl.smarttodo.viewmodel.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(factory: TodoViewModelFactory) {
    val viewModel: TodoViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(

        topBar = {
            TopAppBar(
                title = { Text("Smart To-Do Links") }
            )
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)
        ) {

            OutlinedTextField(
                value = uiState.currentTaskText,
                onValueChange = viewModel::onTaskTextChanged,
                label = { Text("Enter task") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = { viewModel.addTask() }) {
                Text("Add Task")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Text("Analyzing task...")
                Spacer(modifier = Modifier.height(12.dp))
            }

            uiState.tasks.forEach { task ->
                TaskText(task = task)
                Spacer(modifier = Modifier.height(12.dp))
            }

            uiState.errorMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Error: $it")
            }

        }
    }

}


@Composable
private fun TaskText(task: TaskItem) {

    val context = LocalContext.current

    Text (
        text = buildAnnotatedString {
            append("• ")

            if (task.actions.isEmpty()) {
                append(task.text)
                return@buildAnnotatedString
            }

            var currentIndex = 0

            task.actions.sortedBy { it.start }.forEach { action ->

                if (action.start > currentIndex) {
                    append(task.text.substring(currentIndex, action.start))
                }

                val clickedValue = cleanActionValue(action.type, action.value)

                withLink(
                    LinkAnnotation.Clickable(tag = "${action.type.name}|$clickedValue",
                        linkInteractionListener = {
                            openTaskAction(
                                context = context,
                                type = action.type,
                                value = clickedValue
                            )
                        })
                ) {
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF1565C0),
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append(task.text.substring(action.start, action.end))
                    }
                }

                currentIndex = action.end

            }

            if (currentIndex < task.text.length) {
                append(task.text.substring(currentIndex))
            }

        }
    )

}

private fun openTaskAction(
    context: Context,
    type: TaskActionType,
    value: String
) {


    val intent = when (type) {

        TaskActionType.PHONE -> {
            Intent(Intent.ACTION_DIAL, "tel:${value}".toUri())
        }

        TaskActionType.EMAIL -> {
            Intent(Intent.ACTION_SENDTO, "mailto:${value}".toUri())
        }

        TaskActionType.URL -> {
            val normalized = if (
                value.startsWith("http://") || value.startsWith("https://")
            ) value else "http://${value}"
            Intent(Intent.ACTION_VIEW, normalized.toUri())
        }

        TaskActionType.ADDRESS -> {
            Intent(Intent.ACTION_VIEW, "geo:0,0?q=${Uri.encode(value)}".toUri())
        }

    }

    if (intent.resolveActivity(context.packageManager) != null){
        context.startActivity(intent)
    } else {
        Toast.makeText(
            context,
            "No app found for this action",
            Toast.LENGTH_SHORT
        ).show()
    }


}

private fun cleanActionValue(type: TaskActionType, value: String): String {
    return when (type) {

        TaskActionType.PHONE -> value.filter {it.isDigit() || it == '+'}
        TaskActionType.EMAIL -> value.trim().trim(',', '.', ';', ':', ')', '(')
        TaskActionType.URL -> value.trim().trim(',', '.', ';', ':', ')', '(')
        TaskActionType.ADDRESS -> value.trim()

    }
}
