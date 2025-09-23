package com.cd.todoarch

import com.cd.todoarch.core.framework.single
import com.cd.todoarch.core.todoapp.TaskListRepository
import com.cd.todoarch.core.todoapp.TodoFlux
import com.cd.todoarch.core.todoapp.event.TodoEvent
import com.cd.todoarch.core.todoapp.kommand.LoadTasks
import com.cd.todoarch.core.todoapp.state.TodoState
import com.cd.todoarch.core.todoapp.state.model.Task
import com.cd.todoarch.util.fluxTest
import org.junit.Test

class TestLoadTasks {

    @Test
    fun testLoadTasks() {
        val expectedTaskList = listOf(
            Task(id = 1, title = "Task 1", description = "Description 1"),
            Task(id = 2, title = "Task 2", description = "Description 2"),
        )
        single<TaskListRepository> {
            object : TaskListRepository {
                override suspend fun loadTasks(): List<Task> {
                    return expectedTaskList
                }

                override suspend fun saveTasks(tasks: List<Task>) {
                    TODO("Not yet implemented")
                }
            }
        }
        fluxTest(
            fluxKore = TodoFlux(TodoState()),
            kommand = LoadTasks,
            initialEvent = TodoEvent.Nothing,
            expectedFinalState = TodoState(
                    loadingTasks = false,
                    tasks = expectedTaskList
                )
        )
    }

}