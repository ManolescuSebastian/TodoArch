package com.cd.todoarch

import android.app.Application
import com.cd.todoarch.data.InMemoryTaskRepository
import com.cd.todoarch.data.TaskRepository
import com.cd.todoarch.ribs.listRib.TaskListBuilderDependencyProvider
import com.cd.todoarch.ribs.root.RootBuilder
import com.cd.todoarch.ribs.taskDetailRib.TaskDetailBuilder

interface AppComponent {
    fun rootBuilder(): RootBuilder
}

class AppComponentFactory {
    companion object {
        private val taskRepository: TaskRepository by lazy {
            InMemoryTaskRepository()
        }

        fun create(): AppComponent {
            return object : AppComponent {
                override fun rootBuilder(): RootBuilder {
                    val rootDependencyImpl = object : RootBuilder.RootDependency {}

                    val taskListBuilderDependencyProviderImpl = object :
                        TaskListBuilderDependencyProvider {
                        override fun taskDetailBuilder(): TaskDetailBuilder {
                            val taskDetailDependencyImpl =
                                object : TaskDetailBuilder.TaskDetailDependency {
                                    override fun taskRepository(): TaskRepository = taskRepository
                                }
                            return TaskDetailBuilder(taskDetailDependencyImpl)
                        }

                        override fun repository(): TaskRepository = taskRepository
                    }
                    return RootBuilder(rootDependencyImpl, taskListBuilderDependencyProviderImpl)
                }
            }
        }
    }
}

class TaskApplication: Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = AppComponentFactory.create()
    }
}