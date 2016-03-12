package com.lbwan.game.utils;



public class IntToolUtils {
	private int toolValue = 0;
	
	public IntToolUtils(){
		this.toolValue = 0;
	}
	
	public IntToolUtils(int nInitData){
		this.toolValue = nInitData;
	}
	
	public boolean decDataByParam(int nParamValue){
		if(toolValue >= nParamValue){
			toolValue = toolValue - nParamValue;
			return true;
		}
		
		return false;
	}
	
	public boolean addDataByParam(int nParamValue){
		toolValue = toolValue + nParamValue;
		return true;
	}
	
	public void incDataValue(){
		int nParamValue = 1;
		toolValue = toolValue + nParamValue;
	}
	
	public int getToolValueByInt(){
		return this.toolValue;
	}
	
	public boolean isEqualToData(int nCompareData){
		return this.toolValue == nCompareData;
	}
	
	public void resetUtilValue(int nValuelParam){
		this.toolValue = nValuelParam;
	}
}
