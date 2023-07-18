package net.eiradir.server.menu

import net.eiradir.server.network.packets.PacketFactory
import net.eiradir.server.plugin.Initializer

class MenuPackets(packetFactory: PacketFactory) : Initializer {
    init {
        packetFactory.registerPacket(MenuPacket::class, MenuPacket::encode, MenuPacket::decode)
    }
}