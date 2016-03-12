package com.lbwan.game.payTributeHandler;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;



import com.lbwan.game.payTributeChecker.PayTributeEnum;
import com.lbwan.game.room.gameStatus.GameStatusChecker;
import com.lbwan.game.room.roomGame.GameControlUser;
import com.lbwan.game.room.roomGame.RoomGame;
import com.lbwan.game.room.roomGameTimer.RoomGameTimer;

@Service
public class PayTributeHandlerHolder {
	protected Logger logger = Logger.getLogger(getClass());
	
	private Map<Integer, PayTributeHandler> allTributeHandlerMap = new HashMap<Integer, PayTributeHandler>();
	
	public synchronized void initCheckerHolder(){
		if(false == allTributeHandlerMap.isEmpty()){
			return ;
		}
		
		allTributeHandlerMap.put(PayTributeEnum.TRIBUTE_ALL_LOW_REACT, new ReactToPayTributeHandler());
		allTributeHandlerMap.put(PayTributeEnum.TRIBUTE_SINGLE_LOW_REACT, new ReactToPayTributeHandler());
		
		allTributeHandlerMap.put(PayTributeEnum.TRIBUTE_SINGLE_LOW_TRIBUTE, new SinglePayTributeHandler());	
		
		allTributeHandlerMap.put(PayTributeEnum.TRIBUTE_ALL_LOW_SAME_TRIBUTE_PORKER, new DoubleSameTributeHandler());
		allTributeHandlerMap.put(PayTributeEnum.TRIBUTE_ALL_LOW_DIFF_TRIBUTE_PORKER, new DoubleDiffTributeHandler());
	}
	
	private PayTributeHandler getHandlerByTributeType(int nTributeType){
		if(true == allTributeHandlerMap.isEmpty()){
			this.initCheckerHolder();
		}
		
		PayTributeHandler handler = allTributeHandlerMap.get(nTributeType);
		if(null == handler){
			logger.error("PayTributeHandlerHolder::getHandlerByTributeType handler Null Error");
			return null;
		}
		
		return handler;
	}
	
	public boolean processPayTribute(RoomGame currentRoomGame, int nTributeType){
		boolean bProcessResult = false;
		// 设置下一局的主牌
		if(null == currentRoomGame){
			logger.error("PayTributeHandlerHolder::processPayTribute currentRoomGame Null Error");
			return bProcessResult;
		}
		
		GameStatusChecker statusChecker = currentRoomGame.getGameStatusChecker();
		if(null == statusChecker){
			logger.error("PayTributeHandlerHolder::processPayTribute statusChecker Null Error");
			return bProcessResult;
		}
		
		
		PayTributeHandler handler = this.getHandlerByTributeType(nTributeType);
		if(null == handler){
			logger.error("PayTributeHandlerHolder::processPayTribute handler Null Error");
			return bProcessResult;
		}
		
		// 设置器状态为  进贡状态
		boolean bStartPayTribute = statusChecker.startRunPayTribute();
		if(false == bStartPayTribute){
			logger.error("PayTributeHandlerHolder::processPayTribute startRunPayTribute Error");
			return bProcessResult;
		}
		
		bProcessResult = handler.processPayTribute(currentRoomGame);
		if(false == bProcessResult){
			logger.error("PayTributeHandlerHolder::processPayTribute bProcessResult Error");
			return bProcessResult;
		}
		
		// 不为抗贡的情况下
		boolean bReactTribute = PayTributeEnum.isBelongToReactTribute(nTributeType);
		if(false == bReactTribute){
			RoomGameTimer gameTimer = currentRoomGame.getRoomGameTimer();
			if(null == gameTimer){
				logger.error("PayTributeHandlerHolder::processPayTribute gameTimer Null Error");
				return bProcessResult;
			}
			
			//
			
			// 开启进贡定时器
			gameTimer.startPayTributeTimer();
		}
		
		bProcessResult = true;
		return bProcessResult;
	}
	
	// 回调函数  结束进贡  开始游戏
	public boolean startGameByEndTributeCallBack(RoomGame currentRoomGame, String strNewControlUser){
		// 设置其为起牌者
		boolean bProcessResult = false;
		if(null == currentRoomGame){
			logger.error("PayTributeHandlerHolder::startGameByEndTributeCallBack currentRoomGame Null Error");
			return bProcessResult;
		}
		
		GameControlUser controlUser = currentRoomGame.getGameControlUser();
		if(null == controlUser){
			logger.error("PayTributeHandlerHolder::startGameByEndTributeCallBack controlUser Null Error");
			return bProcessResult;
		}
		
		
		// 设置起牌者 到内存
		controlUser.initFirstControlByNewGameRound(strNewControlUser);
		
		// 执行新的一局游戏开始的操作
		currentRoomGame.runGameStartAction();
		
		bProcessResult = true;
		return bProcessResult;
	}
}

