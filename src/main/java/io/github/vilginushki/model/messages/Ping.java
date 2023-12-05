package io.github.vilginushki.model.messages;


import io.github.vilginushki.enums.CommandMap;
import io.github.vilginushki.util.ByteParser;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

public class Ping extends Message implements Sendable, Receivable {
    static Random random = new Random();
    private long temp;

    public Ping(Header header) {
        super(CommandMap.PING);
        deserialize(header);
    }

    public Ping() {
        super(CommandMap.PING);
        do {
            this.temp = random.nextLong();
        } while (this.temp < 0);

    }

    @Override
    public ByteBuffer serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.LITTLE_ENDIAN).putLong(temp);
        return insertHeader(buffer);
    }

    @Override
    public void deserialize(ByteBuffer buffer) {
        header = new Header(buffer);
        if (header.getPayloadSize() == 0) {
            temp = 0;
            return;
        }

        ByteParser parser = new ByteParser(header.getContent());
        temp = parser.parseLong(true);
    }

    public void deserialize(Header header) {
        this.header = header;
        if (header.getPayloadSize() == 0) {
            temp = 0;
            return;
        }

        ByteParser parser = new ByteParser(header.getContent());
        temp = parser.parseLong(true);
    }

    @Override
    public String toString() {
        String sb = "Ping{" + "nonce=" + String.format("0x%x", temp) +
                '}';
        return sb;
    }

    public long getTemp() {
        return temp;
    }

}
