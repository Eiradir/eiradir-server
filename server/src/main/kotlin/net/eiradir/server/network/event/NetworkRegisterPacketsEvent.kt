package net.eiradir.server.network.event

import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.network.packets.Packet
import net.eiradir.server.network.packets.PacketFactory
import kotlin.reflect.KClass

class NetworkRegisterPacketsEvent(private val packets: PacketFactory) {
    fun <T : Packet> registerPacket(clazz: KClass<out T>, encoder: (SupportedOutput, T) -> Unit, decoder: (SupportedInput) -> T) {
        packets.registerPacket(clazz, encoder, decoder)
    }
}