package net.eiradir.server.process

interface Task {
    val taskId: String
    fun execute(context: ProcessContext): Boolean
    fun clearTaskData(context: ProcessContext) {
        context.clearTaskData(taskId)
    }
}