package io.github.vilginushki.model.messages;

import io.github.vilginushki.enums.CommandMap;

import java.nio.ByteBuffer;

public class Verack extends Message implements Sendable, Receivable {

    public Verack() {
        super(CommandMap.VERACK);
    }

    @Override
    public ByteBuffer serialize() {
        return insertHeader(null);
    }

    @Override
    public void deserialize(ByteBuffer buffer) {
        header = new Header(buffer);
    }

    @Override
    public String toString() {
        String sb = "Verack{" + "header=" + header +
                '}';
        return sb;
    }
}
