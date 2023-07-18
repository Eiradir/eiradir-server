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
import net.eiradir.server.registry.RegistryEntry
import java.util.*

interface SupportedOutput {
    val registries: Registries
    fun writeString(value: String)
    fun writeVarInt(value: Int)
    fun writeUniqueId(id: UUID)
    fun writeByte(value: Int)
    fun writeInt(value: Int)
    fun writeComponent(component: Component)
    fun writeId(registryEntry: RegistryEntry<*>?)
    fun writeVector3(vector: Vector3)
    fun writeQuaternion(quaternion: Quaternion)
    fun writeChunkDimensions(dimensions: ChunkDimensions)
    fun writeVector3Int(vector: Vector3Int)
    fun writeBytes(bytes: ByteArray)
    fun writeBytes(bytes: ByteArray, offset: Int, length: Int)
    fun writeLong(value: Long)
    fun writeShort(value: Int)
    fun writeFloat(value: Float)
    fun writeColor(value: Color)
    fun writeEntity(entity: NetworkedEntity)
    fun writeItemInstance(item: ItemInstance)
}

fun <T : Enum<T>> SupportedOutput.writeEnum(enum: T) {
    this.writeByte(enum.ordinal)
}