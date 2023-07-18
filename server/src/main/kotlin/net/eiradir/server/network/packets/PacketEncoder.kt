package net.eiradir.server.network.packets

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import net.eiradir.server.entity.AdvancedEncoders
import net.eiradir.server.io.SupportedByteBuf
import net.eiradir.server.registry.Registries

@ChannelHandler.Sharable
class PacketEncoder(private val factory: PacketFactory, private val registries: Registries, private val advancedEncoders: AdvancedEncoders) : MessageToByteEncoder<Packet>() {
    override fun encode(ctx: ChannelHandlerContext, msg: Packet, buf: ByteBuf) {
        factory.writePacket(SupportedByteBuf(buf, registries, advancedEncoders), msg)
    }
}
