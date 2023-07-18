package net.eiradir.server.network.packets

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import net.eiradir.server.entity.AdvancedEncoders
import net.eiradir.server.io.SupportedByteBuf
import net.eiradir.server.registry.Registries

import java.io.IOException

class PacketDecoder(private val factory: PacketFactory, private val registries: Registries, private val advancedEncoders: AdvancedEncoders) : ByteToMessageDecoder() {

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        if (buf.readableBytes() != 0) {
            val packetId = buf.readUnsignedByte().toInt()
            val packet = factory.readPacket(packetId, SupportedByteBuf(buf, registries, advancedEncoders))
            if (packet != null) {
                if (buf.readableBytes() == 0) {
                    out.add(packet)
                } else {
                    throw IOException("Unexpected packet size, " + buf.readableBytes() + " extra bytes in packet " + packet + " (id: " + packetId + ")")
                }
            } else {
                throw IOException("Received an invalid packet id $packetId")
            }
        }
    }

}
