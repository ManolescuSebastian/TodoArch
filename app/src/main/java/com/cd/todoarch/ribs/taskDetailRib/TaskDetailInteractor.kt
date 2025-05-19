package com.cd.todoarch.ribs.taskDetailRib

import com.cd.todoarch.data.Task
import com.cd.todoarch.data.TaskRepository
import com.cd.todoarch.ribarch.BasicInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskDetailInteractor(
    private val taskId: String,
    private val repository: TaskRepository
) : BasicInteractor {

    private val interactorScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _state = MutableStateFlow(TaskDetailState(isLoading = true))
    val state: StateFlow<TaskDetailState> = _state.asStateFlow()

    internal lateinit var router: TaskDetailRouter

    override fun didBecomeActive() {
        println("TaskDetailInteractor: didBecomeActive for task $taskId")
        loadTaskDetails()
    }

    override fun willResignActive() {
        println("TaskDetailInteractor: willResignActive for task $taskId")
        interactorScope.cancel()
    }

    private fun loadTaskDetails() {
        interactorScope.launch {
            _state.update { it.copy(isLoading = true) }
            println("TaskDetailInteractor: Loading details for task $taskId via repository")
            repository.getTaskStream(taskId)
                .onEach { task ->
                    if (task != null) {
                        println("TaskDetailInteractor: Task update received from stream for $taskId.")
                        _state.update { it.copy(isLoading = false, task = task, error = null) }
                    } else {
                        println("TaskDetailInteractor: Task $taskId not found in stream (or deleted).")
                        _state.update {
                            it.copy(
                                isLoading = false,
                                task = null,
                                error = "Task not found"
                            )
                        }
                    }
                }.catch { e ->
                    println("TaskDetailInteractor: Error observing task $taskId - ${e.message}")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to load task details: ${e.message}"
                        )
                    }
                }.launchIn(interactorScope)
        }
    }

    fun handleBackNavigation(onClose: () -> Unit) {
        println("TaskDetailInteractor: Back navigation requested.")
        onClose.invoke()
    }

    fun handleDelete(onClose: () -> Unit) {
        interactorScope.launch {
            try {
                println("TaskDetailInteractor: Deleting task $taskId via repository")
                repository.deleteTask(taskId)
            } catch (e: Exception) {
                println("TaskDetailInteractor: Error deleting task - ${e.message}")
                _state.update { it.copy(error = "Failed to delete task: ${e.message}") }
            }
            onClose.invoke()
        }
    }

    fun handleEdit(title: String, description: String) {
        interactorScope.launch {
            repository.updateTask(
                Task(taskId, title, description)
            )
        }
    }
}
