package com.cd.todoarch.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.cd.todoarch.data.Task

class TaskViewModel : ViewModel() {
    private val _tasks = mutableStateListOf(
        Task(1, "Buy groceries", "Milk, eggs, bread"),
        Task(2, "Walk the dog", "Around the park"),
        Task(3, "Ride a bike", "From work to home"),
        Task(4, "Finish project", "Submit by end of the week"),
        Task(5, "Read a book", "Finish the last two chapters")
    )
    val tasks: List<Task> get() = _tasks

    fun addTask(title: String, description: String) {
        _tasks.add(Task(_tasks.size + 1, title, description))
    }

    fun removeTask(task: Task) {
        _tasks.remove(task)
    }
}