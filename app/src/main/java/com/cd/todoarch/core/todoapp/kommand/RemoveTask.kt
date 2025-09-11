package com.cd.todoarch.core.todoapp.kommand

import com.cd.todoarch.core.todoapp.TodoKommand
import com.cd.todoarch.core.todoapp.event.TodoEvent.TaskRemoved
import com.cd.todoarch.core.todoapp.state.model.Task
import com.cd.todoarch.core.todoapp.state.removeTask

class RemoveTask(
    private val task: Task
) : TodoKommand() {
    override suspend fun describe() {
        mutation {
            removeTask(task)
        }
        event(TaskRemoved(task))
        composeWith(SaveTaskList)
    }
}