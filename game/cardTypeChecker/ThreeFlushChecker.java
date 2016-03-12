package com.lbwan.game.cardTypeChecker;

import java.util.List;
import com.lbwan.game.porkerEnumSet.FaceValueEnum;
import com.lbwan.game.utils.IntToolUtils;


public class ThreeFlushChecker extends AbstractFlushChecker{
	public ThreeFlushChecker(){
		super();
	}
	
	public int isBelongToSpecficType(List<Integer> sumbitPorkerList, int nCurrentMajorCard){
		int nErrorResult = 0;
		boolean bCheckResult = this.checkIsSatisfyPorkerNum(sumbitPorkerList, 6);
		if(false == bCheckResult){
			return nErrorResult;
		}

		Integer nTempMajorCardNum = this.getMajorHeartNumber(sumbitPorkerList, nCurrentMajorCard);
		if(nTempMajorCardNum > 2){
			return nErrorResult; 
		}
		
		
		IntToolUtils majorCardUtils = new IntToolUtils(nTempMajorCardNum);
		List<Integer> handPorkerList = this.CheckIsContinuePiecesNum(sumbitPorkerList, nCurrentMajorCard, majorCardUtils, 3);
		if(null == handPorkerList){
			return nErrorResult;
		}
		
		
		// 主牌红桃2 大于两张
		if(false == majorCardUtils.isEqualToData(0)){
			return nErrorResult; 
		}
		
		int nHandListSize = handPorkerList.size();
		if(2 != nHandListSize){
			return nErrorResult;
		}
		
		boolean bCheckIsMin = this.checkIsMinThreeFlush(handPorkerList, majorCardUtils.getToolValueByInt());
		if(true == bCheckIsMin){
			return FaceValueEnum.FACE_VALUE_A_VALUE;
		}
		
		// 已经排好序的数组
		if(FaceValueEnum.FACE_VALUE_A_VALUE == handPorkerList.get(0)){
			return nErrorResult;
		}
		
		int nHandPatternResult = this.helpGetOrderPorkerStartIndex(handPorkerList, majorCardUtils.getToolValueByInt());
		return nHandPatternResult;
	}
	
	private boolean checkIsMinThreeFlush(List<Integer> tempPorkerList, int nMajorCardNum){
		boolean bIsFlushResult = false;
		int nSumbitListSize = tempPorkerList.size();
		if(nSumbitListSize + nMajorCardNum != 2){
			return bIsFlushResult;
		}
		
		if(FaceValueEnum.FACE_VALUE_A_VALUE != tempPorkerList.get(nSumbitListSize-1)){
			return bIsFlushResult;
		}	
		
		if(FaceValueEnum.FACE_VALUE_2_VALUE != tempPorkerList.get(0)){
			return bIsFlushResult;
		}
		
		bIsFlushResult = true;
		return bIsFlushResult;
	}
}
