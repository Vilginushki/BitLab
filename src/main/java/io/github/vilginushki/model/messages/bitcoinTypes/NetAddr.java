package io.github.vilginushki.model.messages.bitcoinTypes;


import io.github.vilginushki.util.ByteParser;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
public class NetAddr {
    private final long time;
    private final long services;
    private final IPWrapper ip;
    private final int port;

    public NetAddr(byte[] bytes) {
        ByteParser parser = new ByteParser(bytes);
        time = parser.parseInt(true);
        services = parser.parseLong(true);
        ip = parser.parseIPv6();
        port = parser.parsePort();
    }

    public NetAddr(long time, long services, IPWrapper ip, int port) {
        this.time = time;
        this.services = services;
        this.ip = ip;
        this.port = port;
    }

    public NetAddr(long time, long services, String ip, int port) throws Exception {
        this(time, services, new IPWrapper(ip), port);
    }

    @Override
    public String toString() {
        String sb = "NetAddr{" + "time=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time * 1000)) +
                ", services=" + new Service(services) +
                ", ip=" + ip +
                ", port=" + port +
                '}';
        return sb;
    }

}
