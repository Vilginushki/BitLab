package io.github.vilginushki;

import io.github.vilginushki.enums.NodeType;
import io.github.vilginushki.util.ByteUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class Config {

    public static final int THREADS = 4;
    public static final int DEFAULT_SIZE_OF_HEADER = 24;
    public static final int DEFAULT_SIZE_OF_VERSION = 85;
    public static final int START_STRING = 0xf9beb4d9;
    public static final int EMPTY_HASH = 0x5df6e0e2;
    public static final NodeType[] NODE_TYPES = NodeType.values();
    public static final String[] SEED_DNS = {
            "seed.bitcoin.wiz.biz",
            "dnsseed.bluematt.me",
            "seed.bitcoinstats.com",
            "seed.bitcoin.sipa.be",
            "seed.btc.petertodd.org"
    };
    public static int PROTOCOL_VERSION;
    public static long NODE_TYPE;
    public static byte[] USER_AGENT;
    public static int USER_AGENT_BYTE;
    public static long NONCE;

    static {
        Properties properties;
        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream("config.properties");
            properties = new Properties();
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            log.error("This shouldn't happen! config.properties is missing!");
            throw new IllegalStateException("config.properties is missing");
        }

        for (String key : properties.stringPropertyNames()) {
            String property = properties.getProperty(key);
            switch (key) {
                case "USER_AGENT" -> {
                    USER_AGENT = ByteUtils.stringToBytes(property);
                    USER_AGENT_BYTE = USER_AGENT.length;
                }
                case "PROTOCOL_VERSION" -> PROTOCOL_VERSION = Integer.parseInt(property);
                case "NODE_TYPE" -> NODE_TYPE = Long.parseLong(property, 2);
            }
        }
    }

    private Config() {
    }
}
