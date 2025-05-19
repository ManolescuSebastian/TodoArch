package com.cd.todoarch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cd.todoarch.intents.TodoIntent
import com.cd.todoarch.model.Task
import com.cd.todoarch.states.TodoState
import com.cd.todoarch.ui.theme.TodoArchTheme
import com.cd.todoarch.viewmodel.TaskViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoArchTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    val viewModel: TaskViewModel = viewModel()

                    NavHost(navController, startDestination = "todoList") {
                        composable("todoList") { TodoApp(navController, viewModel, innerPadding) }
                        composable(
                            "taskDetail/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val taskId = backStackEntry.arguments?.getInt("id")
                            TaskDetailScreen(taskId, navController, viewModel)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp(navController: NavController, viewModel: TaskViewModel, innerPadding: PaddingValues) {

    val sheetState = rememberModalBottomSheetState()
    val taskCoroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.userIntent.send(TodoIntent.LoadTodos)
    }

    Scaffold(
        modifier = Modifier.padding(innerPadding),
        topBar = {
            TopAppBar(title = { Text("To-Do List") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showBottomSheet = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { paddingValues ->
        TaskList(
            taskCoroutineScope,
            navController,
            viewModel,
            modifier = Modifier.padding(paddingValues)
        )

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                AddTaskBottomSheet(
                    onSave = { title, description ->
                        taskCoroutineScope.launch {
                            viewModel.userIntent.send(TodoIntent.AddTodo(title, description))
                            sheetState.hide()
                        }
                        showBottomSheet = false
                    }
                )
            }
        }
    }
}

@Composable
fun TaskList(
    taskCoroutineScope: CoroutineScope,
    navController: NavController,
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val todoState by viewModel.state.collectAsState()

    when (val currentState = todoState) {
        is TodoState.Loading -> {
            CircularProgressIndicator()
        }

        is TodoState.TodoList -> {
            LazyColumn(modifier = modifier.padding(16.dp)) {
                items(currentState.todoList) { task ->
                    TaskItem(taskCoroutineScope, task, navController, viewModel)
                }
            }
        }

        is TodoState.Error -> {
            Text("Error: ${currentState.message}")
        }

        else -> {}
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(
    taskCoroutineScope: CoroutineScope,
    task: Task,
    navController: NavController,
    viewModel: TaskViewModel
) {
    var showOptions by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                if (!showOptions) {
                    navController.navigate("taskDetail/${task.id}")
                }
            }
            .combinedClickable(
                onClick = {
                    if (!showOptions) {
                        navController.navigate("taskDetail/${task.id}")
                    }
                },
                onLongClick = { showOptions = true }
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = task.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            task.description?.let { Text(text = it, style = MaterialTheme.typography.bodyMedium) }
        }
    }

    if (showOptions) {
        ModalBottomSheet(onDismissRequest = { showOptions = false }) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth()
            ) {
                Text("Options", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Button(modifier = Modifier.fillMaxWidth(), onClick = {
                    taskCoroutineScope.launch {
                        viewModel.userIntent.send(TodoIntent.DeleteTodo(task.id))
                    }
                    showOptions = false
                }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete Task")
                }
            }
        }
    }
}

