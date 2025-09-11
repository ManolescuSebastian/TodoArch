package com.cd.todoarch.core.todoapp.kommand

import com.cd.todoarch.core.framework.inject
import com.cd.todoarch.core.todoapp.TaskListRepository
import com.cd.todoarch.core.todoapp.TodoKommand
import com.cd.todoarch.core.todoapp.state.addTasks
import com.cd.todoarch.core.todoapp.state.setComplete
import com.cd.todoarch.core.todoapp.state.setLoading

object LoadTasks : TodoKommand() {
    private val taskListRepository by inject<TaskListRepository>()

    override suspend fun describe() {
        mutation {
            setLoading()
        }
        val loadedTasks = taskListRepository.loadTasks()
        mutation {
            addTasks(loadedTasks)
        }
        mutation {
            setComplete()
        }
    }
}