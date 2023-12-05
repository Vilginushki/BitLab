package io.github.vilginushki.connection;

import io.github.vilginushki.enums.TypeOfAction;
import io.github.vilginushki.model.messages.*;
import io.github.vilginushki.model.messages.bitcoinTypes.IPv6;
import io.github.vilginushki.model.messages.bitcoinTypes.NetAddr;
import io.github.vilginushki.model.messages.bitcoinTypes.StateInfo;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ConnectionHandler extends ChannelInboundHandlerAdapter {

    public static final ConcurrentHashMap<String, StateInfo> map = new ConcurrentHashMap<>();
    TypeOfAction typeOfAction;
    private boolean isSendGetAddr;
    private StateInfo bundle;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        log.info("ChannelRegistered: " + ctx.name());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        log.info("ChannelUnRegistered: " + ctx.name());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        isSendGetAddr = false;
        String ip = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
        bundle = map.get(IPv6.convert(ip));
        if (bundle == null) {
            log.warn("Check ths IP Address: " + ip);
            ctx.close();
        }
        typeOfAction = bundle.getTypeOfAction();
        bundle.setSuccess(true);
        log.info("ChannelActive with: " + IPv6.convert(bundle.getIp()) + "/" + bundle.getPort());

        switch (typeOfAction) {
            case GETADDR, SCAN, PING -> writeAndFlush(ctx, new Version(bundle.getIp(), bundle.getPort()).serialize());
            case GETDATA -> writeAndFlush(ctx, new GetData().serialize());
            default -> {
                log.info("ChannelActive: " + ctx.name());
                ctx.close();
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.info("Closed:" + bundle.getIp() + ":" + bundle.getPort());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        Message message = (Message) msg;
        switch (typeOfAction) {
            case GETADDR -> {
                switch (message.getCommand()) {
                    case VERSION -> {
                        log.info("Got: VERSION; From " + IPv6.convert(bundle.getIp()));
                        writeAndFlush(ctx, new Verack().serialize());
                        bundle.setVersion(new Version(message.getHeader()));
                    }
                    case VERACK -> {
                        log.info("Got: VERACK; From " + IPv6.convert(bundle.getIp()));
                        writeAndFlush(ctx, new GetAddr().serialize());
                        isSendGetAddr = true;
                    }
                    case ADDR -> {
                        log.info("Got: ADDR; From " + IPv6.convert(bundle.getIp()));
                        Addr addr = new Addr(message.getHeader());
                        if (isSendGetAddr && addr.getList().size() > 1) {
                            for (NetAddr netAddr : addr.getList()) {
                                if (ConnectionManager.peersFound.insert(netAddr, new StateInfo(netAddr.getIp(), netAddr.getPort(), 0))) {
                                    log.debug("Got first time: " + netAddr.getIp().toString());
                                } else {
                                    log.debug(netAddr.getIp().toString() + " already in peers queue");
                                }
                            }
                            ctx.close();
                        }
                    }
                    case PING -> {
                        Ping ping = new Ping(message.getHeader());
                        log.info("Got: PING(" + ping.getTemp() + "); From " + IPv6.convert(bundle.getIp()));
                        writeAndFlush(ctx, new Pong(ping.getTemp()).serialize());
                        log.info("Sent: PONG; TO:" + IPv6.convert(bundle.getIp()));
                    }
                    case REJECT -> {
                        Reject reject = new Reject(message.getHeader());
                        log.info("GOT: REJECT" + reject);
                    }
                }
            }
            case SCAN -> {
                switch (message.getCommand()) {
                    case VERSION -> {
                        log.debug("Got: VERSION; From " + IPv6.convert(bundle.getIp()));
                        writeAndFlush(ctx, new Verack().serialize());
                        bundle.setVersion(new Version(message.getHeader()));
                    }
                    case VERACK -> {
                        log.debug("Got: VERACK; From " + IPv6.convert(bundle.getIp()));
                        writeAndFlush(ctx, new GetAddr().serialize());
                        isSendGetAddr = true;
                    }
                    case ADDR -> {
                        log.debug("Got: ADDR; From " + IPv6.convert(bundle.getIp()));
                        Addr addr = new Addr(message.getHeader());
                        if (isSendGetAddr && addr.getList().size() > 1) {

                            for (NetAddr netAddr : addr.getList()) {
                                if (ConnectionManager.peersFound.insert(netAddr,
                                        new StateInfo(netAddr.getIp(), netAddr.getPort(), 0))) {
                                    ConnectionManager.peersToLookFor.put(netAddr);
                                    log.debug("Added to queue: " + netAddr.getIp().toString());
                                } else {
                                    log.debug("Skipped: " + netAddr.getIp().toString() + " was already added");
                                }
                            }
                            ctx.close();
                        }
                    }
                    case PING -> {
                        Ping ping = new Ping(message.getHeader());
                        log.debug("Got: PING(" + ping.getTemp() + "); From " + IPv6.convert(bundle.getIp()));
                        writeAndFlush(ctx, new Pong(ping.getTemp()).serialize());
                        log.debug("Sent: PONG; TO:" + IPv6.convert(bundle.getIp()));
                    }
                    case REJECT -> {
                        Reject reject = new Reject(message.getHeader());
                        log.info("GOT: REJECT" + reject);
                    }
                }
            }
            case PING -> {
                switch (message.getCommand()) {
                    case VERSION -> {
                        log.info("Got: VERSION; From " + IPv6.convert(bundle.getIp()));
                        writeAndFlush(ctx, new Verack().serialize());
                        bundle.setVersion(new Version(message.getHeader()));
                    }
                    case VERACK -> {
                        log.info("Got: VERACK; From " + IPv6.convert(bundle.getIp()));
                        Ping tmp = new Ping();
                        writeAndFlush(ctx, tmp.serialize());
                        log.info("Sent: Ping(" + tmp.getTemp() + ") to: " + IPv6.convert(bundle.getIp()));
                    }
                    case ADDR -> log.info("Got: ADDR; From " + IPv6.convert(bundle.getIp()));
                    case PONG -> {
                        Pong tmp2 = new Pong(message.getHeader());
                        log.info("Got: PONG(" + tmp2.getTempValue() + ") from: " + IPv6.convert(bundle.getIp()));
                        ctx.close();
                    }
                    case REJECT -> {
                        Reject reject = new Reject(message.getHeader());
                        log.info("GOT: REJECT" + reject);
                    }
                }
            }
            default -> {
                log.info("Got: " + message.getCommand() + "; From " + IPv6.convert(bundle.getIp()));
                ctx.close();
            }

        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (bundle == null) {
            log.error("exceptionCaught (bundle is null):" + ctx.channel().remoteAddress());
        } else {
            log.error("exceptionCaught: " + bundle.getIp() + "/" + bundle.getPort(), cause);
            if (cause instanceof IOException) {
                bundle.setSuccess(false);
                bundle.setException(cause);
            }
        }

        ctx.close();
    }

    private void writeAndFlush(ChannelHandlerContext ctx, ByteBuffer buffer) {
        ctx.writeAndFlush(Unpooled.wrappedBuffer(buffer));
    }
}
