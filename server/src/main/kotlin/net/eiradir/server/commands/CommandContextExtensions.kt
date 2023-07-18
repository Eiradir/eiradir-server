package net.eiradir.server.commands

import com.badlogic.ashley.core.Entity
import com.mojang.brigadier.context.CommandContext
import ktx.ashley.get
import ktx.ashley.mapperFor
import net.eiradir.server.commands.CommandSource
import net.eiradir.server.entity.ControlledEntity
import net.eiradir.server.network.ServerNetworkContext

fun CommandContext<CommandSource>.client(): ServerNetworkContext? {
    return (source as? ClientCommandSource)?.client
}

fun CommandContext<CommandSource>.clientEntity(): Entity? {
    return (source as? ClientCommandSource)?.client?.connectionEntity
}

val controlledEntityMapper = mapperFor<ControlledEntity>()

fun CommandContext<CommandSource>.controlledEntity(): Entity? {
    return clientEntity()?.let { controlledEntityMapper[it] }?.controlledEntity
}