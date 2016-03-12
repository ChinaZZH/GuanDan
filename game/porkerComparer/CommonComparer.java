package com.lbwan.game.porkerComparer;

import java.util.List;

import com.lbwan.game.cardTypeChecker.Checker;


// 普通比较
public class CommonComparer extends AbstractComparer{
	public int biggerThanLastMaxPorker(List<Integer> sumbitPorkerList, int nLastMaxHandPorker, int nCurrentMajorCard){
		// 从炸开始
		int nSumbitCardNum = sumbitPorkerList.size();
		int nIsBombHandPattern = this.isBombForSumbitPorker(sumbitPorkerList, nCurrentMajorCard, nSumbitCardNum);
		if(0 != nIsBombHandPattern){
			return nIsBombHandPattern;
		}
		
		// 不行的话再找同类
		int nErrorResult = 0;
		int nLastCardType = HandPatternCalculator.getPorkCardTypeByLastPorker(nLastMaxHandPorker);
		Checker speficChecker = checkerManager.getSpecficCheckerByValue(nLastCardType);
		if(null == speficChecker){
			return nErrorResult;
		}
		
		// 是否符合这个牌型
		int nSumbitFaceValue = speficChecker.isBelongToSpecficType(sumbitPorkerList, nCurrentMajorCard);
		if(0 == nSumbitFaceValue){
			return nErrorResult;
		}
		
		// 找到并且比较
		int nLastMaxFaceValue = HandPatternCalculator.getFaceValueByLastPorker(nLastMaxHandPorker);
		boolean bBgiggerResult = this.sumbitPorkerBiggerBySinglePorker(nSumbitFaceValue, nLastMaxFaceValue, nCurrentMajorCard);
		if(false == bBgiggerResult){
			return nErrorResult;
		}
		
		int nNewHandPattern = HandPatternCalculator.makeNewHandPattern(nLastCardType, nSumbitCardNum, nSumbitFaceValue);
		return nNewHandPattern;
	}
}
