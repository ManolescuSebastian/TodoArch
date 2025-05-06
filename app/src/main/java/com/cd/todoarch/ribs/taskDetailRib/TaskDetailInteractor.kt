package com.cd.todoarch.ribs.taskDetailRib

import com.cd.todoarch.data.TaskRepository
import com.cd.todoarch.ribarch.BasicInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TaskDetailInteractor(
    private val itemId: String,
    private val repository: TaskRepository
): BasicInteractor {

    private val interactorScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _state = MutableStateFlow(TaskDetailState(isLoading = true))
    val state: StateFlow<TaskDetailState> = _state.asStateFlow()

    internal lateinit var router: TaskDetailRouter

    override fun didBecomeActive() {
        TODO("Not yet implemented")
    }

    override fun willResignActive() {
        TODO("Not yet implemented")
    }
}