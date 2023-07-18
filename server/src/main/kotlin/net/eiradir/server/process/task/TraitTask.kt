package net.eiradir.server.process.task

import net.eiradir.server.process.ProcessContext
import net.eiradir.server.process.ProcessDefinitionBuilder
import net.eiradir.server.process.Task
import net.eiradir.server.services
import net.eiradir.server.trait.Trait
import net.eiradir.server.trait.TraitInstance
import java.util.*


class TraitTask(private val trait: Trait, private val permanent: Boolean) : Task {
    override val taskId = UUID.randomUUID().toString()
    override fun execute(context: ProcessContext): Boolean {
        val instance = TraitInstance(trait, emptyMap(), permanent)
        context.entity.services().traits.addTrait(context.entity, instance)
        if (!permanent) {
            context.onSuccess {
                context.entity.services().traits.removeTrait(context.entity, instance)
            }
            context.onFailure {
                context.entity.services().traits.removeTrait(context.entity, instance)
            }
        }
        return true
    }
}

fun ProcessDefinitionBuilder.trait(trait: Trait) {
    addTask(TraitTask(trait, false))
}

fun ProcessDefinitionBuilder.permanentTrait(trait: Trait) {
    addTask(TraitTask(trait, true))
}