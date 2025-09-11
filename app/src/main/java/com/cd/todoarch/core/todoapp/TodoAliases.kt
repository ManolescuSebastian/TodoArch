package com.cd.todoarch.core.todoapp

import com.cd.todoarch.core.framework.Kommand
import com.cd.todoarch.core.framework.FluxKore
import com.cd.todoarch.core.todoapp.event.TodoEvent
import com.cd.todoarch.core.todoapp.state.TodoState

typealias TodoFlux = FluxKore<TodoState, TodoEvent>
typealias TodoKommand = Kommand<TodoState, TodoEvent>