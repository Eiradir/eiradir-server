package net.eiradir.server.process.task

import net.eiradir.server.process.ProcessContext
import net.eiradir.server.process.ProcessDefinitionBuilder
import net.eiradir.server.process.Task
import java.util.*


class InformTask(private val message: String) : Task {
    override val taskId = UUID.randomUUID().toString()
    override fun execute(context: ProcessContext): Boolean {
        // TODO send inform
        println(message)
        return true
    }
}

fun ProcessDefinitionBuilder.inform(message: String) {
    addTask(InformTask(message))
}

fun ProcessContext.inform(message: String) {
    InformTask(message).execute(this)
}