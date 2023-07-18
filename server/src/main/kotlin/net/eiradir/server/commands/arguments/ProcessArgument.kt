package net.eiradir.server.commands.arguments

import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import net.eiradir.server.commands.CommandSource
import net.eiradir.server.process.registry.ProcessType
import net.eiradir.server.registry.Registries

class ProcessArgument(private val registries: Registries) : ArgumentType<ProcessType> {
    override fun parse(reader: StringReader): ProcessType {
        val processName = reader.readUnquotedString()
        return registries.processes.getByName(processName) ?: throw ERROR_UNKNOWN_PROCESS.create(processName)
    }

    companion object {
        private val ERROR_UNKNOWN_PROCESS = DynamicCommandExceptionType { value: Any? -> LiteralMessage("Unknown process '$value'") }

        fun process(registries: Registries): ProcessArgument {
            return ProcessArgument(registries)
        }

        fun getProcess(context: CommandContext<CommandSource>, name: String): ProcessType {
            return context.getArgument(name, ProcessType::class.java)
        }
    }
}