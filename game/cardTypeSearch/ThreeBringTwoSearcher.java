package com.lbwan.game.cardTypeSearch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lbwan.game.porkerComparer.HandPatternCalculator;
import com.lbwan.game.porkerEnumSet.CardTypeEnum;
import com.lbwan.game.porkerEnumSet.FaceValueEnum;
import com.lbwan.game.porkerEnumSet.PorkerValueEnum;
import com.lbwan.game.utils.IntToolUtils;

public class ThreeBringTwoSearcher extends AbstractSearcher{
	public ThreeBringTwoSearcher(){
		m_nSearcherPieceNum = 5;
	}
	
	public int getSearcherPieceNum(){
		return this.m_nSearcherPieceNum;
	}
	
	public int searchBiggerPorkerThanMax(List<Integer> playerHandPorker, int nMaxHandPattern, int nMajorFaceValue, List<Integer> sumbitPorkerList){
		int nErrorResult = 0;
		boolean bCheckResult = this.checkListIsValid(playerHandPorker);
		if(false == bCheckResult){
			logger.error("ThreeBringTwoSearcher::searchBiggerPorkerThanMax playerHandPorker isnot Valid Error");
			return nErrorResult;
		}
		
		sumbitPorkerList.clear();
		int nFaceValue = HandPatternCalculator.getFaceValueByLastPorker(nMaxHandPattern);
		IntToolUtils majorHeartPieceNum = new IntToolUtils();
		int nMajorHeartValue = FaceValueEnum.getSpecficHeartByFaceValue(nMajorFaceValue);
		Map<Integer, SearchHelperHolder> searchHolderMap = this.getSearcherMapFromHandPorker(playerHandPorker, majorHeartPieceNum, nMajorHeartValue);
		
		// 找3带2
		List<Integer> biggerFaceValue = this.calculateBiggerFaceValue(nFaceValue, nMajorFaceValue, this.getSearcherPieceNum()); 
		if(null == biggerFaceValue){
			logger.error("ThreeBringTwoSearcher::searchBiggerPorkerThanMax biggerFaceValue null Error");
			return nErrorResult;
		}
		
		ParameterUtils parameter = new ParameterUtils();
		parameter.setNeedPorkerNum(3);
		parameter.setCardTypeEnumValue(CardTypeEnum.CARD_TYPE_THREE_BRING_TWO_VALUE);
		parameter.setMajorFaceValue(nMajorFaceValue);
		parameter.setMajorHeartValueNum(majorHeartPieceNum.getToolValueByInt());
		parameter.setCurrentFaceValue(nFaceValue);
		int nResultHandPattern = this.collectSameFacePorkerByNum(sumbitPorkerList, searchHolderMap, biggerFaceValue, parameter);
		if(0 != nResultHandPattern){
			// 再找两根 同时把
			boolean bSatisfyResult = this.isSatisfyTwoThreeBringTwoCardType(sumbitPorkerList, searchHolderMap, parameter);
			if(true == bSatisfyResult){
				return nResultHandPattern;
			}
		}
		
		sumbitPorkerList.clear();
		
		// 4跟炸
		List<Integer> smallerFaceValueList = this.calculateSmallerFaceValueForFlush(nMajorFaceValue);
		if(null == smallerFaceValueList){
			logger.error("ThreeBringTwoSearcher::searchBiggerPorkerThanMax smallerFaceValueList null Error");
			return nErrorResult;
		}
		
		sumbitPorkerList.clear();
		parameter.setNeedPorkerNum(4);
		parameter.setCardTypeEnumValue(CardTypeEnum.CARD_TYPE_SMALL_COMMON_BOMB_VALUE);
		parameter.setMajorFaceValue(nMajorFaceValue);
		parameter.setMajorHeartValueNum(majorHeartPieceNum.getToolValueByInt());
		nResultHandPattern = this.collectSameFacePorkerByNum(sumbitPorkerList, searchHolderMap, smallerFaceValueList, parameter);
		if(0 != nResultHandPattern){
			return nResultHandPattern;
		}
		
		sumbitPorkerList.clear();
		
		// 同花顺
		parameter.setCardTypeEnumValue(CardTypeEnum.CARD_TYPE_SAME_COLOR_FLUSH_VALUE);
		parameter.setMajorFaceValue(nMajorFaceValue);
		parameter.setMajorHeartValueNum(majorHeartPieceNum.getToolValueByInt());
		parameter.setFlushContinueDiffNumber(5);
		parameter.setFlushSameFacePorkerNumber(1);
		parameter.setMinValueBeginFace(FaceValueEnum.FACE_VALUE_A_VALUE);
		nResultHandPattern = this.searchSameColorFlush(sumbitPorkerList, searchHolderMap, parameter);
		if(0 != nResultHandPattern){
			return nResultHandPattern;
		}
		sumbitPorkerList.clear();
		
		// 王炸
		int nKingHandResult = this.isKingBombOfHandPattern(sumbitPorkerList, searchHolderMap);
		if(0 != nKingHandResult){
			return nKingHandResult;
		}
		
		sumbitPorkerList.clear();
		return nErrorResult;
	}
	
	
	private boolean isSatisfyTwoThreeBringTwoCardType(List<Integer> sumbitPorkerList, Map<Integer, SearchHelperHolder> searchHolderMap, ParameterUtils utils){
		boolean bSatisfyResult = false;
		if(null == sumbitPorkerList){
			logger.error("ThreeBringTwoSearcher::isSatisfyTwoThreeBringTwoCardType smallerFaceValueList null Error");
			return bSatisfyResult;
		}
		
		if(null == searchHolderMap){
			logger.error("ThreeBringTwoSearcher::isSatisfyTwoThreeBringTwoCardType searchHolderMap null Error");
			return bSatisfyResult;
		}
		
		if(null == utils){
			logger.error("ThreeBringTwoSearcher::isSatisfyTwoThreeBringTwoCardType utils null Error");
			return bSatisfyResult;
		}
		
		int nMajorHeartValue = FaceValueEnum.getSpecficHeartByFaceValue(utils.getMajorFaceValue());
		int nMajorHeartValueNum = utils.getMajorHeartValueNum();
		Map<Integer, SearchHelperHolder> holderHelpMgr = new HashMap<Integer, SearchHelperHolder>(searchHolderMap);
		
		int nSumbitListNum = sumbitPorkerList.size();
		for(int i = 0; i < nSumbitListNum; ++i){
			int nTempPorkerValue = sumbitPorkerList.get(i);
			if(nMajorHeartValue == nTempPorkerValue){
				nMajorHeartValueNum = nMajorHeartValueNum - 1;
			}

			int nTempFaceValue = PorkerValueEnum.getFaceValueByPorkerValueOf(nTempPorkerValue);
			int nTempColor = PorkerValueEnum.getCardColorByPorkerValue(nTempPorkerValue);
			SearchHelperHolder searcherHolder = holderHelpMgr.get(nTempFaceValue);
			if(null == searcherHolder){
				logger.error("ThreeBringTwoSearcher::isSatisfyTwoThreeBringTwoCardType searcherHolder null Error");
				continue;
			}
			
			searcherHolder.decFaceValueByColor(nTempColor);
		}
		
		List<Integer> coupleValueList = this.calculateSmallerFaceValueForFlush(nMajorHeartValue);
		coupleValueList.add(FaceValueEnum.FACE_VALUE_SMALL_JORKER);
		coupleValueList.add(FaceValueEnum.FACE_VALUE_BIG_JORKER);
		
		ParameterUtils parameter = new ParameterUtils();
		parameter.setNeedPorkerNum(2);
		parameter.setCardTypeEnumValue(CardTypeEnum.CARD_TYPE_ONE_COUPLE_VALUE);
		parameter.setMajorFaceValue(nMajorHeartValue);
		parameter.setMajorHeartValueNum(nMajorHeartValueNum);
		
		int nCollectResult = this.collectSameFacePorkerByNum(sumbitPorkerList, holderHelpMgr, coupleValueList, parameter);
		if(0 == nCollectResult){
			return bSatisfyResult;
		}
		
		bSatisfyResult = true;
		return bSatisfyResult;
	}
}








