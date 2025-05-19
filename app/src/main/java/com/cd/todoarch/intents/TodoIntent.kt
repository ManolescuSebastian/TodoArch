package com.cd.todoarch.intents

import com.cd.todoarch.model.Task

sealed class TodoIntent {
    data class AddTodo(val text: String, val description: String?) : TodoIntent()
    data class UpdateTodo(val task: Task): TodoIntent()
    data class DeleteTodo(val id: Int) : TodoIntent()
    object LoadTodos : TodoIntent()
}