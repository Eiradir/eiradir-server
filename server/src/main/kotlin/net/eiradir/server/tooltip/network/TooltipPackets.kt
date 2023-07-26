package net.eiradir.server.tooltip.network

import net.eiradir.server.network.packets.PacketFactory
import net.eiradir.server.plugin.Initializer

class TooltipPackets(packetFactory: PacketFactory) : Initializer {
    init {
        packetFactory.registerPacket(TooltipPacket::class, TooltipPacket.Companion::encode, TooltipPacket.Companion::decode)
    }
}