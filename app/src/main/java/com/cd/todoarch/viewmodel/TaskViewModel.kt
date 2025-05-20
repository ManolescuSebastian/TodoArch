package com.cd.todoarch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cd.todoarch.intents.TodoIntent
import com.cd.todoarch.model.Task
import com.cd.todoarch.states.TodoState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {

    private val _state = MutableStateFlow<TodoState>(TodoState.Idle)
    val state: StateFlow<TodoState> = _state

    private val todos = mutableListOf<Task>()

    val userIntent = Channel<TodoIntent>(Channel.UNLIMITED)

    init {
        handleIntents()
    }

    private fun handleIntents() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect { intent ->
                when (intent) {
                    is TodoIntent.AddTodo -> addTodo(intent.text, intent.description)
                    is TodoIntent.DeleteTodo -> deleteTodo(intent.id)
                    is TodoIntent.LoadTodos -> loadTodos()
                    is TodoIntent.UpdateTodo -> updateTodo(intent.task)
                }
            }
        }
    }

    private fun addTodo(text: String, description: String?) {
        val taskId = if (todos.size <= 0) {
            0
        } else {
            todos.size + 1
        }
        val todo = Task(taskId, text, description)
        todos.add(todo)
        _state.value = TodoState.TodoList(todos.toList())
    }

    private fun updateTodo(updatedTask: Task) {
        val currentState = _state.value
        if (currentState is TodoState.TodoList) {
            val updatedList = currentState.todoList.map { task ->
                if (task.id == updatedTask.id) updatedTask else task
            }
            todos.clear()
            todos.addAll(updatedList)
            _state.value = TodoState.TodoList(updatedList)
        }
    }

    private fun deleteTodo(id: Int) {
        todos.removeAll { it.id == id }
        _state.value = TodoState.TodoList(todos.toList())
    }

    private fun loadTodos() {
        _state.value = TodoState.Loading
        _state.value = TodoState.TodoList(todos.toList())
    }
}