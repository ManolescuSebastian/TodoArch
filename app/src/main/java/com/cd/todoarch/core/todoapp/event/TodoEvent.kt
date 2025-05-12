package com.cd.todoarch.core.todoapp.event

import com.cd.todoarch.core.todoapp.model.Task

sealed class TodoEvent {
    data class TaskRemoved(val task: Task) : TodoEvent()
    data class TaskUpdated(val taskId: Int) : TodoEvent()
}