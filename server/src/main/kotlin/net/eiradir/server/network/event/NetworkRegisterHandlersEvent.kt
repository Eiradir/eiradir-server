package net.eiradir.server.network.event

import net.eiradir.server.network.NetworkContext
import net.eiradir.server.network.packets.Packet
import net.eiradir.server.network.packets.PacketFactory
import kotlin.reflect.KClass

class NetworkRegisterHandlersEvent(private val packets: PacketFactory) {
    fun <T : Packet> registerHandler(clazz: KClass<T>, handler: (NetworkContext, T) -> Unit) {
        packets.registerPacketHandler(clazz, handler)
    }
}