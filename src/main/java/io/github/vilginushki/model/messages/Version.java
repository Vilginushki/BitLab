package io.github.vilginushki.model.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.vilginushki.Config;
import io.github.vilginushki.enums.CommandMap;
import io.github.vilginushki.model.messages.bitcoinTypes.IPv6;
import io.github.vilginushki.model.messages.bitcoinTypes.Service;
import io.github.vilginushki.util.ByteParser;
import io.github.vilginushki.util.ByteUtils;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
public class Version extends Message implements Sendable, Receivable {

    private int version;
    private long services;
    private long timestamp;
    private long receiverServices;
    private IPv6 receiverIp;
    private int receiverPort;
    private long transServices;
    private IPv6 transIp;
    private int transPort;
    private long nonce;
    private byte[] userAgent;
    private int startHeight;
    private int relay;
    private int userAgentsBytes;

    private Version() {
        super(CommandMap.VERSION);
        version = Config.PROTOCOL_VERSION;
        services = Config.NODE_TYPE;
        timestamp = Calendar.getInstance().getTimeInMillis() / 1000;
        receiverServices = 0x01;
        transServices = Config.NODE_TYPE;
        transIp = new IPv6("0.0.0.0");
        transPort = 0;
        nonce = new SecureRandom().nextLong();
        userAgentsBytes = Config.USER_AGENT_BYTE;
        userAgent = Config.USER_AGENT;
        startHeight = -1;
        if (version > 70001)
            relay = 0;
        else
            relay = -1;
    }

    public Version(String ip, int port) {
        this(new IPv6(ip), port);
    }

    public Version(IPv6 ipv6, int port) {
        this();
        receiverIp = ipv6;
        receiverPort = port;
    }

    public Version(Header header) {
        super(CommandMap.VERSION);
        deserialize(header);
    }

    @Override
    public ByteBuffer serialize() {
        int size = Config.DEFAULT_SIZE_OF_VERSION
                + (relay != -1 ? 1 : 0)
                + userAgentsBytes;
        ByteBuffer buffer = ByteBuffer.allocate(size);
        Config.NONCE = nonce;
        buffer.order(ByteOrder.LITTLE_ENDIAN)
                .putInt(version)
                .putLong(services)
                .putLong(timestamp)
                .putLong(receiverServices)
                .order(ByteOrder.BIG_ENDIAN)
                .put(receiverIp.toByteArray())
                .putChar((char) receiverPort)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putLong(transServices)
                .order(ByteOrder.BIG_ENDIAN)
                .put(transIp.toByteArray())
                .putChar((char) transPort)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putLong(nonce)
                .put((byte) userAgentsBytes);
        if (userAgentsBytes != 0)
            buffer.order(ByteOrder.BIG_ENDIAN).put(userAgent).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(startHeight);
        if (relay != -1)
            buffer.put(relay >= 1 ? (byte) 1 : (byte) 0);
        Config.NONCE = nonce;
        return insertHeader(buffer);
    }

    @Override
    public void deserialize(ByteBuffer buffer) {
        header = new Header(buffer);
        ByteParser parser = new ByteParser(header.getContent());
        parseAndSetFields(parser);
    }

    private void parseAndSetFields(ByteParser parser) {
        version = parser.parseInt(true);
        services = parser.parseLong(true);
        timestamp = parser.parseLong(true);
        receiverServices = parser.parseLong(true);
        receiverIp = parser.parseIPv6();
        receiverPort = parser.parsePort();
        transServices = parser.parseLong(true);
        transIp = parser.parseIPv6();
        transPort = parser.parsePort();
        nonce = parser.parseLong(true);
        userAgentsBytes = (int) parser.parseVarInt().value();
        if (userAgentsBytes != 0)
            userAgent = parser.parseByte(userAgentsBytes);
        startHeight = parser.parseInt(true);
        relay = version > 70001 ? parser.parseByte(1)[0] & 0xff : -1;
    }

    private void deserialize(Header header) {
        this.header = header;
        ByteParser parser = new ByteParser(header.getContent());
        parseAndSetFields(parser);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Version{");
        sb.append("header=").append(header);
        sb.append(", version=").append(version);
        sb.append(", services=").append(new Service(services));
        sb.append(", timestamp=").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp * 1000)));
        sb.append(", recvServices=").append(new Service(receiverServices));
        sb.append(", recvIp=").append(receiverIp);
        sb.append(", recvPort=").append(receiverPort);
        sb.append(", transServices=").append(new Service(transServices));
        sb.append(", transIp=").append(transIp);
        sb.append(", transPort=").append(transPort);
        sb.append(", nonce=").append(String.format("0x%x", nonce));
        sb.append(", user_agents_bytes=").append(userAgentsBytes);
        if (userAgentsBytes != 0)
            sb.append(", user_agent=").append(ByteUtils.bytesToCharString(userAgent));
        sb.append(", start_height=").append(startHeight);
        sb.append(", relay=").append(relay);
        sb.append('}');
        return sb.toString();
    }

    @JsonProperty("user_agent")
    public String getUser_agentString() {
        return ByteUtils.bytesToCharString(userAgent);
    }
}
