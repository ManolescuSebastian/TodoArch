package com.cd.todoarch.util

import com.cd.todoarch.core.todoapp.TodoFlux
import com.cd.todoarch.core.todoapp.TodoKommand
import com.cd.todoarch.core.todoapp.state.TodoState
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo

open class BaseFluxTest {
    fun fluxTest(
        initialState: TodoState = TodoState(),
        kommand: TodoKommand,
        expectedFinalState: TodoState,
    ) = runBlocking {
        val todoFlux = TodoFlux(initialState)
        todoFlux.execute(kommand)
        todoFlux.state.value shouldBeEqualTo expectedFinalState
    }
}