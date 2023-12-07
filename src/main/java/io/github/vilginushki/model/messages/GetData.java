package io.github.vilginushki.model.messages;


import io.github.vilginushki.enums.CommandMap;

import java.nio.ByteBuffer;

public class GetData extends Message implements Sendable, Receivable {//fixme

    public GetData() {
        super(CommandMap.GETDATA);
    }

    @Override
    public ByteBuffer serialize() {
        return null;
    }

    @Override
    public void deserialize(ByteBuffer buffer) {
    }
}
