package com.lbwan.game.porkerComparer;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lbwan.game.room.gameTeam.TeamGroup;
import com.lbwan.game.room.roomGame.GamePlayer;
import com.lbwan.game.room.roomGame.RoomGame;
import com.lbwan.game.spring.SpringUtils;
import com.lbwan.game.porkerEnumSet.PorkerValueEnum;

public class GameComparer {
	
	@Autowired
	private ComparerManager comparer = (ComparerManager) SpringUtils.getBeanByName("comparerManager");
	
	private String maxPorkerUserId = null;
	
	private List<Integer> maxPorkerArray = new ArrayList<>(); 
	
	private RoomGame currentRoomGame = null;
	
	// 是否是游戏刚开始   如果为true则为游戏刚开始   为false为游戏已经开始了一段时间了.
	private boolean gameStartTag = true;
	
	// 前两未未类型  中间两位为张数   最后两位为值
	private int lastMaxHandPorker = 0;
	
	private int tempCompareBigHandPorker = 0;
	
	private Log logger = LogFactory.getLog(getClass());
	
	public GameComparer(RoomGame roomGameParam){
		this.currentRoomGame = roomGameParam;
	}
	
	public void turnToTeamer(){
		gameStartTag = true;
	}
	
	public void initNewGameRound(){
		this.gameStartTag = true;
		maxPorkerUserId = null;
		lastMaxHandPorker = 0;
		tempCompareBigHandPorker = 0;
	}
	
	public String getMaxPorkerPlayerId(){
		return this.maxPorkerUserId;
	}
	
	public List<Integer> getMaxPorkerValueList(){
		return this.maxPorkerArray;
	}
	
	public int getLastMaxHandPorker(){
		return this.lastMaxHandPorker;
	}
	
	public boolean updateMaxPorkerByHostingStatus(String strUpdateUserId, List<Integer> sumbitPorkerList, int nMaxHandPattern){
		if(null == strUpdateUserId){
			logger.error("PorkerCompareLogic::updateMaxPorkerByHostingStatus strUpdateUserId Null Error");
			return false;
		}
		
		if(null == sumbitPorkerList){
			logger.error("PorkerCompareLogic::updateMaxPorkerByHostingStatus sumbitPorkerList Null Error");
			return false;
		}
		
		TeamGroup teamGroup = currentRoomGame.getTeamGroup();
		if(null == teamGroup){
			logger.error("PorkerCompareLogic::updateMaxPorkerByHostingStatus teamGroup Null Error");
			return false;
		}
		

		GamePlayer currentPlayer = teamGroup.searchGamePlayerByUserId(strUpdateUserId);
		if(null == currentPlayer){
			logger.error("PorkerCompareLogic::updateMaxPorkerByHostingStatus currentPlayer Null Error");
			return false;
		}
		
		currentPlayer.updatePorkerArray(sumbitPorkerList, teamGroup);
		
		maxPorkerUserId = strUpdateUserId;
		maxPorkerArray = sumbitPorkerList;
		lastMaxHandPorker = nMaxHandPattern;
		tempCompareBigHandPorker = 0;
		
		gameStartTag = false;
		return true;
	}
	
	public boolean updateMaxPorker(String strUpdateUserId, List<Integer> sumbitPorkerList){
		if(null == strUpdateUserId){
			logger.error("PorkerCompareLogic::updateMaxPorker strUpdateUserId Null Error");
			return false;
		}
		
		if(null == sumbitPorkerList){
			logger.error("PorkerCompareLogic::updateMaxPorker sumbitPorkerList Null Error");
			return false;
		}
		
		TeamGroup teamGroup = currentRoomGame.getTeamGroup();
		if(null == teamGroup){
			logger.error("PorkerCompareLogic::updateMaxPorker teamGroup Null Error");
			return false;
		}
		

		GamePlayer currentPlayer = teamGroup.searchGamePlayerByUserId(strUpdateUserId);
		if(null == currentPlayer){
			logger.error("PorkerCompareLogic::updateMaxPorker currentPlayer Null Error");
			return false;
		}
		
		currentPlayer.updatePorkerArray(sumbitPorkerList, teamGroup);
		
		maxPorkerUserId = strUpdateUserId;
		maxPorkerArray = sumbitPorkerList;
		lastMaxHandPorker = tempCompareBigHandPorker;
		tempCompareBigHandPorker = 0;
		
		gameStartTag = false;
		return true;
	}
	
	
	
	public boolean isMustHandPlayerPorker(String strControlUser){
		if((true == gameStartTag) || (true == strControlUser.equals(maxPorkerUserId))){
			return true;
		}
		
		return false;
	}
	
	public boolean handleMustHandPorkerStatus(String strControlUser){
		boolean bErrorResult = false;
		GamePlayer player = currentRoomGame.searchGamePlayerByUserId(strControlUser);
		if(null == player){
			logger.error("PorkerCompareLogic::handleMustHandPorkerStatus sumbitPorkerList.size() server error");
			return bErrorResult;
		}
		
		List<Integer> minHandPorker = player.getMustSumbitMinPorker(currentRoomGame.getCurrentMajorCard());
		if(null == minHandPorker){
			logger.error("UserOperationTask::handleMustHandPorkerStatus minHandPorker Null Error");
			return bErrorResult;
		}
		
		int nSumbitCardType = comparer.checkTheCardTypeByPorker(minHandPorker, currentRoomGame.getCurrentMajorCard());
		this.updateMaxPorkerByHostingStatus(strControlUser, minHandPorker, nSumbitCardType);
		currentRoomGame.gameRoundEndAction(player, minHandPorker);
		
		/*StringBuffer printBuffer = new StringBuffer();
    	printBuffer.append(strControlUser + " Server Sumbit Porker When TimeOut ");
    	for(int i = 0; i < minHandPorker.size(); ++i){
    		int nPorkerValue = minHandPorker.get(i);
    		String strPorkerColor = PorkerValueEnum.getColorByPorkValue(nPorkerValue);
			int nPorkerFaceValue = PorkerValueEnum.getFaceValueByPorkerValueOf(nPorkerValue);
			printBuffer.append("   " + strPorkerColor.toString() + "  " + nPorkerFaceValue + "  ;");
    	}
    	System.out.println(printBuffer.toString());*/
    	
    	
		bErrorResult = true;
		return bErrorResult;
	}
	
	public boolean compareMaxPorker(String strCompareUserId, List<Integer> sumbitPorkerList/*, int nCurrentMajorCard*/){
		// 游戏开始后的第一次
		int nSumbitPorkrNum = sumbitPorkerList.size();
		if((nSumbitPorkrNum < 1) || (nSumbitPorkrNum > 10)){
			logger.error("PorkerCompareLogic::compareMaxPorker sumbitPorkerList.size() server error");
			return false;
		}
		
		// 判断
		int nMaxPorkerNum = maxPorkerArray.size();
		if(nMaxPorkerNum > 10){
			logger.error("PorkerCompareLogic::compareMaxPorker maxPorkerArray.size() error");
			return false;
		}
				
		// 同一次  或者 已经一轮了没有玩家大过自己的牌
		int nCurrentMajorCard = this.currentRoomGame.getCurrentMajorCard();
		boolean bOnlyCheckCardType = this.isMustHandPlayerPorker(strCompareUserId);
		int nCompareHandPattern = comparer.compareMaxPorkerWithSumbitPorker(bOnlyCheckCardType, lastMaxHandPorker, sumbitPorkerList, nCurrentMajorCard);
		

		if(0 == nCompareHandPattern){
			return false;
		}
		
		// 是的话将手中的牌记录在案
		tempCompareBigHandPorker = nCompareHandPattern;
		return true;
	}
	
	// 供给外部测试的代码
	public boolean initTestCodeData(boolean bGameStartTag, int nlastMaxPorkerParam){
		gameStartTag = bGameStartTag;
		maxPorkerUserId = "xiaoming";
		//Integer nTestCode = 0;
		//maxPorkerArray = new ArrayList<>();
		//maxPorkerArray.add(nTestCode);
		
		String strCompareUserId = "XiaoJiang";
		lastMaxHandPorker = nlastMaxPorkerParam;
		return true;
	}
	
}
