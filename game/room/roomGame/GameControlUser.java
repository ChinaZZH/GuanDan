package com.lbwan.game.room.roomGame;

import java.util.List;

import org.apache.log4j.Logger;

import com.lbwan.game.porkerComparer.GameComparer;
import com.lbwan.game.room.Player;
import com.lbwan.game.room.gameTeam.TeamGroup;

public class GameControlUser {
	
	private boolean firstGameRoundForRoom = true;
	
	private String currentControllerId = "initPlayer";
	
	private RoomGame game = null;
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	public GameControlUser(RoomGame gameParam){
		firstGameRoundForRoom = true;
		currentControllerId = "initPlayer";
		this.game = gameParam;
	}
	
	public String initFirstControllerUser(List<Player> players){
		if(false == firstGameRoundForRoom){
			return null;
		}
		
		firstGameRoundForRoom = false;
		//int nRandValueIndex = RandomUtils.getRandomValue(0,players.size() - 1);
		//String strControlUser = players.get(nRandValueIndex).getUserId();
		
		String strControlUser = players.get(0).getUserId();	
		this.initFirstControlByNewGameRound(strControlUser);
		//System.out.println("Current Control User: " + strControlUser);
		return strControlUser;
	}
	
	
	public void initFirstControlByNewGameRound(String strNewControlUser){
		this.setCurrentPlayer(strNewControlUser);
		//System.out.println("Current Control User: " + strNewControlUser);
	}
	
	public boolean isFirstRoundForRoom(){
		if(true == firstGameRoundForRoom){
			return true;
		}
		
		return false;
	}
	
	public String getControlUser(){
		return this.currentControllerId;
	}
	
	private void setCurrentPlayer(String strControlUserParam){
		this.currentControllerId = strControlUserParam;
		//System.out.println("Current Control User: " + strControlUserParam);
	}
	

	
	public int nextPlayerControlGame(){
		
		GamePlayer player = game.searchGamePlayerByUserId(this.getControlUser());
		if(null == player){
			logger.error("GameControlUser::nextPlayerControlGame player Error");
			return NextControlUserEnum.FAIL;
		}
		

		int nResultCode = NextControlUserEnum.ANY_PORKER;
		// 判断是否需要接风
		String strNextControllerId = this.IsTakeTeamPlayerTurn(player);
		// 不是接风的情况下
		if(null == strNextControllerId){
			strNextControllerId = this.getNextPlayerWhoHasPorker(player);
			nResultCode = NextControlUserEnum.BIGGER_PORKER;
		}

		if(null == strNextControllerId){
			logger.error("GameControlUser::nextPlayerControlGame strNextControllerId Error");
			return NextControlUserEnum.FAIL;
		}
		
	    // 控制权转交
		this.setCurrentPlayer(strNextControllerId);
		return nResultCode;
	}
	
	private String getNextPlayerWhoHasPorker(GamePlayer currentPlayer){
		String strErrorUserId = null;
		if(null == currentPlayer){
			logger.error("GameControlUser::getNextPlayerWhoHasPorker strErrorUserId Null Error");
	 	    return strErrorUserId;
		}
		
		
		// 不是接风的情况下
		String strFirstUserId = currentPlayer.getGamePlayerId();
		while(true){
			String strNextUserId = currentPlayer.getNextPlayerId();
			if(true == strNextUserId.equals(strFirstUserId)){
				logger.error("GameControlUser::getNextPlayerWhoHasPorker strFirstUserId Null Error");
	     	    return strErrorUserId;
			}
			
			GamePlayer nextPlayer = game.searchGamePlayerByUserId(strNextUserId);
			if(null == nextPlayer){
				logger.error("GameControlUser::getNextPlayerWhoHasPorker nextPlayer Error");
	     	    return strErrorUserId;
			}
			
			if(0 == nextPlayer.getPlayerHandPorkerNum()){
				currentPlayer = nextPlayer;
				continue;
			}
			
			return strNextUserId;
		}
	}
	
	// 是否是接风的情况下
	public String IsTakeTeamPlayerTurn(GamePlayer currentPlayer){
		if(null == currentPlayer){
			return null;
		}
		
		GameComparer roomGameComparer = game.getGameComparer();
		if(null == roomGameComparer){
			logger.error("GameControlUser::nextPlayerControlGame roomGameComparer Null Error");
			return null;
		}
		
		String strLastMaxPorkerPlayer = roomGameComparer.getMaxPorkerPlayerId();
		if(null == strLastMaxPorkerPlayer){
			return null;
		}
		

		String strNextUserId = currentPlayer.getNextPlayerId();
		while(true){
			GamePlayer nextPlayer = game.searchGamePlayerByUserId(strNextUserId);
			if(null == nextPlayer){
				logger.error("GameControlUser::nextPlayerControlGame nextPlayer Error");
		 	    return null;
			}
			
			int nHandPorkerNum = nextPlayer.getPlayerHandPorkerNum();
			// 如果下一个人的手牌大于0 则不是逢人配的情况
			if(nHandPorkerNum > 0){
				return null;
			}
			
			// 以下是牌等于0 的情况下
			boolean bComoparePlayer = strLastMaxPorkerPlayer.equals(strNextUserId);
			// 如果不是那个玩家的情况下 则继续寻找下一个
			if(false == bComoparePlayer){
				strNextUserId = nextPlayer.getNextPlayerId();
				continue;
			}
			
			// 逢人配的情况下
			roomGameComparer.turnToTeamer();
			String strNextTurnUserId = nextPlayer.getTeamPlayerId();
			return strNextTurnUserId;
		}

	}
}


