package io.github.vilginushki.model.messages;

import io.github.vilginushki.enums.CommandMap;
import io.netty.buffer.ByteBufUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Pong extends Message implements Sendable, Receivable {

    private long tempValue;

    public Pong(long tempValue) {
        super(CommandMap.PONG);
        this.tempValue = tempValue;
    }

    public Pong(Header header) {
        super(CommandMap.PONG);
        deserialize(header);
    }

    @Override
    public ByteBuffer serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.LITTLE_ENDIAN).putLong(tempValue);
        return insertHeader(buffer);
    }

    @Override
    public void deserialize(ByteBuffer buffer) {
        this.tempValue = buffer.order(ByteOrder.LITTLE_ENDIAN).getLong();
    }

    private void deserialize(Header header) {
        this.header = header;
        this.tempValue = Long.parseLong(ByteBufUtil.hexDump(header.getContent()), 16);
    }


    public long getTempValue() {
        return tempValue;
    }

    @Override
    public String toString() {
        return "Pong{" + "nonce=" + String.format("0x%x", tempValue) +
                '}';
    }
}