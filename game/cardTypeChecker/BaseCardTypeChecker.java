/*
 * 
 *                   牌型判断器--基类 
 *                   郑祖煌  
 *                   2015.04.11
 * */

package com.lbwan.game.cardTypeChecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.lbwan.game.porker.PorkerMgr;
import com.lbwan.game.porkerEnumSet.FaceValueEnum;
import com.lbwan.game.spring.SpringUtils;



// 牌类型判断
public abstract class BaseCardTypeChecker implements Checker{
	protected Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	protected PorkerMgr porkerManager = (PorkerMgr) SpringUtils.getBeanByName("porkerMgr");
	
	// 返回0为错误
	public int isBelongToSpecficType(List<Integer> sumbitPorkerList, int nCurrentMajorCard){
		return 0;
	}
	
	
	protected boolean checkIsSatisfyPorkerNum(List<Integer> sumbitPorkerList, int nSatifyNum){
		if(null == sumbitPorkerList){
			logger.error("BaseCardTypeChecker::checkIsSatisfyPorkerNum sumbitPorkerList Null error");
			return false;
		}
		
		if(nSatifyNum != sumbitPorkerList.size()){
			return false;
		}
		
		return true;
	}
	
	protected boolean checkIsSatisfyRangePorkerNum(List<Integer> sumbitPorkerList, int nMinNum, int nMaxNum){
		if(null == sumbitPorkerList){
			logger.error("BaseCardTypeChecker::checkIsSatisfyRangePorkerNum sumbitPorkerList Null error");
			return false;
		}
		
		if((sumbitPorkerList.size() < nMinNum) || (sumbitPorkerList.size() > nMaxNum)){
			return false;
		}
		
		return true;
	}

	
	protected int isSameFaceValueOfPorker(List<Integer> sumbitPorkerList, int nCurrentMajorCard){
		// 然后进行比较
		// 判断有一张是否是主牌的红桃
		int nErrorFaceValue = 0;
		int nMajorHeartFaceValue = FaceValueEnum.getSpecficHeartByFaceValue(nCurrentMajorCard);
		List<Integer> tempFaceValueList = new ArrayList<>();
		int nListSize = sumbitPorkerList.size();
		for(int nIndex = 0 ; nIndex < nListSize; ++nIndex){
			int nTempPorkerValue = sumbitPorkerList.get(nIndex);
			if(true == porkerManager.isBelongToBigKing(nTempPorkerValue)){
				return nErrorFaceValue;
			}
			
			if(true == porkerManager.isBelongToSmallKing(nTempPorkerValue)){
				return nErrorFaceValue;
			}
			
			if(nMajorHeartFaceValue != nTempPorkerValue){
				int nTempFaceValue = porkerManager.getFaceValue(nTempPorkerValue);
				tempFaceValueList.add(nTempFaceValue);
			}
		}
		
		if(true == tempFaceValueList.isEmpty()){
			return nCurrentMajorCard;
		}
		
		int nFirstFaveValue = tempFaceValueList.get(0);
		int nTempListSize = tempFaceValueList.size();
		for(int i = 0; i < nTempListSize; ++i){
			int nTempFaceValue = tempFaceValueList.get(i);
			if(nFirstFaveValue != nTempFaceValue){
				return nErrorFaceValue;
			}
		}
		
		return nFirstFaveValue;
	}
	
	
	protected Map<Integer, Integer> extractPorkerMap(List<Integer> sumbitPorkerList, int nCurrentMajorCard){
		if(null == sumbitPorkerList){
			logger.error("PorkerCompareLogic::extractPorkerMap sumbitPorkerList null Error");
			return null;
		}
		
		Map<Integer, Integer> newExtractPorkerMap = new HashMap<Integer, Integer>();
		int nMajorHeartPorker = FaceValueEnum.getSpecficHeartByFaceValue(nCurrentMajorCard);
		int nListMemberNum = sumbitPorkerList.size();
		for(int i = 0; i < nListMemberNum; ++i){
			int nTempPorkerValue = sumbitPorkerList.get(i);
			if(nTempPorkerValue == nMajorHeartPorker){
				continue;
			}
			
			int nTempCardType = porkerManager.getFaceValue(nTempPorkerValue);
			Integer nPorkerNum = newExtractPorkerMap.get(nTempCardType);
			if(null == nPorkerNum){
				nPorkerNum = 1;
			}else{
				nPorkerNum = nPorkerNum + 1;
			}
			
			newExtractPorkerMap.put(nTempCardType, nPorkerNum);
		}
		
		return newExtractPorkerMap;
	}
	
	protected int getMajorHeartNumber(List<Integer> sumbitPorkerList, int nCurrentMajorCard){
		int nMajorHeartNumber = 0;
		if(null == sumbitPorkerList){
			logger.error("PorkerCompareLogic::getMajorHeartNumber sumbitPorkerList null Error");
			return nMajorHeartNumber;
		}
		
		int nMajorHeartPorker = FaceValueEnum.getSpecficHeartByFaceValue(nCurrentMajorCard);
		int nListMemberNum = sumbitPorkerList.size();
		for(int i = 0; i < nListMemberNum; ++i){
			int nTempPorkerValue = sumbitPorkerList.get(i);
			if(nTempPorkerValue == nMajorHeartPorker){
				nMajorHeartNumber = nMajorHeartNumber + 1;
				continue;
			}
		}
		
		return nMajorHeartNumber;
	}
}
