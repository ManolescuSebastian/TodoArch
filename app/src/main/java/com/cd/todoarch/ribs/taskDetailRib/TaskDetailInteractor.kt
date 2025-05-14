package com.cd.todoarch.ribs.taskDetailRib

import com.cd.todoarch.data.TaskRepository
import com.cd.todoarch.ribarch.BasicInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
            try {
                val item = repository.getTask(taskId)
                item?.let {
                    _state.update { it.copy(task = item, isLoading = false) }
                } ?: run {
                    _state.update { it.copy(error = "Task not found", isLoading = false) }
                }
            } catch (e: Exception) {
                println("TaskDetailInteractor: Error loading details - ${e.message}")
                _state.update { it.copy(isLoading = false, error = "Failed to load details") }
            }
        }
    }

    fun handleBackNavigation() {
        println("TaskDetailInteractor: Back navigation requested.")
    }
}
