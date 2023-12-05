package io.github.vilginushki.model.messages;

import java.nio.ByteBuffer;

public interface Receivable {
    void deserialize(ByteBuffer buffer);
}
