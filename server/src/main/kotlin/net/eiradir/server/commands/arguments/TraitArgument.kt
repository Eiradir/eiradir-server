package net.eiradir.server.commands.arguments

import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import net.eiradir.server.commands.CommandSource
import net.eiradir.server.registry.Registries
import net.eiradir.server.trait.Trait

class TraitArgument(private val registries: Registries) : ArgumentType<Trait> {
    override fun parse(reader: StringReader): Trait {
        val processName = reader.readUnquotedString()
        return registries.traits.getByName(processName) ?: throw ERROR_UNKNOWN_TRAIT.create(processName)
    }

    companion object {
        private val ERROR_UNKNOWN_TRAIT = DynamicCommandExceptionType { value: Any? -> LiteralMessage("Unknown trait '$value'") }

        fun trait(registries: Registries): TraitArgument {
            return TraitArgument(registries)
        }

        fun getTrait(context: CommandContext<CommandSource>, name: String): Trait {
            return context.getArgument(name, Trait::class.java)
        }
    }
}