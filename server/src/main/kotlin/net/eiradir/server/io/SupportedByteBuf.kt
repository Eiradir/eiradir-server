package net.eiradir.server.io

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import io.netty.buffer.ByteBuf
import net.eiradir.server.entity.NetworkedEntity
import net.eiradir.server.entity.AdvancedEncoders
import net.eiradir.server.entity.components.PersistedComponent
import net.eiradir.server.item.ItemInstance
import net.eiradir.server.map.ChunkDimensions
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.registry.Registries
import net.eiradir.server.registry.Registry
import net.eiradir.server.registry.RegistryEntry
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.experimental.and

class SupportedByteBuf(private val buf: ByteBuf, override val registries: Registries, private val advancedEncoders: AdvancedEncoders) :
    SupportedInput, SupportedOutput {
    override fun readString(): String {
        val length = this.readVarInt()
        val buffer = ByteArray(length)
        buf.readBytes(buffer)
        return String(buffer, StandardCharsets.UTF_8)
    }

    override fun readUniqueId(): UUID {
        return UUID(buf.readLong(), buf.readLong())
    }

    override fun readVarInt(): Int {
        var numRead = 0
        var result = 0
        var read: Byte
        do {
            read = buf.readByte()
            val value = read and 0b01111111
            result = result or (value.toInt() shl (7 * numRead))

            numRead++
            if (numRead > 5) {
                throw RuntimeException("VarInt is too big")
            }
        } while ((read and 0b10000000.toByte()) != 0.toByte())
        return result
    }

    override fun readByte(): Byte {
        return buf.readByte()
    }

    override fun readComponent(): Component {
        val id = buf.readByte().toInt()
        val component = registries.components.getById(id)?.create() ?: throw IllegalStateException("Component with id $id is not registered!")
        if (component !is PersistedComponent) throw IllegalStateException("Component ${component.javaClass.simpleName} is not a PersistedComponent!")
        component.load(this)
        return component
    }

    override fun <T : RegistryEntry<T>> readFromRegistry(registry: (Registries) -> Registry<T>): T {
        val id = buf.readShort().toInt()
        if (id == -1) {
            val name = this.readString()
            return registry(registries).invalid(name)
        } else {
            return registry(registries).getById(id) ?: registry(registries).invalid(id.toString())
        }
    }

    override fun readVector3(): Vector3 {
        val x = buf.readFloat()
        val y = buf.readFloat()
        val z = buf.readFloat()
        return Vector3(x, y, z)
    }

    override fun readQuaternion(): Quaternion {
        val x = buf.readFloat()
        val y = buf.readFloat()
        val z = buf.readFloat()
        val w = buf.readFloat()
        return Quaternion(x, y, z, w)
    }

    override fun readChunkDimensions(): ChunkDimensions {
        val x = buf.readByte().toInt()
        val y = buf.readByte().toInt()
        val level = buf.readShort().toInt()
        val size = buf.readByte().toInt()
        return ChunkDimensions(x, y, level, size)
    }

    override fun readVector3Int(): Vector3Int {
        val x = buf.readInt()
        val y = buf.readInt()
        val level = buf.readInt()
        return Vector3Int(x, y, level)
    }

    override fun readBoolean(): Boolean {
        return buf.readBoolean()
    }

    override fun readFloat(): Float {
        return buf.readFloat()
    }

    override fun readLong(): Long {
        return buf.readLong()
    }

    override fun readDouble(): Double {
        return buf.readDouble()
    }

    override fun readShort(): Short {
        return buf.readShort()
    }

    override fun readInt(): Int {
        return buf.readInt()
    }

    override fun readBytes(bytes: ByteArray): ByteArray {
        buf.readBytes(bytes)
        return bytes
    }

    override fun writeString(value: String) {
        val bytes = value.toByteArray(StandardCharsets.UTF_8)
        this.writeVarInt(bytes.size)
        buf.writeBytes(bytes)
    }

    override fun writeVarInt(value: Int) {
        var rest = value
        while (true) {
            if (rest and -0x80 == 0) {
                buf.writeByte(rest)
                return
            }

            buf.writeByte(rest and 0x7F or 0x80)
            rest = rest ushr 7
        }
    }

    override fun writeUniqueId(id: UUID) {
        buf.writeLong(id.mostSignificantBits)
        buf.writeLong(id.leastSignificantBits)
    }

    override fun writeByte(value: Int) {
        buf.writeByte(value)
    }

    override fun writeInt(value: Int) {
        buf.writeInt(value)
    }

    override fun writeComponent(component: Component) {
        component as? PersistedComponent ?: throw IllegalArgumentException("Component $component is not a PersistedComponent")
        val id = registries.components.getByName(component.serializedName)?.id(registries)
            ?: throw IllegalStateException("Component ${component.serializedName} is not registered!")
        buf.writeByte(id)
        component.save(this)
    }

    override fun writeId(registryEntry: RegistryEntry<*>?) {
        if (registryEntry == null) {
            this.writeShort(0)
        } else {
            val id = registryEntry.id(registries)
            if (id != null) {
                this.writeShort(id)
            } else {
                this.writeShort(-1)
                this.writeString(registryEntry.name)
            }
        }
    }

    override fun writeVector3(vector: Vector3) {
        buf.writeFloat(vector.x)
        buf.writeFloat(vector.y)
        buf.writeFloat(vector.z)
    }

    override fun writeQuaternion(quaternion: Quaternion) {
        buf.writeFloat(quaternion.x)
        buf.writeFloat(quaternion.y)
        buf.writeFloat(quaternion.z)
        buf.writeFloat(quaternion.w)
    }

    override fun writeChunkDimensions(dimensions: ChunkDimensions) {
        buf.writeByte(dimensions.x)
        buf.writeByte(dimensions.y)
        buf.writeShort(dimensions.level)
        buf.writeByte(dimensions.size)
    }

    override fun writeVector3Int(vector: Vector3Int) {
        buf.writeInt(vector.x)
        buf.writeInt(vector.y)
        buf.writeInt(vector.level)
    }

    override fun writeBytes(bytes: ByteArray) {
        buf.writeBytes(bytes)
    }

    override fun writeBytes(bytes: ByteArray, offset: Int, length: Int) {
        buf.writeBytes(bytes, offset, length)
    }

    override fun writeLong(value: Long) {
        buf.writeLong(value)
    }

    override fun writeShort(value: Int) {
        buf.writeShort(value)
    }

    override fun writeFloat(value: Float) {
        buf.writeFloat(value)
    }

    override fun readColor(): Color {
        return Color(buf.readInt())
    }

    override fun writeColor(value: Color) {
        val rgba = ((255 * value.r).toInt() shl 24) or ((255 * value.g).toInt() shl 16) or ((255 * value.b).toInt() shl 8) or ((255 * value.a).toInt())
        buf.writeInt(rgba)
    }

    override fun readEntity(): NetworkedEntity {
        return advancedEncoders.decodeEntity(this)
    }

    override fun writeEntity(entity: NetworkedEntity) {
        advancedEncoders.encodeEntity(this, entity)
    }

    override fun readItemInstance(): ItemInstance {
        return advancedEncoders.decodeItemInstance(this)
    }

    override fun writeItemInstance(item: ItemInstance) {
        advancedEncoders.encodeItemInstance(this, item)
    }
}