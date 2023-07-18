package net.eiradir.server.process.task

import net.eiradir.server.process.ProcessContext
import net.eiradir.server.process.ProcessDefinition
import net.eiradir.server.process.ProcessDefinitionBuilder
import net.eiradir.server.process.Task
import net.eiradir.server.services
import java.util.*

class ForkTask(subProcessBuilder: ProcessDefinitionBuilder.() -> Unit, private val reportToParent: Boolean, private val reportToFork: Boolean) : Task {

    override val taskId = UUID.randomUUID().toString()
    private val subProcess = ProcessDefinition(taskId, allowCheckpoints = false, transient = true).also(subProcessBuilder)

    init {
        subProcess.interrupted {}
    }

    override fun execute(context: ProcessContext): Boolean {
        val subContext = context.entity.services().processes.startPassiveProcess(context.entity, subProcess)
        if (reportToParent) {
            subContext.onFailure { context.failure() }
            subContext.onSuccess { context.success() }
            subContext.onInterrupt {
                if (it.context != context) {
                    context.interrupted(it)
                }
            }
        }
        if (reportToFork) {
            context.onFailure { subContext.failure() }
            context.onSuccess { subContext.success() }
            context.onInterrupt {
                if (it.context != subContext) {
                    subContext.interrupted(it)
                }
            }
        }
        return true
    }

}

fun ProcessDefinitionBuilder.fork(reportToParent: Boolean = true, reportToFork: Boolean = true, initializer: ProcessDefinitionBuilder.() -> Unit) {
    addTask(ForkTask(initializer, reportToParent, reportToFork))
}