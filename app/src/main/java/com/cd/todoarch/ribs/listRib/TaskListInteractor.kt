package com.cd.todoarch.ribs.listRib

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

class TaskListInteractor(private val repository: TaskRepository): BasicInteractor {

    // Scope for launching coroutines tied to the Interactor's lifecycle
    // In a real RIB, this scope would be provided or managed by the base Interactor class
    private val interactorScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    // --- Router Reference ---
    // This property will be set by the Builder or Router during RIB construction.
    // It's declared as 'internal lateinit var' meaning it must be initialized before use
    // and is accessible within the same module.
    internal lateinit var router: TaskListRouter

    // --- State ---
    private val _state = MutableStateFlow(TaskListState()) // Initial empty state
    val state: StateFlow<TaskListState> = _state.asStateFlow() // Expose state immutably



    override fun didBecomeActive() {
        println("TaskListInteractor: didBecomeActive")
        observeTasks()
    }

    override fun willResignActive() {
        println("TaskListInteractor: willResignActive")
        interactorScope.cancel() // Cancel coroutines tied to this Interactor. Usually handled by the base Interactor from the lib
    }

    //Region business logic

    fun addTask(title: String, description: String? = null) {
        interactorScope.launch {
            try {
                println("TaskListInteractor: Adding task '$title' via repository")
                val newTask = Task(title = title, description = description)
                repository.addTask(newTask)
                // State will update automatically via the observed stream
            } catch (e: Exception) {
                println("TaskListInteractor: Error adding task - ${e.message}")
                _state.update { it.copy(error = "Failed to add task: ${e.message}") }
            }
        }
    }

    fun toggleTaskCompletion(taskId: String) {
        interactorScope.launch {
            try {
                val task = repository.getTask(taskId) // Get the current task
                task?.let {
                    println("TaskListInteractor: Toggling completion for task $taskId via repository")
                    val updatedTask = it.copy(isCompleted = !it.isCompleted)
                    repository.updateTask(updatedTask)
                    // State will update automatically via the observed stream
                } ?: run {
                    println("TaskListInteractor: Task $taskId not found for toggle.")
                    _state.update { it.copy(error = "Task not found for update.") }
                }
            } catch (e: Exception) {
                println("TaskListInteractor: Error toggling task completion - ${e.message}")
                _state.update { it.copy(error = "Failed to toggle task: ${e.message}") }
            }
        }
    }

    fun deleteTask(taskId: String) {
        interactorScope.launch {
            try {
                println("TaskListInteractor: Deleting task $taskId via repository")
                repository.deleteTask(taskId)
                // State will update automatically via the observed stream
            } catch (e: Exception) {
                println("TaskListInteractor: Error deleting task - ${e.message}")
                _state.update { it.copy(error = "Failed to delete task: ${e.message}") }
            }
        }
    }
    //endregion

    // --- Navigation Request ---
    // This method is called, for example, when a user clicks on a TodoItem in the UI,
    // signaling a desire to see its details.
    fun requestDetailView(taskId: String) {
        router.routeToDetailView(taskId)
    }

    //region private
    private fun observeTasks() {
        repository.getTasksStream()
            .onEach { tasks ->
                println("TaskListInteractor: Received task update from repository stream.")
                _state.update {
                    it.copy(isLoading = false, tasks = tasks, error = null)
                }
            }.catch { e ->
            println("TaskListInteractor: Error observing tasks - ${e.message}")
            _state.update {
                it.copy(isLoading = false, error = "Failed to load tasks: ${e.message}")
            }
        }.launchIn(interactorScope) // Launch the flow collection in the interactor's scope
    }
    //endregion


}