package com.lbwan.game.payTributeHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.lbwan.game.room.gameTeam.Team;
import com.lbwan.game.room.gameTeam.TeamGroup;
import com.lbwan.game.room.payTributeData.PayTributeData;
import com.lbwan.game.room.roomGame.GamePlayer;
import com.lbwan.game.room.roomGame.RoomGame;
import com.lbwan.game.utils.GDPropertiesUtils;
import com.lbwan.game.utils.IntToolUtils;
import com.lbwan.game.porkerEnumSet.FaceValueEnum;
import com.lbwan.game.porkerEnumSet.PorkerValueEnum;
import com.lbwan.game.proto.CmdProtocol.CmdType;
import com.lbwan.game.proto.WhippedEgg.SServerNotifyPayTribute;

//双下时  并且两个输家 所持有最大的牌 大小是  不一样大的  一大一小的
public class DoubleDiffTributeHandler extends AbstractPayTributeHandler{
	
	public boolean processPayTribute(RoomGame currentRoomGame){
		boolean bProcessResult = false;
		if(null == currentRoomGame){
			logger.error("DoubleDiffTributeHandler::processPayTribute currentRoomGame Null Error");
			return bProcessResult;
		}
		
		TeamGroup currentTeamGroup = currentRoomGame.getTeamGroup();
		if(null == currentTeamGroup){
			logger.error("DoubleDiffTributeHandler::processPayTribute currentTeamGroup Null Error");
			return bProcessResult;
		}
		
		Team failerTeam = currentTeamGroup.getTeamOfFailGame();
		if(null == failerTeam){
			logger.error("DoubleDiffTributeHandler::processPayTribute failerTeam Null Error");
			return bProcessResult;
		}
		
		
		Team winnerTeam = currentTeamGroup.getTeamOfWinner();
		if(null == winnerTeam){
			logger.error("DoubleDiffTributeHandler::processPayTribute winnerTeam Null Error");
			return bProcessResult;
		}
		
		// 读取关于进贡玩家之间的数据
		int nMajorFaceValue = currentTeamGroup.getCurrentMajorFaceValue();
		int nMajorPorkerValue = FaceValueEnum.getSpecficHeartByFaceValue(nMajorFaceValue);	
		IntToolUtils biggerPlayer = new IntToolUtils();
		IntToolUtils smallerPlayer = new IntToolUtils();
		Map<Integer, GamePlayer> tributePlayerMap = this.getTributePlayerMap(failerTeam, nMajorPorkerValue, biggerPlayer, smallerPlayer);
		if(null == tributePlayerMap){
			logger.error("DoubleDiffTributeHandler::processPayTribute tributePlayerMap Null Error");
			return bProcessResult;
		}
		
		
		// 通知进贡  进贡最大的牌
		int nTributeBiggerFaceValue = biggerPlayer.getToolValueByInt(); 
		int nTributeSmallerFaceValue = smallerPlayer.getToolValueByInt();
		String strBiggerFailerUser = tributePlayerMap.get(nTributeBiggerFaceValue).getGamePlayerId();
		String strSmallFailerUser = tributePlayerMap.get(nTributeSmallerFaceValue).getGamePlayerId();
		
		// 记录玩家的进贡时间
		List<GamePlayer> tributePlayerList = new ArrayList<>(); 
		tributePlayerList.add(tributePlayerMap.get(nTributeBiggerFaceValue));
		tributePlayerList.add(tributePlayerMap.get(nTributeSmallerFaceValue));
		this.recordPayTributeAndNotifyClient(tributePlayerList, currentTeamGroup);
		
		Map<Integer, String>  rankAndPlayerIdMap = this.getRankNumberAndUserIdMap(winnerTeam);
		if(null == rankAndPlayerIdMap){
			logger.error("DoubleDiffTributeHandler::processPayTribute rankAndPlayerIdMap Null Error");
			return bProcessResult;
		}
		
		PayTributeData tributeData = currentRoomGame.getPayTributeData();
		if(null == tributeData){
			logger.error("DoubleDiffTributeHandler::processPayTribute tributeData Null Error");
			return bProcessResult;
		}
		
		// 存储进贡数据  做服务器校验
		String strFirstPlayer  = rankAndPlayerIdMap.get(1);
		String strSecondPlayer = rankAndPlayerIdMap.get(2); 
		tributeData.addNewPayTributeData(strBiggerFailerUser, nTributeBiggerFaceValue, rankAndPlayerIdMap.get(1));
		tributeData.addNewPayTributeData(strSmallFailerUser, nTributeSmallerFaceValue, rankAndPlayerIdMap.get(2));
		
		// 由贡打牌者起牌
		tributeData.initNewGameFirstPlayer(strBiggerFailerUser);
		
		System.out.println("进贡------" + strBiggerFailerUser + "向" + strFirstPlayer + "进贡" + nTributeBiggerFaceValue);
		System.out.println("进贡------" + strSmallFailerUser + "向" + strSecondPlayer + "进贡" + nTributeSmallerFaceValue);
		System.out.println("这一局起牌者" + strBiggerFailerUser);
		
		bProcessResult = true;
		return bProcessResult;
	}
	
	
	private Map<Integer, GamePlayer> getTributePlayerMap(Team failerTeam, int nMajorPorkerValue, IntToolUtils biggerPlayer, IntToolUtils smallerPlayer){
		
		if((null == biggerPlayer) || (null == smallerPlayer)){
			logger.error("DoubleDiffTributeHandler::getTributePlayerMap biggerPlayer Or  smallerPlayer Null Error");
			return null;
		}
		
		if(null == failerTeam){
			logger.error("DoubleDiffTributeHandler::getTributePlayerMap failerTeam Null Error");
			return null;
		}
		
		Map<String, GamePlayer> allTeamMember = failerTeam.getAllTeamMembers();
		if(null == allTeamMember){
			logger.error("DoubleDiffTributeHandler::getTributePlayerMap allTeamMember Null Error");
			return null;
		}
		
		Map<Integer, GamePlayer> tributePlayerMap = new HashMap<Integer, GamePlayer>();
		int nBiggerFaceValue = 0, nSmallerFaceValue = 0;
		Iterator<Map.Entry<String, GamePlayer>> iter = allTeamMember.entrySet().iterator();
		while(true == iter.hasNext()){
			Map.Entry<String, GamePlayer> entry = iter.next();
			GamePlayer player = entry.getValue();
			if(null == player){
				logger.error("DoubleDiffTributeHandler::getTributePlayerMap player Null Error");
				continue;
			}
			
			int nTempBiggestFaceValue = player.getBiggestPorkerFaceExcept(nMajorPorkerValue);
			tributePlayerMap.put(nTempBiggestFaceValue, player);
			
			
			if(nTempBiggestFaceValue > nBiggerFaceValue){
				// 比最大的还要大
				nSmallerFaceValue = nBiggerFaceValue;
				nBiggerFaceValue  = nTempBiggestFaceValue;
				
			}else if((nTempBiggestFaceValue <= nBiggerFaceValue) && (nTempBiggestFaceValue > nSmallerFaceValue)){
				nSmallerFaceValue = nTempBiggestFaceValue;
			}
			
		}
		
		biggerPlayer.resetUtilValue(nBiggerFaceValue);
		smallerPlayer.resetUtilValue(nSmallerFaceValue);
		return tributePlayerMap;
	}
	
	
}
