package com.lbwan.game.room.roomGameTimer;

import org.apache.log4j.Logger;

import com.lbwan.game.porkerComparer.GameComparer;
import com.lbwan.game.room.gameTeam.TeamGroup;
import com.lbwan.game.room.roomGame.GameControlUser;
import com.lbwan.game.room.roomGame.GamePlayer;
import com.lbwan.game.room.roomGame.NextControlUserEnum;
import com.lbwan.game.room.roomGame.RoomGame;
import com.lbwan.game.room.roomGameLogic.HostingLogic;
import com.lbwan.game.room.roomGameLogic.NotifyClientLogic;
import com.lbwan.game.room.roomGameLogic.OperationTag;



public class ClientPerformanceTask implements Runnable{

    private RoomGame roomGame;
    
    private Logger logger = Logger.getLogger(this.getClass());
    
    public ClientPerformanceTask( RoomGame game ){
        this.roomGame = game;
        
    }
    
   
    @Override
    public void run() {
     
    	if(null == this.roomGame){
    		logger.error("ClientPerformanceTask::userOperationTimeOut canOperationTag Error");
     	    return ;
    	}
    	
    	RoomGameTimer roomGameTimer = roomGame.getRoomGameTimer();
    	if(null == roomGameTimer){
    		logger.error("ClientPerformanceTask::userOperationTimeOut roomGameTimer Error");
     	    return ;
    	}
    	
    	TeamGroup teamGroup = roomGame.getTeamGroup();
    	if(null == teamGroup){
    		logger.error("ClientPerformanceTask::operationClientPerformance teamGroup Error");
     	    return ;
    	}
    	
    	GameComparer roomGameComparer = roomGame.getGameComparer();
    	if(null == roomGameComparer){
    		logger.error("ClientPerformanceTask::operationClientPerformance roomGameComparer Error");
     	    return ;
    	}
    	
    	GameControlUser controlUser = this.roomGame.getGameControlUser();
    	if(null == controlUser){
    		logger.error("ClientPerformanceTask::operationClientPerformance controlUser.getControlUser() Null Error");
    		return ;
    	}
    	
    	OperationTag canOperationTag = roomGame.getOperationTag();
    	if(null == canOperationTag){
    		logger.error("ClientPerformanceTask::operationClientPerformance canOperationTag Error");
     	    return ;
    	}
    	
    	HostingLogic hostingClientLogic = roomGame.getHostingClientLogic();
    	if(null == hostingClientLogic){
    		logger.error("ClientPerformanceTask::operationClientPerformance hostingClientLogic Error");
     	    return ;
    	}
    	
    	NotifyClientLogic nofityClientLogic = this.roomGame.getNotifyClientLogic();
    	if(null == nofityClientLogic){
    		logger.error("ClientPerformanceTask::operationClientPerformance nofityClientLogic Null Error");
    		return ;
    	}
    	
    	// 逻辑真正开始的地方
    	// 将控制权转交给下面一个控制者  控制定时器
    	int nNextControlUserResult = controlUser.nextPlayerControlGame(); 
    	if(NextControlUserEnum.FAIL == nNextControlUserResult){
    		logger.error("ClientPerformanceTask::operationClientPerformance Error");
    		return ;
    	}
    	
    	GamePlayer player = teamGroup.searchGamePlayerByUserId(controlUser.getControlUser());
    	if(null == player){
    		logger.error("ClientPerformanceTask::operationClientPerformance player Null Error");
    		return ;
    	}
    	
    	
    	boolean bHandOutAnyPorker = roomGameComparer.isMustHandPlayerPorker(controlUser.getControlUser());
    	nofityClientLogic.nofityTimeCutDownSync(player, bHandOutAnyPorker);
    	
    	canOperationTag.canSumbitPorker(player.getGamePlayerId());
    	
    	// 是否是托管状态
    	boolean bHostingGame = player.isHostingStatusForGame();
    	
    	// 定时器禁掉
    	roomGameTimer.cancelTimer();
    	
    	// 不是托管状态下
    	if(false == bHostingGame){
    		roomGameTimer.startUserOperationTimer();
    		return ;
    	}
    	
    	
    	// 托管状态下
    	hostingClientLogic.hostingClientLogic(player);
    }

}




