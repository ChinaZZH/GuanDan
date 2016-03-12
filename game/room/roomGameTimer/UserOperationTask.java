package com.lbwan.game.room.roomGameTimer;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.lbwan.game.porkerComparer.ComparerManager;
import com.lbwan.game.porkerComparer.GameComparer;
import com.lbwan.game.porkerEnumSet.PorkerValueEnum;
import com.lbwan.game.room.gameTeam.TeamGroup;
import com.lbwan.game.room.roomGame.GamePlayer;
import com.lbwan.game.room.roomGame.RoomGame;
import com.lbwan.game.room.roomGameLogic.HostingLogic;
import com.lbwan.game.room.roomGameLogic.OperationTag;
import com.lbwan.game.spring.SpringUtils;


public class UserOperationTask implements Runnable{
    
	private RoomGame roomGame = null;
    
    private Logger logger = Logger.getLogger(this.getClass());
    

    @Autowired
    private ComparerManager comparerManager = (ComparerManager)SpringUtils.getBeanByName("comparerManager");

    
    public UserOperationTask( RoomGame roomGame ){
        this.roomGame = roomGame;
    }

    @Override
    public void run() {
    	// 是否是当前控制者
    	TeamGroup teamGroup = roomGame.getTeamGroup();
    	if(null == teamGroup){
    		logger.error("UserOperationTask::userOperationTimeOut teamGroup Error");
     	    return ;
    	}
    	
    	GameComparer roomGameComparer = roomGame.getGameComparer();
    	if(null == roomGameComparer){
    		logger.error("UserOperationTask::userOperationTimeOut roomGameComparer Error");
     	    return ;
    	}
    	
    	RoomGameTimer roomGameTimer = roomGame.getRoomGameTimer();
    	if(null == roomGameTimer){
    		logger.error("UserOperationTask::userOperationTimeOut roomGameTimer Error");
     	    return ;
    	}
    	
    	OperationTag canOperationTag = roomGame.getOperationTag();
    	if(null == canOperationTag){
    		logger.error("UserOperationTask::userOperationTimeOut canOperationTag Error");
     	    return ;
    	}
    	
    	HostingLogic hostingClientLogic = roomGame.getHostingClientLogic();
    	if(null == hostingClientLogic){
    		logger.error("UserOperationTask::userOperationTimeOut hostingClientLogic Error");
     	    return ;
    	}
    	
    	String strControlUser = roomGame.getCurrentControlUser();
    	if(null == strControlUser){
    		logger.error("UserOperationTask::userOperationTimeOut controlUser.getControlUser() Error");
     	    return ;
    	}
    	
    	
    	
    	// 真正逻辑开始的地方
    	GamePlayer currentPlayer = teamGroup.searchGamePlayerByUserId(strControlUser);
    	if(null == currentPlayer){
    		logger.error("UserOperationTask::sumbitSomePorker currentPlayer Error");
     	    return ;
    	}
    	
    	
    	// 判断是否掉线
    	boolean bIsOnLine = currentPlayer.getOnLinePlayStatus();
    	boolean bHostingClient = currentPlayer.isHostingStatusForGame();
    	if((false == bIsOnLine) && (false == bHostingClient)){
    		// 执行托管流程
    		currentPlayer.hostingClientOnGame();
    		hostingClientLogic.hostingClientLogic(currentPlayer);
    		return ;
    	}
    	
    	
    	// 是否强制出牌
    	// 如果是第一个 或者 是当前控制者
    	boolean bMustHandPorker = roomGameComparer.isMustHandPlayerPorker(strControlUser);
    	if(false == bMustHandPorker){
    		// 控制权转交流程
    		canOperationTag.canNotSumbitPorker();
    		roomGameTimer.startClientPerformanceTimer();
    		return ;
    	}
    	
    	
    	// 强制出牌
		// 校验玩家手中是否有那些牌
    	roomGameComparer.handleMustHandPorkerStatus(strControlUser);
    }
}
