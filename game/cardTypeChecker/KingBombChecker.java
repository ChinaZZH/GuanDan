package com.lbwan.game.cardTypeChecker;

import java.util.List;



public class KingBombChecker extends BaseCardTypeChecker{
	public KingBombChecker(){
		super();
	}
	
	public int isBelongToSpecficType(List<Integer> sumbitPorkerList, int nCurrentMajorCard){
		int nErrorResult = 0;
		int nSucceedResult = 1;
		boolean bCheckResult = this.checkIsSatisfyPorkerNum(sumbitPorkerList, 4);
		if(false == bCheckResult){
			return nErrorResult;
		}
		
		// 判断是否是两根大王  两根小王
		int nBigKingPorkerNum = 0, nSmallKingPorkerNum = 0;
		int nPorkerNum = sumbitPorkerList.size();
		for(int i = 0; i < nPorkerNum; ++i){
			
			// 不为大王 也不为小王
			int nPorkValue = sumbitPorkerList.get(i);
			boolean bBigKing = porkerManager.isBelongToBigKing(nPorkValue);
			if(true == bBigKing){
				nBigKingPorkerNum = nBigKingPorkerNum + 1;
			}
			
			boolean bSmallKing = porkerManager.isBelongToSmallKing(nPorkValue);
			if(true == bSmallKing){
				nSmallKingPorkerNum = nSmallKingPorkerNum + 1;
			}
		}
		
		bCheckResult = ((2 == nBigKingPorkerNum) && (2 == nSmallKingPorkerNum));
		if(false == bCheckResult){
			return nErrorResult;
		}
		
		return nSucceedResult;
	}
}
