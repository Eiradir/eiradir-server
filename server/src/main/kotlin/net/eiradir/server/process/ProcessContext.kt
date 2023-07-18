package net.eiradir.server.process

import com.badlogic.ashley.core.Entity
import net.eiradir.server.process.task.InitializeTask

class ProcessContext(val entity: Entity, val process: ProcessDefinition, val fromCheckpoint: String? = null, private val _contextData: Map<String, Any> = mutableMapOf()) {

    val contextData: Map<String, Any> = _contextData
    private val _taskData = mutableMapOf<String, Any>()
    val taskData: Map<String, Any> = _taskData

    var taskIndex: Int = 0
    var checkpoint: String? = null
    var overrideTask: Task? = process.tasks.find { it is InitializeTask }
    var state: ProcessState = ProcessState.Pass; private set
    private val successHandlers = mutableListOf<() -> Unit>()
    private val failureHandlers = mutableListOf<() -> Unit>()
    private val interruptHandlers = mutableListOf<(Interruption) -> Unit>()

    init {
        onInterrupt { process.onInterrupt(this, it) }
    }

    fun <T : Any> getTaskData(taskId: String, initializer: () -> T): T {
        @Suppress("UNCHECKED_CAST")
        return _taskData.computeIfAbsent(taskId) { initializer() } as T
    }

    fun clearTaskData(taskId: String) {
        _taskData.remove(taskId)
    }

    fun failure() {
        state = ProcessState.Failure
    }

    fun success() {
        state = ProcessState.Success
    }

    fun onSuccess(handler: () -> Unit) {
        successHandlers.add(handler)
    }

    fun onFailure(handler: () -> Unit) {
        failureHandlers.add(handler)
    }

    fun onInterrupt(handler: (Interruption) -> Unit) {
        interruptHandlers.add(handler)
    }

    fun interrupted(interruption: Interruption) {
        interruptHandlers.forEach { it(interruption) }
    }

    fun complete() {
        when (state) {
            ProcessState.Failure -> failureHandlers.forEach { it() }
            else -> successHandlers.forEach { it() }
        }
    }

}