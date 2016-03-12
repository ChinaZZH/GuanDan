package com.lbwan.game.payTributeHandler;

import java.util.ArrayList;
import java.util.List;

import com.lbwan.game.porkerEnumSet.FaceValueEnum;
import com.lbwan.game.porkerEnumSet.PorkerValueEnum;
import com.lbwan.game.proto.CmdProtocol.CmdType;
import com.lbwan.game.proto.WhippedEgg.SServerNotifyPayTribute;
import com.lbwan.game.room.gameTeam.TeamGroup;
import com.lbwan.game.room.payTributeData.PayTributeData;
import com.lbwan.game.room.roomGame.GamePlayer;
import com.lbwan.game.room.roomGame.RoomGame;
import com.lbwan.game.utils.GDPropertiesUtils;


public class SinglePayTributeHandler extends AbstractPayTributeHandler{
	
	public boolean processPayTribute(RoomGame currentRoomGame){
		boolean bProcessResult = false;
		if(null == currentRoomGame){
			logger.error("SinglePayTributeHandler::processPayTribute currentRoomGame Null Error");
			return bProcessResult;
		}
		
		TeamGroup currentTeamGroup = currentRoomGame.getTeamGroup();
		if(null == currentTeamGroup){
			logger.error("SinglePayTributeHandler::processPayTribute currentTeamGroup Null Error");
			return bProcessResult;
		}
		
		// 下游者  进贡牌的玩家
		GamePlayer lastPlayer = currentTeamGroup.getLastOutPorkerUser();
		if(null == lastPlayer){
			logger.error("SinglePayTributeHandler::processPayTribute lastPlayer Null Error");
			return bProcessResult;
		}
		
		// 上游者  接受进贡牌的玩家
		GamePlayer firstPlayer = currentTeamGroup.getFirstOutPorkerUser();
		if(null == firstPlayer){
			logger.error("SinglePayTributeHandler::processPayTribute firstPlayer Null Error");
			return bProcessResult;
		}
		
		// 进贡的牌  的牌值大小
		int nMajorFaceValue = currentTeamGroup.getCurrentMajorFaceValue();
		int nMajorPorkerValue = FaceValueEnum.getSpecficHeartByFaceValue(nMajorFaceValue);
		int nTributeFaceValue = lastPlayer.getBiggestPorkerFaceExcept(nMajorPorkerValue);
		
		
		// 通知进贡  进贡最大的牌
		// 记录玩家的进贡时间
		List<GamePlayer> tributePlayerList = new ArrayList<>(); 
		tributePlayerList.add(lastPlayer);
		this.recordPayTributeAndNotifyClient(tributePlayerList, currentTeamGroup);
		
		PayTributeData tributeData = currentRoomGame.getPayTributeData();
		if(null == tributeData){
			logger.error("SinglePayTributeHandler::processPayTribute tributeData Null Error");
			return bProcessResult;
		}
		 
		 tributeData.addNewPayTributeData(lastPlayer.getGamePlayerId(), nTributeFaceValue, firstPlayer.getGamePlayerId());
		
		
		// 最新起牌者的设定    (设置下游者 为新一局的起牌者)
		tributeData.initNewGameFirstPlayer(lastPlayer.getGamePlayerId());
		 
		System.out.println("进贡------" + lastPlayer.getGamePlayerId() + "向" + firstPlayer.getGamePlayerId() + "进贡" + nTributeFaceValue);
		System.out.println("这一局起牌者" + lastPlayer.getGamePlayerId());
		
		bProcessResult = true;
		return bProcessResult;
	}
}

