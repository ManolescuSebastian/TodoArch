package com.cd.todoarch.core.todoapp.command

import com.cd.todoarch.core.todoapp.TodoCommand
import com.cd.todoarch.core.todoapp.event.TodoEvent.TaskRemoved
import com.cd.todoarch.core.todoapp.model.Task
import com.cd.todoarch.core.todoapp.state.removeTask

class RemoveTask(
    private val task: Task
) : TodoCommand() {
    override suspend fun buildCommand() {
        stateChange {
            removeTask(task)
        }
        event(TaskRemoved(task))
    }
}