package com.cd.todoarch.states

import com.cd.todoarch.model.Task

sealed class TodoState {
    object Idle: TodoState()
    object Loading: TodoState()
    data class TodoList(val todoList: List<Task>): TodoState()
    data class FindById(val task: Task): TodoState()
    data class Error(val message: String) :TodoState()
}