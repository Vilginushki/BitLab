package io.github.vilginushki.enums;


import io.github.vilginushki.util.ByteUtils;

public enum CommandMap {

    VERSION("version"),
    VERACK("verack"),
    PING("ping"),
    PONG("pong"),
    GETADDR("getaddr"),
    ADDR("addr"),
    INV("inv"),
    ALERT("alert"),
    GETUTXOS("getutxos"),
    UTXOS("utxos"),
    TX("tx"),
    GETDATA("getdata"),
    REJECT("reject"),
    BLOCK("block");

    public final int length;
    private final String command;

    CommandMap(String command) {
        this.command = command;
        this.length = ByteUtils.stringToBytes(command).length;
    }

    @Override
    public String toString() {
        return command;
    }

    public byte[] toByteArray() {
        byte[] bytes = new byte[12];
        System.arraycopy(ByteUtils.stringToBytes(command), 0, bytes, 0, length);
        return bytes;
    }
}
