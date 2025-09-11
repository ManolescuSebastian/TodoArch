package com.cd.todoarch

import android.app.Application
import com.cd.todoarch.core.framework.factory
import com.cd.todoarch.core.framework.single
import com.cd.todoarch.core.todoapp.TaskListRepository
import com.cd.todoarch.core.todoapp.TodoFlux
import com.cd.todoarch.core.todoapp.kommand.LoadTasks
import com.cd.todoarch.core.todoapp.io.FakeTaskListRepository
import com.cd.todoarch.core.todoapp.state.TodoState
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class TodoApplication : Application() {

    private val applicationScope = MainScope()
    private val todoCommandCenter = TodoFlux(initialState = TodoState())
    override fun onCreate() {
        super.onCreate()
        // init bindings
        factory<TodoFlux> { todoCommandCenter  }
        single<TaskListRepository> { FakeTaskListRepository() }

        applicationScope.launch {
            todoCommandCenter.execute(LoadTasks)
        }
    }
}