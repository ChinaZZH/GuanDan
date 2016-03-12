package com.lbwan.game.room.roomGameLogic;

// 是否是开局的第一个发牌者
public class GameStarerLogic {
	private boolean bIsStarterOfRound = true;
	
	public GameStarerLogic(){
		bIsStarterOfRound = true;
	}
	
	public void initNewGameRound(){
		bIsStarterOfRound = true;
	}
	
	public boolean isRoundStater(){
		return bIsStarterOfRound == true;
	}
	
	public void clearStarterToRound(){
		bIsStarterOfRound = false;
	}
}
