package com.cd.todoarch

import android.app.Application
import com.cd.todoarch.core.framework.factory
import com.cd.todoarch.core.framework.single
import com.cd.todoarch.core.todoapp.TaskListRepository
import com.cd.todoarch.core.todoapp.TodoCommandCenter
import com.cd.todoarch.core.todoapp.command.LoadTasks
import com.cd.todoarch.core.todoapp.io.FakeTaskListRepository
import com.cd.todoarch.core.todoapp.state.TodoState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TodoApplication : Application() {

    private val applicationScope = MainScope()
    private val todoCommandCenter = TodoCommandCenter(initialState = TodoState())
    override fun onCreate() {
        super.onCreate()
        // init bindings
        factory<TodoCommandCenter> { todoCommandCenter  }
        single<TaskListRepository> { FakeTaskListRepository() }

        applicationScope.launch {
            todoCommandCenter.execute(LoadTasks)
        }
    }
}