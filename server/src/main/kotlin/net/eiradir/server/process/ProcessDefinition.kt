package net.eiradir.server.process

import net.eiradir.server.process.task.CheckpointTask

class ProcessDefinition(val name: String, val allowCheckpoints: Boolean = true, val transient: Boolean = false) : ProcessDefinitionBuilder {

    private val _tasks = mutableListOf<Task>()
    private val _checkpoints = mutableMapOf<String, Int>()

    val tasks: List<Task> = _tasks
    val checkpoints: Map<String, Int> = _checkpoints
    var mergeStacks: Boolean = false
    private var applyHandler: ProcessContext.() -> Unit = {}
    private var stackHandler: ProcessContext.(ProcessContext) -> Unit = {}
    private var interruptHandler: ProcessContext.(Interruption) -> Unit = { failure() }

    override fun addTask(task: Task) {
        _tasks.add(task)
    }

    fun checkpoint(name: String) {
        if (!allowCheckpoints) {
            throw IllegalStateException("Checkpoints are not allowed in this process")
        }
        addTask(CheckpointTask(name))
        _checkpoints[name] = _tasks.size
    }

    @ProcessDefinitionMarker
    fun applied(handler: ProcessContext.() -> Unit) {
        applyHandler = handler
    }

    @ProcessDefinitionMarker
    fun stacked(handler: ProcessContext.(ProcessContext) -> Unit) {
        stackHandler = handler
    }

    @ProcessDefinitionMarker
    fun interrupted(handler: ProcessContext.(Interruption) -> Unit) {
        interruptHandler = handler
    }

    fun onApply(context: ProcessContext) {
        applyHandler(context)
    }

    fun onStack(context: ProcessContext, other: ProcessContext) {
        stackHandler(context, other)
    }

    fun onInterrupt(context: ProcessContext, interruption: Interruption) {
        interruptHandler(context, interruption)
    }

}