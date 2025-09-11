package com.cd.todoarch.core.todoapp.io

import com.cd.todoarch.core.todoapp.TaskListRepository
import com.cd.todoarch.core.todoapp.model.Task
import kotlinx.coroutines.delay

class FakeTaskListRepository : TaskListRepository {
    private var tasks = listOf(
        Task(1, "Buy groceries", "Milk, eggs, bread"),
        Task(2, "Walk the dog", "Around the park"),
        Task(3, "Ride a bike", "From work to home"),
        Task(4, "Finish project", "Submit by end of the week"),
        Task(5, "Read a book", "Finish the last two chapters")
    )

    override suspend fun loadTasks(): List<Task> {
        delay(5000) // just to add a bit more realism
        return tasks
    }

    override suspend fun saveTasks(tasks: List<Task>) {
        this.tasks = tasks
    }
}