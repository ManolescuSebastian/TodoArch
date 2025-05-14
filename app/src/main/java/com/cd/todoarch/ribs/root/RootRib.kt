package com.cd.todoarch.ribs.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.cd.todoarch.ribs.listRib.TaskListBuilder
import com.cd.todoarch.ribs.listRib.TaskListBuilderDependencyProvider
import com.cd.todoarch.ribs.listRib.TaskListRouter


class RootBuilder(
    private val dependency: RootDependency,
    private val taskListBuilderDependencyProvider: TaskListBuilderDependencyProvider
) {
    interface RootDependency {
        // e.g., ApplicationContext, if RootInteractor needs it
    }

    fun build(): RootRouter {
        val taskListBuilder = TaskListBuilder(taskListBuilderDependencyProvider)
        val router = RootRouter(taskListBuilder)
        return router
    }
}

class RootRouter(private val taskListBuilder: TaskListBuilder) {
    private var taskListRouter: TaskListRouter? by mutableStateOf(null)
    private var isAttached: Boolean = false

    //Called by MainActivity
    fun dispatchAttach() {
        if (!isAttached) {
            println("RootRouter: dispatchAttach called.")
            isAttached = true
            // Attach the main content RIB (TodoListRIB in this case)
            if (taskListRouter == null) {
                taskListRouter = taskListBuilder.build()
            }
            taskListRouter?.didAttach() // Tell child it's attached
            println("RootRouter: TodoListRouter attached.")
        }
    }

    // Called by MainActivity
    fun dispatchDetach() {
        if (isAttached) {
            println("RootRouter: dispatchDetach called.")
            taskListRouter?.willDetach() // Tell child it will detach
            // todoListRouter = null // Optional: clear child if it shouldn't be reused
            isAttached = false
            println("RootRouter: TodoListRouter detached.")
        }
    }

    @Composable
    fun View() {
        // RootRouter delegates its UI to its active child.
        // In a more complex app, RootRouter might decide between different
        // top-level children (e.g., LoggedIn vs LoggedOut).
        taskListRouter?.let {
            println("RootRouter Composable: Rendering TodoListRouter.View")
            it.View() // Call the child's Composable View
        } ?: run {
            println("RootRouter Composable: No active child to render.")
            // Optionally show a loading screen or an error if no child is ready
        }
    }
}