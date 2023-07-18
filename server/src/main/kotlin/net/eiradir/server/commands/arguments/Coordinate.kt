package net.eiradir.server.commands.arguments

import arrow.core.Either
import com.mojang.brigadier.StringReader
import net.eiradir.server.commands.CommandSource
import net.eiradir.server.commands.remains
import net.eiradir.server.commands.skipIf

class Coordinate(val relative: Boolean, val value: Int) {

    fun getValue(source: CommandSource): Int {
        return value // TODO support relative coordinates using ~ prefix
    }

    companion object {
        fun parseCoordinate(reader: StringReader): Either<Throwable, Coordinate> {
            return Either.catch {
                val relative = reader.skipIf('~')
                val value = if (reader.remains()) reader.readInt() else 0
                Coordinate(relative, value)
            }
        }
    }
}