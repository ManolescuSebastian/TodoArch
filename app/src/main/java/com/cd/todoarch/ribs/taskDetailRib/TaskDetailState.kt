package com.cd.todoarch.ribs.taskDetailRib

import com.cd.todoarch.data.Task

data class TaskDetailState(
    val task: Task? = null, // The item to display
    val isLoading: Boolean = false,
    val error: String? = null
)
