package com.lbwan.game.cardTypeSearch;

import java.util.List;
import java.util.Map;

import com.lbwan.game.porkerComparer.HandPatternCalculator;
import com.lbwan.game.porkerEnumSet.CardTypeEnum;
import com.lbwan.game.porkerEnumSet.FaceValueEnum;
import com.lbwan.game.utils.IntToolUtils;

public class SamllBombPorkerSearcher extends AbstractSearcher{
	public SamllBombPorkerSearcher(){
		m_nSearcherPieceNum = 4;
	}
	
	public int getSearcherPieceNum(){
		return this.m_nSearcherPieceNum;
	}
	
	public void setSearcherPieceNum(int nPieceNum){
		this.m_nSearcherPieceNum = nPieceNum;
	}
	
	public int searchBiggerPorkerThanMax(List<Integer> playerHandPorker, int nMaxHandPattern, int nMajorFaceValue, List<Integer> sumbitPorkerList){
		int nErrorResult = 0;
		boolean bCheckResult = this.checkListIsValid(playerHandPorker);
		if(false == bCheckResult){
			logger.error("SamllBombPorkerSearcher::searchBiggerPorkerThanMax playerHandPorker isnot Valid Error");
			return nErrorResult;
		}
		
		sumbitPorkerList.clear();
		// 单根查找
		int nFaceValue = HandPatternCalculator.getFaceValueByLastPorker(nMaxHandPattern);
		IntToolUtils majorHeartPieceNum = new IntToolUtils();
		int nMajorHeartValue = FaceValueEnum.getSpecficHeartByFaceValue(nMajorFaceValue);
		Map<Integer, SearchHelperHolder> searchHolderMap = this.getSearcherMapFromHandPorker(playerHandPorker, majorHeartPieceNum, nMajorHeartValue);
		
		List<Integer> biggerFaceValue = this.calculateBiggerFaceValue(nFaceValue, nMajorFaceValue, this.getSearcherPieceNum()); 
		if(null == biggerFaceValue){
			logger.error("SamllBombPorkerSearcher::searchBiggerPorkerThanMax biggerFaceValue null Error");
			return nErrorResult;
		}
		
		int nPieceNum = HandPatternCalculator.getPiecesNumByLastPorker(nMaxHandPattern);
		this.setSearcherPieceNum(nPieceNum);
		
		ParameterUtils parameter = new ParameterUtils();
		parameter.setNeedPorkerNum(this.getSearcherPieceNum());
		parameter.setCardTypeEnumValue(CardTypeEnum.CARD_TYPE_SMALL_COMMON_BOMB_VALUE);
		parameter.setMajorFaceValue(nMajorFaceValue);
		parameter.setMajorHeartValueNum(majorHeartPieceNum.getToolValueByInt());
		parameter.setCurrentFaceValue(nFaceValue);
		
		int nResultHandPattern = this.collectSameFacePorkerByNum(sumbitPorkerList, searchHolderMap, biggerFaceValue, parameter);
		if(0 != nResultHandPattern){
			return nResultHandPattern;
		}
		sumbitPorkerList.clear();
		
		// 根数更多情况下的炸牌
		List<Integer> smallerFaceValueList = this.calculateSmallerFaceValue(nFaceValue, nMajorFaceValue);
		if(null == smallerFaceValueList){
			logger.error("SamllBombPorkerSearcher::searchBiggerPorkerThanMax smallerFaceValueList null Error");
			return nErrorResult;
		}
		
		if(4 == this.getSearcherPieceNum()){
			// 根数更多的情况下的炸	
			parameter.setNeedPorkerNum(this.getSearcherPieceNum() + 1);
			nResultHandPattern = this.collectSameFacePorkerByNum(sumbitPorkerList, searchHolderMap, smallerFaceValueList, parameter);
			if(0 != nResultHandPattern){
				return nResultHandPattern;
			}
			
			sumbitPorkerList.clear();
		}
		
		
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
		
		// 根数更多的情况下的炸
		if(5 == this.getSearcherPieceNum()){
			
			parameter.setCardTypeEnumValue(CardTypeEnum.CARD_TYPE_BIG_COMMON_BOMB_VALUE);
			parameter.setMajorFaceValue(nMajorFaceValue);
			parameter.setMajorHeartValueNum(majorHeartPieceNum.getToolValueByInt());
			parameter.setCurrentFaceValue(nFaceValue);
			parameter.setNeedPorkerNum(this.getSearcherPieceNum() + 1);
			
			nResultHandPattern = this.collectSameFacePorkerByNum(sumbitPorkerList, searchHolderMap, smallerFaceValueList, parameter);
			if(0 != nResultHandPattern){
				return nResultHandPattern;
			}
			
			sumbitPorkerList.clear();
		}
		
		
		// 查找是否有王炸
		int nKingHandResult = this.isKingBombOfHandPattern(sumbitPorkerList, searchHolderMap);
		if(0 != nKingHandResult){
			return nKingHandResult;
		}
		
		sumbitPorkerList.clear();
		return nErrorResult;
	}
}
