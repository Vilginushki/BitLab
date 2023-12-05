package io.github.vilginushki.model.messages;

import java.nio.ByteBuffer;

public interface Sendable {
    ByteBuffer serialize();
}
