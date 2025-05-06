package com.cd.todoarch.ribs.listRib

import com.cd.todoarch.data.TaskRepository
import com.cd.todoarch.ribarch.BasicInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TaskListInteractor(repository: TaskRepository): BasicInteractor {

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
        TODO("Not yet implemented")
    }

    override fun willResignActive() {
        TODO("Not yet implemented")
    }

    fun addTask(text: String) {

    }

    fun toggleTaskCompletion(taskId: String) {

    }

    fun deleteTask(taskId: String) {

    }

    // --- Navigation Request ---
    // This method is called, for example, when a user clicks on a TodoItem in the UI,
    // signaling a desire to see its details.
    fun requestDetailView(taskId: String) {
        router.routeToDetailView(taskId)
    }

    //region private

    //endregion


}