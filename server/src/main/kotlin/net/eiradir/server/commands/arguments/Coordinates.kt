package net.eiradir.server.commands.arguments

import arrow.core.Either
import arrow.core.continuations.either
import com.badlogic.gdx.math.Vector3
import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import kotlinx.coroutines.runBlocking
import net.eiradir.server.extensions.floorToIntVector
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.commands.CommandSource
import net.eiradir.server.commands.expectOr

interface Coordinates {
    companion object {
        private val ERROR_UNKNOWN_FLAG = SimpleCommandExceptionType(LiteralMessage("Expected -c flag"))

        fun parseCoordinates(reader: StringReader): Either<Throwable, Coordinates> = runBlocking {
            either {
                val dash = reader.peek()
                if (dash == '-') {
                    reader.skip()
                    parseFlagCoordinates(reader).bind()
                } else {
                    WorldCoordinates.parseWorldCoordinates(reader).bind()
                }
            }
        }

        private fun parseFlagCoordinates(reader: StringReader): Either<Throwable, Coordinates> {
            return Either.catch {
                when (reader.readString()) {
                    "c" -> CursorCoordinates
                    else -> throw ERROR_UNKNOWN_FLAG.createWithContext(reader)
                }
            }
        }
    }

    fun getVector3Int(source: CommandSource): Vector3Int
}

object CursorCoordinates : Coordinates {
    override fun getVector3Int(source: CommandSource): Vector3Int {
        return source.cursorPosition
    }
}

class WorldCoordinates(val x: Coordinate, val y: Coordinate, val z: Coordinate) : Coordinates {
    override fun getVector3Int(source: CommandSource): Vector3Int {
        return Vector3Int(
            x.getValue(source),
            y.getValue(source),
            z.getValue(source)
        )
    }

    companion object {
        private val ERROR_INCOMPLETE = SimpleCommandExceptionType(LiteralMessage("Expected x y z coordinates"))

        fun parseWorldCoordinates(reader: StringReader): Either<Throwable, WorldCoordinates> = runBlocking {
            either {
                val x = Coordinate.parseCoordinate(reader).bind()
                reader.expectOr(' ', Companion::ERROR_INCOMPLETE).bind()
                val y = Coordinate.parseCoordinate(reader).bind()
                reader.expectOr(' ', Companion::ERROR_INCOMPLETE).bind()
                val z = Coordinate.parseCoordinate(reader).bind()
                WorldCoordinates(x, y, z)
            }
        }
    }
}