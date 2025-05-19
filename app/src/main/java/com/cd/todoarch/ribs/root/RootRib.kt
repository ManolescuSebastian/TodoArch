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
    private var isAttached: Boolean = false

    private var activeChild: Any? by mutableStateOf(null)

    //Called by MainActivity
    fun dispatchAttach() {
        if (!isAttached) {
            println("RootRouter: dispatchAttach called.")
            isAttached = true
            attachTaskList()
        }
    }

    // Called by MainActivity
    fun dispatchDetach() {
        if (isAttached) {
            println("RootRouter: dispatchDetach called.")
            detachCurrentChild()
            // todoListRouter = null // Optional: clear child if it shouldn't be reused
            isAttached = false
            println("RootRouter: TodoListRouter detached.")
        }
    }

    private fun detachCurrentChild() {
        val childToDetach = activeChild
        if (childToDetach != null) {
            println("RootRouter: Detaching current child")
            if (childToDetach is TaskListRouter) {
                childToDetach.willDetach()
            }
            activeChild = null
        }
    }

    private fun attachTaskList() {
        detachCurrentChild()
        val taskListRouter = taskListBuilder.build()
        activeChild = taskListRouter
        taskListRouter.didAttach()
    }

    @Composable
    fun View() {
        val child = activeChild
        if(child is TaskListRouter) {
            child.View()
        } else {
            println("RootRouter Composable: No active/recognized child router to render.")
        }
    }

    /** Handles back press events. */
    fun handleBackPress(): Boolean {
        val childRouter = activeChild
        val handled = if (childRouter is TaskListRouter) { // Check for TaskListRouter
            println("RootRouter: Delegating back press to TaskListRouter")
            childRouter.handleBackPress() // Delegate to TaskListRouter
        } else {
            false
        }

        if (handled) {
            println("RootRouter: Back press handled by child.")
            return true
        } else {
            println("RootRouter: Back press not handled by children (or no children).")
            return false
        }
    }
}