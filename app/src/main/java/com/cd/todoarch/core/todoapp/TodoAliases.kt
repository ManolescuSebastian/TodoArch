package com.cd.todoarch.core.todoapp

import com.cd.todoarch.core.framework.Command
import com.cd.todoarch.core.framework.CommandCenter
import com.cd.todoarch.core.todoapp.event.TodoEvent
import com.cd.todoarch.core.todoapp.state.TodoState

typealias TodoCommandCenter = CommandCenter<TodoState, TodoEvent>
typealias TodoCommand = Command<TodoState, TodoEvent>