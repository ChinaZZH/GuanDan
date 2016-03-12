package com.lbwan.game.cardTypeSearch;

import com.lbwan.game.porkerEnumSet.CardColorEnum;


public class ParameterUtils {
	
	private int majorFaceValue;
	
	private int majorHeartValueNum;
	
	private int currentFaceValue;
	
	private int needPorkerNum;
	
	private int cardTypeEnumValue;
	
	// true 为等于主牌的时候考虑主牌, false 为不等于主牌的时候考虑主牌
	private boolean considerMajorFace;
	
	// 连续的多少根牌
	private int flushContinueDiffNumber;
	
	// 每个不同类型的牌的个数
	private int flushSameFacePorkerNumber;
	
	// 最小的开始的面值的值
	private int minValueBeginFace;
	
	private int color = CardColorEnum.CARD_COLOR_ERROR;
	
	public ParameterUtils(){
		
	}
	
	public void setCardColor(int nCardColorParam){
		this.color = nCardColorParam;
	}
	
	public int getCardColor(){
		return this.color;
	}
	
	public void setFlushContinueDiffNumber(int nFlushContinueDiffNumber){
		this.flushContinueDiffNumber = nFlushContinueDiffNumber;
	}
	
	public int getFlushContinueDiffNumber(){
		return this.flushContinueDiffNumber;
	}
	
	public void setFlushSameFacePorkerNumber(int nFlushSameFacePorkerNumber){
		this.flushSameFacePorkerNumber = nFlushSameFacePorkerNumber;
	}
	
	public int getFlushSameFacePorkerNumber(){
		return this.flushSameFacePorkerNumber;
	}
	
	public void setMinValueBeginFace(int nMinValueBeginFace){
		this.minValueBeginFace = nMinValueBeginFace;
	}
	
	public int getMinValueBeginFace(){
		return this.minValueBeginFace;
	}
	
	public void setMajorFaceValue(int nMajorFaceValue){
		this.majorFaceValue = nMajorFaceValue;
	}
	
	public int getMajorFaceValue(){
		return this.majorFaceValue;
	}
	
	public void setMajorHeartValueNum(int nMajorHeartValueNum){
		this.majorHeartValueNum = nMajorHeartValueNum;
	}
	
	public int getMajorHeartValueNum(){
		return this.majorHeartValueNum;
	}
	
	
	public void setCurrentFaceValue(int nCurrentFaceValue){
		this.currentFaceValue = nCurrentFaceValue;
	}
	
	public int getCurrentFaceValue(){
		return this.currentFaceValue;
	}
	
	
	public void setNeedPorkerNum(int nNeedPorkerNum){
		this.needPorkerNum = nNeedPorkerNum;
	}
	
	public int getNeedPorkerNum(){
		return this.needPorkerNum;
	}
	
	public void setCardTypeEnumValue(int nCardTypeEnumValue){
		this.cardTypeEnumValue = nCardTypeEnumValue;
	}
	
	public int getCardTypeEnumValue(){
		return this.cardTypeEnumValue;
	}
	
	
	public void setConsiderMajorFace(boolean bConsiderMajorFace){
		this.considerMajorFace = bConsiderMajorFace;
	}
	
	public boolean getConsiderMajorFace(){
		return this.considerMajorFace;
	}
}
