package com.lbwan.game.room.roomGameLogic;

public class OperationTag {
	// 当前时间段 是否可以出牌
	private boolean canOperationPorker = false;
	
	// 如果可以出牌  对应的出牌玩家
	private String operationUser = null;
	
	public OperationTag(){
		this.canOperationPorker = false;
		this.operationUser = null;
	}
	
	public boolean isCanSumbitPorker(String strSumbitUser){
		boolean bCanSumbitPorker = false;
		if(null == strSumbitUser){
			return bCanSumbitPorker;
		}
		
		boolean bEqualUser = strSumbitUser.equals(operationUser);
		if(false == bEqualUser){
			return bCanSumbitPorker;
		}
		
		bCanSumbitPorker = (true == canOperationPorker);
		return bCanSumbitPorker;
	}
	
	public void canSumbitPorker(String strOperationUser){
		this.operationUser = strOperationUser;
		this.canOperationPorker = true;
	}
	
	public void canNotSumbitPorker(){
		this.operationUser = null;
		this.canOperationPorker = false;
	}
}
