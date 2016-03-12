package com.lbwan.game.room.roomGameTimer;

import org.apache.log4j.Logger;

import com.lbwan.game.room.gameTeam.TeamGroup;
import com.lbwan.game.room.roomGame.GamePlayer;
import com.lbwan.game.room.roomGame.RoomGame;
import com.lbwan.game.room.roomGameLogic.NotifyClientLogic;
import com.lbwan.game.room.roomGameLogic.OperationTag;

public class GameBeginTask implements Runnable{
    
	private RoomGame roomGame = null;
    
    private Logger logger = Logger.getLogger(this.getClass());
    
    
    public GameBeginTask( RoomGame game ){
        this.roomGame = game;
    }
    
   
    @Override
    public void run() {
    
    	if(null == this.roomGame){
    		logger.error("GameBeginTask::userOperationTimeOut canOperationTag Error");
     	    return ;
    	}
    	
    	RoomGameTimer roomGameTimer = roomGame.getRoomGameTimer();
    	if(null == roomGameTimer){
    		logger.error("GameBeginTask::userOperationTimeOut roomGameTimer Error");
     	    return ;
    	}
    	
    	OperationTag canOperationTag = roomGame.getOperationTag();
    	if(null == canOperationTag){
    		logger.error("GameBeginTask::userOperationTimeOut canOperationTag Error");
     	    return ;
    	}
    	
    	TeamGroup teamGroup = roomGame.getTeamGroup();
    	if(null == teamGroup){
    		logger.error("GameBeginTask::userOperationTimeOut teamGroup Error");
     	    return ;
    	}
    	
    	String strControlUser = this.roomGame.getCurrentControlUser();
    	if(null == strControlUser){
    		logger.error("GameBeginTask::operationClientPerformance controlUser.getControlUser() Null Error");
    		return ;
    	}
    	
    	NotifyClientLogic nofityClientLogic = this.roomGame.getNotifyClientLogic();
    	if(null == nofityClientLogic){
    		logger.error("GameBeginTask::operationClientPerformance nofityClientLogic Null Error");
    		return ;
    	}
    	
    	// 逻辑开始的地方
    	GamePlayer player = teamGroup.searchGamePlayerByUserId(strControlUser);
    	if(null == player){
    		logger.error("GameBeginTask::operationClientPerformance player Null Error");
    		return ;
    	}
    	
    	// 控制权暂时屏蔽
    	boolean bHandOutAnyPorker = true;
    	nofityClientLogic.nofityTimeCutDownSync(player, bHandOutAnyPorker);
    	
    	canOperationTag.canSumbitPorker(player.getGamePlayerId());
    	
    	
    	roomGameTimer.startUserOperationTimer();
    	
    	
    	// 测试代码  设置胜利的队伍
    	// 测试代码
    	/*
    	roomGameTimer.cancelTimer();
    	
    	teamGroup.testCode();
    	
    	teamGroup.setNewMajorFaceValueOfGameEnd();
		
		// 通知客户端
    	boolean bEndGameTag = true;
    	List<Integer> sumbitPorkerList = new ArrayList<>();
		nofityClientLogic.notifyGameRoundEndToClient(bEndGameTag, GameResultCode.GRC_SUCCEED, player, sumbitPorkerList);
		
		// 计算 下一局的专家(下一局的第一个控制者)
		System.out.println("游戏结束 Token: "+ this.gameToken);
		
		// 状态
		statusChecker.endGame();
		
        RoomManager.resetGame(gameToken);
        */
    }
}
