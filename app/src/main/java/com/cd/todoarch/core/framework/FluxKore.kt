package com.cd.todoarch.core.framework

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import com.cd.todoarch.core.framework.FluxStep.Action
import com.cd.todoarch.core.framework.FluxStep.Event
import com.cd.todoarch.core.framework.FluxStep.Mutation
import com.cd.todoarch.core.framework.FluxStep.SubKommand

class FluxKore<STATE, EVENT>(
    initialState: STATE,
) {
    private val _events = MutableSharedFlow<EVENT>()
    val events: SharedFlow<EVENT> = _events

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<STATE> = _state

    suspend fun execute(kommand: Kommand<STATE, EVENT>) {
        kommand.createCommandFlow().collect {
            when (it) {
                is Mutation -> {
                    _state.value = it.mutator(state.value)
                }
                is Event -> {
                    _events.emit(it.event)
                }
                is SubKommand -> {
                    execute(it.kommand)
                }
                is Action -> {
                    it.action(_state.value)
                }
            }
        }
    }
}

abstract class Kommand<STATE, EVENT> {
    private lateinit var collector: FlowCollector<FluxStep<STATE, EVENT>>

    internal fun createCommandFlow(): Flow<FluxStep<STATE, EVENT>> {
        return flow {
            collector = this
            describe()
        }
    }

    abstract suspend fun describe()

    suspend fun event(event: EVENT) {
        collector.emit(Event(event))
    }

    suspend fun mutation(mutation: STATE.() -> STATE) {
        collector.emit(Mutation(mutation))
    }

    suspend fun action(action: suspend STATE.() -> Unit) {
        collector.emit(Action(action))
    }

    suspend fun composeWith(kommand: Kommand<STATE, EVENT>) {
        collector.emit(SubKommand(kommand))
    }
}

sealed class FluxStep<STATE, EVENT> {
    data class Event<STATE, EVENT>(val event: EVENT) : FluxStep<STATE, EVENT>()
    data class Mutation<STATE, EVENT>(val mutator: STATE.() -> STATE) : FluxStep<STATE, EVENT>()
    data class Action<STATE, EVENT>(val action: suspend STATE.() -> Unit) : FluxStep<STATE, EVENT>()
    data class SubKommand<STATE, EVENT>(val kommand: Kommand<STATE, EVENT>) : FluxStep<STATE, EVENT>()
}
