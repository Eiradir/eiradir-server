package net.eiradir.server.process.task

import net.eiradir.server.process.Interruption
import net.eiradir.server.process.ProcessContext
import net.eiradir.server.process.ProcessDefinitionBuilder
import net.eiradir.server.process.Task
import java.util.*

class InterruptTask : Task {
    override val taskId = UUID.randomUUID().toString()
    override fun execute(context: ProcessContext): Boolean {
        context.interrupted(Interruption(context))
        return true
    }

}

fun ProcessDefinitionBuilder.interrupt() {
    addTask(InterruptTask())
}

fun ProcessDefinitionBuilder.interruptible(options: ProcessDefinitionBuilder.() -> Unit) {
    initialize {
        fork {
            repeat {
                addTask(EitherTask(options))
                addTask(InterruptTask())
            }
        }
    }
}