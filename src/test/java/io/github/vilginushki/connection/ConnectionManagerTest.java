package io.github.vilginushki.connection;

import io.github.vilginushki.model.messages.bitcoinTypes.NetAddr;
import io.github.vilginushki.model.messages.bitcoinTypes.NodeHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConnectionManagerTest {
    private ConnectionManager connectionManager;
    private NodeHashMap peers;
    private LinkedBlockingQueue<NetAddr> queue;

    @BeforeEach
    public void setup() {
        connectionManager = new ConnectionManager();
        peers = ConnectionManager.peersFound;
        queue = ConnectionManager.peersToLookFor;
    }

    @Test
    public void shouldAddPeerToQueueOnGetPeersByDNS() throws Exception {
        connectionManager.getPeersByDNS("seed.bitcoin.sipa.be");

        assertTrue(queue.size() > 0);
    }

}