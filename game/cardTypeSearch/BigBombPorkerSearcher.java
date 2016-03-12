package com.lbwan.game.cardTypeSearch;

import java.util.List;
import java.util.Map;

import com.lbwan.game.porkerComparer.HandPatternCalculator;
import com.lbwan.game.porkerEnumSet.CardTypeEnum;
import com.lbwan.game.porkerEnumSet.FaceValueEnum;
import com.lbwan.game.utils.IntToolUtils;

public class BigBombPorkerSearcher extends AbstractSearcher{
	public BigBombPorkerSearcher(){
		m_nSearcherPieceNum = 6;
	}
	
	public int getSearcherPieceNum(){
		return this.m_nSearcherPieceNum;
	}
	
	private void setSearcherPieceNum(int nPieceNum){
		this.m_nSearcherPieceNum = nPieceNum;
	}
	
	public int searchBiggerPorkerThanMax(List<Integer> playerHandPorker, int nMaxHandPattern, int nMajorFaceValue, List<Integer> sumbitPorkerList){
		int nErrorResult = 0;
		boolean bCheckResult = this.checkListIsValid(playerHandPorker);
		if(false == bCheckResult){
			logger.error("BigBombPorkerSearcher::searchBiggerPorkerThanMax playerHandPorker isnot Valid Error");
			return nErrorResult;
		}
		
		sumbitPorkerList.clear();
		int nFaceValue = HandPatternCalculator.getFaceValueByLastPorker(nMaxHandPattern);
		IntToolUtils majorHeartPieceNum = new IntToolUtils();
		int nMajorHeartValue = FaceValueEnum.getSpecficHeartByFaceValue(nMajorFaceValue);
		Map<Integer, SearchHelperHolder> searchHolderMap = this.getSearcherMapFromHandPorker(playerHandPorker, majorHeartPieceNum, nMajorHeartValue);
		
		List<Integer> biggerFaceValue = this.calculateBiggerFaceValue(nFaceValue, nMajorFaceValue, this.getSearcherPieceNum()); 
		if(null == biggerFaceValue){
			logger.error("BigBombPorkerSearcher::searchBiggerPorkerThanMax biggerFaceValue null Error");
			return nErrorResult;
		}
		
		int nPieceNum = HandPatternCalculator.getPiecesNumByLastPorker(nMaxHandPattern);
		this.setSearcherPieceNum(nPieceNum);
		
		ParameterUtils parameter = new ParameterUtils();
		parameter.setNeedPorkerNum(this.getSearcherPieceNum());
		parameter.setCardTypeEnumValue(CardTypeEnum.CARD_TYPE_BIG_COMMON_BOMB_VALUE);
		parameter.setMajorFaceValue(nMajorFaceValue);
		parameter.setMajorHeartValueNum(majorHeartPieceNum.getToolValueByInt());
		parameter.setCurrentFaceValue(nFaceValue);
		
		int nResultHandPattern = this.collectSameFacePorkerByNum(sumbitPorkerList, searchHolderMap, biggerFaceValue, parameter);
		if(0 != nResultHandPattern){
			return nResultHandPattern;
		}
		sumbitPorkerList.clear();
		
		
		// 根数更多情况下的炸牌
		if(this.getSearcherPieceNum() < 10){
			// 根数更多的情况下的炸
			List<Integer> smallerFaceValueList = this.calculateSmallerFaceValue(nFaceValue, nMajorFaceValue);
			if(null == smallerFaceValueList){
				logger.error("BigBombPorkerSearcher::searchBiggerPorkerThanMax smallerFaceValueList null Error");
				return nErrorResult;
			}
			
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
		return 0;
	}
}
