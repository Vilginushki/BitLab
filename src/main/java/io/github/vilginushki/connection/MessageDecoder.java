package io.github.vilginushki.connection;

import io.github.vilginushki.Config;
import io.github.vilginushki.model.messages.Header;
import io.github.vilginushki.model.messages.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MessageDecoder extends ByteToMessageDecoder {


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {
        if (in.readableBytes() < 24) {
            log.debug("Got trash: " + ByteBufUtil.hexDump(in));
            return;
        }

        while (in.isReadable()) {
            int start = in.getInt(in.readerIndex());
            if (start == Config.START_STRING) {
                Header header = new Header(in);
                if (header.isValid()) {
                    list.add(new Message(header));
                } else {
                    return;
                }
            } else {
                log.debug("Start: " + start);
                log.debug("Payload: " + ByteBufUtil.hexDump(in));
                return;
            }
        }
    }
}
