package net.eiradir.server.commands.arguments

import arrow.core.Either
import arrow.core.getOrHandle
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.commands.CommandSource
import net.eiradir.server.commands.suggestions.SuggestionSource
import java.util.concurrent.CompletableFuture

class Vector3IntArgument : ArgumentType<Coordinates> {

    override fun parse(reader: StringReader): Coordinates {
        return Coordinates.parseCoordinates(reader).getOrHandle { throw it }
    }

    override fun <S> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        return Either.fromNullable(context.source as? SuggestionSource)
            .map {
                builder.suggest("0")
                builder.buildFuture()
            }
            .getOrHandle { Suggestions.empty() }
    }

    companion object {
        fun vector3Int(): Vector3IntArgument {
            return Vector3IntArgument()
        }

        fun getVector3Int(context: CommandContext<CommandSource>, name: String): Vector3Int {
            return context.getArgument(name, Coordinates::class.java).getVector3Int(context.source)
        }
    }
}