package com.lbwan.game.room.roomGameLogic;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.lbwan.game.porkerComparer.GameComparer;
import com.lbwan.game.room.RoomManager;
import com.lbwan.game.room.gameStatus.GameStatusChecker;
import com.lbwan.game.room.gameTeam.TeamGroup;
import com.lbwan.game.room.roomGame.GamePlayer;
import com.lbwan.game.room.roomGame.RoomGame;
import com.lbwan.game.room.roomGameTimer.RoomGameTimer;


public class GameEndLogic {
	
	private RoomGame currentRoomGame = null; 
	
	protected Logger logger = Logger.getLogger(getClass());
	
	public GameEndLogic(RoomGame roomGameParam){
		this.currentRoomGame = roomGameParam;
	}
	
	public boolean comparePorkerAction(GamePlayer currentPlayer, List<Integer> sumbitPorkerList){
		if(null == this.currentRoomGame){
			 logger.error("SumbitPorkerLogic::sumbitNullPorkerToServer this.currentRoomGame Null Error");
			 return false;
	     }
		
		 GameComparer roomGameComparer = this.currentRoomGame.getGameComparer();
	     if(null == roomGameComparer){
		    logger.error("UserOperationTask::userOperationTimeOut roomGameComparer Error");
		    return false;
		 }
	     
	     RoomGameTimer roomGameTimer = this.currentRoomGame.getRoomGameTimer();
	     if(null == roomGameTimer){
	    	logger.error("GameBeginTask::userOperationTimeOut roomGameTimer Error");
	    	return false;
	     }
	    	
	     NotifyClientLogic nofityClientLogic = this.currentRoomGame.getNotifyClientLogic();
	     if(null == nofityClientLogic){
	    	logger.error("GameBeginTask::operationClientPerformance nofityClientLogic Null Error");
	    	return false;
	    }
	    	
		// 手中的牌是否比最大的牌大
    	boolean bCompareResult = roomGameComparer.compareMaxPorker(currentRoomGame.getCurrentControlUser(), sumbitPorkerList);
    	if(false == bCompareResult){
    		System.out.println("牌比较小");
    		nofityClientLogic.notifySumbitNullPorkerToServer(currentPlayer);
    		return false;
    	}
    	
    	
    	roomGameTimer.cancelTimer();
    	
    	// 提交的牌比 最大的牌 更大  进行相对应的更新
    	roomGameComparer.updateMaxPorker(currentRoomGame.getCurrentControlUser(), sumbitPorkerList);
    	
    	// 手中剩下的牌为0的时候为特殊情况
    	gameRoundEndAction(currentPlayer, sumbitPorkerList);
    	
    	return true;
    }
    
    
    public void gameRoundEndAction(GamePlayer currentPlayer, List<Integer> sumbitPorkerList){
    	if(null == this.currentRoomGame){
			 logger.error("SumbitPorkerLogic::sumbitNullPorkerToServer this.currentRoomGame Null Error");
			 return ;
	     }
    	
    	RoomGameTimer roomGameTimer = this.currentRoomGame.getRoomGameTimer();
	    if(null == roomGameTimer){
	    	logger.error("GameBeginTask::userOperationTimeOut roomGameTimer Error");
	    	return ;
	    }
	     
    	NotifyClientLogic nofityClientLogic = this.currentRoomGame.getNotifyClientLogic();
	    if(null == nofityClientLogic){
	    	logger.error("GameBeginTask::operationClientPerformance nofityClientLogic Null Error");
	    	return ;
	    }
	     
	    OperationTag canOperationTag = this.currentRoomGame.getOperationTag();
    	if(null == canOperationTag){
    		logger.error("GameBeginTask::userOperationTimeOut canOperationTag Error");
     	    return ;
    	}
    	
    	TeamGroup teamGroup = this.currentRoomGame.getTeamGroup();
    	if(null == teamGroup){
    		logger.error("GameBeginTask::userOperationTimeOut teamGroup Error");
     	    return ;
    	}
    	
    	GameStatusChecker statusChecker = this.currentRoomGame.getGameStatusChecker();
    	if(null == statusChecker){
    		logger.error("GameBeginTask::userOperationTimeOut statusChecker Error");
     	    return ;
    	}
    	
    	// 逻辑开始的地方
    	boolean bEndGameTag = teamGroup.isEndOfRoundGame();
    	if(true == bEndGameTag){
    		
    		roomGameTimer.cancelTimer();
    		
    		// 处理后续的结果
    		// 计算排位, 游戏结束 升级相对应的主牌, 设置下一局的主牌
    		teamGroup.processForGameEnd();
    		
    		// 通知客户端
    		nofityClientLogic.notifyGameEndToClient(currentPlayer);
    		
    		// 计算 下一局的专家(下一局的第一个控制者)
    		System.out.println("游戏结束 Token: "+ this.currentRoomGame.getRoomToken());
    		
    		// 状态
    		statusChecker.endGame();
    		
    		// 是否有人中途离场
    		boolean bExitGame = this.isPlayerExitGame();
    		if(true == bExitGame){
    			RoomManager.releaseRoom(currentRoomGame.getRoomToken());
    		}else{
    			RoomManager.resetGame(currentRoomGame.getRoomToken());
    		}
    		
            
    		
            // 重新开始一局游戏
            /*
            String strRoomToken = "testToken";
            
            List<Player> playersList = new ArrayList<>();
            Player player1 = new Player(strRoomToken);
            player1.setUserId("test" + 1);
            playersList.add(player1);
            
            Player player2 = new Player(strRoomToken);
            player2.setUserId("test" + 2);
            playersList.add(player2);
            
            Player player3 = new Player(strRoomToken);
            player3.setUserId("test" + 3);
            playersList.add(player3);
            
            Player player4 = new Player(strRoomToken);
            player4.setUserId("test" + 4);
            playersList.add(player4);
            
            this.startGame(strRoomToken, playersList);
            */
            
    	}else{
    		// 通知客户端
    		canOperationTag.canNotSumbitPorker();
    		roomGameTimer.startClientPerformanceTimer();
    	}
    }
    
    private boolean isPlayerExitGame(){
    	TeamGroup teamGroup = currentRoomGame.getTeamGroup();
		if(null == teamGroup){
			 logger.error("SumbitPorkerLogic::sumbitNullPorkerToServer teamGroup Null Error");
			 return false;
	     }
		
    	Set<String> allUser = teamGroup.getAllUserSet();
    	Iterator<String> iter = allUser.iterator();
    	while(true == iter.hasNext()){
    		String strPlayer = iter.next();
    		GamePlayer player = teamGroup.searchGamePlayerByUserId(strPlayer);
    		if(null == player){
    			logger.error("RoomGame::isPlayerExitGame player Null Error");
	    		continue;
    		}
    		
    		if(true == player.isAutoExitBySelf()){
    			return true;
    		}
    	}
    	
    	return false;
    }
}
