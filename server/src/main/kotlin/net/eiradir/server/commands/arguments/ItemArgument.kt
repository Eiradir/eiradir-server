package net.eiradir.server.commands.arguments

import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import net.eiradir.server.data.Tile
import net.eiradir.server.commands.CommandSource
import net.eiradir.server.data.Item
import net.eiradir.server.registry.Registries

class ItemArgument(private val registries: Registries) : ArgumentType<Item> {
    override fun parse(reader: StringReader): Item {
        val itemName = reader.readUnquotedString()
        return registries.items.getByName(itemName) ?: throw ERROR_UNKNOWN_ITEM.create(itemName)
    }

    companion object {
        private val ERROR_UNKNOWN_ITEM = DynamicCommandExceptionType { value: Any? -> LiteralMessage("Unknown item '$value'") }

        fun item(registries: Registries): ItemArgument {
            return ItemArgument(registries)
        }

        fun getItem(context: CommandContext<CommandSource>, name: String): Item {
            return context.getArgument(name, Item::class.java)
        }
    }
}