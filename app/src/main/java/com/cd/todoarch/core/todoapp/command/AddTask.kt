package com.cd.todoarch.core.todoapp.command

import com.cd.todoarch.core.todoapp.TodoCommand
import com.cd.todoarch.core.todoapp.model.Task
import com.cd.todoarch.core.todoapp.state.addTasks

class AddTask(
    private val title: String,
    private val description: String,
) : TodoCommand() {
    override suspend fun buildCommand() {
        stateChange {
            addTasks(listOf(
                Task(
                    id = tasks.size + 1,
                    title = title,
                    description = description
                )
            ))
        }
    }
}