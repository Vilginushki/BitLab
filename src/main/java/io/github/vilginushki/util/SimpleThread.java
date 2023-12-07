package io.github.vilginushki.util;

import io.github.vilginushki.connection.ConnectionManager;

import java.util.Objects;

public class SimpleThread extends Thread {
    private final ConnectionManager cm;
    public ThreadEnum operation;
    public boolean shouldRun = true;

    public SimpleThread(ThreadEnum operation, ConnectionManager connectionManager) {
        this.operation = operation;
        this.cm = connectionManager;
    }

    @Override
    public void run() {
        if (Objects.requireNonNull(operation) == ThreadEnum.SCAN && shouldRun) {
            try {
                cm.getPeersByDNS();
                cm.runScan(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public enum ThreadEnum {
        SCAN
    }

}
