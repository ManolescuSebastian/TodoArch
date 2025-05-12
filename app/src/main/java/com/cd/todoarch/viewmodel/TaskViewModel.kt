package com.cd.todoarch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cd.todoarch.core.framework.inject
import com.cd.todoarch.core.todoapp.TodoCommand
import com.cd.todoarch.core.todoapp.TodoCommandCenter
import com.cd.todoarch.core.todoapp.command.AddTask
import com.cd.todoarch.core.todoapp.command.RemoveTask
import com.cd.todoarch.core.todoapp.command.UpdateTask
import com.cd.todoarch.core.todoapp.event.TodoEvent
import com.cd.todoarch.core.todoapp.event.TodoEvent.TaskRemoved
import com.cd.todoarch.core.todoapp.event.TodoEvent.TaskUpdated
import com.cd.todoarch.core.todoapp.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {

    private val todoCommandCenter by inject<TodoCommandCenter>()

    val tasks: Flow<List<Task>> = todoCommandCenter.state.map { it.tasks }
    val tasksChangedEvents: Flow<TodoEvent> = todoCommandCenter.events.filter {
        it is TaskRemoved || it is TaskUpdated
    }

    fun addTask(title: String, description: String) {
        executeCommand(AddTask(title, description))
    }

    fun removeTask(task: Task) {
        executeCommand(RemoveTask(task))
    }

    fun updateTask(taskId: Int, title: String, description: String) {
        executeCommand(UpdateTask(taskId, title, description))
    }

    private fun executeCommand(command: TodoCommand) {
        viewModelScope.launch {
            todoCommandCenter.execute(command)
        }
    }
}