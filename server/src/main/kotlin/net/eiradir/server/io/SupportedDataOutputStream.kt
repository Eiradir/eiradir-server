package net.eiradir.server.io

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import net.eiradir.server.entity.NetworkedEntity
import net.eiradir.server.entity.components.PersistedComponent
import net.eiradir.server.item.ItemInstance
import net.eiradir.server.map.ChunkDimensions
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.registry.Registries
import net.eiradir.server.registry.RegistryEntry
import java.io.DataOutputStream
import java.io.FilterOutputStream
import java.util.*

class SupportedDataOutputStream(private val dataOut: DataOutputStream, override val registries: Registries) : FilterOutputStream(dataOut), SupportedOutput {
    override fun writeString(value: String) {
        dataOut.writeUTF(value)
    }

    override fun writeVarInt(value: Int) {
        var rest = value
        while (true) {
            if (rest and -0x80 == 0) {
                dataOut.writeByte(rest)
                return
            }

            this.writeByte(rest and 0x7F or 0x80)
            rest = rest ushr 7
        }
    }

    override fun writeUniqueId(id: UUID) {
        dataOut.writeLong(id.mostSignificantBits)
        dataOut.writeLong(id.leastSignificantBits)
    }

    override fun writeByte(value: Int) {
        dataOut.writeByte(value)
    }

    override fun writeInt(value: Int) {
        dataOut.writeInt(value)
    }

    override fun writeComponent(component: Component) {
        component as? PersistedComponent ?: throw IllegalArgumentException("Component $component is not a PersistedComponent")
        val id = registries.components.getByName(component.serializedName)?.id(registries)
            ?: throw IllegalStateException("Component ${component.serializedName} is not registered!")
        dataOut.writeByte(id)
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
        dataOut.writeFloat(vector.x)
        dataOut.writeFloat(vector.y)
        dataOut.writeFloat(vector.z)
    }

    override fun writeQuaternion(quaternion: Quaternion) {
        dataOut.writeFloat(quaternion.x)
        dataOut.writeFloat(quaternion.y)
        dataOut.writeFloat(quaternion.z)
        dataOut.writeFloat(quaternion.w)
    }

    override fun writeChunkDimensions(dimensions: ChunkDimensions) {
        dataOut.writeByte(dimensions.x)
        dataOut.writeByte(dimensions.y)
        dataOut.writeShort(dimensions.level)
        dataOut.writeByte(dimensions.size)
    }

    override fun writeVector3Int(vector: Vector3Int) {
        dataOut.writeInt(vector.x)
        dataOut.writeInt(vector.y)
        dataOut.writeInt(vector.level)
    }

    override fun writeBytes(bytes: ByteArray) {
        dataOut.write(bytes)
    }

    override fun writeBytes(bytes: ByteArray, offset: Int, length: Int) {
        dataOut.write(bytes, offset, length)
    }

    override fun writeLong(value: Long) {
        dataOut.writeLong(value)
    }

    override fun writeShort(value: Int) {
        dataOut.writeShort(value)
    }

    override fun writeFloat(value: Float) {
        dataOut.writeFloat(value)
    }

    override fun writeColor(value: Color) {
        val rgba = ((255 * value.r).toInt() shl 24) or ((255 * value.g).toInt() shl 16) or ((255 * value.b).toInt() shl 8) or ((255 * value.a).toInt())
        dataOut.writeInt(rgba)
    }

    override fun writeEntity(entity: NetworkedEntity) {
        TODO("Not yet implemented")
    }

    override fun writeItemInstance(item: ItemInstance) {
        TODO("Not yet implemented")
    }
}