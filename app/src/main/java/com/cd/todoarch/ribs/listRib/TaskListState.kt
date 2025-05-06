package com.cd.todoarch.ribs.listRib

import com.cd.todoarch.data.Task

data class TaskListState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)