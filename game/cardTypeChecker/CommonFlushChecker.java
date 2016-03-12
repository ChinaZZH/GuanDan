package com.lbwan.game.cardTypeChecker;

import java.util.List;



public class CommonFlushChecker extends AbstractFlushChecker{
	public CommonFlushChecker(){
		super();
	}
	
	public int isBelongToSpecficType(List<Integer> sumbitPorkerList, int nCurrentMajorCard){
		int nErrorResult = 0;
		boolean bCheckResult = this.checkIsSatisfyPorkerNum(sumbitPorkerList, 5);
		if(false == bCheckResult){
			return nErrorResult;
		}
		
		boolean bSamColor = this.isSameColorOfPorker(sumbitPorkerList, nCurrentMajorCard);
		if(true == bSamColor){
			return nErrorResult;
		}
		
		// 同花顺：同花的五张牌，最大的为同花10-J-Q-K-A，最小的为A-2-3-4-5。
		int nFlushResult = this.isFlushWithoutColor(sumbitPorkerList, nCurrentMajorCard);
		if(0 == nFlushResult){
			return nErrorResult;
		}
		
		
		return nFlushResult;
	}
}
