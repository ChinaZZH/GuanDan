package com.lbwan.game.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import org.apache.log4j.Logger;

import com.lbwan.game.channel.ChannelManager;
import com.lbwan.game.spring.SpringUtils;

/**
 * The Class ChannelCloseHandler.
 */
public class ChannelCloseHandler extends ChannelOutboundHandlerAdapter {

    private Logger logger = Logger.getLogger(ChannelCloseHandler.class);

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        Channel channel = ctx.channel();
        logger.info(channel +"ChannelId" + ChannelManager.computeChannelId(channel) + "is closed...");
        //logger.info(channel + "is closed...");
        ChannelManager channelManager = (ChannelManager) SpringUtils.getBeanByName("channelManager");
        channelManager.channelClose(channel);
        super.close(ctx, promise);
    }
}
