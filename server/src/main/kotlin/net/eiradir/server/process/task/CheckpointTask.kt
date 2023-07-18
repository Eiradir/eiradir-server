package net.eiradir.server.process.task

import net.eiradir.server.process.ProcessContext
import net.eiradir.server.process.Task

class CheckpointTask(override val taskId: String) : Task {
    override fun execute(context: ProcessContext): Boolean {
        context.checkpoint = taskId
        return true
    }
}