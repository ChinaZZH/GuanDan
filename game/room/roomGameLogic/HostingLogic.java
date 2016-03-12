package com.lbwan.game.room.roomGameLogic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lbwan.game.cardTypeSearch.SearcherManager;
import com.lbwan.game.porkerComparer.GameComparer;
import com.lbwan.game.porkerComparer.HandPatternCalculator;
import com.lbwan.game.porkerEnumSet.CardTypeEnum;
import com.lbwan.game.porkerEnumSet.PorkerValueEnum;
import com.lbwan.game.room.gameTeam.TeamGroup;
import com.lbwan.game.room.roomGame.GamePlayer;
import com.lbwan.game.room.roomGame.RoomGame;
import com.lbwan.game.room.roomGameTimer.RoomGameTimer;
import com.lbwan.game.spring.SpringUtils;

public class HostingLogic {
	private Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	protected SearcherManager searcherMgr = (SearcherManager) SpringUtils.getBeanByName("searcherManager");
	
	private RoomGame currentRoom = null;
	
	public HostingLogic(RoomGame roomGameParam){
		this.currentRoom = roomGameParam;
	}
	
	public void hostingClientToServer(String strHostingUser){
		TeamGroup teamGroup = currentRoom.getTeamGroup();
		if(null == teamGroup){
			 logger.error("HostingLogic::hostingClientToServer teamGroup Null Error");
			 return ;
	     }
		
    	GamePlayer player = teamGroup.searchGamePlayerByUserId(strHostingUser);
    	if(null == player){
    		logger.error("HostingLogic::hostingClientToServer player Null Error");
    		return ;
    	}
    	
    	RoomGameTimer roomGameTimer = currentRoom.getRoomGameTimer();
		if(null == roomGameTimer){
			logger.error("HostingLogic::hostingClientToServer roomGameTimer Null Error");
			return ;
	    }
		
		OperationTag canOperation = currentRoom.getOperationTag();
		if(null == canOperation){
			logger.error("HostingLogic::hostingClientToServer canOperation Null Error");
			return ;
	    }
		
    	player.hostingClientOnGame();
    	
    	// 是否是出牌阶段
    	boolean isPlayingStatus = canOperation.isCanSumbitPorker(strHostingUser);
    	if(true == isPlayingStatus){
    		roomGameTimer.cancelTimer();
    		
    		this.hostingClientLogic(player);
    	}
    }
    
    public void cancelHostingToServer(String strCancelHostingUser){
    	TeamGroup teamGroup = currentRoom.getTeamGroup();
		if(null == teamGroup){
			 logger.error("HostingLogic::cancelHostingToServer teamGroup Null Error");
			 return ;
	     }
		
		
    	GamePlayer player = teamGroup.searchGamePlayerByUserId(strCancelHostingUser);
    	if(null == player){
    		logger.error("HostingLogic::cancelHostingToServer player Null Error");
    		return ;
    	}
    	
    	player.cancelHostingForGame();
    }
    
    
    public void hostingClientLogic(GamePlayer player){
    	if(null == player){
    		logger.error("HostingLogic::hostingClintLogic player Null Error");
    		return ;
    	}
    	
    	GameComparer roomGameComparer  = currentRoom.getGameComparer();
		if(null == roomGameComparer){
			 logger.error("HostingLogic::hostingClientToServer roomGameComparer Null Error");
			 return ;
	     }
		
		RoomGameTimer roomGameTimer = currentRoom.getRoomGameTimer();
		if(null == roomGameTimer){
			logger.error("HostingLogic::hostingClientToServer roomGameTimer Null Error");
			return ;
	    }
		
		OperationTag canOperationTag = currentRoom.getOperationTag();
		if(null == canOperationTag){
			 logger.error("HostingLogic::hostingClientToServer canOperationTag Null Error");
			 return ;
	     }
		
		NotifyClientLogic nofityClientLogic = currentRoom.getNotifyClientLogic();
		if(null == nofityClientLogic){
			 logger.error("HostingLogic::hostingClientToServer nofityClientLogic Null Error");
			 return ;
	     }
		
		// 真正开始逻辑的地方
		String strControlUser = currentRoom.getCurrentControlUser();
		
    	// 托管状态下
    	boolean bMustHandPorker = roomGameComparer.isMustHandPlayerPorker(strControlUser);
    	if(true == bMustHandPorker){
    		// 上一轮  最大的人是  controlUser.getControlUser()
    		roomGameComparer.handleMustHandPorkerStatus(strControlUser);
    	    return ;
    	}
    	
    	List<Integer> sumbitPorkerForHostingPlayer = new ArrayList<>();
    	int nSumbitCardType = searcherMgr.searchIsBiggerPorkerThanLastMax(player.getHandPorkerArray(), roomGameComparer.getLastMaxHandPorker(), currentRoom.getCurrentMajorCard(), sumbitPorkerForHostingPlayer);
    	if((0 != nSumbitCardType) && (false == sumbitPorkerForHostingPlayer.isEmpty())){
    		// 提交的牌比 最大的牌 更大  进行相对应的更新
	    	roomGameComparer.updateMaxPorkerByHostingStatus(strControlUser, sumbitPorkerForHostingPlayer, nSumbitCardType);
	    	
	    	// 手中剩下的牌为0的时候为特殊情况
	    	this.currentRoom.gameRoundEndAction(player, sumbitPorkerForHostingPlayer);
    	}else{
    		// 通知客户端
    		nofityClientLogic.notifySumbitNullPorkerToServer(player);
    		canOperationTag.canNotSumbitPorker();
    		roomGameTimer.startClientPerformanceTimer();
    	}
    	
    }
}
