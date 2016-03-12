package com.lbwan.game.room.gameTeam;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.lbwan.game.room.roomGame.GamePlayer;
import com.lbwan.game.utils.GDPropertiesUtils;
import com.lbwan.game.porkerEnumSet.FaceValueEnum;


public class Team {
	private Log logger = LogFactory.getLog(getClass());
	
	// 该队伍的主牌
	private int majorFaceValue = GDPropertiesUtils.getPropertyAsInteger(GDPropertiesUtils.DEFAULT_MAJOR_CARD);;
	
	private boolean winTheGame = false;
	
	// 队伍
	private Map<String, GamePlayer> gamePlayerMap = new HashMap<String, GamePlayer>();
	
	public Team(String strPlayerOne, String strPlayerTwo, TeamGroup teamGroup){
		GamePlayer playerOne = new GamePlayer(strPlayerOne, teamGroup);
		playerOne.initTeamPlayer(strPlayerTwo);
		gamePlayerMap.put(strPlayerOne, playerOne);
		
		GamePlayer playerTwo = new GamePlayer(strPlayerTwo, teamGroup);
		playerTwo.initTeamPlayer(strPlayerOne);
		gamePlayerMap.put(strPlayerTwo, playerTwo);
		
		majorFaceValue = 2;
		
		winTheGame = false;
	}
	

	public void initControlNextPlayer(String strControlerId, String strNextUserId){
		GamePlayer controlPlayer = gamePlayerMap.get(strControlerId);
		if(null == controlPlayer){
			logger.error("Team::initControlNextPlayer controlPlayer Null Error");
			return ;
		}
		
		controlPlayer.initNextPlayerUserId(strNextUserId);
	}
	
	public GamePlayer searchPlayer(String strSearchUser){
		GamePlayer controlPlayer = gamePlayerMap.get(strSearchUser);
		if(null == controlPlayer){
			return null;
		}
		
		return controlPlayer;
	}
	
	public int getMajorFace(){
		return this.majorFaceValue;
	}
	
	public void resetNewMajorFaceValue(int nNewMajorFaceValue){
		this.majorFaceValue = nNewMajorFaceValue;
	}
	
	public int getHandOutPorkerNumOfTeam(){
		int nHandOutPlayers = 0;
		Iterator<Map.Entry<String, GamePlayer>> iter = gamePlayerMap.entrySet().iterator();
		while(true == iter.hasNext()){
			Map.Entry<String, GamePlayer> entry = iter.next();
			GamePlayer player = entry.getValue();
			if(null == player){
				logger.error("Team::initControlNextPlayer player Null Error");
				continue;
			}
			
			if(0 == player.getPlayerHandPorkerNum()){
				nHandOutPlayers = nHandOutPlayers + 1;
				continue;
			}
		}
		
		return nHandOutPlayers;
	}
	
	public int teamPlayerNum(){
		return gamePlayerMap.size();
	}
	
	public void winTheGame(){
		this.winTheGame = true;
		
		Iterator<Map.Entry<String, GamePlayer>> iter = gamePlayerMap.entrySet().iterator();
		while(true == iter.hasNext()){
			Map.Entry<String, GamePlayer> entry = iter.next();
			GamePlayer player = entry.getValue();
			if(null == player){
				logger.error("Team::winTheGame player Null Error");
				continue;
			}
			
			player.winTheGameOfThisRound();
		}
	}
	
	public boolean isTeamWinTheGame(){
		if(true == this.winTheGame){
			return true;
		}
		
		return false;
	}
	
	public void initNewRound(){
		winTheGame = false;
		
		Iterator<Map.Entry<String, GamePlayer>> iter = gamePlayerMap.entrySet().iterator();
		while(true == iter.hasNext()){
			Map.Entry<String, GamePlayer> entry = iter.next();
			GamePlayer player = entry.getValue();
			if(null == player){
				logger.error("Team::initNewRound() player Null Error");
				continue;
			}
			
			// 初始化
			player.initPlayerData();
		}
	}
	
	// 计算排位
	public void calculateRank(TeamGroup teamGroup){
		if(null == teamGroup){
			logger.error("Team::calculateRank teamGroup Null Error");
			return ;
		}
		
		GamePlayer minPlayer = new GamePlayer("MinPlayer", null);
		minPlayer.resetToTestGamePlayer();
		
		GamePlayer secondMinPlayer = new GamePlayer("SecondMinPlayer", null);
		secondMinPlayer.resetToTestGamePlayer();
		
		Iterator<Map.Entry<String, GamePlayer>> iter = gamePlayerMap.entrySet().iterator();
		while(true == iter.hasNext()){
			Map.Entry<String, GamePlayer> entry = iter.next();
			GamePlayer player = entry.getValue();
			if(null == player){
				logger.error("Team::calculateRank player Null Error");
				continue;
			}
			
			// 手上没有牌了  则直接continue;
			int nHandPorkerNum = player.getPlayerHandPorkerNum();
			if(0 == nHandPorkerNum){
				continue;
			}
			
			// 小于最小值
			if(nHandPorkerNum < minPlayer.getPlayerHandPorkerNum()){
				secondMinPlayer = minPlayer;
				minPlayer = player;
			}else if((nHandPorkerNum >= minPlayer.getPlayerHandPorkerNum()) && (nHandPorkerNum < secondMinPlayer.getPlayerHandPorkerNum())){
				// 大于等于最小值  并且 小于最大值
				secondMinPlayer = player;
			}
		}
		
		if(false == minPlayer.isTestPlayer()){
			minPlayer.recordOutPorkerPlayer(teamGroup);
		}
		
		if(false == secondMinPlayer.isTestPlayer()){
			secondMinPlayer.recordOutPorkerPlayer(teamGroup);
		}
		
	}
	
	// 主牌做相对应的升级
	public void upMajorFaceValue(){
		int nLastHandOut = 0, nFirstHandOut = 0;
		Iterator<Map.Entry<String, GamePlayer>> iter = gamePlayerMap.entrySet().iterator();
		while(true == iter.hasNext()){
			Map.Entry<String, GamePlayer> entry = iter.next();
			GamePlayer player = entry.getValue();
			if(null == player){
				logger.error("Team::upMajorFaceValue player Null Error");
				continue;
			}
			
			int nOutPorkerRank = player.getOutAllPorkerRank();
			if(1 == nOutPorkerRank){
				nFirstHandOut = nFirstHandOut + 1;
				continue;
			}
			
			nLastHandOut = nOutPorkerRank;
		}
		
		if(1 != nFirstHandOut){
			logger.error("Team::upMajorFaceValue server Error");
			return ;
		}
		
		int nNewMajorFaceValue = this.calcuNewMajorValue(nLastHandOut);
		if(0 == nNewMajorFaceValue){
			logger.error("Team::upMajorFaceValue server Error Of calcuNewMajorValue");
			return ;
		}
		
		this.resetNewMajorFaceValue(nNewMajorFaceValue);
	}
	
	private int calcuNewMajorValue(int nLastHandOut){
		int nAddValue = 0;
		switch(nLastHandOut){
		// 第二个出完牌  则升三级
		case 2:
			nAddValue = 3;
			break;
			
		case 3:
			nAddValue = 2;
			break;
			
		case 4:
			nAddValue = 1;
			break;
			
		default:
			logger.error("Team::calcuNewMajorValue server Error");
			break;
		}
		
		// 判断是否达到A
		int nCurrentMajorValue = this.getMajorFace();
		if(FaceValueEnum.FACE_VALUE_A_VALUE == nCurrentMajorValue){
			return (nAddValue + 1);
		}
		
		int nNewFaceValue = nCurrentMajorValue + nAddValue;
		// A必须打
		if(nNewFaceValue >= FaceValueEnum.FACE_VALUE_A_VALUE){
			nNewFaceValue = FaceValueEnum.FACE_VALUE_A_VALUE;
			return nNewFaceValue;
		}
		
		return nNewFaceValue;
	}
	
	// 是否是双下
	public boolean isDoubleLow(){
		// 赢得比赛 则不是
		boolean bIsDoubleLowResult = false;
		if(true == this.isTeamWinTheGame()){
			return bIsDoubleLowResult;
		}
		
		Set<Integer> doubelLowSet = new HashSet<>();
		doubelLowSet.add(3);
		doubelLowSet.add(4);
		
		
		Iterator<Map.Entry<String, GamePlayer>> iter = gamePlayerMap.entrySet().iterator();
		while(true == iter.hasNext()){
			Map.Entry<String, GamePlayer> entry = iter.next();
			GamePlayer player = entry.getValue();
			if(null == player){
				logger.error("Team::isDoubleLow() player Null Error");
				continue;
			}
			
			boolean bIsDouble = doubelLowSet.contains(player.getOutAllPorkerRank());
			if(false == bIsDouble){
				return bIsDoubleLowResult;
			}
		}
		
		bIsDoubleLowResult = true;
		return bIsDoubleLowResult;
	}
	
	public Map<String, GamePlayer> getAllTeamMembers(){
		return gamePlayerMap;
	}
}
