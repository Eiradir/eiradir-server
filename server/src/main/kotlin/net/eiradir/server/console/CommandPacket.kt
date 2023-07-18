package net.eiradir.server.console

import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.network.packets.Packet
import java.util.UUID

data class CommandPacket(val command: String, val cursorPosition: Vector3Int, val selectedEntityId: UUID) : Packet {

    companion object {
        fun decode(buf: SupportedInput): CommandPacket {
            val command = buf.readString()
            val cursorPosition = buf.readVector3Int()
            val selectedEntityId = buf.readUniqueId()
            return CommandPacket(command, cursorPosition, selectedEntityId)
        }

        fun encode(buf: SupportedOutput, packet: CommandPacket) {
            buf.writeString(packet.command)
            buf.writeVector3Int(packet.cursorPosition)
            buf.writeUniqueId(packet.selectedEntityId)
        }
    }
}