/**
 * 
 */
package com.lbwan.game.room.roomGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.lbwan.game.channel.ChannelManager;
import com.lbwan.game.proto.CmdProtocol.CmdType;
import com.lbwan.game.proto.WhippedEgg.GameResultCode;
import com.lbwan.game.proto.WhippedEgg.GameUserPorkerNum;
import com.lbwan.game.proto.WhippedEgg.SServerUpdateGold;
import com.lbwan.game.proto.WhippedEgg.SSumbitPorkerResultResponse;
import com.lbwan.game.room.gameTeam.Team;
import com.lbwan.game.room.gameTeam.TeamGroup;
import com.lbwan.game.room.payTributeData.TributePorkerEnum;
import com.lbwan.game.spring.SpringUtils;
import com.lbwan.game.userGoldDao.GameUserGoldService;
import com.lbwan.game.utils.GDPropertiesUtils;
import com.lbwan.game.utils.PropertiesUtils;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lbwan.game.porker.PorkerMgr;
import com.lbwan.game.porkerEnumSet.CardColorEnum;
import com.lbwan.game.porkerEnumSet.FaceValueEnum;
import com.lbwan.game.porkerEnumSet.PorkerValueEnum;
import com.lbwan.gamecenter.remoting.entity.GameDataInfo;
import com.lbwan.gamecenter.remoting.spi.GameDataService;

/**
 * @author zhengzuhuang
 *
 */


public class GamePlayer{
	@Autowired
	protected PorkerMgr porkerManager = (PorkerMgr) SpringUtils.getBeanByName("porkerMgr");
	
	@Autowired
	protected GameUserGoldService userGoldService = (GameUserGoldService)SpringUtils.getBeanByName("gameUserGoldService");
	
	private String gameUserId = null;
	
	private Log logger = LogFactory.getLog(getClass());
	
	private List<Integer> handPorkerArray = null;
	
	private String nextPlayerUserId = null;
	private String teamPlayerUserId = null;

    // 玩家第几个出完牌
    private int handOutPorkerRank = 0;
     
    private GameResultCode resultCode = GameResultCode.GRC_FAIL;
    
    private boolean autoExitGameBySelf = false;
    
    // 是否是测试玩家
    private boolean isTestPlayer = false;
    
    // 是否是托管状态
    private boolean isHostingStatus = false;
    
    // 队伍
    private TeamGroup playerTeamGroup = null;
  
    // 金蛋
    private int playerGold = 0;
    
    // 玩家的场次
    GamePlayerRound playerRound = null; 
    
    @Autowired
    private GameDataService gameDataService = (GameDataService)SpringUtils.getBeanByName("gameDataService");
    
    private String gameTag = PropertiesUtils.getPropertyAsString(PropertiesUtils.ZK_GAME_TAG);
    
    
    public GamePlayer(String strUserId, TeamGroup groupOfTeam){
		this.gameUserId = strUserId;
		if(null == this.gameUserId){
			logger.error("GamePlayer::GamePlayer strUserId Null Error");
		}
		
		playerRound = new GamePlayerRound(strUserId);
		if(null == this.playerRound){
			logger.error("GamePlayer::GamePlayer playerRound Null Error");
		}

		this.playerTeamGroup = groupOfTeam;
		//playerGold = userGoldService.getUserGoldFromRedis(strUserId);
		this.initPlayerData();
		
		//GameDataInfo dataInfo = gameDataService.getGameDataInfo(strUserId, gameTag);
	    //playerRound.initGameWithDataInfo(dataInfo);
	}
	
    public int getHandPorkerSize(){
    	return this.handPorkerArray.size();
    }
    
    public int getPorkerValueByIndex(int nIndex){
    	if((nIndex < 0) || (nIndex >= this.getHandPorkerSize())){
    		logger.error("GamePlayer::getPorkerValueByIndex Null Error Index: " + nIndex);
    		return 0;
    	}
    	
    	return this.handPorkerArray.get(nIndex);
    }
    
    
    public void addWinRound(){
    	playerRound.addWinTotalRound();
    }
    
    public void addFailRound(){
    	playerRound.addFailTotalRound();
    }
    
    public void addEscapeRound(){
    	playerRound.addEscapeTotalRound();
    }
    
    
	public void resetToTestGamePlayer(){
		isTestPlayer = true;
		isHostingStatus = false;
	}
	
	public void addPlayerGold(int nAwardGold){
		/*
		int nResultTotalGold = playerGold + nAwardGold;
		if(nResultTotalGold < 0){
			nResultTotalGold = 0;
		}
		
		// 内存
		playerGold = nResultTotalGold;
		
		// 上传到redis服务器
		userGoldService.setUserGoldByUserId(gameUserId, nResultTotalGold);
		
		SServerUpdateGold.Builder updateGoldBuilder = SServerUpdateGold.newBuilder();
		updateGoldBuilder.setStrUserId(gameUserId);
		updateGoldBuilder.setNGoldValue(nResultTotalGold);
		playerTeamGroup.nofityAllOnLineUser(CmdType.CMD_GOLD_SYNC_TO_CLIENT_VALUE, updateGoldBuilder.build().toByteArray());
		*/
	}
	
	
	public boolean isAutoExitBySelf(){
		return true == autoExitGameBySelf;
	}
	
	public void setPlayerIsAutoExitGame(){
		autoExitGameBySelf = true;
	}
	
	public void initPlayerData(){
		autoExitGameBySelf = false;
		isHostingStatus = false;
		
		this.handOutPorkerRank = 0;
		
        resultCode = GameResultCode.GRC_FAIL;
	}
	
	public boolean initPlayerHandPorker(List<Integer> playerPorkerArray){
		if(null == playerPorkerArray){
			logger.error("GamePlayer::initPlayerHandPorker playerPorkerArray Null Error");
			return false;
		}
		
		this.handPorkerArray = playerPorkerArray;
		Collections.sort(this.handPorkerArray);
		return true;
	}
	
	public void initNextPlayerUserId(String strNextPlayerId){
		this.nextPlayerUserId = strNextPlayerId;
	}
	
	public void initTeamPlayer(String strTeamPlayerId){
		teamPlayerUserId = strTeamPlayerId;
	}
	
	public String getGamePlayerId(){
		return this.gameUserId;
	}
	
	public int getPlayerHandPorkerNum(){
		if(true == isTestPlayer){
			int nOnePairPorker = PorkerValueEnum.getMaxPorkerValue();
			int nMaxPorker = (nOnePairPorker * 2) + 1;
			return nMaxPorker;
		}
		
		return this.handPorkerArray.size();
	}
	
	public List<Integer> getHandPorkerArray(){
		return this.handPorkerArray;
	}
	
	
	public String getNextPlayerId(){
		return this.nextPlayerUserId;
	}
	
	public String getTeamPlayerId(){
		return this.teamPlayerUserId;
	}
	
	public GamePlayerRound getGamePlayerRound(){
		return this.playerRound;
	}
	
	
	public boolean checkIsExistPorkerArray(List<Integer> comparePorkerList){
		
		int nComparePorkerNum = comparePorkerList.size();
		if(0 == nComparePorkerNum){
			return true;
		}
		
		
		int nStartIndex = 0;
		int nHandPorkerNum = this.handPorkerArray.size();
		for(int i = 0; i < nHandPorkerNum; ++i){
			int nHandPorkValue = handPorkerArray.get(i);
			int nComparePorkValue = comparePorkerList.get(nStartIndex);
			if(nHandPorkValue != nComparePorkValue){
				continue;
			}
			
			nStartIndex = nStartIndex + 1;
			if(nStartIndex >= nComparePorkerNum){
				return true;
			}
		}
		
		
		if(nStartIndex >= nComparePorkerNum){
			return true;
		}
		
		return false;
	}
	
	public boolean updatePorkerArray(List<Integer> sumbitPokerList, TeamGroup teamGroup){
		if(null == teamGroup){
			logger.error("GamePlayer::updatePorkerArray strUserId teamGroup Error");
			return false;
		}
		
		if(null == sumbitPokerList){
			logger.error("GamePlayer::updatePorkerArray strUserId sumbitPokerList Error");
			return false;
		}
		
		if(true == sumbitPokerList.isEmpty()){
			logger.error("GamePlayer::updatePorkerArray sumbitPokerList.isEmpty() Error");
			return false;
		}
		
		if(null == handPorkerArray){
			logger.error("GamePlayer::updatePorkerArray handPorkerArray Null Error");
			return false;
		}
		
	
		// 删除手中的牌
		
		List<Integer> delePorkers = new ArrayList<>();
		
		int nSumbitSize = sumbitPokerList.size();
		for(int i = 0; i < nSumbitSize; ++i){
			
			Iterator<Integer> it = handPorkerArray.iterator();
			while(it.hasNext()){
				Integer nTempHandPorkValue = it.next();
				if(nTempHandPorkValue != sumbitPokerList.get(i)){
					continue;
				}
				
				delePorkers.add(nTempHandPorkValue);
				
				// 如果删除 则应该跳出内层循环  一个数只能删除一次
				it.remove();
				break;
			}
			
		}
		
		// 测试代码
		StringBuffer delePorker = new StringBuffer();
		delePorker.append("玩家出的牌  : " + this.getGamePlayerId() + "  张数--" + delePorkers.size() + "   服务器删除的牌 ");
		for(int i = 0; i < delePorkers.size(); ++i){
			delePorker.append(PorkerValueEnum.getColorByPorkValue(delePorkers.get(i)) + "  " + PorkerValueEnum.getFaceValueByPorkerValueOf(delePorkers.get(i))+"  ,");
		}
		System.out.println(delePorker.toString());
		
		
		StringBuffer userPorkerInHand = new StringBuffer();
		userPorkerInHand.append("玩家手上: " + this.getGamePlayerId() + "  张数--" + handPorkerArray.size() +  "还剩下的牌:");
		for(int i = 0; i < handPorkerArray.size(); ++i){
			userPorkerInHand.append(PorkerValueEnum.getColorByPorkValue(handPorkerArray.get(i)) + "  " + PorkerValueEnum.getFaceValueByPorkerValueOf(handPorkerArray.get(i))+"  ,");
		}
		System.out.println(userPorkerInHand.toString());
		
		
		this.notifySumbitResultToClient(sumbitPokerList);
		
		// 手牌为0的情况下
		if(true == handPorkerArray.isEmpty()){
			
			// 若是第一个先出完牌的  则分出胜负
			int nPlayerRankOfGame = teamGroup.getHandOutPorkerGameRank();
			if(1 == nPlayerRankOfGame){
				Team controlTeam = teamGroup.getTeamOfControlUser();
				if(null == controlTeam){
					logger.error("GamePlayer::updatePorkerArray controlTeam Null Error");
					return false;
				}
				
				// 设置其为赢得比赛
				controlTeam.winTheGame();
				System.out.println("Player: " + this.getGamePlayerId() + "  Win The Game ");
			}
			
			
			this.recordOutPorkerPlayer(teamGroup);
		}
		
		return true;
	}
	
	
	 public boolean getOnLinePlayStatus(){
		 // 主动退出房间  则不能再次进入游戏中
		 if( true == isAutoExitBySelf()){
			 return false;
		 }
		 
		 
		 boolean bUserOnline = ChannelManager.isUserOnLine(gameUserId);
	     return bUserOnline;
	 }
	 
	 public void recordOutPorkerPlayer(TeamGroup teamGroup){
		 if(null == teamGroup){
			 logger.error("GamePlayer::recordOutPorkerPlayer teamGroup null Error ");
			 return ;
		 }
		 
		 this.handOutPorkerRank = teamGroup.getHandOutPorkerGameRank();
		 teamGroup.incHandOutRank();
		 System.out.println("Player: " + this.getGamePlayerId() + "   RankNo: " + this.handOutPorkerRank);
	 }
	 
	 public int getOutAllPorkerRank(){
		 return this.handOutPorkerRank;
	 }
	 
	 
	 public void winTheGameOfThisRound(){
		 resultCode = GameResultCode.GRC_SUCCEED;
	 }
	 
	 public GameResultCode getGameResult(){
		 return this.resultCode;
	 }
	 
	 public boolean isTestPlayer(){
		 if(true == isTestPlayer){
			 return true;
		 }
		 
		 return false;
	 }
	 
	 public List<Integer> getMustSumbitMinPorker(int nMajorFaceValue){
		 // 去单根~~双根最小的牌
		 if(true == handPorkerArray.isEmpty()){
			 logger.error("GamePlayer::updatePorkerArray controlTeam Null Error");
			 return null;
		 }
		 
		 
		 // 过滤主牌
		 //int nSelectFaceValue = 0;
		 int nSumbitPorkerValue = 0;
		 int nStartIndex = -1;
		 int nHandPorkerNum = handPorkerArray.size();
		 for(int i = 0; i < nHandPorkerNum; ++i){
			 int nTempPorkerValue = handPorkerArray.get(i);
			 int nTempFaceValue = porkerManager.getFaceValue(nTempPorkerValue);
			 
			 if(nMajorFaceValue != nTempFaceValue){
				 nStartIndex = i;
				 //nSelectFaceValue = nTempFaceValue;
				 nSumbitPorkerValue = nTempPorkerValue;
				 break;
				 
			 }
			 
		 }
		 
		 // 将剩下的手牌抛出
		 if((0 == nSumbitPorkerValue) || (-1 == nStartIndex)){
			 return handPorkerArray;
		 }
		 
		 
		 List<Integer> minSumbitList = new ArrayList<>();
		 minSumbitList.add(nSumbitPorkerValue);
		 
		 // 辅助测试函数
		StringBuffer printBuffer = new StringBuffer();
	    printBuffer.append(this.getGamePlayerId() + " :getMustSumbitMinPorker Have Porker List: ");
	    for(int i = 0 ; i < handPorkerArray.size(); ++i){
	    	printBuffer.append("  " + handPorkerArray.get(i) + " ;");
	    }
	    
	    //System.out.println(printBuffer.toString());
		return minSumbitList;
	 }
	 
	 public int calcuPorkerNumByPorkerValue(int nPorkerValue){
		 int nPorkerNum = 0;
		 if(null == handPorkerArray){
			 logger.error("GamePlayer::calcuPorkerNumByPorkerValue handPorkerArray Null Error");
			 return nPorkerNum;
		 }
		 
		 if(true == handPorkerArray.isEmpty()){
			 logger.error("GamePlayer::calcuPorkerNumByPorkerValue handPorkerArray.isEmpty() Error");
			 return nPorkerNum;
		 }
		 
		 int nHandPorkerSize = handPorkerArray.size();
		 for(int i = 0; i < nHandPorkerSize; ++i){
			 int nTempPorkerValue = handPorkerArray.get(i);
			 if(nTempPorkerValue == nPorkerValue){
				 nPorkerNum = nPorkerNum + 1;
			 }
		 }
		 
		 return nPorkerNum;
	 }
	 
	
	 // 双下 时   进贡需要考虑 主牌的红桃
	 public int getBiggestPorkerFaceExcept(int nMajorPorkerValue){
		 List<Integer> tempPorkerArray = new ArrayList<>();
		 int nMajorFaceValue = PorkerValueEnum.getFaceValueByPorkerValueOf(nMajorPorkerValue);
		 int nMajorFaceNum = 0;
		 
		 int nHandPorkerSize = handPorkerArray.size();
		 for(int i = nHandPorkerSize-1; i >= 0; i--){
			 int nTempPorkerValue = handPorkerArray.get(i);
			 if(nMajorPorkerValue == nTempPorkerValue){
				 continue;
			 }
			 
			 int nTempFaceValue = PorkerValueEnum.getFaceValueByPorkerValueOf(nTempPorkerValue);
			 if(nTempFaceValue == nMajorFaceValue){
				 nMajorFaceNum = nMajorFaceNum + 1;
			 }
			 
			 tempPorkerArray.add(nTempPorkerValue);
		 }
		 
		 if(true == tempPorkerArray.isEmpty()){
			 int nErrorResult = 0;
			 return nErrorResult;
		 }
		 
		 
		 int nMaxPorkValue = tempPorkerArray.get(0);
		 int nMaxPorkerFaceValue = PorkerValueEnum.getFaceValueByPorkerValueOf(nMaxPorkValue);
		 if((true == FaceValueEnum.isBelongToBigKing(nMaxPorkerFaceValue)) || (true == FaceValueEnum.isBelongToSmallKing(nMaxPorkerFaceValue))){
			 return nMaxPorkerFaceValue;
		 }
		 
		 if(nMajorFaceNum >= 1){
			 nMaxPorkerFaceValue = nMajorFaceValue;
			 return nMaxPorkerFaceValue;
		 }
		 
		 return nMaxPorkerFaceValue;
	 }
	 
	 public int getPorkerValueByFaceValue(int nNeedFaceValue, int nMajorFaceValue){
		 // 是否排除红桃
		 boolean bExceptHeart = false;
		 if(nMajorFaceValue == nNeedFaceValue){
			 bExceptHeart = true;
		 }
		 
		 int nHandPorkerSize = handPorkerArray.size();
		 for(int j = nHandPorkerSize-1, i = 0; j >= i; --j, ++i){
			 // j
			 int nTempEndPorkerValue = handPorkerArray.get(j);
			 nTempEndPorkerValue = helpGetPorkerValueByFaceValue(nTempEndPorkerValue, nNeedFaceValue, bExceptHeart);
			 if(0 != nTempEndPorkerValue){
				 return nTempEndPorkerValue;
			 }
			 
			 // i
			 int nTempBeginPorkerValue = handPorkerArray.get(i);
			 nTempBeginPorkerValue = helpGetPorkerValueByFaceValue(nTempBeginPorkerValue, nNeedFaceValue, bExceptHeart);
			 if(0 != nTempBeginPorkerValue){
				 return nTempBeginPorkerValue;
			 }
		 }
		 
		 int nErrorResult = 0;
		 return nErrorResult;
	 }
	 
	 private int helpGetPorkerValueByFaceValue(int nTempEndPorkerValue, int nNeedFaceValue, boolean bExceptHeart){
		 int nTempEndFaceValue = PorkerValueEnum.getFaceValueByPorkerValueOf(nTempEndPorkerValue);
		 if(nTempEndFaceValue == nNeedFaceValue){
			 if(false == bExceptHeart){
				 return nTempEndPorkerValue;
			 }
			 
			 // 排除红桃的情况下
			 int nColor = PorkerValueEnum.getCardColorByPorkerValue(nTempEndPorkerValue);
			 if(CardColorEnum.CARD_COLOR_HEART != nColor){
				 return nTempEndPorkerValue;
			 }
		 }
		 
		 int nErrorResult = 0;
		 return nErrorResult;
	 }
	 
	 // 删除手牌
	 public boolean delPorkerValueFromHandPorker(int nPorkerValue){
		 boolean bDelPoerkResult = false;
		 if(null == this.handPorkerArray){
			 logger.error("GamePlayer::delPorkerValueFromHandPorker handPorkerArray Null Error");
			 return bDelPoerkResult;
		 }
		 
		 int nDelPorkerIndex = -1;
		 int nHandPorkerSize = handPorkerArray.size();
		 for(int i = 0; i < nHandPorkerSize; ++i){
			 int nTempHandPorkerValue = handPorkerArray.get(i);
			 if(nTempHandPorkerValue == nPorkerValue){
				 nDelPorkerIndex = i;
				 break;
			 }
		 }
		 
		 if(-1 == nDelPorkerIndex){
			 logger.error("GamePlayer::delPorkerValueFromHandPorker Server Error delPorkerValue: " + nPorkerValue);
			 return bDelPoerkResult;
		 }
		 
		 handPorkerArray.remove(nDelPorkerIndex);
		 bDelPoerkResult = true;
		 return bDelPoerkResult;
	 }
	 
	 
	 // 增加手牌
	 public boolean addPoerkValueToHandPorker(int nPorkerValue){
		 boolean bAddPoerkResult = false;
		 if(null == this.handPorkerArray){
			 logger.error("GamePlayer::addPoerkValueToHandPorker handPorkerArray Null Error");
			 return bAddPoerkResult;
		 }
		 
		 this.handPorkerArray.add(nPorkerValue);
		 Collections.sort(handPorkerArray);
		 bAddPoerkResult = true;
		 return bAddPoerkResult;
	 }
	 
	 // 在游戏中  是否为托管状态
	 public boolean isHostingStatusForGame(){
		 if(true == this.isHostingStatus){
			 return true;
		 }
		 
		 return false;
	 }
	 
	 // 托管游戏
	 public void hostingClientOnGame(){
		 this.isHostingStatus = true;
	 }
	 
	 // 取消托管
	 public void cancelHostingForGame(){
		 this.isHostingStatus = false;
	 }
	 
	 
	 private boolean notifySumbitResultToClient(List<Integer> sumbitPokerList){
		if(null == sumbitPokerList){
			logger.error("GamePlayer::notifySumbitResultToClient sumbitPokerList teamGroup Error");
			return false;
		}
		
		if(null == this.playerTeamGroup){
			logger.error("GamePlayer::notifySumbitResultToClient playerTeamGroup Null Error");
			return false;
		}
			
		StringBuffer porkerNumBuffer = new StringBuffer();
		porkerNumBuffer.append("玩家剩下的牌的数目:");
		List<GameUserPorkerNum.Builder> userPorkerNumList = new ArrayList<>();
		Set<String> gamePlayerContainer = this.playerTeamGroup.getAllUserSet();
		Iterator<String> iter = gamePlayerContainer.iterator();
		while(true == iter.hasNext()){
			String strPlayerId = iter.next();
			GamePlayer player = this.playerTeamGroup.searchGamePlayerByUserId(strPlayerId);
			if(null == player){
				logger.error("GamePlayer::notifySumbitResultToClient currentPlayer null Error");
				continue;
			}
	        
	        GameUserPorkerNum.Builder userPorkerNumBuilder = GameUserPorkerNum.newBuilder();
	        userPorkerNumBuilder.setStrUserId(player.getGamePlayerId());
	        userPorkerNumBuilder.setNPorkerNumInHand(player.getPlayerHandPorkerNum());
	        
	        userPorkerNumList.add(userPorkerNumBuilder);
	        
	        porkerNumBuffer.append( player.getGamePlayerId() + " PorkerNum "+ player.getPlayerHandPorkerNum() + " ,");
		}
		System.out.println(porkerNumBuffer.toString());
		
		SSumbitPorkerResultResponse.Builder sumbitResultBuilder = SSumbitPorkerResultResponse.newBuilder();
		sumbitResultBuilder.setStrCurrentController(this.getGamePlayerId());
		sumbitResultBuilder.setGameResultCode(GameResultCode.GRC_SUCCEED);
		
		StringBuffer serverTellDelePorkerBuffer = new StringBuffer();
		serverTellDelePorkerBuffer.append("服务器告诉客户端要删除的牌    玩家: " + this.getGamePlayerId()+ " 牌的数量" + sumbitPokerList.size());
		int nPorkerNum = sumbitPokerList.size();
		for(int i = 0; i < nPorkerNum; ++i){
			sumbitResultBuilder.addPorkerArray(sumbitPokerList.get(i));
			
			serverTellDelePorkerBuffer.append(PorkerValueEnum.getColorByPorkValue(sumbitPokerList.get(i)) + "  " + PorkerValueEnum.getFaceValueByPorkerValueOf(sumbitPokerList.get(i))+"  ,");
		}
		//System.out.println(serverTellDelePorkerBuffer.toString());
		
		int nHandPorkerSize = this.handPorkerArray.size();
		for(int i = 0; i < nHandPorkerSize; ++i){
			sumbitResultBuilder.addHandPorkerArray(this.handPorkerArray.get(i));
		}
		
		int nListSize = userPorkerNumList.size();
		for(int i = 0; i < nListSize; ++i){
			GameUserPorkerNum.Builder userPorkerNum = userPorkerNumList.get(i);
			sumbitResultBuilder.addUserPorkerNum(userPorkerNum);
		}
		
		this.playerTeamGroup.nofityAllOnLineUser(CmdType.CMD_SUMBIT_PORKER_RESULT_VALUE, sumbitResultBuilder.build().toByteArray());
		System.out.println(this.getGamePlayerId() + "出牌成功  更新牌");
		return true;
	}
	 
	public void showLogPlayerPorker(){
		StringBuffer handPorkerBuffer = new StringBuffer();
		handPorkerBuffer.append(this.getGamePlayerId()+ " 玩家手上的牌:" + handPorkerArray.size());
		
		int nHandPorkerSize = this.handPorkerArray.size();
		for(int i = 0; i < nHandPorkerSize; ++i){
			handPorkerBuffer.append(PorkerValueEnum.getColorByPorkValue(handPorkerArray.get(i)) + "  " + PorkerValueEnum.getFaceValueByPorkerValueOf(handPorkerArray.get(i)));
		}
		
		System.out.println(handPorkerBuffer.toString());
	}
}






