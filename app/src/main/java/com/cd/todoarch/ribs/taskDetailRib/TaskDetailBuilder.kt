package com.cd.todoarch.ribs.taskDetailRib

import com.cd.todoarch.data.TaskRepository
import com.cd.todoarch.ribs.listRib.TaskListInteractor

class TaskDetailBuilder(
    private val dependency: TaskDetailDependency
) {

    interface TaskDetailDependency {
        fun taskRepository(): TaskRepository
//        fun parentInteractor(): TaskListInteractor // if needed
    }

    data class BuildParams(
        val itemId: String,
        val listener: () -> Unit // The onClose listener for back
    )

    fun build(buildParams: BuildParams): TaskDetailRouter {
        val interactor = TaskDetailInteractor(
            itemId = buildParams.itemId,
            repository = dependency.taskRepository(),
        )
        val router = TaskDetailRouter(
            interactor = interactor,
            onClose = buildParams.listener
        )

        return router
    }
}