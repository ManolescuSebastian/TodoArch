package com.cd.todoarch.core.todoapp

import com.cd.todoarch.core.todoapp.state.model.Task

interface TaskListRepository {
    suspend fun loadTasks(): List<Task>
    suspend fun saveTasks(tasks: List<Task>)
}