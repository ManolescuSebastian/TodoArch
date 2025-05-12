package com.cd.todoarch.core.todoapp.state

import com.cd.todoarch.core.todoapp.model.Task

data class TodoState(
    val tasks: List<Task> = emptyList()
)
