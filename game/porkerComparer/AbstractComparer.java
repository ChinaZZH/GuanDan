package com.lbwan.game.porkerComparer;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.lbwan.game.cardTypeChecker.Checker;
import com.lbwan.game.cardTypeSearch.CardTypeCheckerMgr;
import com.lbwan.game.porker.PorkerMgr;
import com.lbwan.game.porkerEnumSet.CardTypeEnum;
import com.lbwan.game.porkerEnumSet.FaceValueEnum;
import com.lbwan.game.spring.SpringUtils;

public abstract class AbstractComparer implements Comparer{
	
	@Autowired
	protected PorkerMgr porkerManager = (PorkerMgr) SpringUtils.getBeanByName("porkerMgr");
	
	@Autowired
	protected ComparerManager compManager = (ComparerManager) SpringUtils.getBeanByName("comparerManager");
	
	@Autowired
	protected CardTypeCheckerMgr checkerManager = (CardTypeCheckerMgr) SpringUtils.getBeanByName("cardTypeCheckerMgr");
	
	protected Logger logger = Logger.getLogger(getClass());
	
	public int biggerThanLastMaxPorker(List<Integer> sumbitPorkerList, int nLastMaxHandPorker, int nCurrentMajorCard){
		return 0;
	}
	
	// 单根牌进行比较  如果提交的牌比较大 则返回true 否则为false
	public boolean sumbitPorkerBiggerBySinglePorker(int nSumbitFaceValue, int nMaxFaceValue, int nMajorFaceValue){
		boolean bSumbitBiggerResult = true, bSumbitFailResult = false;
		boolean bMaxIsBigKing = FaceValueEnum.isBelongToBigKing(nMaxFaceValue); 
		if(true == bMaxIsBigKing){
			return bSumbitFailResult;
		}
		
		boolean bMaxIsSmallKing  = FaceValueEnum.isBelongToSmallKing(nMaxFaceValue); 
		boolean bSumbitIsBigKing = FaceValueEnum.isBelongToBigKing(nSumbitFaceValue); 
		if(true == bMaxIsSmallKing){
			if(true == bSumbitIsBigKing){
				return bSumbitBiggerResult;
			}
			
			return bSumbitFailResult;
		}
		
		boolean bSumbitIsSmallKing = FaceValueEnum.isBelongToSmallKing(nSumbitFaceValue); 
		if((true == bSumbitIsBigKing) || (true == bSumbitIsSmallKing)){
			return bSumbitBiggerResult; 
		}
		
		// 判断nSumbitPorkerValue是否是主牌, nMaxPorkerValue 是否是主牌
		// 同一个数值类型 则大小相同
		if(nSumbitFaceValue == nMaxFaceValue){
			return bSumbitFailResult;
		}
		
		// 如果sumbitCardType 为主牌 则一定比nMaxFaceValue更大
		if(nSumbitFaceValue == nMajorFaceValue){
			return bSumbitBiggerResult;
		}
		
		// 如果nMaxFaceValue 为主牌 则一定比sumbitCardType更大
		if(nMaxFaceValue == nMajorFaceValue){
			return bSumbitFailResult;
		}
		
		// 如果nMaxFaceValue与nSumbitFaceValue都不为大小鬼  并且都不是主牌
		int nCompareResult = FaceValueEnum.compareTwoFaceValue(nSumbitFaceValue, nMaxFaceValue);
		if(FaceValueEnum.Greater_Result == nCompareResult){
			return bSumbitBiggerResult;
		}
		
		return bSumbitFailResult; 
	}
	
	protected int isBombForSumbitPorker(List<Integer> sumbitPorkerList, int nCurrentMajorCard, int nSumbitCardNum){
		int nNewHandPattern = 0;
		boolean bStatifyBomb = CardTypeEnum.isStatisfyBomb(nSumbitCardNum);
		if(true == bStatifyBomb){
			nNewHandPattern = compManager.isBelongToBombGroup(sumbitPorkerList, nCurrentMajorCard);
			return nNewHandPattern;
		}
		
		return nNewHandPattern;
	}
	
	// 比较顺子的大小
	protected boolean compareFlushByCardType(int nSumbitFaceValue, int nMaxFaceValue){
		boolean bSumbitBiggerSucceed = true;
		if(nSumbitFaceValue == nMaxFaceValue){
			bSumbitBiggerSucceed = false;
			return bSumbitBiggerSucceed;
		}
		
		if(FaceValueEnum.FACE_VALUE_A_VALUE == nMaxFaceValue){
			return bSumbitBiggerSucceed;
		}
		
		if(FaceValueEnum.FACE_VALUE_A_VALUE == nSumbitFaceValue){
			bSumbitBiggerSucceed = false;
			return bSumbitBiggerSucceed;
		}
		
		if(nSumbitFaceValue > nMaxFaceValue){
			return bSumbitBiggerSucceed;
		}
		
		bSumbitBiggerSucceed = false;
		return bSumbitBiggerSucceed;
	}
	
	protected int getFaceValueBySpecficCardType(int nCardType, List<Integer> sumbitPorkerList, int nCurrentMajorCard){
		int nErrorResult = 0;
		Checker checker = checkerManager.getSpecficCheckerByValue(nCardType);
		if(null == checker){
			logger.error("AbstractComparer::getFaceValueBySpecficCardTyp kingChecker null Error");
			return nErrorResult;
		}
		
		int nFaceValue = checker.isBelongToSpecficType(sumbitPorkerList, nCurrentMajorCard);
		return nFaceValue;
	}
	
	protected int comparerPiecesNum(int nSumbitPorkerSize, int nLastPorkerSize){
		if(nSumbitPorkerSize > nLastPorkerSize){
			return FaceValueEnum.Greater_Result;
		}
		
		if(nSumbitPorkerSize == nLastPorkerSize){
			return FaceValueEnum.Equal_Result;
		}
		
		return FaceValueEnum.Less_Result;
	}
}
