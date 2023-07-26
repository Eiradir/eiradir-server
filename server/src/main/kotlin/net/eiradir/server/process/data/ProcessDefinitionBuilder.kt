package net.eiradir.server.process.data

import net.eiradir.server.process.Task

interface ProcessDefinitionBuilder {
    fun addTask(task: Task)
}