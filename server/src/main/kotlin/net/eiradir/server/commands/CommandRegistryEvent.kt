package net.eiradir.server.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.tree.LiteralCommandNode

data class CommandRegistryEvent(val dispatcher: CommandDispatcher<CommandSource>) {
    fun register(command: LiteralArgumentBuilder<CommandSource>): LiteralCommandNode<CommandSource> {
        return dispatcher.register(command)
    }
}