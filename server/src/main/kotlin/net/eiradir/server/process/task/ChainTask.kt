package net.eiradir.server.process.task

import net.eiradir.server.process.*
import java.util.*

class ChainTask(subProcessBuilder: ProcessDefinitionBuilder.() -> Unit) : Task {

    override val taskId = UUID.randomUUID().toString()
    private val subProcess = ProcessDefinition(taskId, allowCheckpoints = false).also(subProcessBuilder)

    override fun execute(context: ProcessContext): Boolean {
        val data = context.getTaskData(taskId) { SubProcessTaskData() }
        val task = subProcess.tasks[data.innerTaskIndex]
        if (task.execute(context)) {
            task.clearTaskData(context)
            data.innerTaskIndex++
            if (data.innerTaskIndex >= subProcess.tasks.size) {
                return true
            }
        }
        return false
    }

    override fun clearTaskData(context: ProcessContext) {
        context.getTaskData(taskId) { SubProcessTaskData(0) }.innerTaskIndex = 0
    }
}

fun ProcessDefinitionBuilder.chain(sequence: ProcessDefinitionBuilder.() -> Unit) {
    addTask(ChainTask(sequence))
}
