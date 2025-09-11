package com.cd.todoarch.core.todoapp.kommand

import com.cd.todoarch.core.framework.inject
import com.cd.todoarch.core.todoapp.TaskListRepository
import com.cd.todoarch.core.todoapp.TodoKommand

object SaveTaskList : TodoKommand() {
    val taskListRepository by inject<TaskListRepository>()
    override suspend fun describe() {
        action {
            taskListRepository.saveTasks(tasks)
        }
    }
}