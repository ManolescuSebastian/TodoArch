package com.cd.todoarch.core.todoapp.command

import com.cd.todoarch.core.todoapp.TodoCommand
import com.cd.todoarch.core.todoapp.model.Task
import com.cd.todoarch.core.todoapp.state.addTasks

object LoadTasks : TodoCommand() {
    override suspend fun buildCommand() {
        stateChange {
            addTasks(
                listOf(
                    Task(1, "Buy groceries", "Milk, eggs, bread"),
                    Task(2, "Walk the dog", "Around the park"),
                    Task(3, "Ride a bike", "From work to home"),
                    Task(4, "Finish project", "Submit by end of the week"),
                    Task(5, "Read a book", "Finish the last two chapters")
                )
            )
        }
    }
}