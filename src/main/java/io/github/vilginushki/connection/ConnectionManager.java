package io.github.vilginushki.connection;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vilginushki.Config;
import io.github.vilginushki.enums.TypeOfAction;
import io.github.vilginushki.model.BlockDto;
import io.github.vilginushki.model.messages.bitcoinTypes.NetAddr;
import io.github.vilginushki.model.messages.bitcoinTypes.NodeHashMap;
import io.github.vilginushki.model.messages.bitcoinTypes.StateInfo;
import io.github.vilginushki.ui.UIConsole;
import io.github.vilginushki.util.GenericUtils;
import io.github.vilginushki.util.SimpleThread;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class ConnectionManager {

    public static final NodeHashMap peersFound = new NodeHashMap();
    public static final LinkedBlockingQueue<NetAddr> peersToLookFor = new LinkedBlockingQueue<>();
    static Bootstrap bootstrap;
    private static EventLoopGroup worker;

    public ConnectionManager() {
        worker = new NioEventLoopGroup(Config.THREADS);
        bootstrap = getNewBootstrap(worker);
    }

    public void getPeersByDNS(String address) throws Exception {
        ArrayList<InetAddress> byDNS = new ArrayList<>(Arrays.asList(Objects.requireNonNull(GenericUtils.lookup(address))));
        for (InetAddress addr : byDNS) {
            peersToLookFor.put(new NetAddr(0, 0, addr.getHostAddress(), 8333));
        }
        log.info("Found by DNS " + byDNS.size());
    }

    public void getPeersByDNS() throws Exception {
        log.debug("Starting DNS lookup");
        for (String dns : Config.SEED_DNS) {
            log.info("Looking up " + dns + "...");
            getPeersByDNS(dns);
        }
        log.debug("Finished DNS lookup");

    }

    public void getAddr(NetAddr addr) {
        try {
            ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            log.debug("Connecting to " + addr.getIp().toString() + ":" + addr.getPort());
            StateInfo bundle = new StateInfo(addr.getIp(), addr.getPort(), new Date().getTime());
            bundle.setTypeOfAction(TypeOfAction.GETADDR);
            establishConnectionAndSyncChannels(channels, addr, bundle);
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
    }

    public void ping(NetAddr addr) {
        try {
            ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            log.info("Pinging to " + addr.getIp().toString() + ":" + addr.getPort());
            StateInfo bundle = new StateInfo(addr.getIp(), addr.getPort(), new Date().getTime());
            bundle.setTypeOfAction(TypeOfAction.PING);
            establishConnectionAndSyncChannels(channels, addr, bundle);
        } catch (Exception e) {
            log.debug(e.getMessage());
        }

    }

    private void establishConnectionAndSyncChannels(ChannelGroup channels, NetAddr target, StateInfo bundle) throws InterruptedException {
        ConnectionHandler.map.put(target.getIp().toString(), bundle);
        ChannelFuture future = bootstrap.connect(target.getIp().toString(), target.getPort());
        channels.add(future.channel());
        while (channels.size() >= 1) {
            wait(20);
        }
        channels.close().sync();
    }

    public void getData(final String hash) {
        try {
            final String bitcoinApiUrl = "https://api.blockcypher.com/v1/btc/main/blocks/";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(bitcoinApiUrl + hash))
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper()
                    .findAndRegisterModules()
                    .configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

            BlockDto blockDto = mapper.readValue(response.body(), BlockDto.class);

            UIConsole.printToUI(blockDto.toString());
        } catch (Exception ex) {
            log.warn("Block with hash: " + hash + " does not exist");
        }
    }

    public void runScan(SimpleThread thread) {
        try {
            ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            while (!peersToLookFor.isEmpty() && thread.shouldRun) {
                while (channels.size() <= Config.THREADS) {
                    NetAddr target = peersToLookFor.take();
                    log.debug("Connecting to " + target.getIp().toString() + ":" + target.getPort());
                    StateInfo bundle = new StateInfo(target.getIp(), target.getPort(), new Date().getTime());
                    bundle.setTypeOfAction(TypeOfAction.SCAN);
                    ConnectionHandler.map.put(target.getIp().toString(), bundle);
                    ChannelFuture future = bootstrap.connect(target.getIp().toString(), target.getPort());
                    channels.add(future.channel());
                }
            }


            channels.close().sync();
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
    }

    private Bootstrap getNewBootstrap(EventLoopGroup group) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 100000);
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) {
                socketChannel.pipeline().addLast(new MessageDecoder(), new ConnectionHandler());
            }
        });

        return bootstrap;
    }

    public void stop() {
        worker.shutdownGracefully();
    }
}
