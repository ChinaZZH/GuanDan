package com.lbwan.game.modules.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.protobuf.InvalidProtocolBufferException;
import com.lbwan.game.channel.ChannelManager;
import com.lbwan.game.channel.ChannelWrapper;
import com.lbwan.game.handler.BaseHandler;
import com.lbwan.game.proto.CmdProtocol.CmdType;
import com.lbwan.game.proto.CommonProtocol.CommonMessage;
import com.lbwan.game.proto.WhippedEgg.CBackTributeRequest;
import com.lbwan.game.proto.WhippedEgg.CPayTributeRequest;
import com.lbwan.game.proto.WhippedEgg.GameResultCode;
import com.lbwan.game.proto.WhippedEgg.SBackTributeResponse;
import com.lbwan.game.proto.WhippedEgg.SPayTributeResult;
import com.lbwan.game.room.RoomManager;
import com.lbwan.game.room.RoomWrapper;
import com.lbwan.game.room.roomGame.RoomGame;
import com.lbwan.game.utils.DataSendUtils;

@Component
public class ProcessTributeHandler extends BaseHandler{
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
        case CmdType.CMD_PAY_TRIBUTE_REQUEST_VALUE: {
            this.payTributeRequestValue(channelWrapper, commonMsg);
        }
        break;
        
        case CmdType.CMD_BACK_TRIBUTE_REQUEST_VALUE:{
        	this.backTributeRequestValue(channelWrapper, commonMsg);
        }
        break;
         
        default:
            logger.warn("ProcessTributeHandler::subHandle can not found handler,cmd:" + commonMsg.getCmd());
            break;
        }
	}

	private boolean payTributeRequestValue(ChannelWrapper channelWrapper, CommonMessage commonMsg)
			throws InvalidProtocolBufferException{
		
		String strUserId = channelMgr.getChannelUserId(channelWrapper.getChannel());
        if(null == strUserId){
            // 打印日志并且返回
            logger.error("ProcessTributeHandler::payTributeRequestValue strUserId Error");
            return false;
        }
        
        RoomWrapper room =  RoomManager.getRoomByUserId(strUserId);
        if(null == room){
            // 打印日志并且返回
            logger.error("ProcessTributeHandler::payTributeRequestValue room Error");
            return false;
        }
        
        RoomGame game = (RoomGame)room.getRoomGame(); 
        if(null == game){
         // 打印日志并且返回
            logger.error("ProcessTributeHandler::payTributeRequestValue game Error");
            return false;
        }
        
		CPayTributeRequest payTribute = CPayTributeRequest.parseFrom(commonMsg.getData());
		int nPayTributePorkerValue = payTribute.getNTributePorker();
		boolean bPayTributeResult = game.payTributePorkerValue(strUserId, nPayTributePorkerValue);
		if(false == bPayTributeResult){
			SPayTributeResult.Builder tributeResult = SPayTributeResult.newBuilder();
			tributeResult.setPayTributeResult(GameResultCode.GRC_FAIL);
			DataSendUtils.sendData(channelWrapper.getChannel(), CmdType.CMD_PAY_TRIBUTE_RESULT_VALUE, tributeResult.build().toByteArray());
		}
		
		return true;
		
	}
	
	private boolean backTributeRequestValue(ChannelWrapper channelWrapper, CommonMessage commonMsg)
			throws InvalidProtocolBufferException{
		
		String strUserId = channelMgr.getChannelUserId(channelWrapper.getChannel());
        if(null == strUserId){
            // 打印日志并且返回
            logger.error("ProcessTributeHandler::backTributeRequestValue strUserId Error");
            return false;
        }
        
        RoomWrapper room =  RoomManager.getRoomByUserId(strUserId);
        if(null == room){
            // 打印日志并且返回
            logger.error("ProcessTributeHandler::backTributeRequestValue room Error");
            return false;
        }
        
        RoomGame game = (RoomGame)room.getRoomGame(); 
        if(null == game){
         // 打印日志并且返回
            logger.error("ProcessTributeHandler::backTributeRequestValue game Error");
            return false;
        }
        
        CBackTributeRequest backTribute = CBackTributeRequest.parseFrom(commonMsg.getData());
		int nBackTributePorkerValue = backTribute.getNBackTributePorker();
		boolean bBackTributeResult = game.backTributePorkerValue(strUserId, nBackTributePorkerValue);
		if(false == bBackTributeResult){
			SBackTributeResponse.Builder tributeResult = SBackTributeResponse.newBuilder();
			tributeResult.setBackTributeResult(GameResultCode.GRC_FAIL);
			DataSendUtils.sendData(strUserId, CmdType.CMD_BACK_TRIBUTE_RESPONSE_VALUE, tributeResult.build().toByteArray());
		}
		
		return true;
		
	}
}


