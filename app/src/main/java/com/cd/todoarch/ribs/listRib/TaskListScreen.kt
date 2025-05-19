package com.cd.todoarch.ribs.listRib

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cd.todoarch.AddTaskBottomSheet
import com.cd.todoarch.data.Task
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    taskListState: TaskListState,
    onItemClicked: (String) -> Unit,
    onTaskDeleted: (String) -> Unit,
    onTaskAdded: (String, String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.padding(PaddingValues(16.dp)),
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
            tasks = taskListState.tasks,
            modifier = Modifier.padding(paddingValues),
            onItemClicked,
            onTaskDeleted
        )

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                AddTaskBottomSheet(
                    onSave = { title, description ->
                        coroutineScope.launch { sheetState.hide() }
                        showBottomSheet = false
                        onTaskAdded(title, description)
                    }
                )
            }
        }
    }
}

@Composable
fun TaskList(
    tasks: List<Task>, modifier: Modifier = Modifier,
    onItemClicked: (String) -> Unit,
    onTaskDeleted: (String) -> Unit,
) {
    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(tasks) { task ->
            TaskItem(
                task,
                onItemClicked = { onItemClicked(task.id) },
                onTaskDeleted
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(
    task: Task,
    onItemClicked: () -> Unit,
    onTaskDeleted: (String) -> Unit,
) {
    var showOptions by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onItemClicked() }
            .combinedClickable(
                onClick = onItemClicked,
                onLongClick = { showOptions = true }
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = task.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = task.description, style = MaterialTheme.typography.bodyMedium)
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
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onTaskDeleted(task.id)
                    }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete Task")
                }
            }
        }
    }
}