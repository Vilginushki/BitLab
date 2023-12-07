package io.github.vilginushki;

import io.github.vilginushki.connection.ConnectionManager;
import io.github.vilginushki.enums.UserCommandMap;
import io.github.vilginushki.ui.UIConsole;
import io.github.vilginushki.util.SimpleThread;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Scanner;

@Slf4j
public class BitLab {

    public static void main(String... args) {
        String command;
        ArrayList<SimpleThread> threads = new ArrayList<>();
        ConnectionManager connectionManager = new ConnectionManager();
        while (!(command = UIConsole.read()).equals("exit")) {
            log.info("Command: " + command);
            try {
                switch (UserCommandMap.valueOf(command.toUpperCase())) {
                    case GETADDR -> {
                        connectionManager.getPeersByDNS();
                        connectionManager.getAddr(ConnectionManager.peersToLookFor.take());
                    }
                    case GETDATA -> {
                        UIConsole.printToUI("Type in hash and press enter:");
                        Scanner scanner = new Scanner(System.in);
                        String hash = scanner.nextLine();
                        connectionManager.getData(hash);
                    }
                    case SCAN -> {
                        SimpleThread thread = new SimpleThread(SimpleThread.ThreadEnum.SCAN, connectionManager);
                        new Thread(thread).start();
                        threads.add(thread);
                    }
                    case STOP -> {
                        for (SimpleThread simpleThread : threads) {
                            simpleThread.shouldRun = false;
                        }
                    }
                    case PING -> {
                        connectionManager.getPeersByDNS();
                        ConnectionManager.peersToLookFor.take();
                        connectionManager.ping(ConnectionManager.peersToLookFor.take());
                    }
                    default -> {
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("Program is closing...");
        if (threads.size() > 0) {
            for (SimpleThread simpleThread : threads) {
                simpleThread.shouldRun = false;
            }
        }
        connectionManager.stop();
    }
}
