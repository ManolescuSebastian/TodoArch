package com.cd.todoarch.ribs.listRib

import com.cd.todoarch.data.TaskRepository
import com.cd.todoarch.ribs.taskDetailRib.TaskDetailBuilder

interface TaskListBuilderDependencyProvider {
    fun taskDetailBuilder(): TaskDetailBuilder
    fun repository(): TaskRepository
}

class TaskListBuilder(
    private val dependency: TaskListBuilderDependencyProvider
) {

    fun build(): TaskListRouter {
        val interactor = TaskListInteractor(dependency.repository())

        val router = TaskListRouter(
            interactor = interactor,
            taskDetailBuilder = dependency.taskDetailBuilder()
        )
        interactor.router = router
        return router
    }
}
