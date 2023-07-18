package net.eiradir.server.commands

import arrow.core.Either
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType

fun StringReader.remains(): Boolean {
    return canRead() && peek() != ' '
}

fun StringReader.skipIf(char: Char): Boolean {
    if (peek() == char) {
        skip()
        return true
    }
    return false
}

fun StringReader.expectOr(char: Char, supplier: () -> SimpleCommandExceptionType): Either<Throwable, Unit> {
    return Either.conditionally(canRead() && peek() == char, supplier) { skip() }
        .mapLeft { it.createWithContext(this) }
}

fun StringReader.expectOrThrow(char: Char, supplier: () -> SimpleCommandExceptionType) {
    if (!canRead() || peek() != char) {
        throw supplier().createWithContext(this)
    }
    skip()
}