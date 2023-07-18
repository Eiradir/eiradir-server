package net.eiradir.server.process

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class ProcessSystem : IteratingSystem(allOf(ProcessComponent::class).get()) {
    private val processMapper = mapperFor<ProcessComponent>()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val processComponent = processMapper[entity] ?: return
        processComponent.passiveContexts.iterator().let {
            while (it.hasNext()) {
                val context = it.next()
                if (process(context)) {
                    it.remove()
                }
            }
        }

        processComponent.activeContext?.let {
            if (process(it)) {
                if (processComponent.activeContext == it) {
                    processComponent.activeContext = null
                }
            }
        }
    }

    private fun process(context: ProcessContext): Boolean {
        val process = context.process
        if (context.overrideTask == null && context.fromCheckpoint != null && context.checkpoint == null) {
            val checkpointIndex = process.checkpoints[context.fromCheckpoint] ?: return false
            context.taskIndex = checkpointIndex
            context.checkpoint = context.fromCheckpoint
        }
        if (context.overrideTask?.execute(context) == true) {
            context.overrideTask = null
        }
        while (context.overrideTask == null && context.taskIndex < process.tasks.size && context.state == ProcessState.Pass && process.tasks[context.taskIndex].execute(context)) {
            context.clearTaskData(process.tasks[context.taskIndex].taskId)
            context.taskIndex++
        }
        if (context.overrideTask == null && (context.state != ProcessState.Pass || context.taskIndex >= process.tasks.size)) {
            context.complete()
            return true
        }
        return false
    }
}