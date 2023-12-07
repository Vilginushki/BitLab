package io.github.vilginushki.model.messages;

import io.github.vilginushki.enums.CommandMap;
import io.github.vilginushki.util.ByteParser;
import io.github.vilginushki.util.ByteUtils;

import java.nio.ByteBuffer;

public class Reject extends Message implements Receivable {

    private int messageBytes;
    private String message;
    private byte code;
    private int reasonBytes;
    private String reason;
    private String additionalData;

    public Reject(Header header) {
        super(CommandMap.REJECT);
        deserialize(header);
    }

    @Override
    public void deserialize(ByteBuffer buffer) {
        header = new Header(buffer);
        ByteParser parser = new ByteParser(header.getContent());
        messageBytes = (int) parser.parseVarInt().value();
        message = parser.parseCharString(messageBytes);
        code = parser.parseChar();
        reasonBytes = (int) parser.parseVarInt().value();
        reason = parser.parseCharString(reasonBytes);
        additionalData = ByteUtils.bytesToHexString(parser.parseRemain());
    }

    private void deserialize(Header header) {
        this.header = header;
        ByteParser parser = new ByteParser(header.getContent());
        messageBytes = (int) parser.parseVarInt().value();
        message = parser.parseCharString(messageBytes);
        code = parser.parseChar();
        reasonBytes = (int) parser.parseVarInt().value();
        reason = parser.parseCharString(reasonBytes);
        additionalData = ByteUtils.bytesToHexString(parser.parseRemain());
    }

    @Override
    public String toString() {
        String sb = "Reject{" + "message_bytes=" + messageBytes +
                ", message='" + message + '\'' +
                ", code=" + code +
                ", reason_bytes=" + reasonBytes +
                ", reason='" + reason + '\'' +
                ", extra_data='" + additionalData + '\'' +
                '}';
        return sb;
    }
}

