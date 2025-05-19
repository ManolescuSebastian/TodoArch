package com.cd.todoarch.data

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

interface TaskRepository {
    /** Returns a flow of the current list of tasks. */
    fun getTasksStream(): Flow<List<Task>> // Changed to Flow

    /** Gets a list of all tasks (one-shot). */
    suspend fun getTasks(): List<Task>

    /** Gets a single task by its ID. */
    suspend fun getTask(id: String): Task?

    suspend fun getTaskStream(id: String): Flow<Task?>

    /** Adds a new task. */
    suspend fun addTask(task: Task)

    /** Updates an existing task. */
    suspend fun updateTask(task: Task)

    /** Deletes a task by its ID. */
    suspend fun deleteTask(id: String)

}

class InMemoryTaskRepository : TaskRepository {

    private val mutex = Mutex()

    private val tasks = mutableStateListOf(
        Task(title = "Buy groceries", description = "Milk, eggs, bread"),
        Task(title = "Walk the dog", description = "Around the park"),
        Task(title = "Ride a bike", description = "From work to home"),
        Task(title = "Finish project", description = "Submit by end of the week"),
        Task(title = "Read a book", description = "Finish the last two chapters")
    )

    // MutableStateFlow to hold the current list and emit updates
    private val tasksFlow = MutableStateFlow(tasks.toList())

    override fun getTasksStream(): Flow<List<Task>> {
        return tasksFlow
    }

    override suspend fun getTasks(): List<Task> {
        // Simulate potential delay and ensure thread safety
        return withContext(Dispatchers.IO) {
            delay(100) // Simulate some I/O delay
            mutex.withLock {
                tasks.toList() // Return a copy
            }
        }
    }

    override suspend fun getTask(id: String): Task? {
        return withContext(Dispatchers.IO) {
            delay(50) // Simulate I/O
            mutex.withLock {
                tasks.find { it.id == id }
            }
        }
    }

    override suspend fun getTaskStream(id: String): Flow<Task?> {
        return tasksFlow
            .map { taskList -> taskList.find { it.id == id } }
            .distinctUntilChanged()
    }

    override suspend fun addTask(task: Task) {
        withContext(Dispatchers.IO) {
            delay(100) // Simulate I/O
            mutex.withLock {
                tasks.add(task)
                // Emit the updated list to the flow
                tasksFlow.value = tasks.toList()
            }
            println("InMemoryTaskRepository: Added task ${task.id}")
        }
    }

    override suspend fun updateTask(task: Task) {
        withContext(Dispatchers.IO) {
            delay(100) // Simulate I/O
            mutex.withLock {
                val index = tasks.indexOfFirst { it.id == task.id }
                if (index != -1) {
                    tasks[index] = task
                    tasksFlow.value = tasks.toList() // Emit update
                    println("InMemoryTaskRepository: Updated task ${task.id}")
                } else {
                    println("InMemoryTaskRepository: Update failed - task ${task.id} not found")
                }
            }
        }
    }

    override suspend fun deleteTask(id: String) {
        withContext(Dispatchers.IO) {
            delay(100) // Simulate I/O
            mutex.withLock {
                val removed = tasks.removeAll { it.id == id }
                if (removed) {
                    tasksFlow.value = tasks.toList() // Emit update
                    println("InMemoryTaskRepository: Deleted task $id")
                } else {
                    println("InMemoryTaskRepository: Delete failed - task $id not found")
                }
            }
        }
    }
}