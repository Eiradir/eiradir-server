package net.eiradir.server.process.task

import net.eiradir.server.process.ProcessContext
import net.eiradir.server.process.data.ProcessDefinitionBuilder
import net.eiradir.server.process.Task
import java.util.*


class FailureTask : Task {
    override val taskId = UUID.randomUUID().toString()
    override fun execute(context: ProcessContext): Boolean {
        context.failure()
        return true
    }
}

fun ProcessDefinitionBuilder.failure() {
    addTask(FailureTask())
}
