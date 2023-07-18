package net.eiradir.server.tooltip

import net.eiradir.server.network.packets.PacketFactory
import net.eiradir.server.plugin.Initializer

class TooltipPackets(packetFactory: PacketFactory) : Initializer {
    init {
        packetFactory.registerPacket(TooltipPacket::class, TooltipPacket::encode, TooltipPacket::decode)
    }
}