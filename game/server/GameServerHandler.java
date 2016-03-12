package com.lbwan.game.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import org.apache.log4j.Logger;

import com.lbwan.game.handler.Handler;
import com.lbwan.game.handler.HandlerHolder;
import com.lbwan.game.proto.CommonProtocol.CommonMessage;
import com.lbwan.game.spring.SpringUtils;

/**
 * The Class GameServerHandler.
 * @author hli
 */
public class GameServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private final Logger logger = Logger.getLogger(GameServerHandler.class);

    private static HandlerHolder handlerHolder;

    public GameServerHandler() {
        if (handlerHolder == null) {
            synchronized (GameServerHandler.class) {
                if (handlerHolder == null) {
                    handlerHolder = (HandlerHolder) SpringUtils.getBeanByName("handlerHolder");
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("when game server handle:", cause);
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        // 鏈韩鏈夐暱搴︼紝涓嶉渶瑕佸垎鐗囦簡锛岃繖閲岃幏鍙栧埌channel鍜宑ontent浜�
        Channel channel = ctx.channel();
        ByteBuf content = msg.content();
        ByteBuf readBytes = content.readBytes(content.readableBytes());
        CommonMessage commonMsg = CommonMessage.parseFrom(readBytes.array());
        Handler handler = handlerHolder.getHandler(commonMsg.getCmd());
        if (handler == null) {
            logger.warn("GameServerHandler::can not found handler,cmd:" + commonMsg.getCmd());
            ctx.close();
            readBytes.release();
            return;
        }
        try {
            handler.handle(channel, commonMsg);
        } catch (Exception e) {
            logger.error("when handler handle:", e);
        } finally{
        	readBytes.release();
        }

    }

}
