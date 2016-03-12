package com.lbwan.game.cardTypeChecker;

import java.util.List;



public class CommonBombChecker extends BaseCardTypeChecker{
	public CommonBombChecker(){
		super();
	}
	
	public int isBelongToSpecficType(List<Integer> sumbitPorkerList, int nCurrentMajorCard){
		int nErrorResult = 0;
		boolean bCheckResult = this.checkIsSatisfyRangePorkerNum(sumbitPorkerList, 4, 10);
		if(false == bCheckResult){
			return nErrorResult;
		}
		
		
		int nFaceValue = this.isSameFaceValueOfPorker(sumbitPorkerList, nCurrentMajorCard);
		return nFaceValue;
	}
}
