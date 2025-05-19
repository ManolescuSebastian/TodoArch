package com.cd.todoarch

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cd.todoarch.intents.TodoIntent
import com.cd.todoarch.model.Task
import com.cd.todoarch.states.TodoState
import com.cd.todoarch.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

@Composable
fun TaskDetailScreen(id: Int?, navController: NavController, viewModel: TaskViewModel) {

    val todoState by viewModel.state.collectAsState()

    val currentTask = (todoState as? TodoState.TodoList)?.todoList
    val task = currentTask?.firstOrNull { it.id == id }
    task?.let {
        TaskDetails(it, viewModel, navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetails(task: Task, viewModel: TaskViewModel,navController: NavController){
    val taskCoroutineScope = rememberCoroutineScope()
    var showEditSheet by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(task.title) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showEditSheet = true
                    }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = {
                        taskCoroutineScope.launch {
                            viewModel.userIntent.send(TodoIntent.DeleteTodo(task.id))
                            navController.popBackStack()
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            Text(text = task.title, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            task.description?.let { Text(text = it, style = MaterialTheme.typography.bodyLarge) }
        }
    }

    if (showEditSheet) {
        ModalBottomSheet(onDismissRequest = { showEditSheet = false }) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth()
            ) {
                Text("Edit Task", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        taskCoroutineScope.launch {
                            viewModel.userIntent.send(
                                TodoIntent.UpdateTodo(task.copy(title = title, description = description))
                            )
                        }
                        showEditSheet = false
                    }
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Save")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Changes")
                }
            }
        }
    }
}