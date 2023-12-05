package io.github.vilginushki.model.messages;


import io.github.vilginushki.Config;
import io.github.vilginushki.util.ByteBufParser;
import io.github.vilginushki.util.ByteParser;
import io.github.vilginushki.util.ByteUtils;
import io.github.vilginushki.util.GenericUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;

@Getter
@Setter
public class Header {
    private final int startString;
    private final String commandName;
    private final int payloadSize;
    private final String recvChecksum;
    private String calcChecksum;
    private byte[] content;

    public Header(ByteBuffer buffer) {
        ByteParser parser = new ByteParser(buffer);
        startString = parser.parseInt(false);
        commandName = parser.parseCharString(12).trim();
        payloadSize = parser.parseInt(true);
        recvChecksum = parser.parseHexString(4);
        content = parser.parseByte(payloadSize);
        calcChecksum = ByteUtils.bytesToHexString(GenericUtils.sha256Twice(content), 0, 4);
    }

    public Header(ByteBuf buf) {
        ByteBufParser parser = new ByteBufParser(buf);
        startString = parser.parseInt(false);
        commandName = parser.parseAsciiString(12).trim();
        payloadSize = parser.parseInt(true);
        recvChecksum = parser.parseHexString(4);
        if (buf.readerIndex() + payloadSize <= buf.writerIndex()) {
            content = parser.parseByte(payloadSize);
            calcChecksum = ByteBufUtil.hexDump(GenericUtils.sha256Twice(content), 0, 4);
        } else {
            buf.readerIndex(buf.readerIndex() - Config.DEFAULT_SIZE_OF_HEADER);
        }
    }

    public boolean isValid() {
        return isValidStartString() && isValidChecksum();
    }

    public boolean isValidStartString() {
        return startString == Config.START_STRING;
    }

    public boolean isValidChecksum() {
        return recvChecksum.equals(calcChecksum);
    }

    @Override
    public String toString() {
        String sb = "Header{" + "startString=" + String.format("0x%x", startString) +
                ", commandName='" + commandName + '\'' +
                ", payloadSize=" + payloadSize +
                ", RecvChecksum='" + recvChecksum + '\'' +
                ", CalcChecksum='" + calcChecksum + '\'' +
                '}';
        return sb;
    }

}
