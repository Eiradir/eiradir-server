package net.eiradir.server.io

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import net.eiradir.server.entity.NetworkedEntity
import net.eiradir.server.item.ItemInstance
import net.eiradir.server.map.ChunkDimensions
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.registry.Registries
import net.eiradir.server.registry.Registry
import net.eiradir.server.registry.RegistryEntry
import java.util.*

interface SupportedInput {
    fun readString(): String
    fun readUniqueId(): UUID
    fun readVarInt(): Int
    fun readByte(): Byte
    fun readComponent(): Component
    fun <T : RegistryEntry<T>> readFromRegistry(registry: (Registries) -> Registry<T>): T
    fun readVector3(): Vector3
    fun readQuaternion(): Quaternion
    fun readChunkDimensions(): ChunkDimensions
    fun readVector3Int(): Vector3Int
    fun readBoolean(): Boolean
    fun readFloat(): Float
    fun readLong(): Long
    fun readDouble(): Double
    fun readShort(): Short
    fun readInt(): Int
    fun readBytes(bytes: ByteArray): ByteArray
    fun readColor(): Color
    fun readEntity(): NetworkedEntity
    fun readItemInstance(): ItemInstance
}

inline fun <reified T> SupportedInput.readEnum(): T {
    return T::class.java.enumConstants[this.readByte().toInt()]
}