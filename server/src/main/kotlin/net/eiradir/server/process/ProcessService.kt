package net.eiradir.server.process

import com.badlogic.ashley.core.Entity
import ktx.ashley.mapperFor
import net.eiradir.server.process.data.ProcessDefinition
import net.eiradir.server.process.entity.ProcessComponent

class ProcessService {
    private val processMapper = mapperFor<ProcessComponent>()

    fun startProcess(entity: Entity, process: ProcessDefinition, processComponent: ProcessComponent? = processMapper[entity]): ProcessContext {
        val component = processComponent ?: ProcessComponent().also(entity::add)
        component.activeContext?.failure()
        val context = ProcessContext(entity, process, null)
        component.activeContext = context
        return context
    }

    fun startPassiveProcess(entity: Entity, process: ProcessDefinition, processComponent: ProcessComponent? = processMapper[entity]): ProcessContext {
        val component = processComponent ?: ProcessComponent().also(entity::add)
        val context = ProcessContext(entity, process, null)
        component.passiveContexts.add(context)
        return context
    }
}