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
import net.eiradir.server.registry.Registry
import net.eiradir.server.registry.RegistryEntry
import java.io.DataInputStream
import java.io.FilterInputStream
import java.util.*
import kotlin.experimental.and

class SupportedDataInputStream(private val dataIn: DataInputStream, private val registries: Registries) : FilterInputStream(dataIn), SupportedInput {
    override fun readString(): String {
        return dataIn.readUTF()
    }

    override fun readUniqueId(): UUID {
        return UUID(dataIn.readLong(), dataIn.readLong())
    }

    override fun readVarInt(): Int {
        var numRead = 0
        var result = 0
        var read: Byte
        do {
            read = dataIn.readByte()
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
        return dataIn.readByte()
    }

    override fun readComponent(): Component {
        val id = dataIn.readByte().toInt()
        val component = registries.components.getById(id)?.create() ?: throw IllegalStateException("Component with id $id is not registered!")
        if (component !is PersistedComponent) throw IllegalStateException("Component ${component.javaClass.simpleName} is not a PersistedComponent!")
        component.load(this)
        return component
    }

    override fun <T : RegistryEntry<T>> readFromRegistry(registry: (Registries) -> Registry<T>): T {
        val id = dataIn.readShort().toInt()
        if (id == -1) {
            val name = this.readString()
            return registry(registries).invalid(name)
        } else {
            return registry(registries).getById(id) ?: registry(registries).invalid(id.toString())
        }
    }

    override fun readVector3(): Vector3 {
        return Vector3(dataIn.readFloat(), dataIn.readFloat(), dataIn.readFloat())
    }

    override fun readQuaternion(): Quaternion {
        return Quaternion(dataIn.readFloat(), dataIn.readFloat(), dataIn.readFloat(), dataIn.readFloat())
    }

    override fun readChunkDimensions(): ChunkDimensions {
        return ChunkDimensions(dataIn.readByte().toInt(), dataIn.readByte().toInt(), dataIn.readShort().toInt(), dataIn.readByte().toInt())
    }

    override fun readVector3Int(): Vector3Int {
        return Vector3Int(dataIn.readInt(), dataIn.readInt(), dataIn.readInt())
    }

    override fun readBoolean(): Boolean {
        return dataIn.readBoolean()
    }

    override fun readFloat(): Float {
        return dataIn.readFloat()
    }

    override fun readLong(): Long {
        return dataIn.readLong()
    }

    override fun readDouble(): Double {
        return dataIn.readDouble()
    }

    override fun readShort(): Short {
        return dataIn.readShort()
    }

    override fun readInt(): Int {
        return dataIn.readInt()
    }

    override fun readBytes(bytes: ByteArray): ByteArray {
        dataIn.readFully(bytes)
        return bytes
    }

    override fun readColor(): Color {
        return Color(dataIn.readInt())
    }

    override fun readEntity(): NetworkedEntity {
        TODO("Not yet implemented")
    }

    override fun readItemInstance(): ItemInstance {
        TODO("Not yet implemented")
    }
}