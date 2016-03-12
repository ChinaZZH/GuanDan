package com.lbwan.game.cardTypeSearch;

import java.util.List;
import java.util.Map;

import com.lbwan.game.porkerComparer.HandPatternCalculator;
import com.lbwan.game.porkerEnumSet.CardTypeEnum;
import com.lbwan.game.porkerEnumSet.FaceValueEnum;
import com.lbwan.game.utils.IntToolUtils;

public class FlyPorkerSearcher extends AbstractSearcher{
	public FlyPorkerSearcher(){
		m_nSearcherPieceNum = 6;
	}
	
	public int getSearcherPieceNum(){
		return this.m_nSearcherPieceNum;
	}
	
	
	public int searchBiggerPorkerThanMax(List<Integer> playerHandPorker, int nMaxHandPattern, int nMajorFaceValue, List<Integer> sumbitPorkerList){
		int nErrorResult = 0;
		boolean bCheckResult = this.checkListIsValid(playerHandPorker);
		if(false == bCheckResult){
			logger.error("ThreeFlushPorkerSearcher::searchBiggerPorkerThanMax playerHandPorker isnot Valid Error");
			return nErrorResult;
		}
		
		sumbitPorkerList.clear();
		int nFaceValue = HandPatternCalculator.getFaceValueByLastPorker(nMaxHandPattern);
		IntToolUtils majorHeartPieceNum = new IntToolUtils();
		int nMajorHeartValue = FaceValueEnum.getSpecficHeartByFaceValue(nMajorFaceValue);
		Map<Integer, SearchHelperHolder> searchHolderMap = this.getSearcherMapFromHandPorker(playerHandPorker, majorHeartPieceNum, nMajorHeartValue);
		
		// 顺子 比当前大的跟
		int nBiggerFlushIndex = this.calculateFlushBiggerFace(nFaceValue);
		
		// 查找两根的情况下
		ParameterUtils parameter = new ParameterUtils();
		parameter.setCardTypeEnumValue(CardTypeEnum.CARD_TYPE_FLY_OF_THERE_PAIR);
		parameter.setMajorFaceValue(nMajorFaceValue);
		parameter.setMajorHeartValueNum(majorHeartPieceNum.getToolValueByInt());
		parameter.setFlushContinueDiffNumber(3);
		parameter.setFlushSameFacePorkerNumber(2);
		parameter.setMinValueBeginFace(nBiggerFlushIndex);
		int nResultHandPattern = this.searchMinFlush(sumbitPorkerList, searchHolderMap, parameter);
		if(0 != nResultHandPattern){
			return nResultHandPattern;
		}
		sumbitPorkerList.clear();
		
		
		List<Integer> smallerFaceValueList = this.calculateSmallerFaceValueForFlush(nMajorFaceValue);
		if(null == smallerFaceValueList){
			logger.error("ThreeFlushPorkerSearcher::searchBiggerPorkerThanMax smallerFaceValueList null Error");
			return nErrorResult;
		}
		
		parameter.setNeedPorkerNum(4);
		parameter.setCardTypeEnumValue(CardTypeEnum.CARD_TYPE_SMALL_COMMON_BOMB_VALUE);
		parameter.setMajorFaceValue(nMajorFaceValue);
		parameter.setMajorHeartValueNum(majorHeartPieceNum.getToolValueByInt());
		nResultHandPattern = this.collectSameFacePorkerByNum(sumbitPorkerList, searchHolderMap, smallerFaceValueList, parameter);
		if(0 != nResultHandPattern){
			return nResultHandPattern;
		}
		sumbitPorkerList.clear();
		
		// 查找同花顺
		parameter.setCardTypeEnumValue(CardTypeEnum.CARD_TYPE_SAME_COLOR_FLUSH_VALUE);
		parameter.setMajorFaceValue(nMajorFaceValue);
		parameter.setMajorHeartValueNum(majorHeartPieceNum.getToolValueByInt());
		parameter.setFlushContinueDiffNumber(5);
		parameter.setFlushSameFacePorkerNumber(1);
		parameter.setMinValueBeginFace(FaceValueEnum.FACE_VALUE_A_VALUE);
		int nNewHandResult = this.searchSameColorFlush(sumbitPorkerList, searchHolderMap, parameter);
		if(0 != nNewHandResult){
			return nNewHandResult;
		}
		sumbitPorkerList.clear();		
		
		// 查找是否有王炸
		int nKingHandResult = this.isKingBombOfHandPattern(sumbitPorkerList, searchHolderMap);
		if(0 != nKingHandResult){
			return nKingHandResult;
		}
		
		sumbitPorkerList.clear();
		return nErrorResult;
	}
}
