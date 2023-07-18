package net.eiradir.server.process

import com.mojang.brigadier.CommandDispatcher
import net.eiradir.server.commands.*
import net.eiradir.server.commands.arguments.ProcessArgument
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.registry.Registries

class ProcessCommands(
    private val registries: Registries,
    private val dispatcher: CommandDispatcher<CommandSource>,
    private val processService: ProcessService
) : Initializer {
    init {
        dispatcher.register(
            literal("process").then(
                literal("start").then(
                    argument("process", ProcessArgument.process(registries)).executes {
                        val targetEntity = it.controlledEntity() ?: return@executes 0
                        val processType = ProcessArgument.getProcess(it, "process")
                        processService.startPassiveProcess(targetEntity, processType.process)
                        it.source.respond("Process ${processType.name} started")
                        1
                    }.then(literal("active").executes {
                        val targetEntity = it.controlledEntity() ?: return@executes 0
                        val processType = ProcessArgument.getProcess(it, "process")
                        processService.startProcess(targetEntity, processType.process)
                        it.source.respond("Process ${processType.name} started actively")
                        1
                    }))
            )
        )
    }
}