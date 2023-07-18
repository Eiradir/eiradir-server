package net.eiradir.server.commands

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder

fun literal(name: String): LiteralArgumentBuilder<CommandSource> {
    return LiteralArgumentBuilder.literal(name)
}

fun <T> argument(name: String, type: ArgumentType<T>): RequiredArgumentBuilder<CommandSource, T> {
    return RequiredArgumentBuilder.argument(name, type)
}