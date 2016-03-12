package com.lbwan.game.cardTypeChecker;


import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.lbwan.game.porkerEnumSet.FaceValueEnum;


public class ThreeBringTwoChecker extends BaseCardTypeChecker{
	public ThreeBringTwoChecker(){
		super();
	}
	
	private int getSpecficCheckerMaxPorkerNum(){
		return 3;
	}
	
	public int isBelongToSpecficType(List<Integer> sumbitPorkerList, int nCurrentMajorCard){
		int nErrorResult = 0;
		boolean bCheckResult = this.checkIsSatisfyPorkerNum(sumbitPorkerList, 5);
		if(false == bCheckResult){
			return nErrorResult;
		}
		
		
		Integer nMajorPorkerNum = this.getMajorHeartNumber(sumbitPorkerList, nCurrentMajorCard);
		if(nMajorPorkerNum > 2){
			return nErrorResult; 
		}
		
		Map<Integer, Integer> porkTypeAndNumMap = this.extractPorkerMap(sumbitPorkerList, nCurrentMajorCard);
		if(null == porkTypeAndNumMap){
			return nErrorResult; 
		}
		
		
		
		bCheckResult = false;
		if(2 == porkTypeAndNumMap.size()){
			bCheckResult = true;
		}else if((1 == porkTypeAndNumMap.size()) && (2 == nMajorPorkerNum)){
			bCheckResult = true;
		}else{
			bCheckResult = false;
		}
		
		if(false == bCheckResult){
			return nErrorResult;
		}
		 
		int nMaxCardType = 0;
		Iterator<Map.Entry<Integer, Integer>> iter = porkTypeAndNumMap.entrySet().iterator();
		while(true == iter.hasNext()){
			Map.Entry<Integer, Integer> entry = iter.next();
			Integer nCardType = entry.getKey();
			
			// 大小王  过滤
			if(true == FaceValueEnum.isBelongToKing(nCardType)){
				continue;
			}
			
			if(0 == nCardType){
				return nErrorResult;
			}
			
			// 如果其值加上
			int nPieceNumOfFaceValue = entry.getValue(); 
			if(nPieceNumOfFaceValue + nMajorPorkerNum < this.getSpecficCheckerMaxPorkerNum()){
				continue;
			}
		
			
			if(nCardType > nMaxCardType){
				nMaxCardType = nCardType;
			}
		}
		
		return nMaxCardType;
	}
}


