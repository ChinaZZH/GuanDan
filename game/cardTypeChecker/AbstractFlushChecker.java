package com.lbwan.game.cardTypeChecker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lbwan.game.porkerEnumSet.FaceValueEnum;
import com.lbwan.game.utils.IntToolUtils;

public abstract class AbstractFlushChecker extends BaseCardTypeChecker{
	
	public int isBelongToSpecficType(List<Integer> sumbitPorkerList, int nCurrentMajorCard){
		return 0;
	}
	
	// 是否是同花
	protected boolean isSameColorOfPorker(List<Integer> sumbitPorkerList, int nMajorCardType){
		int nMajorHeartFaceValue = FaceValueEnum.getSpecficHeartByFaceValue(nMajorCardType);
		List<Integer> tempColorValueList = new ArrayList<>();
		int nListSize = sumbitPorkerList.size();
		for(int nIndex = 0 ; nIndex < nListSize; ++nIndex){
			int nTempPorkerValue = sumbitPorkerList.get(nIndex);
			if(nMajorHeartFaceValue != nTempPorkerValue){
				int nTempColorValue = porkerManager.getColor(nTempPorkerValue);
				tempColorValueList.add(nTempColorValue);
			}
		}
		
		if(true == tempColorValueList.isEmpty()){
			return true;
		}
		
		int nFirstColorValue = tempColorValueList.get(0);
		int nTempListSize = tempColorValueList.size();
		for(int i = 0; i < nTempListSize; ++i){
			int nTempColorValue = tempColorValueList.get(i);
			if(nFirstColorValue != nTempColorValue){
				return false;
			}
		}
		
		return true;
	}
	
	
	// 判断是否是顺子    返回顺子的第一张牌  如果是0说明不是顺子
	protected int isFlushWithoutColor(List<Integer> sumbitPorkerList, int nMajorCardType){
		// 同花顺：同花的五张牌，最大的为同花10-J-Q-K-A，最小的为A-2-3-4-5。
		// 是否是顺子
		int nNoFlushResult = 0;
		boolean bStatisfyResult = checkIsSatisfyPorkerNum(sumbitPorkerList, 5);
		if(false == bStatisfyResult){
			return nNoFlushResult;
		}
		
		int nMajorPorkerValue = FaceValueEnum.getSpecficHeartByFaceValue(nMajorCardType);
		if(0 == nMajorPorkerValue){
			return nNoFlushResult;
		}
		
		List<Integer> tempSumbitCardType = new ArrayList<Integer>();
		// 将主牌从容器中删除
		int nMajorCardNum = 0;
		Iterator<Integer> it =  sumbitPorkerList.iterator();
		while(true == it.hasNext()){
			Integer nPorkerValue = it.next();
			if(nMajorPorkerValue == nPorkerValue){
				nMajorCardNum = nMajorCardNum + 1;
				continue;
			}
			
			int nTempCardType = porkerManager.getFaceValue(nPorkerValue); 
			if(0 == nTempCardType){
				logger.error("AbstractFlushChecker::isFlushWithoutColor getCardTypeByPorkerValueOf Null error");
				return nNoFlushResult;
			}
			
			tempSumbitCardType.add(nTempCardType);
		}
		
		
		if(true == tempSumbitCardType.isEmpty()){
			return nNoFlushResult;
		}
		
		if(nMajorCardNum > 2){
			return nNoFlushResult;
		}
		
		// 同花顺：同花的五张牌，最大的为同花10-J-Q-K-A，最小的为A-2-3-4-5。
		// 按从小到大排序
		
		// 判断是否是A,2,3,4,5
		boolean bCheckResult = this.isMinFlushOfPorker(tempSumbitCardType, nMajorCardNum);
		if(true == bCheckResult){
			return FaceValueEnum.FACE_VALUE_A_VALUE;
		}
		
		// 如果不是 A,2,3,4,5 则使用常规算法去计算
		// 已经排好序的数组
		int nFirstFaceValue = tempSumbitCardType.get(0);
		if(FaceValueEnum.FACE_VALUE_A_VALUE == nFirstFaceValue){
			return nNoFlushResult;
		}
		
		int nFlushResult = this.helpGetOrderPorkerStartIndex(tempSumbitCardType, nMajorCardNum);
		return nFlushResult;
	}
	
	// 是否是  A-2-3-4-5
	private boolean isMinFlushOfPorker(List<Integer> sumbitPorkerList, int nMajorCardNum){
		boolean bIsFlushResult = false;
		if(nMajorCardNum > 2){
			return bIsFlushResult;
		}
		
		Set<Integer> minFlushSet = new HashSet<Integer>();
		minFlushSet.add(FaceValueEnum.FACE_VALUE_2_VALUE);
		minFlushSet.add(FaceValueEnum.FACE_VALUE_3_VALUE);
		minFlushSet.add(FaceValueEnum.FACE_VALUE_4_VALUE);
		minFlushSet.add(FaceValueEnum.FACE_VALUE_5_VALUE);
		
		int nSumbitListSize = sumbitPorkerList.size();
		if(nSumbitListSize < 3){
			return bIsFlushResult;
		}
		
		bIsFlushResult = this.checkIsMinPorkerHelp(sumbitPorkerList, minFlushSet, nMajorCardNum, 5);
		return bIsFlushResult;
	}
	
	protected boolean checkIsMinPorkerHelp(List<Integer> sumbitPorkerList, Set<Integer> minFlushSet, int nMajorCardNum, int nTotalElementNum){
		boolean bIsFlushResult = false;
		int nSumbitListSize = sumbitPorkerList.size();
		if(nSumbitListSize + nMajorCardNum != nTotalElementNum){
			return bIsFlushResult;
		}
		
		// 最后一个是否为A
		if(FaceValueEnum.FACE_VALUE_A_VALUE != sumbitPorkerList.get(nSumbitListSize-1)){
			return bIsFlushResult;
		}		
				
		// 0 ~~ nSumbitListSize-2  共 nSumbitListSize-1个数进行校验
		for(int i = 0; i <= nSumbitListSize-2; ++i){
			boolean bContainResult = minFlushSet.contains(sumbitPorkerList.get(i));
			if(false == bContainResult){
				return bIsFlushResult;
			}
		}
		
		bIsFlushResult = true;
		return bIsFlushResult;
	}
	
	// 获取顺序排  返回0 失败  返回其他值为开始的牌号
	protected int helpGetOrderPorkerStartIndex(List<Integer> tempSumbitCardType, int nMajorCardNum){
		int nNoFlushResult = 0;
		if(null == tempSumbitCardType){
			return nNoFlushResult;
		}
		
		if(true == tempSumbitCardType.isEmpty()){
			return nNoFlushResult;
		}
		
		int nTempPorkSize = tempSumbitCardType.size();
		int nPreCardType = tempSumbitCardType.get(0);
		int nFirstCardType = nPreCardType;
		int nStartIndex = 1;
		while(nStartIndex < nTempPorkSize){
			int nCurrCardType = tempSumbitCardType.get(nStartIndex);
			
			// 特殊情况校验
			if((nStartIndex != nTempPorkSize-1) && (FaceValueEnum.FACE_VALUE_A_VALUE == nCurrCardType)){ 
				// 不是同花顺  排完序  A是最后一个 且只有一个
				return nNoFlushResult;
			}
			
			// 判断是否是后续的那一个
			boolean bIsNextCardType = FaceValueEnum.isNextCardType(nPreCardType, nCurrCardType);
			if(true == bIsNextCardType){
				nStartIndex = nStartIndex + 1;
				nPreCardType = nCurrCardType;
				continue;
			}	
			
			// 如果不是后续的那一个 则调用前面的那个
			if(nMajorCardNum > 0){
				nMajorCardNum = nMajorCardNum - 1;
				nPreCardType = FaceValueEnum.getNextCardType(nPreCardType);
			}else{
				return nNoFlushResult;
			}			
		}
		
		// 如果 nMajorCardNum = 0, 则已经求出顺子的开始 下标
		if(0 == nMajorCardNum){
			return nFirstCardType;
		}else{
			// 扫描后面的
			int nEndCardType = tempSumbitCardType.get(nTempPorkSize-1);
			while(nEndCardType < FaceValueEnum.getMaxCardType()){
				nEndCardType = FaceValueEnum.getNextCardType(nEndCardType);
				nMajorCardNum = nMajorCardNum - 1;
				if(0 == nMajorCardNum){
					return nFirstCardType;
				}
			}
			
			// 扫描前面的
			while(nFirstCardType > FaceValueEnum.getMinCardType()){
				nFirstCardType = FaceValueEnum.getPreCardType(nFirstCardType);
				nMajorCardNum = nMajorCardNum - 1;
				if(0 == nMajorCardNum){
					return nFirstCardType;
				}
			}
			
		}
		
		return nNoFlushResult;
	}
	
	protected List<Integer> CheckIsContinuePiecesNum(List<Integer> sumbitPorkerList, int nCurrentMajorCard, IntToolUtils majorHeartNum, int nNeedPorkerNum){
		if(null == sumbitPorkerList){
			logger.error("PorkerCompareLogic::isHandPatternThreeBringTwo sumbitPorkerList null Error");
			return null;
		}
		

		Map<Integer, Integer> porkTypeAndNumMap = this.extractPorkerMap(sumbitPorkerList, nCurrentMajorCard);
		if(null == porkTypeAndNumMap){
			return null;
		}
	    
		List<Integer> handPorkerList = new ArrayList<>();
		Iterator<Map.Entry<Integer, Integer>> iter = porkTypeAndNumMap.entrySet().iterator();
		while(true == iter.hasNext()){
			Map.Entry<Integer, Integer> entry = iter.next();
			
			// 大小王 为负数 不作比较
			Integer nCardType = entry.getKey();
			Integer nCardNum  = entry.getValue();
			if(nCardNum > nNeedPorkerNum){
				return null;
			}
			
			if(nCardNum == nNeedPorkerNum){
				handPorkerList.add(nCardType);
				continue;
			}
			
			// cardNum < nNeedPorkerNum
			int nDiffCardNum = nNeedPorkerNum - nCardNum;
			if(nDiffCardNum <= 0){
				return null;
			}
			
			boolean bResult = majorHeartNum.decDataByParam(nDiffCardNum);
			if(false == bResult){
				return null;
			}
			
			
			nCardNum = nNeedPorkerNum;
			handPorkerList.add(nCardType);
		}
		
		
		return handPorkerList;
	}
}
