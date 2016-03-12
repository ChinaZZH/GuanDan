package com.lbwan.game.modules.handler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.protobuf.InvalidProtocolBufferException;
import com.lbwan.game.channel.ChannelManager;
import com.lbwan.game.channel.ChannelWrapper;
import com.lbwan.game.handler.BaseHandler;
import com.lbwan.game.proto.CmdProtocol.CmdType;
import com.lbwan.game.proto.CommonProtocol.CommonMessage;
import com.lbwan.game.proto.WhippedEgg.CSumbitSomePorkerRequest;
import com.lbwan.game.proto.WhippedEgg.LostConnectionStatus;
import com.lbwan.game.proto.WhippedEgg.SEnterGameSuccess;
import com.lbwan.game.proto.WhippedEgg.SServerNofityBackTribute;
import com.lbwan.game.proto.WhippedEgg.SServerNotifyMaxPorkerInfo;
import com.lbwan.game.proto.WhippedEgg.SServerNotifyPayTribute;
import com.lbwan.game.room.RoomManager;
import com.lbwan.game.room.RoomWrapper;
import com.lbwan.game.room.roomGame.RoomGame;
import com.lbwan.game.utils.DataSendUtils;

import java.util.Collections;

@Component
public class WhiipedEggHandler extends BaseHandler{

	@Autowired
    private ChannelManager channelMgr;
	
	@Override
	protected void subHandle(ChannelWrapper channelWrapper,
			CommonMessage commonMsg) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		logger.trace("call handler CMD: " + commonMsg.getCmd());
        // TODO 自动生成的方法存根
        switch (commonMsg.getCmd()) {
        // 创建游戏房间
        case CmdType.CMD_SUMBIT_SOME_PORKER_VALUE: {
            this.sumbitSomePorkerToServer(channelWrapper, commonMsg);
        }
        break;
        
        case CmdType.CMD_SUMBIT_NULL_PORKER_VALUE:{
        	this.sumbitNullPorkerToServer(channelWrapper, commonMsg);
        }
        break;
        
        case CmdType.CMD_REQUEST_GAME_INIT_INFO_VALUE:{
        	this.requestGameInitInfo(channelWrapper, commonMsg);
        }
        break;
        
        default:
            logger.warn("WhiipedEggHandler::subHandle can not found handler,cmd:" + commonMsg.getCmd());
            break;
        }
	}

	private boolean sumbitSomePorkerToServer(ChannelWrapper channelWrapper, CommonMessage commonMsg)
			throws InvalidProtocolBufferException {
		
		String strUserId = channelMgr.getChannelUserId(channelWrapper.getChannel());
        if(null == strUserId){
            // 打印日志并且返回
            logger.error("WhiipedEggHandler::sumbitSomePorkerToServer strUserId Error");
            return false;
        }
        
        RoomWrapper room =  RoomManager.getRoomByUserId(strUserId);
        if(null == room){
            // 打印日志并且返回
            logger.error("WhiipedEggHandler::sumbitSomePorkerToServer room Error");
            return false;
        }
        
        RoomGame game = (RoomGame)room.getRoomGame(); 
        if(null == game){
         // 打印日志并且返回
            logger.error("WhiipedEggHandler::sumbitSomePorkerToServer game Error");
            return false;
        }
        
        CSumbitSomePorkerRequest sumbitSomePorkerRequest = CSumbitSomePorkerRequest.parseFrom(commonMsg.getData());
        List<Integer> porkerList = sumbitSomePorkerRequest.getPorkerArrayList();
        game.sumbitSomePorker(strUserId, porkerList);
		return true;
	}
	
	private boolean sumbitNullPorkerToServer(ChannelWrapper channelWrapper, CommonMessage commonMsg)
			throws InvalidProtocolBufferException{
		
		String strUserId = channelMgr.getChannelUserId(channelWrapper.getChannel());
        if(null == strUserId){
            // 打印日志并且返回
            logger.error("WhiipedEggHandler::sumbitNullPorkerToServer strUserId Null Error");
            return false;
        }
        
        RoomWrapper room =  RoomManager.getRoomByUserId(strUserId);
        if(null == room){
            // 打印日志并且返回
            logger.error("WhiipedEggHandler::sumbitNullPorkerToServer room Null Error");
            return false;
        }
        
        RoomGame game = (RoomGame)room.getRoomGame(); 
        if(null == game){
         // 打印日志并且返回
            logger.error("WhiipedEggHandler::sumbitNullPorkerToServer game Null Error");
            return false;
        }
        
        game.sumbitNullPorkerToServer(strUserId);
        return true;
	}
	
	private boolean requestGameInitInfo(ChannelWrapper channelWrapper, CommonMessage commonMsg)
			throws InvalidProtocolBufferException{
		
		String strUserId = channelMgr.getChannelUserId(channelWrapper.getChannel());
        if(null == strUserId){
            // 打印日志并且返回
            logger.error("WhiipedEggHandler::requestGameInitInfo strUserId Null Error");
            return false;
        }
        
        RoomWrapper room =  RoomManager.getRoomByUserId(strUserId);
        if(null == room){
            // 打印日志并且返回
            logger.error("WhiipedEggHandler::requestGameInitInfo room Null Error");
            return false;
        }
        
        RoomGame game = (RoomGame)room.getRoomGame(); 
        if(null == game){
         // 打印日志并且返回
            logger.error("WhiipedEggHandler::requestGameInitInfo game Null Error");
            return false;
        }
        
        // 发送桌面初始化信息到客户端
        SEnterGameSuccess.Builder gameBuilder = game.getReConnectionInfo(strUserId);
        if(null == gameBuilder){
            // 打印日志并且返回
        	logger.error("WhiipedEggHandler::requestGameInitInfo gameBuilder Null Error");
            return false;
         }
        
        DataSendUtils.sendData(channelWrapper.getChannel(), CmdType.CMD_INIT_GAME_DESKTOP_INFO_VALUE, gameBuilder.build().toByteArray());
        
        // 发进贡退贡消息到客户端
        LostConnectionStatus connectionStatus = gameBuilder.getLostConnection();
        
        int nConnectionStatusValue = connectionStatus.getNumber();
        switch(nConnectionStatusValue){
        
        case LostConnectionStatus.LOST_CONNECTION_PAY_TRIBUTING_VALUE:{
        	int nPayTributeSeconds = game.getPayTributeSeconds(strUserId);
        	
        	SServerNotifyPayTribute.Builder payTributeBuilder = SServerNotifyPayTribute.newBuilder();
        	payTributeBuilder.addStrPayTributerId(strUserId);
        	payTributeBuilder.setNPayTributeNeedSecs(nPayTributeSeconds);
        	DataSendUtils.sendData(channelWrapper.getChannel(), CmdType.CMD_SERVER_NOTIFY_PAY_TRIBUTE_VALUE, payTributeBuilder.build().toByteArray());
        }
        break;
        	
        case LostConnectionStatus.LOST_CONNECTION_BACK_TRIBUTING_VALUE:{
        	int nBackTributeSeconds = game.getBackTributeSeconds(strUserId);
        	//System.out.println("断线重连   退贡的时间: " + nBackTributeSeconds);
        	
        	SServerNofityBackTribute.Builder backTributeBuilder = SServerNofityBackTribute.newBuilder();
        	backTributeBuilder.setStrBackTributerId(strUserId);
        	backTributeBuilder.setNBackTributeNeedSecs(nBackTributeSeconds);
        	DataSendUtils.sendData(channelWrapper.getChannel(), CmdType.CMD_SERVER_NOTIFY_BACK_TRIBUTE_VALUE, backTributeBuilder.build().toByteArray());
        }
        break;
        	
        case LostConnectionStatus.LOST_CONNECTION_PLAYING_NOW_VALUE:{
        	
        	SServerNotifyMaxPorkerInfo.Builder maxPorkerInfoBuilder = SServerNotifyMaxPorkerInfo.newBuilder();
        	maxPorkerInfoBuilder.setStrMaxPorkerUser(game.getLastMaxPorkerUser());
        	maxPorkerInfoBuilder.addAllNMaxPorkerValueList(game.getLastMaxPorkerValueList());
        	DataSendUtils.sendData(channelWrapper.getChannel(), CmdType.CMD_SERVER_NOTIFY_MAX_PORKER_VALUE, maxPorkerInfoBuilder.build().toByteArray());
        }
        break;
        
        default:{
            
        }
        break;
        
        }
        return true;
	}
}
