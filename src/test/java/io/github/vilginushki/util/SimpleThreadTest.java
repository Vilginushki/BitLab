package io.github.vilginushki.util;

import io.github.vilginushki.connection.ConnectionManager;
import io.github.vilginushki.util.SimpleThread.ThreadEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class SimpleThreadTest {
    private SimpleThread simpleThread;
    private ConnectionManager connectionManager;

    @BeforeEach
    public void setup() {
        connectionManager = Mockito.mock(ConnectionManager.class);
        simpleThread = new SimpleThread(ThreadEnum.SCAN, connectionManager);
    }

    @Test
    public void shouldRunScanWhenOperationIsScan() throws Exception {
        simpleThread.start();

        verify(connectionManager, after(20)).getPeersByDNS();
        verify(connectionManager, after(20)).runScan(simpleThread);
    }

    @Test
    public void shouldNotRunScanWhenOperationIsNull() throws Exception {
        simpleThread.operation = null; //it throws but its okay in that case, just wanted to verify behaviour

        simpleThread.start();

        verify(connectionManager, times(0)).getPeersByDNS();
        verify(connectionManager, times(0)).runScan(simpleThread);
    }

    @Test
    public void shouldNotRunScanWhenShouldRunIsFalse() throws Exception {
        simpleThread.shouldRun = false;

        simpleThread.start();

        verify(connectionManager, times(0)).getPeersByDNS();
        verify(connectionManager, times(0)).runScan(simpleThread);
    }
}