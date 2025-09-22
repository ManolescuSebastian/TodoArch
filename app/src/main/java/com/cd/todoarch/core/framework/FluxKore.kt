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

/**
 * A simple implementation of the Command pattern with support for:
 * - State mutations
 * - One-off events
 * - Composable commands
 * - Side effects (actions)
 *
 * Usage:
 * 1. Create a FluxKore instance with an initial state
 * 2. Define your state and event types
 * 3. Create Kommand subclasses to describe state changes and events
 * 4. Execute Kommands using the FluxKore instance
 *
 * Example:
 * ```
 * val flux = FluxKore<MyState, MyEvent>(initialState = MyState())
 *
 * class MyKommand : Kommand<MyState, MyEvent>() {
 *     override suspend fun describe() {
 *         mutation { copy(loading = true) }
 *         // perform some work
 *         mutation { copy(loading = false) }
 *         event(MyEvent.DataLoaded)
 *     }
 * }
 *
 * flux.execute(MyKommand())
 * ```
 */
class FluxKore<STATE, EVENT>(
    initialState: STATE,
) {
    private val _events = MutableSharedFlow<EVENT>()

    /**
     * A flow of one-off events emitted during Kommand execution.
     */
    val events: SharedFlow<EVENT> = _events

    private val _state = MutableStateFlow(initialState)

    /**
     * A flow representing the current state, updated after each mutation.
     */
    val state: StateFlow<STATE> = _state

    /**
     * Execute a Kommand, processing its defined steps sequentially.
     * State mutations will update the current state.
     * Events will be emitted to the events flow.
     * Sub-Kommands will be executed recursively.
     * Actions will be performed with access to the current state.
     */
    suspend fun execute(kommand: Kommand<STATE, EVENT>) {
        kommand.steps().collect {
            runCatching { // Kommand should never crash the app
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
}

/**
 * A Kommand describes a series of state mutations, events, side effects, and/or sub-commands.
 * It is executed by the FluxKore instance.
 *
 * Implement the [describe] method to define the sequence of operations.
 *
 * Use the following methods within [describe]:
 * - [mutation]: to define a state mutation
 * - [event]: to emit an event
 * - [action]: to perform a side effect
 * - [composeWith]: to execute another Kommand
 */
abstract class Kommand<STATE, EVENT> {
    private var collector: FlowCollector<FluxStep<STATE, EVENT>>? = null

    internal fun steps(): Flow<FluxStep<STATE, EVENT>> = flow {
        collector = this
        describe()
        collector = null
    }

    /**
     * Override this method to describe the sequence of mutations, events, actions, and sub-commands.
     * Use the provided helper methods to emit each step.
     */
    abstract suspend fun describe()

    /**
     * Emit an event.
     */
    suspend fun event(event: EVENT) {
        requireNotNull(collector).emit(Event(event))
    }

    /**
     * Emit a state mutation.
     */
    suspend fun mutation(mutation: STATE.() -> STATE) {
        requireNotNull(collector).emit(Mutation(mutation))
    }

    /**
     * Perform a side effect without modifying the state.
     * Use this for operations like logging, analytics, or other non-state-changing tasks.
     */
    suspend fun action(action: suspend STATE.() -> Unit) {
        requireNotNull(collector).emit(Action(action))
    }

    /**
     * Compose this Kommand with another Kommand.
     * The composed Kommand will be executed as part of this Kommand's execution flow.
     */
    suspend fun composeWith(kommand: Kommand<STATE, EVENT>) {
        requireNotNull(collector).emit(SubKommand(kommand))
    }
}

internal sealed class FluxStep<STATE, EVENT> {
    data class Event<STATE, EVENT>(val event: EVENT) : FluxStep<STATE, EVENT>()
    data class Mutation<STATE, EVENT>(val mutator: STATE.() -> STATE) : FluxStep<STATE, EVENT>()
    data class Action<STATE, EVENT>(val action: suspend STATE.() -> Unit) : FluxStep<STATE, EVENT>()
    data class SubKommand<STATE, EVENT>(val kommand: Kommand<STATE, EVENT>) : FluxStep<STATE, EVENT>()
}
