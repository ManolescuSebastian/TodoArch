package com.cd.todoarch.ribs.listRib

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.cd.todoarch.ribs.taskDetailRib.TaskDetailBuilder
import com.cd.todoarch.ribs.taskDetailRib.TaskDetailRouter

class TaskListRouter(
    private val interactor: TaskListInteractor,
    private val taskDetailBuilder: TaskDetailBuilder,
) {

    // --- State for Active Child ---
    // Track which child router is currently active.
    // `null` signifies that the TodoListScreen itself should be shown.
    // Using `mutableStateOf` for direct integration with Compose's recomposition.
    private var currentChild by mutableStateOf<Any?>(null)

    // Rib lifecycle simulation
    fun didAttach() {
        interactor.router = this
        interactor.didBecomeActive()
    }

    fun willDetach() {
        interactor.willResignActive()
        detachCurrentChild()
    }

    fun routeToDetailView(taskId: String) {
        // Before attaching a new child, ensure any existing child is detached.
        // This example supports only one child active at a time.
        detachCurrentChild()
        println("TaskListRouter: Routing to Task Detail for task $taskId")
        val detailRouter = taskDetailBuilder.build(
            TaskDetailBuilder.BuildParams(
                itemId = taskId,
                onClose = ::detachDetail
            )
        )
        detailRouter.didAttach()
        currentChild = detailRouter
    }

    private fun detachDetail() {
        println("TaskListRouter: detaching detail child")
        detachCurrentChild()
    }

    private fun detachCurrentChild() {
        val childToDetach = currentChild
        if (childToDetach != null) {
            println("TaskListRouter: Detaching child router.")
            if (childToDetach is TaskDetailRouter) { // Check for TaskDetailRouter
                childToDetach.willDetach()
            }
            currentChild = null
        }
    }

    @Composable
    fun View() {
        val listScreenState by interactor.state.collectAsState()
        val activeChild = currentChild
        println("TodoListRouter Composable: Recomposing. Active child is ${activeChild?.let { it::class.simpleName } ?: "None"}")

        if (activeChild == null) {
            // --- No active child: Show the List Screen ---
            println("TodoListRouter Composable: Rendering TodoListScreen.")
            TaskListScreen(
                listScreenState,
                onItemClicked = { taskId: String ->
                    routeToDetailView(taskId)
                },
                onTaskDeleted = interactor::deleteTask,
                onTaskAdded = interactor::addTask
            )
        } else if (activeChild is TaskDetailRouter) {
            // --- TodoDetailRouter is active: Show the Detail Screen ---
            // Call the child router's Composable View function.
            // The child router (TodoDetailRouter) is responsible for its own UI (TodoDetailScreen).
            println("TodoListRouter Composable: Rendering Detail Child (TodoDetailRouter's View).")
            activeChild.View()
        }
    }

    // --- Back Press Handling (Simplified) ---
    fun handleBackPress(): Boolean {
        if (currentChild != null) {
            println("TaskListRouter: Handling back press by detaching child.")
            detachCurrentChild() // Pop the detail screen if it's open
            return true
        }
        println("TaskListRouter: No child to handle back press.")
        return false
    }
}