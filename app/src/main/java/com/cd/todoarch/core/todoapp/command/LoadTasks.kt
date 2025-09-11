package com.cd.todoarch.core.todoapp.command

import com.cd.todoarch.core.framework.inject
import com.cd.todoarch.core.todoapp.TaskListRepository
import com.cd.todoarch.core.todoapp.TodoCommand
import com.cd.todoarch.core.todoapp.model.Task
import com.cd.todoarch.core.todoapp.state.addTasks
import com.cd.todoarch.core.todoapp.state.setComplete
import com.cd.todoarch.core.todoapp.state.setLoading
import kotlinx.coroutines.delay

object LoadTasks : TodoCommand() {
    private val taskListRepository by inject<TaskListRepository>()

    override suspend fun buildCommand() {
        stateChange {
            setLoading()
        }
        val loadedTasks = taskListRepository.loadTasks()
        stateChange {
            addTasks(loadedTasks)
        }
        stateChange {
            setComplete()
        }
    }
}