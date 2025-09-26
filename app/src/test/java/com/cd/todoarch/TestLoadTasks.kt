package com.cd.todoarch

import com.cd.todoarch.core.todoapp.kommand.LoadTasks
import com.cd.todoarch.core.todoapp.state.TodoState
import com.cd.todoarch.core.todoapp.state.model.Task
import com.cd.todoarch.util.BaseFluxTest
import com.cd.todoarch.util.setupTaskList
import org.junit.Test

class TestLoadTasks : BaseFluxTest() {

    @Test
    fun testLoadTasks() {
        val expectedTaskList = listOf(
            Task(id = 1, title = "Task 1", description = "Description 1"),
            Task(id = 2, title = "Task 2", description = "Description 2"),
        )
        setupTaskList(expectedTaskList)
        fluxTest(
            kommand = LoadTasks,
            expectedFinalState = TodoState(
                loadingTasks = false,
                tasks = expectedTaskList
            )
        )
    }

}