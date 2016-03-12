package com.lbwan.game.payTributeHandler;

import com.lbwan.game.porkerEnumSet.PorkerValueEnum;
import com.lbwan.game.room.gameTeam.TeamGroup;
import com.lbwan.game.room.roomGame.GamePlayer;
import com.lbwan.game.room.roomGame.RoomGame;

// 抗贡
public class ReactToPayTributeHandler extends AbstractPayTributeHandler{
	
	public boolean processPayTribute(RoomGame currentRoomGame){
		// 抗贡  则设置上游者为起牌者
		boolean bProcessResult = false;
		if(null == currentRoomGame){
			logger.error("ReactToPayTributeHandler::processPayTribute currentRoomGame Null Error");
			return bProcessResult;
		}
		
		TeamGroup teamGroupOfGame = currentRoomGame.getTeamGroup();
		if(null == teamGroupOfGame){
			logger.error("ReactToPayTributeHandler::processPayTribute teamGroupOfGame Null Error");
			return bProcessResult;
		}
		
		// 抗贡  选取上一局游戏上游者  为这一轮的起牌者
		GamePlayer firstPlayer = teamGroupOfGame.getFirstOutPorkerUser();
		if(null == firstPlayer){
			logger.error("ReactToPayTributeHandler::processPayTribute firstPlayer Null Error");
			return bProcessResult;
		}
		
		System.out.println("抗贡, 不执行进贡 退贡");
		System.out.println("这一局起牌者" + firstPlayer.getGamePlayerId());
		
		// 然后设置  开始游戏
		payTributeHandlerHolder.startGameByEndTributeCallBack(currentRoomGame, firstPlayer.getGamePlayerId());
		bProcessResult = true;
		return bProcessResult;
	}
}
