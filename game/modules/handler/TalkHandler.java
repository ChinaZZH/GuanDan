package com.lbwan.game.modules.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.protobuf.InvalidProtocolBufferException;
import com.lbwan.game.channel.ChannelManager;
import com.lbwan.game.channel.ChannelWrapper;
import com.lbwan.game.handler.BaseHandler;
import com.lbwan.game.proto.CmdProtocol.CmdType;
import com.lbwan.game.proto.CommonProtocol.CommonMessage;
import com.lbwan.game.proto.ResultCodeProtocol.ResultCode;
import com.lbwan.game.proto.WhippedEgg.ChatWord;
import com.lbwan.game.proto.WhippedEgg.SChatRespond;
import com.lbwan.game.proto.WhippedEgg.SChats;
import com.lbwan.game.room.RoomManager;
import com.lbwan.game.room.RoomWrapper;
import com.lbwan.game.room.roomGame.RoomGame;
import com.lbwan.game.utils.DataSendUtils;

@Component
public class TalkHandler extends BaseHandler{
	
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
        case CmdType.CMD_GAME_SEND_CHAT_VALUE: {
        	this.sendChat(channelWrapper, commonMsg);
        }
        break;
        
        
        default:
            logger.warn("TalkHandler::subHandle can not found handler,cmd:" + commonMsg.getCmd());
            break;
        }
	}

	public void sendChat(ChannelWrapper channelWrapper, CommonMessage commonMsg)
			throws InvalidProtocolBufferException {

    	String userId = channelMgr.getChannelUserId(channelWrapper.getChannel());

		SChatRespond.Builder response = SChatRespond.newBuilder();
		response.setResult(ResultCode.SUCCESS);
		DataSendUtils.sendData(channelWrapper.getChannel(), commonMsg.getCmd(),response.build().toByteArray());

		RoomWrapper room = RoomManager.getRoomByUserId(userId);
		RoomGame game = (RoomGame)room.getRoomGame();
		ChatWord charWord = ChatWord.parseFrom(commonMsg.getData());
		SChats.Builder schats = SChats.newBuilder();
		schats.addChatWord(charWord);
		game.nofityAllOnLineUser(CmdType.CMD_GAME_GET_CHAT_VALUE, schats.build().toByteArray());
	}
}
