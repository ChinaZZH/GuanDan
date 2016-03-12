package com.lbwan.game.cardTypeChecker;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.lbwan.game.porkerEnumSet.FaceValueEnum;
import com.lbwan.game.utils.IntToolUtils;


public class FlyPairsChecker extends AbstractFlushChecker{
	public FlyPairsChecker(){
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
		
		IntToolUtils majorCardNumUtils = new IntToolUtils(nTempMajorCardNum);
		List<Integer> handPorkerList = this.CheckIsContinuePiecesNum(sumbitPorkerList, nCurrentMajorCard, majorCardNumUtils, 2);
		if(null == handPorkerList){
			return nErrorResult;
		}
		
		// 主牌红桃2 大于两张  必须是两个 或者 三连对
		int nMapSize = handPorkerList.size();
		if((nMapSize  != 2) && (nMapSize != 3)){
			return nErrorResult;
		}		
		
		boolean bCheckResultPartOne = ((2 == nMapSize) && (2 == majorCardNumUtils.getToolValueByInt()));
		boolean bCheckResultPartTwo = ((3 == nMapSize) && (0 == majorCardNumUtils.getToolValueByInt()));
		if((false == bCheckResultPartOne) && (false == bCheckResultPartTwo)){
			return nErrorResult;
		}
		
		if(true == majorCardNumUtils.isEqualToData(2)){
			majorCardNumUtils.resetUtilValue(1);
		}
		
		
		boolean bCheckIsMin = this.checkIsMinThreeContinuePair(handPorkerList, majorCardNumUtils.getToolValueByInt());
		if(true == bCheckIsMin){
			return FaceValueEnum.FACE_VALUE_A_VALUE;
		}
		
		// 已经排好序的数组
		if(FaceValueEnum.FACE_VALUE_A_VALUE == handPorkerList.get(0)){
			return nErrorResult;
		}
				

		int nResult = this.helpGetOrderPorkerStartIndex(handPorkerList, majorCardNumUtils.getToolValueByInt());
		return nResult;
	}
	
	private boolean checkIsMinThreeContinuePair(List<Integer> tempPorkerList, int nMajorCardNum){
		boolean bIsFlushResult = false;
		if(nMajorCardNum > 1){
			return bIsFlushResult;
		}
		
		Set<Integer> minFlushSet = new HashSet<Integer>();
		minFlushSet.add(FaceValueEnum.FACE_VALUE_2_VALUE);
		minFlushSet.add(FaceValueEnum.FACE_VALUE_3_VALUE);
		int nSumbitListSize = tempPorkerList.size();
		if(nSumbitListSize < 2){
			return bIsFlushResult;
		}
		
		bIsFlushResult = this.checkIsMinPorkerHelp(tempPorkerList, minFlushSet, nMajorCardNum, 3);
		return bIsFlushResult;
	}
		
}



 