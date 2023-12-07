package io.github.vilginushki.model.messages;

import io.github.vilginushki.Config;
import io.github.vilginushki.enums.CommandMap;
import io.github.vilginushki.util.ByteUtils;
import io.github.vilginushki.util.GenericUtils;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Getter
@Setter
public class Message {

    protected final CommandMap command;
    protected Header header;

    protected Message(CommandMap command) {
        this.command = command;
    }

    public Message(Header header) {
        this.command = CommandMap.valueOf(header.getCommandName().toUpperCase());
        this.header = header;
    }

    protected ByteBuffer insertHeader(ByteBuffer content) {
        int size = 0;
        if (content != null)
            size += content.capacity();
        ByteBuffer message = ByteBuffer.allocate(Config.DEFAULT_SIZE_OF_HEADER + size);
        message.putInt(Config.START_STRING)
                .put(command.toByteArray())
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(content != null ? content.limit() : 0)
                .order(ByteOrder.BIG_ENDIAN)
                .putInt(content != null ? (int) Long.parseLong(ByteUtils.bytesToHexString(GenericUtils.sha256Twice(content.array()), 0, 4), 16) : Config.EMPTY_HASH);
        if (content != null)
            message.put(content.array());
        message.rewind();
        return message;
    }

}
