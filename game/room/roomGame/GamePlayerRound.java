package com.lbwan.game.room.roomGame;

import org.apache.log4j.Logger;

import com.lbwan.gamecenter.remoting.entity.GameDataInfo;


public class GamePlayerRound {
	
	// 玩家Id
	private String gameUserId;
	
	// 玩家玩的总场次
	private int playTotalRounds;
	
	// 玩家胜的场次
	private int winTotalRounds;
	
	// 玩家负的场次
	private int failTotalRounds;
	
	// 逃跑的场次
	private int escapeTotalRounds;
	
	// 胜率
	private double winGameRate;
	
	 
	protected Logger logger = Logger.getLogger(getClass());
	 
	// 构造函数
	public GamePlayerRound(String strGameUserId){
		
		this.gameUserId = strGameUserId;
		this.playTotalRounds = 0;
		this.winTotalRounds = 0;
		this.failTotalRounds = 0;
		this.escapeTotalRounds = 0;
	}
	
	public void initGameWithDataInfo(GameDataInfo dataInfo){
		if(null == dataInfo){
			return ;
		}
		
		int nTotalNum = dataInfo.getWinNum() + dataInfo.getLoseNum() + dataInfo.getExitNum();
		this.initGameRoundData(nTotalNum, dataInfo.getWinNum(), dataInfo.getLoseNum(), dataInfo.getExitNum());
	}
	
	// 初始化数据
	public void initGameRoundData(int nPlayTotalRounds, int nWinTotalRounds, int nFailTotalRounds, int nEscapeTotalRounds){
		this.playTotalRounds   = nPlayTotalRounds;
		this.winTotalRounds    = nWinTotalRounds;
		this.failTotalRounds   = nFailTotalRounds;
		this.escapeTotalRounds = nEscapeTotalRounds;
	
		this.changeWinRate();
	}
	
	private void addTotalPlayRound(){
		playTotalRounds = playTotalRounds + 1;
	}
	
	public void addWinTotalRound(){
		this.addTotalPlayRound();
		winTotalRounds = winTotalRounds + 1;
		this.changeWinRate();
	}
	
	public void addFailTotalRound(){
		this.addTotalPlayRound();
		failTotalRounds = failTotalRounds + 1;
		this.changeWinRate();
	}
	
	public void addEscapeTotalRound(){
		this.addTotalPlayRound();
		escapeTotalRounds = escapeTotalRounds + 1;
		this.changeWinRate();
	}
	
	private void changeWinRate(){
		// 除数 为0 的情况下
		if(0 == this.winTotalRounds){
			this.winGameRate = 0.0f;
			return ;
		}
		
		double fDividedNum = (this.winTotalRounds * (0.01));
		this.winGameRate =  (fDividedNum / this.playTotalRounds) * 100;
	}
	
	public int getTotalPlayRound(){
		return this.playTotalRounds;
	}
	
	public int getWinTotalRound(){
		return this.winTotalRounds;
	}
	
	public int getFailTotalRound(){
		return this.failTotalRounds;
	}
	
	public int getEscapeTotalRound(){
		return this.escapeTotalRounds;
	}
	
	public double getWinGameRate(){
		return this.winGameRate;
	}
}
