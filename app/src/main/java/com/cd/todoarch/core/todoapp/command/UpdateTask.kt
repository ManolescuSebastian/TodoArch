package com.cd.todoarch.core.todoapp.command

import com.cd.todoarch.core.todoapp.TodoCommand
import com.cd.todoarch.core.todoapp.event.TodoEvent
import com.cd.todoarch.core.todoapp.event.TodoEvent.TaskUpdated
import com.cd.todoarch.core.todoapp.model.Task
import com.cd.todoarch.core.todoapp.state.addTasks
import com.cd.todoarch.core.todoapp.state.updateTask

class UpdateTask(
    private val taskId: Int,
    private val title: String,
    private val description: String,
) : TodoCommand() {
    override suspend fun buildCommand() {
        stateChange {
            updateTask(
                taskId = taskId,
                title = title,
                description = description
            )
        }
        event(TaskUpdated(taskId))
    }
}