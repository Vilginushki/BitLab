package io.github.vilginushki.model.messages.bitcoinTypes;

public class Tag {

    private final String fromIP;
    private final long fromTime;

    public Tag(String fromIP, long fromTime) {
        this.fromIP = fromIP;
        this.fromTime = fromTime;
    }
}
