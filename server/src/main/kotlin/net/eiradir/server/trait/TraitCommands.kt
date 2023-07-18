package net.eiradir.server.trait

import com.mojang.brigadier.CommandDispatcher
import net.eiradir.server.commands.*
import net.eiradir.server.commands.arguments.ProcessArgument
import net.eiradir.server.commands.arguments.TraitArgument
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.registry.Registries

class TraitCommands(
    private val registries: Registries,
    private val dispatcher: CommandDispatcher<CommandSource>,
    private val traitsService: TraitsService
) : Initializer {
    init {
        dispatcher.register(
            literal("trait").then(
                literal("add").then(
                    argument("trait", TraitArgument.trait(registries)).executes {
                        val targetEntity = it.controlledEntity() ?: return@executes 0
                        val trait = TraitArgument.getTrait(it, "trait")
                        val traitInstance = TraitInstance(trait, emptyMap(), false)
                        traitsService.addTrait(targetEntity, traitInstance)
                        it.source.respond("Trait ${trait.name} added")
                        1
                    })
            ).then(
                literal("remove").then(
                    argument("trait", TraitArgument.trait(registries)).executes {
                        val targetEntity = it.controlledEntity() ?: return@executes 0
                        val trait = TraitArgument.getTrait(it, "trait")
                        traitsService.removeTraitByType(targetEntity, trait)
                        it.source.respond("Trait ${trait.name} removed")
                        1
                    })
            )
        )
    }
}