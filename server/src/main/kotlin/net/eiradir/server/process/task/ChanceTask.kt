package net.eiradir.server.process.task

import net.eiradir.server.process.*
import java.util.*

class ChanceTask(private val chance: Float, subProcessBuilder: ProcessDefinitionBuilder.() -> Unit) : Task {

    override val taskId = UUID.randomUUID().toString()
    private val subProcess = ProcessDefinition(taskId, allowCheckpoints = false).also(subProcessBuilder)

    override fun execute(context: ProcessContext): Boolean {
        val data = context.getTaskData(taskId) { SubProcessTaskData(-1) }
        if (data.innerTaskIndex == -1) {
            if (Math.random() <= chance) {
                data.innerTaskIndex = 0
                return false
            } else {
                return true
            }
        } else {
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

    override fun clearTaskData(context: ProcessContext) {
        context.getTaskData(taskId) { SubProcessTaskData(-1) }.innerTaskIndex = -1
    }
}

fun ProcessDefinitionBuilder.chance(chance: Float, subProcess: ProcessDefinitionBuilder.() -> Unit) {
    addTask(ChanceTask(chance, subProcess))
}
