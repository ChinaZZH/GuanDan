package com.lbwan.game.room.roomGameLogic;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.lbwan.game.payTributeChecker.PayTributeCheckerHolder;
import com.lbwan.game.payTributeHandler.PayTributeHandlerHolder;
import com.lbwan.game.porkerComparer.GameComparer;
import com.lbwan.game.room.Player;
import com.lbwan.game.room.gameStatus.GameStatusChecker;
import com.lbwan.game.room.gameTeam.TeamGroup;
import com.lbwan.game.room.roomGame.GameControlUser;
import com.lbwan.game.room.roomGame.GamePlayer;
import com.lbwan.game.room.roomGame.RoomGame;
import com.lbwan.game.room.roomGameTimer.RoomGameTimer;
import com.lbwan.game.spring.SpringUtils;
import com.lbwan.game.userLoginDateDao.AllLoginUserService;
import com.lbwan.game.utils.PropertiesUtils;

public class StartGameLogic {
	
	private RoomGame currentRoomGame = null; 
	
	private PorkerInitLogic newPorkerInitLogic = null;
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private final static int GAME_PLAYER_MIN_COUNT = PropertiesUtils
			.getPropertyAsInteger(PropertiesUtils.GAME_PLAYER_MIN_COUNT); 
	
	@Autowired
	protected PayTributeHandlerHolder payTributeHandlerHolder = (PayTributeHandlerHolder) SpringUtils.getBeanByName("payTributeHandlerHolder");
	
	@Autowired
	protected PayTributeCheckerHolder payTributeCheckerHolder = (PayTributeCheckerHolder) SpringUtils.getBeanByName("payTributeCheckerHolder");
	
	@Autowired
	protected AllLoginUserService allUserService = (AllLoginUserService)SpringUtils.getBeanByName("allLoginUserService");
	
	
	public StartGameLogic(RoomGame roomGameParam){
		this.currentRoomGame = roomGameParam;
		
		newPorkerInitLogic = new PorkerInitLogic(this.currentRoomGame);
		if(null == newPorkerInitLogic){
    		logger.error("RoomGame::RoomGame newPorkerInitLogic Null Error");
    	}
	}
	
	public boolean runGameStartAction(){
		TeamGroup teamGroup = currentRoomGame.getTeamGroup();
	    if(null == teamGroup){
	    	logger.error("StartGameLogic::runGameStartAction teamGroup Error");
	    	return false;
	    }
	     
	    GameStatusChecker statusChecker = this.currentRoomGame.getGameStatusChecker();
	    if(null == statusChecker){
	    	logger.error("StartGameLogic::runGameStartAction statusChecker Error");
			return false;
		}
	     
	    RoomGameTimer roomGameTimer = currentRoomGame.getRoomGameTimer();
	    if(null == roomGameTimer){
	    	logger.error("StartGameLogic::runGameStartAction statusChecker Error");
			return false;
		}
	     
	    NotifyClientLogic notifyClient = currentRoomGame.getNotifyClientLogic();
	    if(null == notifyClient){
	    	logger.error("StartGameLogic::runGameStartAction notifyClient null Error");
			return false;
		}
	    
	    // 设置为开始游戏
	    statusChecker.startGame();
	 		
	    notifyClient.notifyPlayerRound();
	    
		teamGroup.clearLastRoundData();
		
		roomGameTimer.startGameBeginTimer();

		return true;
	}
	
	public boolean startGamebyPlayerList(String token, List<Player> players) {
		
		// TODO Auto-generated method stub
		 System.out.println("StartGameLogicAddress:" + this.toString());
		 TeamGroup teamGroup = currentRoomGame.getTeamGroup();
	     if(null == teamGroup){
	    	logger.error("RoomGame::startGame teamGroup Error");
	    	return false;
	     }
	     
	     GameControlUser controlUser = this.currentRoomGame.getGameControlUser();
	     if(null == controlUser){
	    	 logger.error("StartGameLogic::operationClientPerformance controlUser.getControlUser() Null Error");
	    	 return false;
	     }
	    
	     NotifyClientLogic nofityClientLogic = this.currentRoomGame.getNotifyClientLogic();
	     if(null == nofityClientLogic){
	    	logger.error("UserOperationTask::userOperationTimeOut nofityClientLogic Error");
	    	return false;
	     }
	    	
	     GameComparer roomGameComparer = this.currentRoomGame.getGameComparer();
	     if(null == roomGameComparer){
		    logger.error("UserOperationTask::userOperationTimeOut roomGameComparer Error");
		    return false;
		 }
	     
	     GameStatusChecker statusChecker = this.currentRoomGame.getGameStatusChecker();
	     if(null == statusChecker){
			 logger.error("UserOperationTask::userOperationTimeOut statusChecker Error");
			 return false;
		  }
	     
	     // 逻辑真正开始的地方
		// 判断人数是否满足条件
		if (GAME_PLAYER_MIN_COUNT != players.size()) {
			// 房间人员不足  强行关闭
			return false;
		}


		// 设置状态为准备游戏
		boolean bReady = statusChecker.readyForGame();
		if(false == bReady){
			logger.error("RoomGame::startGame readyForGame() Error");
			return false;
		}
		
		boolean bStartGameResult = teamGroup.initOfTeamGroup(players);
		if (false == bStartGameResult) {
			logger.error("RoomGame::startGame initGamePlayerData Error");
			return false;
		}

		roomGameComparer.initNewGameRound();
		
		// 洗牌
		bStartGameResult = newPorkerInitLogic.shuttleTheCard();
		
		// 测试使用
		if (false == bStartGameResult) {
			logger.error("RoomGame::startGame shuttleTheCard Error");
			return false;
		}

		// 为每一个玩家分牌 分牌
		int nPlayerSize = players.size();
		for(int i = 0; i < nPlayerSize; ++i){
			GamePlayer player = teamGroup.searchGamePlayerByUserId(players.get(i).getUserId());
			if(null == player) {
				logger.error("RoomGame::startGame player null Error");
				return false;
			}
			
			boolean bGainResult = newPorkerInitLogic.gainInitPorker(player);
			if(false == bGainResult) {
				logger.error("RoomGame::startGame player null Error");
				return false;
			}
		}
		
		
		System.out.println("游戏开始!!!");
		
		
		boolean bFirstRound = controlUser.isFirstRoundForRoom();
		if(true == bFirstRound){
			// 如果是第一次 则随机选择控制者
			String strControlUser = controlUser.initFirstControllerUser(players);
			if(null == strControlUser){
				logger.error("RoomGame::startGame strControlUser Null Error");
				return false;
			}
			
			teamGroup.setMajorSceneForRoomInit(strControlUser);
		}
				
		// 通知客户端
		nofityClientLogic.notifyStartGame();
		
		if(true == bFirstRound){
		    // 登陆的时间换算成金币
			//this.goldAwardForLoginGame(players);
			
			payTributeHandlerHolder.startGameByEndTributeCallBack(currentRoomGame, controlUser.getControlUser());
			
		}else{
			// 不是第一次  则执行进贡流程
			int nTributeType = payTributeCheckerHolder.checkPayTributeType(currentRoomGame);
			if(0 == nTributeType){
				logger.error("RoomGame::startGame nTributeType Error");
				return false;
			}
			
			payTributeHandlerHolder.processPayTribute(currentRoomGame, nTributeType);
		}
		
		
		return true;
	}
	
	
	private boolean goldAwardForLoginGame(List<Player> players){
		 boolean bLoginResult = false;
		 TeamGroup teamGroup = currentRoomGame.getTeamGroup();
	     if(null == teamGroup){
	    	logger.error("RoomGame::goldAwardForLoginGame teamGroup Error");
	    	return bLoginResult;
	     }
	    	
		 if(null == players){
			 logger.error("RoomGame::goldAwardForLoginGame players Null Error");
			 return bLoginResult;
		 }
		 
		 int nPlayersNum = players.size();
		 for(int i = 0; i < nPlayersNum; ++i){
			 GamePlayer gamePlayer = teamGroup.searchGamePlayerByPlayer(players.get(i));
			 if(null == gamePlayer){
				 logger.error("RoomGame::goldAwardForLoginGame gamePlayer Null Error");
				 continue;
			 }
			 
			 // 是否是 首次进入该游戏
			 
			 String strPlayerId = gamePlayer.getGamePlayerId();
			 boolean bFirstLogin = allUserService.isFirstLogin(strPlayerId);
			 if(true == bFirstLogin){	 
				 allUserService.updateUserNewLogin(strPlayerId);
				 int nFirstLoginGold = 5000;
				 gamePlayer.addPlayerGold(nFirstLoginGold);
				 continue;
			 }
			 
			 // 是否是今天第一次登陆该游戏
			boolean bTodayFirstLogin = allUserService.isTodayFirstLogin(strPlayerId);
			if(true == bTodayFirstLogin){
				allUserService.updateUserNewLogin(strPlayerId);
				int nTodayFirstLoginGold = 1000;
				 gamePlayer.addPlayerGold(nTodayFirstLoginGold);
			}
			
			 
			continue;
		 }
		 
		 bLoginResult = true;
		 return bLoginResult;
	 }
}
