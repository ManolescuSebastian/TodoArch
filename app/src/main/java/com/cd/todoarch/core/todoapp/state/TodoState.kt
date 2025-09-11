package com.cd.todoarch.core.todoapp.state

import com.cd.todoarch.core.todoapp.model.Task

data class TodoState(
    val loadingTasks: Boolean = false,
    val tasks: List<Task> = emptyList()
)
