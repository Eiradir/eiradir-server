package net.eiradir.server.process.task

import net.eiradir.server.process.ProcessContext
import net.eiradir.server.process.data.ProcessDefinitionBuilder
import net.eiradir.server.process.Task
import java.util.*


class TimedTask(override val taskId: String, private val initializer: Initializer.(ProcessContext) -> Unit) : Task {

    data class Initializer(var name: String = "", var duration: Int = 1000)
    data class TimedData(var finishedAt: Long = -1L)

    override fun execute(context: ProcessContext): Boolean {
        val data = context.getTaskData(taskId, ::TimedData)
        if (data.finishedAt == -1L) {
            val initData = Initializer()
            initializer(initData, context)
            // TODO do the thingy for the action bar on the client
            data.finishedAt = System.currentTimeMillis() + initData.duration
        } else if (System.currentTimeMillis() > data.finishedAt) {
            return true
        }
        return false
    }

    override fun clearTaskData(context: ProcessContext) {
        context.getTaskData(taskId, ::TimedData).finishedAt = -1
    }
}

fun ProcessDefinitionBuilder.timed(taskId: String = UUID.randomUUID().toString(), initializer: TimedTask.Initializer.(ProcessContext) -> Unit) {
    addTask(TimedTask(taskId, initializer))
}
