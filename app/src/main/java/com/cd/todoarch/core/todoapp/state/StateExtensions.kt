package com.cd.todoarch.core.todoapp.state

import com.cd.todoarch.core.todoapp.state.model.Task

fun TodoState.setLoading() = copy(loadingTasks = true)
fun TodoState.setComplete() = copy(loadingTasks = false)
fun TodoState.addTasks(tasks: List<Task>) = copy(tasks = this.tasks + tasks)

fun TodoState.removeTask(task: Task) = copy(tasks = tasks.filterNot { it == task })

fun TodoState.updateTask(taskId: Int, title: String, description: String) =
    copy(tasks = tasks.map { if (it.id == taskId) it.copy(title = title, description =  description) else it })