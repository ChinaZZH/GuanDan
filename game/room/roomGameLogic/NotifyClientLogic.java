package com.lbwan.game.room.roomGameLogic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;






import com.lbwan.game.proto.CmdProtocol.CmdType;
import com.lbwan.game.proto.WhippedEgg.ConnectionStatus;
import com.lbwan.game.proto.WhippedEgg.GameResultCode;
import com.lbwan.game.proto.WhippedEgg.GameUserPorker;
import com.lbwan.game.proto.WhippedEgg.GameUserPorkerNum;
import com.lbwan.game.proto.WhippedEgg.GameUserRank;
import com.lbwan.game.proto.WhippedEgg.SCutDownSecsSync;
import com.lbwan.game.proto.WhippedEgg.SEnterGameSuccess;
import com.lbwan.game.proto.WhippedEgg.SGameEndRequest;
import com.lbwan.game.proto.WhippedEgg.SPlayerGroundInfo;
import com.lbwan.game.proto.WhippedEgg.SServerNotifyPlayerRound;
import com.lbwan.game.proto.WhippedEgg.SSumbitPorkerResultResponse;
import com.lbwan.game.room.gameTeam.Team;
import com.lbwan.game.room.gameTeam.TeamGroup;
import com.lbwan.game.room.roomGame.GamePlayer;
import com.lbwan.game.room.roomGame.GamePlayerRound;
import com.lbwan.game.room.roomGame.RoomGame;
import com.lbwan.game.room.roomGameTimer.RoomGameTimer;
import com.lbwan.game.utils.DataSendUtils;
import com.lbwan.game.utils.GDPropertiesUtils;

public class NotifyClientLogic {
	private Log logger = LogFactory.getLog(getClass());
	private RoomGame roomGame = null;
	
	public NotifyClientLogic(RoomGame roomGameParam){
		this.roomGame = roomGameParam;
	}
	
	public boolean notifyPlayerRound(){
		boolean bNotifyResult = false;
		TeamGroup teamGroup = this.roomGame.getTeamGroup();
		if(null == teamGroup){
			logger.error("NotifyClientLogic::notifyPlayerRound() teamGroup Null Error");
			return bNotifyResult;
		}
		
		SServerNotifyPlayerRound.Builder playerRoundBuilder = SServerNotifyPlayerRound.newBuilder();
		
		Set<String> gamePlayerContainer = teamGroup.getAllUserSet();
		Iterator<String> iter = gamePlayerContainer.iterator();
		while(true == iter.hasNext()){
			String strPlayerId = iter.next();
			GamePlayer currentPlayer = teamGroup.searchGamePlayerByUserId(strPlayerId);
			if(null == currentPlayer){
				logger.error("NotifyClientLogic::notifyPlayerRound() currentPlayer null Error");
				continue;
			}
			
			GamePlayerRound playerRound = currentPlayer.getGamePlayerRound();
			if(null == playerRound){
				logger.error("NotifyClientLogic::notifyPlayerRound() playerRound null Error");
				continue;
			}
			
			SPlayerGroundInfo.Builder eachPlayerInfo = SPlayerGroundInfo.newBuilder();
			eachPlayerInfo.setStrUserId(strPlayerId);
			eachPlayerInfo.setNTotalGameRound(playerRound.getTotalPlayRound());
			eachPlayerInfo.setNWinGameRound(playerRound.getWinTotalRound());
			eachPlayerInfo.setNLoseGameRound(playerRound.getFailTotalRound());
			eachPlayerInfo.setNEscapeRound(playerRound.getEscapeTotalRound());
			
			playerRoundBuilder.addPlayerGround(eachPlayerInfo);
		}
		
		teamGroup.nofityAllOnLineUser(CmdType.CMD_SERVER_NOTIFY_GROUND_VALUE, playerRoundBuilder.build().toByteArray());
		bNotifyResult = true;
		return bNotifyResult;
	}
	
	
	public boolean notifyStartGame(){
		boolean bNotifyResult = false;
		TeamGroup teamGroup = this.roomGame.getTeamGroup();
		if(null == teamGroup){
			logger.error("NotifyClientLogic::notifyStartGame() teamGroup Null Error");
			return bNotifyResult;
		}
		
		
		Set<String> gamePlayerContainer = teamGroup.getAllUserSet();
		Iterator<String> iter = gamePlayerContainer.iterator();
		while(true == iter.hasNext()){
			String strPlayerId = iter.next();
			GamePlayer currentPlayer = teamGroup.searchGamePlayerByUserId(strPlayerId);
			if(null == currentPlayer){
				logger.error("NotifyClientLogic::notifyStartGame currentPlayer null Error");
				continue;
			}
			
			GameUserPorker.Builder userPorker = GameUserPorker.newBuilder();
			if(null == userPorker){
				logger.error("NotifyClientLogic::notifyStartGame userPorker null Error");
				continue;
			}
			
			GamePlayer nextPlayer = teamGroup.searchGamePlayerByUserId(currentPlayer.getNextPlayerId());
			if(null == nextPlayer){
				logger.error("NotifyClientLogic::notifyStartGame nextPlayer null Error");
				continue;
			}
			
			SEnterGameSuccess.Builder enterGameBuilder = this.getConnectionOfGameStart(teamGroup);
			if(null == enterGameBuilder){
				logger.error("NotifyClientLogic::notifyStartGame  enterGameBuilder null Error");
				return bNotifyResult;
			}
			
			boolean bResult = this.getPlayerSelfInfo(currentPlayer, enterGameBuilder);
			if(false == bResult){
				logger.error("NotifyClientLogic::notifyStartGame bResult Error");
				continue;
			}
			
			DataSendUtils.sendData(currentPlayer.getGamePlayerId(), CmdType.CMD_INIT_GAME_DESKTOP_INFO_VALUE, enterGameBuilder.build().toByteArray());
		}
		
		
		bNotifyResult = true;
		return bNotifyResult;
	}
	
	private boolean getPlayerSelfInfo(GamePlayer currentPlayer, SEnterGameSuccess.Builder enterGameBuilder){
		boolean bNotifyResult = false;
		TeamGroup teamGroup = this.roomGame.getTeamGroup();
		if(null == teamGroup){
			logger.error("NotifyClientLogic::getPlayerSelfInfo teamGroup Null Error");
			return bNotifyResult;
		}
		
		if(null == enterGameBuilder){
			logger.error("NotifyClientLogic::getPlayerSelfInfo  enterGameBuilder null Error");
			return bNotifyResult;
		}
		
		GamePlayer nextPlayer = teamGroup.searchGamePlayerByUserId(currentPlayer.getNextPlayerId());
		if(null == nextPlayer){
			logger.error("NotifyClientLogic::getPlayerSelfInfo nextPlayer null Error");
			return bNotifyResult;
		}
		
		boolean bIsMajorScene = teamGroup.isBelongToMajorScene(currentPlayer.getGamePlayerId());
		enterGameBuilder.setStrSelfUserId(currentPlayer.getGamePlayerId());
		enterGameBuilder.setStrNextUserId(currentPlayer.getNextPlayerId());
		enterGameBuilder.setStrTeamUserId(currentPlayer.getTeamPlayerId());
		enterGameBuilder.setStrLastUserId(nextPlayer.getTeamPlayerId());
		enterGameBuilder.addAllPorkerArray(currentPlayer.getHandPorkerArray());	
		enterGameBuilder.setBIsMajorScene(bIsMajorScene);
		
		bNotifyResult = true;
		return bNotifyResult;
	}
	
	// 返回开始游戏的游戏信息
	private SEnterGameSuccess.Builder getConnectionOfGameStart(TeamGroup teamGroup){
		
		if(null == teamGroup){
			logger.error("NotifyClientLogic::getConnectionOfGameStart  teamGroup null Error");
			return null;
		}
		
		
		SEnterGameSuccess.Builder enterGameBuilder = SEnterGameSuccess.newBuilder();
		if(null == enterGameBuilder){
			logger.error("NotifyClientLogic::getConnectionOfGameStart enterGameBuilder null Error");
			return null;
		}
		
		Set<String> gamePlayerContainer = teamGroup.getAllUserSet();
		Iterator<String> iter = gamePlayerContainer.iterator();
		while(true == iter.hasNext()){
			String strPlayerId = iter.next();
			GamePlayer currentPlayer = teamGroup.searchGamePlayerByUserId(strPlayerId);
			if(null == currentPlayer){
				logger.error("NotifyClientLogic::getConnectionOfGameStart currentPlayer null Error");
				continue;
			}
			
			Team currentTeam = teamGroup.getTeamByUserId(strPlayerId);
			if(null == currentTeam){
				logger.error("NotifyClientLogic::getConnectionOfGameStart currentTeam null Error");
				continue;
			}
			
			GameUserPorkerNum.Builder userPorkerNum = GameUserPorkerNum.newBuilder();
			if(null == userPorkerNum){
				logger.error("NotifyClientLogic::getConnectionOfGameStart  userPorkerNum null Error");
				continue;
			}
			
			userPorkerNum.setStrUserId(currentPlayer.getGamePlayerId());
			userPorkerNum.setNPorkerNumInHand(currentPlayer.getPlayerHandPorkerNum());
			userPorkerNum.setNMajorFaceValue(currentTeam.getMajorFace());
			enterGameBuilder.addGameUserPorkerNum(userPorkerNum);
			//System.out.println(currentPlayer.getGamePlayerId() + "当前的主牌是: " + currentTeam.getMajorFace());
		}
		
		
		enterGameBuilder.setStrCurrentControllerUser(this.roomGame.getCurrentControlUser());
		enterGameBuilder.setNActiveMajorValue(this.roomGame.getCurrentMajorCard());
		enterGameBuilder.setConnectionStatus(ConnectionStatus.LOGIN_CONNECTION);
		return enterGameBuilder;
	}
	
	// 返回断线重登的信息
	public SEnterGameSuccess.Builder getReConnectionInfo(String strPlayerId){
		TeamGroup teamGroup = this.roomGame.getTeamGroup();
		if(null == teamGroup){
			logger.error("NotifyClientLogic::getReConnectionInfo teamGroup Null Error");
			return null;
		}
		
		SEnterGameSuccess.Builder enterGameBuilder = this.getConnectionOfGameStart(teamGroup);
		if(null == enterGameBuilder){
			logger.error("NotifyClientLogic::getReConnectionInfo  enterGameBuilder null Error");
			return null;
		}
		
		GamePlayer currentPlayer = teamGroup.searchGamePlayerByUserId(strPlayerId);
		if(null == currentPlayer){
			logger.error("NotifyClientLogic::getReConnectionInfo currentPlayer null Error");
			return null;
		}
		
		GameUserPorker.Builder userPorker = GameUserPorker.newBuilder();
		if(null == userPorker){
			logger.error("NotifyClientLogic::getReConnectionInfo userPorker null Error");
			return null;
		}
		
		boolean bGetResult = this.getPlayerSelfInfo(currentPlayer, enterGameBuilder);
		if(false == bGetResult){
			logger.error("NotifyClientLogic::getReConnectionInfo userPorker null Error");
			return null;
		}
		
		RoomGameTimer roomTimer = this.roomGame.getRoomGameTimer();
		if(null == roomTimer){
			logger.error("NotifyClientLogic::getReConnectionInfo roomTimer null Error");
			return null;
		}
		
		

		int nTimerSecs = roomTimer.getUserOperationSec();
		enterGameBuilder.setConnectionStatus(ConnectionStatus.LOGIN_RECONNECTION);
		enterGameBuilder.setNCutDwonSecs(nTimerSecs);
		//System.out.println("断线重连  倒计时秒数: " + nTimerSecs);
		return enterGameBuilder;
	}
	
	public boolean notifyGameEndToClient(GamePlayer gamePlayer){
		TeamGroup teamGroup = this.roomGame.getTeamGroup();
		if(null == teamGroup){
			logger.error("NotifyClientLogic::notifyNewGamePorkerInfo teamGroup Null Error");
			return false;
		}
		
		
		List<GameUserRank.Builder> userRankList = new ArrayList<>();
		Set<String> gamePlayerContainer = teamGroup.getAllUserSet();
		Iterator<String> iter = gamePlayerContainer.iterator();
		while(true == iter.hasNext()){
			String strPlayerId = iter.next();
			GamePlayer player = teamGroup.searchGamePlayerByUserId(strPlayerId);
			if(null == player){
				logger.error("NotifyClientLogic::notifyGameEndToClient currentPlayer null Error");
				continue;
			}
            
			GameUserRank.Builder userRankBuilder = GameUserRank.newBuilder();
			userRankBuilder.setStrUserId(player.getGamePlayerId());
			userRankBuilder.setNRankOfGame(player.getOutAllPorkerRank());
			userRankBuilder.setResultCode(player.getGameResult());
			
			userRankList.add(userRankBuilder);
		}
		
		SGameEndRequest.Builder gameEndBuilder = SGameEndRequest.newBuilder();
		int nListSize = userRankList.size();
		for(int i = 0; i < nListSize; ++i){
			GameUserRank.Builder tempUserRankBuilder = userRankList.get(i);
			gameEndBuilder.addUserRank(tempUserRankBuilder);
		}
		
		teamGroup.nofityAllOnLineUser(CmdType.CMD_GAME_END_OF_RESULT_VALUE, gameEndBuilder.build().toByteArray());
		//System.out.println("发送游戏结束消息到客户端");
		return true;
	}

	public boolean notifySumbitNullPorkerToServer(GamePlayer gamePlayer){
		TeamGroup teamGroup = this.roomGame.getTeamGroup();
		if(null == teamGroup){
			logger.error("NotifyClientLogic::notifyNewGamePorkerInfo teamGroup Null Error");
			return false;
		}
		
		StringBuffer porkerNumBuffer = new StringBuffer();
		porkerNumBuffer.append("玩家剩下的牌的数目:");
		
		List<GameUserPorkerNum.Builder> userPorkerNumList = new ArrayList<>();
		Set<String> gamePlayerContainer = teamGroup.getAllUserSet();
		Iterator<String> iter = gamePlayerContainer.iterator();
		while(true == iter.hasNext()){
			String strPlayerId = iter.next();
			GamePlayer player = teamGroup.searchGamePlayerByUserId(strPlayerId);
			if(null == player){
				logger.error("NotifyClientLogic::notifyNewGamePorkerInfo currentPlayer null Error");
				continue;
			}
            
            GameUserPorkerNum.Builder userPorkerNumBuilder = GameUserPorkerNum.newBuilder();
            userPorkerNumBuilder.setStrUserId(player.getGamePlayerId());
            userPorkerNumBuilder.setNPorkerNumInHand(player.getPlayerHandPorkerNum());
            
            userPorkerNumList.add(userPorkerNumBuilder);
            
            porkerNumBuffer.append( player.getGamePlayerId() + " PorkerNum "+ player.getPlayerHandPorkerNum() + " ,");
		}
		//System.out.println(porkerNumBuffer.toString());
		
		SSumbitPorkerResultResponse.Builder sumbitResultBuilder = SSumbitPorkerResultResponse.newBuilder();
		sumbitResultBuilder.setStrCurrentController(gamePlayer.getGamePlayerId());
		sumbitResultBuilder.setGameResultCode(GameResultCode.GRC_FAIL);
		
		int nHandPorkerSize = gamePlayer.getHandPorkerSize();
		for(int i = 0; i < nHandPorkerSize; ++i){
			int nTempPorkerValue = gamePlayer.getPorkerValueByIndex(i);
			sumbitResultBuilder.addHandPorkerArray(nTempPorkerValue);
		}
		
		int nListSize = userPorkerNumList.size();
		for(int i = 0; i < nListSize; ++i){
			GameUserPorkerNum.Builder userPorkerNum = userPorkerNumList.get(i);
			sumbitResultBuilder.addUserPorkerNum(userPorkerNum);
		}
		
		teamGroup.nofityAllOnLineUser(CmdType.CMD_SUMBIT_PORKER_RESULT_VALUE, sumbitResultBuilder.build().toByteArray());
		System.out.println(gamePlayer.getGamePlayerId() + " 过牌");
		return true;
	}
	
	
	public boolean nofityTimeCutDownSync(GamePlayer player, boolean bHandOutAnyPorker){
		TeamGroup teamGroup = this.roomGame.getTeamGroup();
		if(null == teamGroup){
			logger.error("NotifyClientLogic::notifyGameEndToClient teamGroup Null Error");
			return false;
		}
		
		SCutDownSecsSync.Builder cutDownSyncBuilder = SCutDownSecsSync.newBuilder();
    	if(null == cutDownSyncBuilder){
    		logger.error("RoomGame::gameStartLogic cutDownSyncBuilder Null Error");
    		return false;
    	}
    	
    	
    	Integer nSecsDiff = GDPropertiesUtils.getPropertyAsInteger(GDPropertiesUtils.SERVER_CLIENTSECS, 2);
    	Integer nCutDwonSecs = GDPropertiesUtils.getPropertyAsInteger(GDPropertiesUtils.USEROPERATION_TIMER);
    	int nClientCutDownSecs = nCutDwonSecs - nSecsDiff;
    	if(nClientCutDownSecs <= 0){
    		logger.error("RoomGame::gameStartLogic nClientCutDownSecs 0 Error");
    		return false;
    	}
    	
    	
    	System.out.println("控制权转移到: " + player.getGamePlayerId());
    	cutDownSyncBuilder.setStrCurrentController(player.getGamePlayerId());
    	cutDownSyncBuilder.setNCutDownSecs(nClientCutDownSecs);
    	cutDownSyncBuilder.setBHandAnyPorker(bHandOutAnyPorker);
    	teamGroup.nofityAllOnLineUser(CmdType.CMD_CUT_DOWN_SECS_SYNC_VALUE, cutDownSyncBuilder.build().toByteArray());
    	return true;
	}
}





 