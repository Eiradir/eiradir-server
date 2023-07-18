package net.eiradir.server.network.packets

import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.network.NetworkContext
import net.eiradir.server.registry.IdResolver
import java.io.IOException
import kotlin.reflect.KClass

class PacketFactory(private val idResolver: IdResolver) {
    private val packetDecoders = mutableMapOf<Int, (SupportedInput) -> Packet>()
    private val packetEncoders = mutableMapOf<Int, (SupportedOutput, Packet) -> Unit>()
    private val packetHandlers = mutableMapOf<Int, (NetworkContext, Packet) -> Unit>()
    private val packetToId = mutableMapOf<KClass<out Packet>, Int>()

    private fun computePacketId(clazz: KClass<out Packet>): Byte {
        val name = clazz.simpleName?.substringBeforeLast("Packet") ?: throw IllegalArgumentException("Packet class name is null")
        return idResolver.resolve("packets", name)?.toByte() ?: throw IllegalArgumentException("Packet $name has no id mapping")
    }

    fun <T : Packet> registerPacket(clazz: KClass<out T>, encoder: (SupportedOutput, T) -> Unit, decoder: (SupportedInput) -> T) {
        val packetId = computePacketId(clazz).toInt()
        check(!packetDecoders.containsKey(packetId)) {
            "Could not register $clazz: packet id $packetId is already occupied by ${packetDecoders[packetId]}"
        }

        packetDecoders[packetId] = decoder
        @Suppress("UNCHECKED_CAST")
        packetEncoders[packetId] = encoder as (SupportedOutput, Packet) -> Unit
        packetToId[clazz] = packetId
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Packet> registerPacketHandler(clazz: KClass<T>, handler: (NetworkContext, T) -> Unit) {
        val id = packetToId[clazz] ?: throw IllegalStateException("Packet $clazz has not been registered.")
        packetHandlers[id] = handler as ((NetworkContext, Packet) -> Unit)
    }

    private fun getIdForPacket(packet: Packet): Int {
        return packetToId[packet::class] ?: throw IllegalStateException("Packet $packet has not been registered.")
    }

    fun getPacketHandler(packet: Packet): ((NetworkContext, Packet) -> Unit) {
        val id = getIdForPacket(packet)
        return packetHandlers[id] ?: throw IllegalStateException("No packet handler registered for packet $packet")
    }

    fun readPacket(id: Int, buf: SupportedInput): Packet? {
        return packetDecoders[id]?.invoke(buf)
    }

    fun writePacket(buf: SupportedOutput, msg: Packet) {
        val packetId = getIdForPacket(msg)
        if (packetId == -1) {
            throw IOException("Packet type $msg is not registered in the packet factory.")
        }

        buf.writeByte(packetId)
        packetEncoders[packetId]?.invoke(buf, msg)
    }
}
