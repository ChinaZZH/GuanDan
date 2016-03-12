package com.lbwan.game.FlushSearchMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lbwan.game.cardTypeSearch.ParameterUtils;
import com.lbwan.game.cardTypeSearch.SearchHelperHolder;
import com.lbwan.game.channel.ChannelWrapper;
import com.lbwan.game.porkerComparer.HandPatternCalculator;
import com.lbwan.game.porkerEnumSet.FaceValueEnum;
import com.lbwan.game.porkerEnumSet.PorkerValueEnum;
import com.lbwan.game.proto.CommonProtocol.CommonMessage;

public abstract class AbstractFlushSearch {
	protected Logger logger = Logger.getLogger(getClass());
	 
	public int searchSameColorFlushByColorHelp(List<Integer> sumbitPorkerList, Map<Integer, SearchHelperHolder> searchHolderMap, ParameterUtils parameter/*, int nCardColor*/){
		if(1 == parameter.getFlushContinueDiffNumber()){
			int nSearchResult = 0;
			return nSearchResult;
		}
		
		int nMajorHeartValue = FaceValueEnum.getSpecficHeartByFaceValue(parameter.getMajorFaceValue());
		int nEndIndex = (FaceValueEnum.getMaxCardType() - parameter.getFlushContinueDiffNumber()) + 1;
		
		// 最小值
		for(int i = parameter.getMinValueBeginFace(); i != (nEndIndex + 1); i = FaceValueEnum.incFaceValueEnum(i)){
			int nTempMajorValueNum = parameter.getMajorHeartValueNum();
			List<Integer> tempSumbitList = new ArrayList<>();
			
			SearchHelperHolder majorFaceHolder = searchHolderMap.get(parameter.getMajorFaceValue());
			if(null != majorFaceHolder){
				majorFaceHolder.setHeartValueNum(parameter.getMajorHeartValueNum());
			}
			
			boolean bFlushResult = true;
			int nFaceValueEndIndex = FaceValueEnum.addFaceValueEnum(i, parameter.getFlushContinueDiffNumber());
			sumbitPorkerList.clear();
			
			for(int j = i; j != nFaceValueEndIndex; j = FaceValueEnum.incFaceValueEnum(j)){
				int nTempFaceValue = j;
				SearchHelperHolder sameColorFlush = searchHolderMap.get(nTempFaceValue);
				
				if(null == sameColorFlush){
					// 失败  跳出循环
					if(nTempMajorValueNum < parameter.getFlushSameFacePorkerNumber()){
						bFlushResult = false;
						break;
					}
					
					nTempMajorValueNum = nTempMajorValueNum - parameter.getFlushSameFacePorkerNumber();
					majorFaceHolder.decHeartValueNum(parameter.getFlushSameFacePorkerNumber());
					continue;
				}
				
				
				int nAddPorkerToContainer = parameter.getFlushSameFacePorkerNumber();
				int nPorkerNum = this.getPorkerTotalNum(sameColorFlush, parameter);
				
				if(nPorkerNum < parameter.getFlushSameFacePorkerNumber()){
					int nNeedMajorHeartValue = parameter.getFlushSameFacePorkerNumber() - nPorkerNum;
					// 失败  跳出循环
					if(nTempMajorValueNum < nNeedMajorHeartValue){
						bFlushResult = false;
						break;
					}
					
					nTempMajorValueNum = nTempMajorValueNum - nNeedMajorHeartValue;
					majorFaceHolder.decHeartValueNum(nNeedMajorHeartValue);
					
					nAddPorkerToContainer = nPorkerNum;
				}
				
				this.collectPorkerToSumbitContainer(sumbitPorkerList, sameColorFlush, parameter, nAddPorkerToContainer);
			}
			
			
			if(false == bFlushResult){
				continue;
			}
			
			if(false == this.isAllTheDiffColorOfPorker(sumbitPorkerList)){
				continue;
			}
			
			sumbitPorkerList.addAll(tempSumbitList);
			int nUsedMajorNum = parameter.getMajorHeartValueNum() - nTempMajorValueNum;
			for(int k = 0; k < nUsedMajorNum; ++k){
				sumbitPorkerList.add(nMajorHeartValue);
			}
			
			int nTotalPorkerNum = (parameter.getFlushContinueDiffNumber() * parameter.getFlushSameFacePorkerNumber());
			int nNewHandPattern = HandPatternCalculator.makeNewHandPattern(parameter.getCardTypeEnumValue(), nTotalPorkerNum, i);
			return nNewHandPattern;
		}
		
		int nErrorResult = 0;
		return nErrorResult;
	}
	
	protected abstract int getPorkerTotalNum(SearchHelperHolder sameColorFlush, ParameterUtils parameter);
	
	protected abstract boolean collectPorkerToSumbitContainer(List<Integer> sumbitPorkerList, SearchHelperHolder flushHolder, ParameterUtils parameter, int nAddPorkerToContainer);
	
	protected abstract boolean isAllTheDiffColorOfPorker(List<Integer> sumbitPorkerList);
}



