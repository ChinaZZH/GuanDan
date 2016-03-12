package com.lbwan.game.room.payTributeData;

public class PayTributeDataUnit {
	// 进贡者
	private String payTributeUser = null;
	
	// 进贡的牌值
	private int  payTributeFaceValue = 0;
	
	// 接受进贡的人
	private String receivePayTributeUser = null;
	
	// 是否已经进贡过
	private boolean isPayTributed  = false;
	
	// 是否已经退贡
	private boolean isBackTributed = false;
	
	public PayTributeDataUnit(String strPayTributeUser, int nPayTributePorkerFaceValue, String strReceivePayTributeUser){
		this.payTributeUser = strPayTributeUser;
		this.payTributeFaceValue = nPayTributePorkerFaceValue;
		this.receivePayTributeUser = strReceivePayTributeUser;
	}

	public String getPayTributeUser(){
		return this.payTributeUser;
	}
	
	public int getPayTributeFaceValue(){
		return this.payTributeFaceValue;
	}
	
	public String getReceivePayTributeUser(){
		return this.receivePayTributeUser;
	}
	
	// 是否已经进贡过
	public boolean isPayTributed(){
		if(true == this.isPayTributed){
			return true;
		}
		
		return false;
	}
	
	public void payTributed(){
		this.isPayTributed = true;
	}
	
	public void clearTributedStatus(){
		this.isPayTributed = false;
		this.isBackTributed = false;
	}
	
	public boolean isBackTributeToPayPlayer(){
		if(true == this.isBackTributed){
			return true;
		}
		
		return false;
	}
	
	public void backTributeToPayPlayer(){
		this.isBackTributed = true;
	}
	
}

