package com.cd.todoarch.ribs.taskDetailRib

import com.cd.todoarch.data.TaskRepository

class TaskDetailBuilder(
    private val dependency: TaskDetailDependency
) {

    interface TaskDetailDependency {
        fun taskRepository(): TaskRepository
//        fun parentInteractor(): TaskListInteractor // if needed
    }

    data class BuildParams(
        val itemId: String,
        val onClose: () -> Unit
    )

    fun build(buildParams: BuildParams): TaskDetailRouter {
        val interactor = TaskDetailInteractor(
            taskId = buildParams.itemId,
            repository = dependency.taskRepository(),
        )
        val router = TaskDetailRouter(
            interactor = interactor,
            onClose = buildParams.onClose
        )

        println("TaskDetailBuilder: TaskDetailRIB built.")
        return router
    }
}
