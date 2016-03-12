/*
 * 
 *                   单根牌类型判断器 
 *                   郑祖煌  
 *                   2015.04.11
 * */

package com.lbwan.game.cardTypeChecker;

import java.util.List;



public class SinglePorkerChecker extends BaseCardTypeChecker{

	public SinglePorkerChecker(){
		super();
	}
	
	public int isBelongToSpecficType(List<Integer> sumbitPorkerList, int nCurrentMajorCard){
		int nErrorResult = 0;
		boolean bCheckResult = this.checkIsSatisfyPorkerNum(sumbitPorkerList, 1);
		if(false == bCheckResult){
			return nErrorResult;
		}
		
		int nPorkValue = sumbitPorkerList.get(0);
		int nFaceValue = porkerManager.getFaceValue(nPorkValue);
		return nFaceValue;
	}
}
