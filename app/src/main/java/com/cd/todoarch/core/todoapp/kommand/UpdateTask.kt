package com.cd.todoarch.core.todoapp.kommand

import com.cd.todoarch.core.todoapp.TodoKommand
import com.cd.todoarch.core.todoapp.event.TodoEvent.TaskUpdated
import com.cd.todoarch.core.todoapp.state.updateTask

class UpdateTask(
    private val taskId: Int,
    private val title: String,
    private val description: String,
) : TodoKommand() {
    override suspend fun describe() {
        mutation {
            updateTask(
                taskId = taskId,
                title = title,
                description = description
            )
        }
        event(TaskUpdated(taskId))
        composeWith(SaveTaskList)
    }
}