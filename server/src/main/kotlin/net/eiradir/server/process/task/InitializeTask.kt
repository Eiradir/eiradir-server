package net.eiradir.server.process.task

import net.eiradir.server.process.*
import net.eiradir.server.process.data.ProcessDefinition
import net.eiradir.server.process.data.ProcessDefinitionBuilder
import java.util.*

class InitializeTask(subProcessBuilder: ProcessDefinitionBuilder.() -> Unit) : Task {

    override val taskId = UUID.randomUUID().toString()
    private val subProcess = ProcessDefinition(taskId, allowCheckpoints = false).also(subProcessBuilder)

    override fun execute(context: ProcessContext): Boolean {
        val data = context.getTaskData(taskId) { SubProcessTaskData(0) }
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

}

fun ProcessDefinitionBuilder.initialize(initializer: ProcessDefinitionBuilder.() -> Unit) {
    // TODO look for existing initialize task and add onto it
    addTask(InitializeTask(initializer))
}