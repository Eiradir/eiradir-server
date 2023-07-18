package net.eiradir.server.process.task

import net.eiradir.server.process.ProcessContext
import net.eiradir.server.process.ProcessDefinitionBuilder
import net.eiradir.server.process.Task
import java.util.*


class ScriptTask(private val script: (ProcessContext) -> Boolean) : Task {
    override val taskId = UUID.randomUUID().toString()
    override fun execute(context: ProcessContext): Boolean {
        return script(context)
    }
}

fun ProcessDefinitionBuilder.script(script: (ProcessContext) -> Boolean) {
    addTask(ScriptTask(script))
}