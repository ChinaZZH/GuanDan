/*
 * 
 *                   一对牌类型判断器 
 *                   郑祖煌  
 *                   2015.04.11
 * */

package com.lbwan.game.cardTypeChecker;

import java.util.List;

import com.lbwan.game.porkerEnumSet.FaceValueEnum;


public class CouplePorkerChecker extends BaseCardTypeChecker{
	public CouplePorkerChecker(){
		super();
	}
	
	public int isBelongToSpecficType(List<Integer> sumbitPorkerList, int nCurrentMajorCard){
		int nErrorResult = 0;
		boolean bCheckResult = this.checkIsSatisfyPorkerNum(sumbitPorkerList, 2);
		if(false == bCheckResult){
			return nErrorResult;
		}
		
		int nFristPorkValue  = sumbitPorkerList.get(0);
		int nSecondPorkValue = sumbitPorkerList.get(1);
		
		// 是否是王对
		int nIsKingCouple = this.checkIsKingCouple(nFristPorkValue, nSecondPorkValue);
		if(0 != nIsKingCouple){
			return nIsKingCouple;
		}
			
		// 不是王对的情况下  一对含有王的时候是不能成为一对的.
		boolean bIsHavaKingValue = this.checkIsHaveKingValue(nFristPorkValue, nSecondPorkValue);
		if(true == bIsHavaKingValue){
			return nErrorResult;
		}
		
		int nFaceValueResult = this.isSameFaceValueOfPorker(sumbitPorkerList, nCurrentMajorCard);
		return nFaceValueResult;
	}
	
	private int checkIsKingCouple(int nFristPorkValue, int nSecondPorkValue){
		int nNoKingCouople = 0;
		boolean bFirstIsBigKing  = porkerManager.isBelongToBigKing(nFristPorkValue);
		boolean bSecondIsBigKing = porkerManager.isBelongToBigKing(nSecondPorkValue);
		if((true == bFirstIsBigKing) && (true == bSecondIsBigKing)){
			return FaceValueEnum.FACE_VALUE_BIG_JORKER;
		}
		
		boolean bFirstIsSmallKing  = porkerManager.isBelongToSmallKing(nFristPorkValue);
		boolean bSecondIsSmallKing = porkerManager.isBelongToSmallKing(nSecondPorkValue);
		if((true == bFirstIsSmallKing) && (true == bSecondIsSmallKing)){
			return FaceValueEnum.FACE_VALUE_SMALL_JORKER;
		}
		
		return nNoKingCouople;
	}
	
	private boolean checkIsHaveKingValue(int nFristPorkValue, int nSecondPorkValue){
		boolean bFirstIsBigKing  = porkerManager.isBelongToBigKing(nFristPorkValue);
		boolean bSecondIsBigKing = porkerManager.isBelongToBigKing(nSecondPorkValue);
		if((true == bFirstIsBigKing) || (true == bSecondIsBigKing)){
			return true;
		}
		
		boolean bFirstIsSmallKing  = porkerManager.isBelongToSmallKing(nFristPorkValue);
		boolean bSecondIsSmallKing = porkerManager.isBelongToSmallKing(nSecondPorkValue);
		if((true == bFirstIsSmallKing) || (true == bSecondIsSmallKing)){
			return true;
		}
		
		return false;
	}
}


