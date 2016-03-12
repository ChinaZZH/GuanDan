package com.lbwan.game.cardTypeSearch;

import java.util.List;
import java.util.Map;

import com.lbwan.game.porkerComparer.HandPatternCalculator;
import com.lbwan.game.porkerEnumSet.CardTypeEnum;
import com.lbwan.game.porkerEnumSet.FaceValueEnum;
import com.lbwan.game.utils.IntToolUtils;

public class SameColorPorkerSearcher extends AbstractSearcher{
	public SameColorPorkerSearcher(){
		m_nSearcherPieceNum = 5;
	}
	
	public int getSearcherPieceNum(){
		return this.m_nSearcherPieceNum;
	}
	
	public int searchBiggerPorkerThanMax(List<Integer> playerHandPorker, int nMaxHandPattern, int nMajorFaceValue, List<Integer> sumbitPorkerList){
		int nErrorResult = 0;
		boolean bCheckResult = this.checkListIsValid(playerHandPorker);
		if(false == bCheckResult){
			logger.error("SameColorPorkerSearcher::searchBiggerPorkerThanMax playerHandPorker isnot Valid Error");
			return nErrorResult;
		}
		
		sumbitPorkerList.clear();
		int nFaceValue = HandPatternCalculator.getFaceValueByLastPorker(nMaxHandPattern);
		IntToolUtils majorHeartPieceNum = new IntToolUtils();
		int nMajorHeartValue = FaceValueEnum.getSpecficHeartByFaceValue(nMajorFaceValue);
		Map<Integer, SearchHelperHolder> searchHolderMap = this.getSearcherMapFromHandPorker(playerHandPorker, majorHeartPieceNum, nMajorHeartValue);
		
		// 顺子 比当前大的跟
		int nBiggerFlushIndex = this.calculateFlushBiggerFace(nFaceValue);
		
		// 查找同花顺
		ParameterUtils parameter = new ParameterUtils();
		parameter.setCardTypeEnumValue(CardTypeEnum.CARD_TYPE_SAME_COLOR_FLUSH_VALUE);
		parameter.setMajorFaceValue(nMajorFaceValue);
		parameter.setMajorHeartValueNum(majorHeartPieceNum.getToolValueByInt());
		parameter.setFlushContinueDiffNumber(5);
		parameter.setFlushSameFacePorkerNumber(1);
		parameter.setMinValueBeginFace(nBiggerFlushIndex);
		int nResultHandPattern = this.searchSameColorFlush(sumbitPorkerList, searchHolderMap, parameter);
		if(0 != nResultHandPattern){
			return nResultHandPattern;
		}
		sumbitPorkerList.clear();		
		
		// 6跟炸
		List<Integer> smallerFaceValueList = this.calculateSmallerFaceValueForFlush(nMajorFaceValue);
		if(null == smallerFaceValueList){
			logger.error("SameColorPorkerSearcher::searchBiggerPorkerThanMax smallerFaceValueList null Error");
			return nErrorResult;
		}
		
		parameter.setNeedPorkerNum(6);
		parameter.setCardTypeEnumValue(CardTypeEnum.CARD_TYPE_BIG_COMMON_BOMB_VALUE);
		parameter.setMajorFaceValue(nMajorFaceValue);
		parameter.setMajorHeartValueNum(majorHeartPieceNum.getToolValueByInt());
		nResultHandPattern = this.collectSameFacePorkerByNum(sumbitPorkerList, searchHolderMap, smallerFaceValueList, parameter);
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
}
