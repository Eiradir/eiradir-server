package net.eiradir.server.process.task

import net.eiradir.server.process.ProcessContext
import net.eiradir.server.process.data.ProcessDefinitionBuilder
import net.eiradir.server.process.Task
import java.util.*

class SuccessTask : Task {
    override val taskId = UUID.randomUUID().toString()
    override fun execute(context: ProcessContext): Boolean {
        context.success()
        return true
    }
}

fun ProcessDefinitionBuilder.success() {
    addTask(SuccessTask())
}
