package net.eiradir.server.commands.arguments

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector3
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import net.eiradir.server.commands.CommandSource
import java.util.*

class EntityArgument : ArgumentType<EntityProvider> {

    override fun parse(reader: StringReader): EntityProvider {
        when (val value = reader.readUnquotedString()) {
            "-c" -> return EntityProvider.ByCursorPosition
            "-s" -> return EntityProvider.BySelection
            else -> return EntityProvider.ByEntityId(UUID.fromString(value))
        }
    }

    companion object {
        fun entity(): EntityArgument {
            return EntityArgument()
        }

        fun getEntity(context: CommandContext<CommandSource>, name: String): Entity? {
            return context.getArgument(name, EntityProvider::class.java).get(context)
        }
    }
}