package com.cd.todoarch.core.todoapp.command

import com.cd.todoarch.core.framework.inject
import com.cd.todoarch.core.todoapp.TaskListRepository
import com.cd.todoarch.core.todoapp.TodoCommand

object SaveTaskList : TodoCommand() {
    val taskListRepository by inject<TaskListRepository>()
    override suspend fun buildCommand() {
        action {
            taskListRepository.saveTasks(tasks)
        }
    }
}