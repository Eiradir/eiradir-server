package net.eiradir.server.process.task

import net.eiradir.server.process.*
import net.eiradir.server.process.data.ProcessDefinition
import net.eiradir.server.process.data.ProcessDefinitionBuilder
import java.util.*

class Repeat(subProcessBuilder: ProcessDefinitionBuilder.() -> Unit) : Task {

    override val taskId = UUID.randomUUID().toString()
    private val subProcess = ProcessDefinition(taskId, allowCheckpoints = false).also(subProcessBuilder)

    override fun execute(context: ProcessContext): Boolean {
        val data = context.getTaskData(taskId) { SubProcessTaskData(0) }
        val task = subProcess.tasks[data.innerTaskIndex]
        if (task.execute(context)) {
            task.clearTaskData(context)
            data.innerTaskIndex = (data.innerTaskIndex + 1) % subProcess.tasks.size
        }
        return false
    }

    override fun clearTaskData(context: ProcessContext) {
        super.clearTaskData(context)
    }
}

fun ProcessDefinitionBuilder.repeat(sequence: ProcessDefinitionBuilder.() -> Unit) {
    addTask(Repeat(sequence))
}
