package com.cd.todoarch.ribs.taskDetailRib

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class TaskDetailRouter(
    private val interactor: TaskDetailInteractor,
    private val onClose: () -> Unit
) {
    // --- RIB Lifecycle --- (Conceptual)
    fun didAttach() {
        interactor.router = this // Provide router reference to interactor
        interactor.didBecomeActive() // Activate interactor logic
    }

    fun willDetach() {
        //Cleanup if needed
    }

    @Composable
    fun View() {
        val state by interactor.state.collectAsState()
        TaskDetailScreen(state) {
//            interactor.handleBackNavigation
            onClose
        }
    }

}
