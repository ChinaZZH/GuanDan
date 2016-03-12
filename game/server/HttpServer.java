package com.lbwan.game.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.apache.log4j.Logger;

import com.lbwan.game.spring.SpringUtils;

public class HttpServer {
    private Logger logger = Logger.getLogger(HttpServer.class);

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    /** The port. */
    private final int port;

    public HttpServer(int port) {
        this.port = port;
    }
    
    private void init() {
        // 初始化spring
        SpringUtils.init();
    }

    public void run() throws Exception {
        init();
        startHttpServer();
    }

    /**
     * Start http server.
     */
    private void startHttpServer() {
        logger.info("start http server...");
        if (bossGroup != null || workerGroup != null) {
            logger.error("can not start http server already started!");
            throw new IllegalArgumentException();
        }
        try {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();

            ServerBootstrap b = new ServerBootstrap();
            // EpollServerSocketChannel？
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new HttpServerInitializer());
            b.childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE);
            b.childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);

            b.bind(port).sync().channel().closeFuture().sync();
        } catch (Exception ex) {
            logger.error("when start http server", ex);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * Shutdown.
     */
    public void shutdown() {
        if (bossGroup != null && workerGroup != null) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
