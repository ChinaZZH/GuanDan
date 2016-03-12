package com.lbwan.game.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * The Class CarrotServerInitializer.
 * @author hli
 */
public class GameServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("decoder", new HttpRequestDecoder());

        // aggregator of http message and http content
        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));

        pipeline.addLast("closeHandler", new ChannelCloseHandler());

        pipeline.addLast("websockethandler", new WebSocketServerProtocolHandler("gd"));
        pipeline.addLast("hallServerHandler", new GameServerHandler());

    }
}
