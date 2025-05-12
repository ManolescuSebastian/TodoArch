package com.cd.todoarch

import android.app.Application
import com.cd.todoarch.core.framework.factory
import com.cd.todoarch.core.todoapp.TodoCommandCenter
import com.cd.todoarch.core.todoapp.command.LoadTasks
import com.cd.todoarch.core.todoapp.state.TodoState
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TodoApplication : Application() {
    private val todoCommandCenter = TodoCommandCenter(initialState = TodoState())
    override fun onCreate() {
        super.onCreate()
        // init bindings
        factory<TodoCommandCenter> { todoCommandCenter  }

        runBlocking {
            launch {
                todoCommandCenter.execute(LoadTasks)
            }
        }
    }
}