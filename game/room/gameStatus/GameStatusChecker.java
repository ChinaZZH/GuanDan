package com.lbwan.game.room.gameStatus;

import com.lbwan.game.room.roomGame.RoomGame;

public class GameStatusChecker {
	private GameStatusEnum statusValue;
	
	private RoomGame gameOfRoom = null;
	
	public GameStatusChecker(RoomGame gameParam){
		statusValue = GameStatusEnum.GameStatus_End;
		gameOfRoom = gameParam;
	}
	
	public void startGame(){
		statusValue = GameStatusEnum.GameStatus_Start;
	}
	
	public boolean readyForGame(){
		if(GameStatusEnum.GameStatus_End != GameStatusEnum.GameStatus_End){
			return false;
		}
		
		statusValue = GameStatusEnum.GameStatus_Ready;
		return true;
	}
	
	public boolean startRunPayTribute(){
		if(GameStatusEnum.GameStatus_Ready != statusValue){
			return false;
		}
		
		// 跟在准备游戏后面
		statusValue = GameStatusEnum.GameStatus_PayTribute;
		return true;
	}
	
	public void endGame(){
		statusValue = GameStatusEnum.GameStatus_End;
	}
	
	public GameStatusEnum getGameStatus(){
		return this.statusValue;
	}
}
