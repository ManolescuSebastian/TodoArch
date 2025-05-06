package com.cd.todoarch.data

import androidx.compose.runtime.mutableStateListOf

class TaskRepository {
    private val _tasks = mutableStateListOf(
        Task(title = "Buy groceries", description = "Milk, eggs, bread"),
        Task(title = "Walk the dog", description = "Around the park"),
        Task(title = "Ride a bike", description = "From work to home"),
        Task(title = "Finish project", description = "Submit by end of the week"),
        Task(title = "Read a book", description = "Finish the last two chapters")
    )
    val tasks: List<Task> get() = _tasks

    fun addTask(title: String, description: String) {
        _tasks.add(Task(_tasks.size + 1, title, description))
    }

    fun removeTask(task: Task) {
        _tasks.remove(task)
    }

}