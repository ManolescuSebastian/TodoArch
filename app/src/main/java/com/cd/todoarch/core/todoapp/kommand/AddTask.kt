package com.cd.todoarch.core.todoapp.kommand

import com.cd.todoarch.core.todoapp.TodoKommand
import com.cd.todoarch.core.todoapp.state.model.Task
import com.cd.todoarch.core.todoapp.state.addTasks

class AddTask(
    private val title: String,
    private val description: String,
) : TodoKommand() {
    override suspend fun describe() {
        mutation {
            addTasks(listOf(
                Task(
                    id = tasks.size + 1,
                    title = title,
                    description = description
                )
            ))
        }
        composeWith(SaveTaskList)
    }
}