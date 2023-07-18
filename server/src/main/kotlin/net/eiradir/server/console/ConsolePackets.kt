package net.eiradir.server.console

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.eiradir.server.commands.CommandSource
import net.eiradir.server.extensions.logger
import net.eiradir.server.map.MapManager
import net.eiradir.server.network.packets.PacketFactory
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.commands.NetworkClientCommandSource
import net.eiradir.server.network.ServerNetworkContext

class ConsolePackets(packets: PacketFactory, dispatcher: CommandDispatcher<CommandSource>, mapManager: MapManager) : Initializer {

    private val log = logger()

    init {
        packets.registerPacket(CommandPacket::class, CommandPacket::encode, CommandPacket::decode)
        packets.registerPacket(CommandResponsePacket::class, CommandResponsePacket::encode, CommandResponsePacket::decode)

        packets.registerPacketHandler(CommandPacket::class) { context, packet ->
            val source = NetworkClientCommandSource(context as ServerNetworkContext, mapManager)
            source.cursorPosition = packet.cursorPosition
            if (packet.selectedEntityId.leastSignificantBits != 0L || packet.selectedEntityId.mostSignificantBits != 0L) {
                source.selectedEntityId = packet.selectedEntityId
            } else {
                source.selectedEntityId = null
            }

            context.scheduleTask {
                try {
                    dispatcher.execute(packet.command, source)
                } catch (e: CommandSyntaxException) {
                    source.respond(e.message ?: "An unknown error occurred.")
                } catch (e: Exception) {
                    log.error("An error occurred handling command", e)
                    source.respond("An unknown error occurred.")
                }
            }
        }
    }
}