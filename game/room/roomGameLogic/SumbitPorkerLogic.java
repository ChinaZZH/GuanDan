package com.lbwan.game.room.roomGameLogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.lbwan.game.porkerComparer.GameComparer;
import com.lbwan.game.porkerEnumSet.PorkerValueEnum;
import com.lbwan.game.room.gameTeam.TeamGroup;
import com.lbwan.game.room.roomGame.GamePlayer;
import com.lbwan.game.room.roomGame.RoomGame;
import com.lbwan.game.room.roomGameTimer.RoomGameTimer;

public class SumbitPorkerLogic {

	private RoomGame currentRoom = null;
	
	protected Logger logger = Logger.getLogger(getClass());
	
	
	public SumbitPorkerLogic(RoomGame roomParam){
		this.currentRoom = roomParam;
	}
	
	
	public boolean sumbitSomePorker(String strUserId, List<Integer> sumbitPorkerList){
    	boolean bSumbitPorkerResult = false;
		if(null == strUserId){
    		logger.error("SumbitPorkerLogic::sumbitSomePorker strUserId Null Error");
     	    return bSumbitPorkerResult;
    	}
    	
    	TeamGroup teamGroup = currentRoom.getTeamGroup();
		if(null == teamGroup){
			 logger.error("SumbitPorkerLogic::sumbitNullPorkerToServer teamGroup Null Error");
			 return bSumbitPorkerResult;
	     }
		
		OperationTag canOperationTag = currentRoom.getOperationTag();
		if(null == canOperationTag){
			 logger.error("SumbitPorkerLogic::sumbitNullPorkerToServer canOperationTag Null Error");
			 return bSumbitPorkerResult;
	     }
		
		NotifyClientLogic nofityClientLogic = currentRoom.getNotifyClientLogic();
		if(null == nofityClientLogic){
			 logger.error("SumbitPorkerLogic::sumbitNullPorkerToServer nofityClientLogic Null Error");
			 return bSumbitPorkerResult;
	     }
		
    	if(null == sumbitPorkerList){
    		logger.error("SumbitPorkerLogic::sumbitSomePorker sumbitPorkerList Null Error");
     	    return bSumbitPorkerResult;
    	}
    	
    	if(true == sumbitPorkerList.isEmpty()){
    		return bSumbitPorkerResult;
    	}
    	
    	// 真正逻辑函数开始的地方
    	boolean bIsCanSumbitPorker = canOperationTag.isCanSumbitPorker(strUserId);
    	if(false == bIsCanSumbitPorker){
    		System.out.println("现在还没到出牌的时候");
    		return false;
    	}
    	
    	// 是否是当前控制者
    	String strCurrentControler = currentRoom.getCurrentControlUser();
    	if(false == strUserId.equals(strCurrentControler)){
    		logger.error("SumbitPorkerLogic::sumbitSomePorker StrUserId Error-----Client Sumbit: " + strUserId + "  Server Controler: "+ strCurrentControler);
    		return bSumbitPorkerResult;
    	}
    	
    	// 校验玩家手中是否有那些牌
    	GamePlayer currentPlayer = teamGroup.searchGamePlayerByUserId(strUserId);
    	if(null == currentPlayer){
    		logger.error("SumbitPorkerLogic::sumbitSomePorker currentPlayer Error");
    		return bSumbitPorkerResult;
    	}
    	
    	// 测试打印信息
    	StringBuffer printBuffer = new StringBuffer();
		printBuffer.append(currentPlayer.getGamePlayerId()+" 上发牌 到服务器:");
		for(int i = 0; i < sumbitPorkerList.size(); ++i){
			int nPorkerValue = sumbitPorkerList.get(i);
			
			String strPorkerColor = PorkerValueEnum.getColorByPorkValue(nPorkerValue);
			int nPorkerFaceValue = PorkerValueEnum.getFaceValueByPorkerValueOf(nPorkerValue);
			printBuffer.append("   " + strPorkerColor.toString() + "  " + nPorkerFaceValue + "  ;");
		}
		System.out.println(printBuffer.toString());
		
    	
    	// 排序
		List<Integer> containerSumbitPorker = new ArrayList<>(sumbitPorkerList);
    	Collections.sort(containerSumbitPorker);
    	
    	// 判断是否有这些牌
    	boolean bCheckResult = currentPlayer.checkIsExistPorkerArray(containerSumbitPorker);
    	if(false == bCheckResult){
    		System.out.println("该玩家没有这些手牌");
    		currentPlayer.showLogPlayerPorker();
    		nofityClientLogic.notifySumbitNullPorkerToServer(currentPlayer);
    		return bSumbitPorkerResult;
    	}
    	
    	// 比较函数
    	boolean bSumbitResult = currentRoom.comparePorkerAction(currentPlayer, containerSumbitPorker);
    	return bSumbitResult;
    }
	
	
	
	// 过牌
	public boolean sumbitNullPorkerToServer(String strUserId){
		boolean bSumbitPorkerResult = false; 
		if(null == strUserId){
			 logger.error("SumbitPorkerLogic::sumbitNullPorkerToServer strUserId Null Error");
	     	 return bSumbitPorkerResult;
	     }
		 
		 
		TeamGroup teamGroup = currentRoom.getTeamGroup();
		if(null == teamGroup){
			 logger.error("SumbitPorkerLogic::sumbitNullPorkerToServer teamGroup Null Error");
			 return bSumbitPorkerResult;
	     }
		
		OperationTag canOperationTag = currentRoom.getOperationTag();
		if(null == canOperationTag){
			 logger.error("SumbitPorkerLogic::sumbitNullPorkerToServer canOperationTag Null Error");
			 return bSumbitPorkerResult;
	     }
		
		NotifyClientLogic nofityClientLogic = currentRoom.getNotifyClientLogic();
		if(null == nofityClientLogic){
			 logger.error("SumbitPorkerLogic::sumbitNullPorkerToServer nofityClientLogic Null Error");
			 return bSumbitPorkerResult;
	     }
		
		RoomGameTimer roomGameTimer = currentRoom.getRoomGameTimer();
		if(null == roomGameTimer){
			 logger.error("SumbitPorkerLogic::sumbitNullPorkerToServer roomGameTimer Null Error");
			 return bSumbitPorkerResult;
	     }
		
		GameComparer roomGameComparer = currentRoom.getGameComparer();
		if(null == roomGameComparer){
			 logger.error("SumbitPorkerLogic::sumbitNullPorkerToServer roomGameComparer Null Error");
			 return bSumbitPorkerResult;
	     }
		
		// 真正逻辑函数开始的地方
		boolean bIsCanSumbitPorker = canOperationTag.isCanSumbitPorker(strUserId);
		if(false == bIsCanSumbitPorker){
			System.out.println("现在还没到出牌的时候");
			return bSumbitPorkerResult;
	    }
	    	
		// 是否是当前控制者
		String strControler = currentRoom.getCurrentControlUser();
	    if(false == strUserId.equals(strControler)){
	    	logger.error("SumbitPorkerLogic::sumbitNullPorkerToServer controlUser.getControlUser() Error");
	     	return false;
	   }
	    	
	    GamePlayer player = teamGroup.searchGamePlayerByUserId(strUserId);
	    if(null == player){
	    	logger.error("SumbitPorkerLogic::sumbitNullPorkerToServer player Error");
	     	return false;
	    }
	    	
	 
	   // 如果是第一个 或者 是当前控制者
	   boolean bMustHandPorker = roomGameComparer.isMustHandPlayerPorker(strUserId);
	   if(true == bMustHandPorker){
		   System.out.println("上一局最大者   这一局必须出牌  不能过牌");
		   nofityClientLogic.notifySumbitNullPorkerToServer(player);
	       return false;
	   }
	    	
	    // 禁用定时器
	    roomGameTimer.cancelTimer();
	    	
	    StringBuffer printBuffer = new StringBuffer();
	    printBuffer.append(strUserId + " Sumbit Null Porker To Server ");
	    System.out.println(printBuffer.toString());
	    	
	    // 通知客户端
		nofityClientLogic.notifySumbitNullPorkerToServer(player);
		
	    // 控制权转交给下一个人
		canOperationTag.canNotSumbitPorker();
		roomGameTimer.startClientPerformanceTimer();
	    return true;
	  }
		  
}
