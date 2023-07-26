package net.eiradir.server.process.task

import net.eiradir.server.process.ProcessContext
import net.eiradir.server.process.data.ProcessDefinitionBuilder
import net.eiradir.server.process.Task
import java.util.*

class PickTask(private val options: List<Pair<Task, Int>>) : Task {

    data class PickData(var pickedIndex: Int = -1)

    override val taskId = UUID.randomUUID().toString()

    override fun execute(context: ProcessContext): Boolean {
        val data = context.getTaskData(taskId, ::PickData)
        if (data.pickedIndex == -1) {
            // TODO weighted pick
            data.pickedIndex = options.indices.random()
            return false
        } else {
            val task = options[data.pickedIndex].first
            if (task.execute(context)) {
                task.clearTaskData(context)
                return true
            } else {
                return false
            }
        }
    }

    override fun clearTaskData(context: ProcessContext) {
        context.getTaskData(taskId, ::PickData).pickedIndex = -1
    }
}

fun ProcessDefinitionBuilder.pick(vararg options: Pair<Task, Int>) {
    addTask(PickTask(options.toList()))
}

fun ProcessDefinitionBuilder.pick(vararg options: Task) {
    addTask(PickTask(options.map { it to 1 }))
}