package com.cd.todoarch.util

import com.cd.todoarch.core.framework.single
import com.cd.todoarch.core.todoapp.TaskListRepository
import com.cd.todoarch.core.todoapp.state.model.Task

fun setupTaskList(tasks: List<Task>) {
    single<TaskListRepository> {
        object : TaskListRepository {
            override suspend fun loadTasks(): List<Task> {
                return tasks
            }

            override suspend fun saveTasks(tasks: List<Task>) {
                TODO("Not yet implemented")
            }
        }
    }
}