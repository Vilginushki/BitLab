package io.github.vilginushki.model.messages;

import io.github.vilginushki.enums.CommandMap;

import java.nio.ByteBuffer;

public class GetAddr extends Message implements Sendable {

    public GetAddr() {
        super(CommandMap.GETADDR);
    }

    @Override
    public ByteBuffer serialize() {
        return insertHeader(null);
    }

}
