package com.cd.todoarch.core.framework

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import com.cd.todoarch.core.framework.CommandCenterAction.ActionWithState
import com.cd.todoarch.core.framework.CommandCenterAction.Event
import com.cd.todoarch.core.framework.CommandCenterAction.StateChange
import com.cd.todoarch.core.framework.CommandCenterAction.Subcommand

class CommandCenter<STATE, EVENT>(
    initialState: STATE,
) {
    private val _events = MutableSharedFlow<EVENT>()
    val events: SharedFlow<EVENT> = _events

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<STATE> = _state

    suspend fun execute(command: Command<STATE, EVENT>) {
        command.createCommandFlow().collect {
            when (it) {
                is StateChange -> {
                    _state.value = it.stateChange(state.value)
                }
                is Event -> {
                    _events.emit(it.event)
                }
                is Subcommand -> {
                    execute(it.command)
                }
                is ActionWithState -> {
                    it.action(_state.value)
                }
            }
        }
    }
}

abstract class Command<STATE, EVENT> {
    private lateinit var collector: FlowCollector<CommandCenterAction<STATE, EVENT>>

    internal fun createCommandFlow(): Flow<CommandCenterAction<STATE, EVENT>> {
        return flow {
            collector = this
            buildCommand()
        }
    }

    abstract suspend fun buildCommand()

    suspend fun event(event: EVENT) {
        collector.emit(Event(event))
    }

    suspend fun stateChange(stateChange: STATE.() -> STATE) {
        collector.emit(StateChange(stateChange))
    }

    suspend fun action(action: suspend STATE.() -> Unit) {
        collector.emit(ActionWithState(action))
    }

    suspend fun subCommand(command: Command<STATE, EVENT>) {
        collector.emit(Subcommand(command))
    }
}

sealed class CommandCenterAction<STATE, EVENT> {
    data class Event<STATE, EVENT>(val event: EVENT) : CommandCenterAction<STATE, EVENT>()
    data class StateChange<STATE, EVENT>(val stateChange: STATE.() -> STATE) : CommandCenterAction<STATE, EVENT>()
    data class ActionWithState<STATE, EVENT>(val action: suspend STATE.() -> Unit) : CommandCenterAction<STATE, EVENT>()
    data class Subcommand<STATE, EVENT>(val command: Command<STATE, EVENT>) : CommandCenterAction<STATE, EVENT>()
}
