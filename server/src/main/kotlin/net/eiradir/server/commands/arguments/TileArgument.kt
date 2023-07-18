package net.eiradir.server.commands.arguments

import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import net.eiradir.server.data.Tile
import net.eiradir.server.data.TileRegistry
import net.eiradir.server.commands.CommandSource
import net.eiradir.server.registry.Registries

class TileArgument(private val registries: Registries) : ArgumentType<Tile> {
    override fun parse(reader: StringReader): Tile {
        val tileName = reader.readUnquotedString()
        return registries.tiles.getByName(tileName) ?: throw ERROR_UNKNOWN_TILE.create(tileName)
    }

    companion object {
        private val ERROR_UNKNOWN_TILE = DynamicCommandExceptionType { value: Any? -> LiteralMessage("Unknown tile '$value'") }

        fun tile(registries: Registries): TileArgument {
            return TileArgument(registries)
        }

        fun getTile(context: CommandContext<CommandSource>, name: String): Tile {
            return context.getArgument(name, Tile::class.java)
        }
    }
}