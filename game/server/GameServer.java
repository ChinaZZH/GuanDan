package com.lbwan.game.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.apache.log4j.Logger;
import com.lbwan.game.spring.SpringUtils;
import com.lbwan.game.testServerLogicCode.TestSearcherCode;
import com.lbwan.game.utils.PropertiesUtils;

/**
 * The Class GameServer.
 * @author hli
 */
public class GameServer {

    private static Logger logger = Logger.getLogger(GameServer.class);

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    /** The port. */
    private final int port;

    public GameServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        init();
        startWebSocketServer();
    }


    /**=
     * Start web socket server.
     */
    private void startWebSocketServer() { 
        logger.info("start http server...");
        if (bossGroup != null || workerGroup != null) {
            logger.error("can not start game server already started!");
            throw new IllegalArgumentException();
        }
        try {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();

            ServerBootstrap b = new ServerBootstrap();
            b.childOption(ChannelOption.TCP_NODELAY, true).childOption(ChannelOption.SO_KEEPALIVE, true);
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new GameServerInitializer());
            b.bind(port).sync().channel().closeFuture().sync();
        } catch (Exception ex) {
            logger.error("when start carrot server", ex);
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

    /**
     * Inits.
     */
    private void init() {
        // 初始化spring
        SpringUtils.init();
        
        //TestSearcherCode searcherCode = new TestSearcherCode();
        //searcherCode.testSearchLogic();
    }
   
    public static void main(String[] args) throws Exception {
    	 Integer httpPort = PropertiesUtils.getPropertyAsInteger(PropertiesUtils.HTTP_PORT, 8003);
         final HttpServer httpServer = new HttpServer(httpPort);
         new Thread(new Runnable() {
             @Override
             public void run() {
                 try {
                     httpServer.run();
                 } catch (Exception e) {
                     logger.warn("when httpserver:" + e);
                 }
             }
         }).start();
         
    	Integer port = PropertiesUtils.getPropertyAsInteger(PropertiesUtils.SERVER_PORT, 8082);
        final GameServer gameServer = new GameServer(port);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                gameServer.shutdown();
                httpServer.shutdown();
            }
        }));
        
        gameServer.run();
    }
    
}

