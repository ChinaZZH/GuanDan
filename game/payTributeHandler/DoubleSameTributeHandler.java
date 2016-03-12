package com.lbwan.game.payTributeHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.lbwan.game.porkerEnumSet.FaceValueEnum;
import com.lbwan.game.porkerEnumSet.PorkerValueEnum;
import com.lbwan.game.proto.CmdProtocol.CmdType;
import com.lbwan.game.proto.WhippedEgg.SServerNotifyPayTribute;
import com.lbwan.game.room.gameTeam.Team;
import com.lbwan.game.room.gameTeam.TeamGroup;
import com.lbwan.game.room.payTributeData.PayTributeData;
import com.lbwan.game.room.roomGame.GamePlayer;
import com.lbwan.game.room.roomGame.RoomGame;
import com.lbwan.game.utils.GDPropertiesUtils;

// 双下时  并且两个输家 所持有最大的牌 大小是  一样大的
public class DoubleSameTributeHandler extends AbstractPayTributeHandler{
	
	public boolean processPayTribute(RoomGame currentRoomGame){
		boolean bProcessResult = false;
		if(null == currentRoomGame){
			logger.error("DoubleSameTributeHandler::processPayTribute currentRoomGame Null Error");
			return bProcessResult;
		}
		
		TeamGroup currentTeamGroup = currentRoomGame.getTeamGroup();
		if(null == currentTeamGroup){
			logger.error("DoubleSameTributeHandler::processPayTribute currentTeamGroup Null Error");
			return bProcessResult;
		}
		
		Team failerTeam = currentTeamGroup.getTeamOfFailGame();
		if(null == failerTeam){
			logger.error("DoubleSameTributeHandler::processPayTribute failerTeam Null Error");
			return bProcessResult;
		}
		
		Team winnerTeam = currentTeamGroup.getTeamOfWinner();
		if(null == winnerTeam){
			logger.error("DoubleSameTributeHandler::processPayTribute winnerTeam Null Error");
			return bProcessResult;
		}
		
		// 取主牌红桃的
		int nMajorFaceValue = currentTeamGroup.getCurrentMajorFaceValue();
		int nMajorPorkerValue = FaceValueEnum.getSpecficHeartByFaceValue(nMajorFaceValue);	
		
		List<GamePlayer> tributePlayerList = new ArrayList<>(); 
		
		// 取最后一个  和 第三个 的名字
		int nPayTributeFaceValue = 0;
		String strLastPlayer = null, strThreeRankPlayer = null;
		Map<String, GamePlayer> failerTeamPlayers = failerTeam.getAllTeamMembers();
		Iterator<Map.Entry<String, GamePlayer>> iter = failerTeamPlayers.entrySet().iterator();
		while(true == iter.hasNext()){
			Map.Entry<String, GamePlayer> entry = iter.next();
			GamePlayer player = entry.getValue();
			if(null == player){
				logger.error("DoubleSameTributeHandler::processPayTribute failerTeamPlayers.entrySet().iterator() Error");
				return bProcessResult;
			}
			
			if(0 == nPayTributeFaceValue){
				nPayTributeFaceValue = player.getBiggestPorkerFaceExcept(nMajorPorkerValue);
			}
			
			tributePlayerList.add(player);
			if(4 == player.getOutAllPorkerRank()){
				strLastPlayer = player.getGamePlayerId();
				continue;
			}
			
			strThreeRankPlayer = player.getGamePlayerId();
		}
		
		// 记录玩家的进贡时间
		this.recordPayTributeAndNotifyClient(tributePlayerList, currentTeamGroup);
				
		
		// 设置数据
		PayTributeData tributeData = currentRoomGame.getPayTributeData();
		if(null == tributeData){
			logger.error("DoubleDiffTributeHandler::processPayTribute tributeData Null Error");
			return bProcessResult;
		}
		
		Map<Integer, String>  rankAndPlayerIdMap = this.getRankNumberAndUserIdMap(winnerTeam);
		if(null == rankAndPlayerIdMap){
			logger.error("DoubleDiffTributeHandler::processPayTribute rankAndPlayerIdMap Null Error");
			return bProcessResult;
		}
		
		// 存储进贡数据  做服务器校验
		String strFirstPlayer  = rankAndPlayerIdMap.get(1);
		String strSecondPlayer = rankAndPlayerIdMap.get(2);
		tributeData.addNewPayTributeData(strThreeRankPlayer, nPayTributeFaceValue, strFirstPlayer);
		tributeData.addNewPayTributeData(strLastPlayer, nPayTributeFaceValue, strSecondPlayer);
		
		// 上游的下家出牌
		tributeData.initNewGameFirstPlayer(strSecondPlayer);
		
		System.out.println("进贡------" + strThreeRankPlayer + "向" + strFirstPlayer + "进贡" + nPayTributeFaceValue);
		System.out.println("进贡------" + strLastPlayer + "向" + strSecondPlayer + "进贡" + nPayTributeFaceValue);
		System.out.println("这一局起牌者" + strSecondPlayer);
		
		bProcessResult = true;
		return bProcessResult;
	}
}
