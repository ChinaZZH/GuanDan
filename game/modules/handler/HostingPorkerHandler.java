package com.lbwan.game.modules.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.protobuf.InvalidProtocolBufferException;
import com.lbwan.game.channel.ChannelManager;
import com.lbwan.game.channel.ChannelWrapper;
import com.lbwan.game.handler.BaseHandler;
import com.lbwan.game.proto.CmdProtocol.CmdType;
import com.lbwan.game.proto.CommonProtocol.CommonMessage;
import com.lbwan.game.redisTest.LoginService;
import com.lbwan.game.room.RoomManager;
import com.lbwan.game.room.RoomWrapper;
import com.lbwan.game.room.roomGame.RoomGame;

@Component
public class HostingPorkerHandler extends BaseHandler{
	
	@Autowired
    private ChannelManager channelMgr;
	
	@Override
	protected void subHandle(ChannelWrapper channelWrapper,
			CommonMessage commonMsg) throws InvalidProtocolBufferException {
		
		logger.trace("call handler CMD: " + commonMsg.getCmd());
        // TODO 自动生成的方法存根
        switch (commonMsg.getCmd()) {
        // 创建游戏房间
        case CmdType.CMD_NEED_HOSTING_PORKER_VALUE: {
            this.needHostingPorkerToServer(channelWrapper, commonMsg);
        }
        break;
        
        case CmdType.CMD_CANCEL_HOSTING_PORKER_VALUE:{
        	this.cancelHostingPorkerToServer(channelWrapper, commonMsg);
        }
        break;
        
        
        default:
            logger.warn("HostingPorkerHandler::subHandle can not found handler,cmd:" + commonMsg.getCmd());
            break;
        }
	}
	
	// 托管请求到服务器
	private boolean needHostingPorkerToServer(ChannelWrapper channelWrapper,
			CommonMessage commonMsg) throws InvalidProtocolBufferException {
		
		String strUserId = channelMgr.getChannelUserId(channelWrapper.getChannel());
        if(null == strUserId){
            // 打印日志并且返回
            logger.error("HostingPorkerHandler::needHostingPorkerToServer strUserId Error");
            return false;
        }
        
        RoomWrapper room =  RoomManager.getRoomByUserId(strUserId);
        if(null == room){
            // 打印日志并且返回
            logger.error("HostingPorkerHandler::needHostingPorkerToServer room Error");
            return false;
        }
        
        RoomGame game = (RoomGame)room.getRoomGame(); 
        if(null == game){
         // 打印日志并且返回
            logger.error("HostingPorkerHandler::needHostingPorkerToServer game Error");
            return false;
        }
        
        game.hostingClientToServer(strUserId);
		return true;
	}
	
	// 取消托管请求到服务器
	private boolean cancelHostingPorkerToServer(ChannelWrapper channelWrapper,
			CommonMessage commonMsg) throws InvalidProtocolBufferException {
		
		String strUserId = channelMgr.getChannelUserId(channelWrapper.getChannel());
        if(null == strUserId){
            // 打印日志并且返回
            logger.error("HostingPorkerHandler::cancelHostingPorkerToServer strUserId Error");
            return false;
        }
        
        RoomWrapper room =  RoomManager.getRoomByUserId(strUserId);
        if(null == room){
            // 打印日志并且返回
            logger.error("HostingPorkerHandler::cancelHostingPorkerToServer room Error");
            return false;
        }
        
        RoomGame game = (RoomGame)room.getRoomGame(); 
        if(null == game){
         // 打印日志并且返回
            logger.error("HostingPorkerHandler::cancelHostingPorkerToServer game Error");
            return false;
        }
        
        game.cancelHostingToServer(strUserId);
		return true;
	}

}

