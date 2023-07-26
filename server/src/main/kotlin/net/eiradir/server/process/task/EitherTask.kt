package net.eiradir.server.process.task

import net.eiradir.server.process.ProcessContext
import net.eiradir.server.process.data.ProcessDefinitionBuilder
import net.eiradir.server.process.Task
import java.util.*

class EitherTask(options: ProcessDefinitionBuilder.() -> Unit) : Task, ProcessDefinitionBuilder {

    override val taskId = UUID.randomUUID().toString()
    private val tasks = mutableListOf<Task>()

    init {
        options()
    }

    override fun addTask(task: Task) {
        tasks.add(task)
    }

    override fun execute(context: ProcessContext): Boolean {
        for (task in tasks) {
            if (task.execute(context)) {
                task.clearTaskData(context)
                return true
            }
        }
        return false
    }
}

fun ProcessDefinitionBuilder.either(options: ProcessDefinitionBuilder.() -> Unit) {
    addTask(EitherTask(options))
}