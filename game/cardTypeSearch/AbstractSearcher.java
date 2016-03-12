package com.lbwan.game.cardTypeSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.lbwan.game.FlushSearchMethod.DiffColorMethod;
import com.lbwan.game.FlushSearchMethod.FlushMethod;
import com.lbwan.game.FlushSearchMethod.SameColorMethod;
import com.lbwan.game.porker.PorkerMgr;
import com.lbwan.game.porkerComparer.HandPatternCalculator;
import com.lbwan.game.porkerEnumSet.CardColorEnum;
import com.lbwan.game.porkerEnumSet.CardTypeEnum;
import com.lbwan.game.porkerEnumSet.FaceValueEnum;
import com.lbwan.game.porkerEnumSet.PorkerValueEnum;
import com.lbwan.game.spring.SpringUtils;
import com.lbwan.game.utils.IntToolUtils;

public abstract class AbstractSearcher implements Searcher{
	
	protected int m_nSearcherPieceNum = 1;
	
	protected Logger logger = Logger.getLogger(getClass());
	
	
	@Autowired
	protected PorkerMgr porkerManager = (PorkerMgr) SpringUtils.getBeanByName("porkerMgr");
	
	public int searchBiggerPorkerThanMax(List<Integer> playerHandPorker, int nMaxHandPattern, int nMajorFaceValue, List<Integer> sumbitPorkerList){
		int nErrorResult = 0;
		return nErrorResult;
	}
	
	public int getSearcherPieceNum(){
		return this.m_nSearcherPieceNum;
	}
	
	protected boolean checkListIsValid(List<Integer> playerHandPorker){
		boolean bErrorResult = false;
		if(null == playerHandPorker){
			logger.error("AbstractSearcher::checkListIsValid playerHandPorker Null Error");
			return bErrorResult;
		}
		
		if(true == playerHandPorker.isEmpty()){
			logger.error("AbstractSearcher::checkListIsValid playerHandPorker.isEmpty() True Error");
			return bErrorResult;
		}
		
		bErrorResult = true;
		return bErrorResult;
	}
	
	
	protected Map<Integer, SearchHelperHolder> getSearcherMapFromHandPorker(List<Integer> playerHandPorker, IntToolUtils majorHeartPieceNum, int nMajorHeartValue){
		int nPorkerSize = playerHandPorker.size();
		Map<Integer, SearchHelperHolder> searchHelperHolderMap = new HashMap<Integer, SearchHelperHolder>();
		for(int i = 0; i < nPorkerSize; ++i){
			int nTempPorkerValue = playerHandPorker.get(i);
			Integer nFaceValue = PorkerValueEnum.getFaceValueByPorkerValueOf(nTempPorkerValue);
			
			SearchHelperHolder helperHolder = searchHelperHolderMap.get(nFaceValue);
			if(null == helperHolder){
				helperHolder = new SearchHelperHolder(nFaceValue);
				searchHelperHolderMap.put(nFaceValue, helperHolder);
			}
			
			int nCardColor = PorkerValueEnum.getCardColorByPorkerValue(nTempPorkerValue);
			helperHolder.addFaceValueByColor(nCardColor);
			
			// 统计逢人配的个数
			if(nMajorHeartValue == nTempPorkerValue){
				majorHeartPieceNum.incDataValue();
			}
		}
		
		return searchHelperHolderMap;
	}
	
	
	private int getKingBombPieceNum(){
		return 2;
	}
	
	protected int isKingBombOfHandPattern(List<Integer> sumbitPorkerList, Map<Integer, SearchHelperHolder> searchHolderMap){
		int nErrorResult = 0;
		SearchHelperHolder smallKingHolder = searchHolderMap.get(FaceValueEnum.FACE_VALUE_SMALL_JORKER);
		if(null == smallKingHolder){
			return nErrorResult;
		}
		
		if(smallKingHolder.getTotalPorkerNum() < this.getKingBombPieceNum()){
			return nErrorResult;
		}
		
		SearchHelperHolder bigKingHolder = searchHolderMap.get(FaceValueEnum.FACE_VALUE_BIG_JORKER);
		if(null == bigKingHolder){
			return nErrorResult;
		}
		
		if(bigKingHolder.getTotalPorkerNum() < this.getKingBombPieceNum()){
			return nErrorResult;
		}
		
		int nSmallKingPorkerValue = porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_SMALL_JORKER, CardColorEnum.CARD_COLOR_NULL);
		int nBigKingPorkerValue = porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_BIG_JORKER, CardColorEnum.CARD_COLOR_NULL);
		
		sumbitPorkerList.add(nSmallKingPorkerValue);
		sumbitPorkerList.add(nSmallKingPorkerValue);
		sumbitPorkerList.add(nBigKingPorkerValue);
		sumbitPorkerList.add(nBigKingPorkerValue);
		int nKingOfBomb = HandPatternCalculator.makeNewHandPattern(CardTypeEnum.CARD_TYPE_KING_OF_BOMB_VALUE, 4, 0);
		return nKingOfBomb;
	}
	
	protected int getSpecficFaceValueByPiecesNum(List<Integer> biggerFaceValue, Map<Integer, SearchHelperHolder> searchHolderMap){
		int nFaceValueListNum = biggerFaceValue.size();
		for(int i = 0; i < nFaceValueListNum; ++i){
			int nTempFaceValue = biggerFaceValue.get(i);
			
			SearchHelperHolder helperHolder = searchHolderMap.get(nTempFaceValue);
			if(null == helperHolder){
				continue;
			}
			
			
			int nTotealPorkers = helperHolder.getTotalPorkerNum();
			if(nTotealPorkers < this.getSearcherPieceNum()){
				continue;
			}
			
			return nTempFaceValue;
		}
		
		int nResultFaceValue = 0;
		return nResultFaceValue;
	}
	
	protected int calculateFlushBiggerFace(int nFaceValue){
		int nNextFaceValue = FaceValueEnum.incFaceValueEnum(nFaceValue);
		return nNextFaceValue;
	}
	
	
	protected List<Integer> calculateSmallerFaceValueForFlush(int nMajorFaceValue){
		List<Integer> smallerFaceValueList = new ArrayList<>();	
		int nMinFaceValue = FaceValueEnum.getMinCardType();
		for( ; nMinFaceValue > 0; nMinFaceValue = FaceValueEnum.getNextCardType(nMinFaceValue)){
			if(nMinFaceValue == nMajorFaceValue){
				continue;
			}
			
			smallerFaceValueList.add(nMinFaceValue);
		}
		
		
		smallerFaceValueList.add(nMajorFaceValue);
		return smallerFaceValueList;
	}
	
	// 大于
	protected List<Integer> calculateBiggerFaceValue(int nFaceValue, int nMajorFaceValue, int nPorkerNum){
		// 如果不为主牌的话
		// 如果是大王的话
		List<Integer> biggerFaceValueList = new ArrayList<>();
		if(FaceValueEnum.FACE_VALUE_BIG_JORKER == nFaceValue){
			return biggerFaceValueList;
		}

		// 如果是小王的话
		if(FaceValueEnum.FACE_VALUE_SMALL_JORKER == nFaceValue){
			biggerFaceValueList.add(FaceValueEnum.FACE_VALUE_BIG_JORKER);
			return biggerFaceValueList;
		}
		
		
		if(nFaceValue != nMajorFaceValue){
			int nNextFaceValue = nFaceValue;
			while(true){
				nNextFaceValue = FaceValueEnum.getNextCardType(nNextFaceValue);
				if(0 == nNextFaceValue){
					break;
				}
				
				if(nNextFaceValue == nMajorFaceValue){
					continue;
				}
				
				biggerFaceValueList.add(nNextFaceValue);
			}
			
			// 见主牌放入
			biggerFaceValueList.add(nMajorFaceValue);
		}
		
		if((1 == nPorkerNum) || (2 == nPorkerNum)){
			biggerFaceValueList.add(FaceValueEnum.FACE_VALUE_SMALL_JORKER);
			biggerFaceValueList.add(FaceValueEnum.FACE_VALUE_BIG_JORKER);
		}
		
		return biggerFaceValueList;
	}
	
	// 小于等于
	protected List<Integer> calculateSmallerFaceValue(int nFaceValue, int nMajorFaceValue){
		boolean bIsKingFaceValue = false;
		if((FaceValueEnum.FACE_VALUE_BIG_JORKER == nFaceValue) || (FaceValueEnum.FACE_VALUE_SMALL_JORKER == nFaceValue)){
			bIsKingFaceValue = true;
		}
			
		List<Integer> smallerFaceValueList = new ArrayList<>();		
		int nMaxFaceValue = nFaceValue;
		if((nFaceValue == nMajorFaceValue) || (true == bIsKingFaceValue)){
			nMaxFaceValue = FaceValueEnum.FACE_VALUE_A_VALUE;
		}
		
		for(int i = FaceValueEnum.FACE_VALUE_2_VALUE; i <= nMaxFaceValue; ++i){
			if(i == nMajorFaceValue){
				continue;
			}
			
			smallerFaceValueList.add(i);
		}
		
		
		if((nFaceValue == nMajorFaceValue) || (true == bIsKingFaceValue)){
			smallerFaceValueList.add(nMajorFaceValue);
		}
		
		return smallerFaceValueList;
	}
	
	protected int collectSameFacePorkerByNum(List<Integer> sumbitPorkerList, Map<Integer, SearchHelperHolder> searchHolderMap, List<Integer> faceValueList, ParameterUtils utils){
		int nMajorHeartValue = FaceValueEnum.getSpecficHeartByFaceValue(utils.getMajorFaceValue()); 
		
		// 计算不是主牌的情况下
		boolean bIsBigKing = false, bIsSamllKing = false, bIsMajorPorker = false;
		int nFaceList = faceValueList.size();
		for(int i = 0; i < nFaceList; ++i){
			int nTempFaceValue = faceValueList.get(i);
			SearchHelperHolder kingHolder = searchHolderMap.get(nTempFaceValue);
			if(null == kingHolder){
				continue;
			}
			
			if(utils.getMajorFaceValue() == nTempFaceValue){
				bIsMajorPorker = true;
				continue;
			}
			
			// 判断小王的情况下
			boolean bTempSmallKing = FaceValueEnum.isBelongToSmallKing(nTempFaceValue); // porkerManager.isBelongToSmallKing(nTempFaceValue);
			if(true == bTempSmallKing){
				bIsSamllKing = true;
				continue;
			}
			
			// 判断大王的情况下
			boolean bTempBigKing = FaceValueEnum.isBelongToBigKing(nTempFaceValue); 
			if(true == bTempBigKing){
				bIsBigKing = true;
				continue;
			}
			

			// 如果是zhup
			int nTotalNum = kingHolder.getTotalPorkerNum();
			int nInstallPieceNum = 0;
			//int nMajorHearValueNum = nMajorHeartNum;
			for(int j = utils.getNeedPorkerNum() /*nMinBombNum*/; j >= (utils.getNeedPorkerNum() - utils.getMajorHeartValueNum()); /*(nMinBombNum - nMajorHeartValueNum);*/ j--){
				if(nTotalNum >= j){
					nInstallPieceNum = j;
					break;
				}
			}
			
			// 除了配牌 其余的放入队列数组中
			if(0 == nInstallPieceNum){
				continue;
			}
			
			kingHolder.collectFaceValueByNum(sumbitPorkerList, nInstallPieceNum);
			
			// 将主牌红桃2 放入sumbit 队列数组中
			int nAddMajorHeartNum = (utils.getNeedPorkerNum() - nInstallPieceNum);
			for(int k = 0; k < nAddMajorHeartNum; ++k){
				sumbitPorkerList.add(nMajorHeartValue);
			}
			
			// 计算 handPattern
			int nNewHandPattern = HandPatternCalculator.makeNewHandPattern(utils.getCardTypeEnumValue(), utils.getNeedPorkerNum(), nTempFaceValue);
			return nNewHandPattern;
		}
		
		// 如果是主牌的情况是(则进行计算)
		List<Integer> calculateList = new ArrayList<>();
		if(true == bIsMajorPorker){
			calculateList.add(utils.getMajorFaceValue());
		}
		
		if(true == bIsSamllKing){
			calculateList.add(FaceValueEnum.FACE_VALUE_SMALL_JORKER);
		}
		
		if(true == bIsBigKing){
			calculateList.add(FaceValueEnum.FACE_VALUE_BIG_JORKER);
		}

		int nErrorResult = 0;
		for(int i = 0; i < calculateList.size(); i++){
			int nFaceValue = calculateList.get(i);
			SearchHelperHolder majorFaceHolder = searchHolderMap.get(nFaceValue);
			if(null == majorFaceHolder){
				continue;
			}
			
			int nTotalPorkers = majorFaceHolder.getTotalPorkerNum();
			if(nTotalPorkers < utils.getNeedPorkerNum()){
				continue;
			}
			
			majorFaceHolder.collectFaceValueByNum(sumbitPorkerList, utils.getNeedPorkerNum());
			int nNewHandPattern = HandPatternCalculator.makeNewHandPattern(utils.getCardTypeEnumValue(), utils.getNeedPorkerNum(), nFaceValue); 
			return nNewHandPattern;
		}
		
		
		nErrorResult = 0;
		return nErrorResult;
	}
	
	protected int searchSameColorFlush(List<Integer> sumbitPorkerList, Map<Integer, SearchHelperHolder> searchHolderMap, ParameterUtils parameter /*int nMajorFaceValue, int nMajorHeartValueNum*/){
		
		int nSearchResult = this.searchSameColorFlushByColorHelp(sumbitPorkerList, searchHolderMap, parameter, CardColorEnum.CARD_COLOR_HEART);
		if(0 != nSearchResult){
			return nSearchResult;
		}
		
		nSearchResult = this.searchSameColorFlushByColorHelp(sumbitPorkerList, searchHolderMap, parameter, CardColorEnum.CARD_COLOR_DIAMOND);
		if(0 != nSearchResult){
			return nSearchResult;
		}
		
		nSearchResult = this.searchSameColorFlushByColorHelp(sumbitPorkerList, searchHolderMap, parameter, CardColorEnum.CARD_COLOR_SPADE);
		if(0 != nSearchResult){
			return nSearchResult;
		}
		
		nSearchResult = this.searchSameColorFlushByColorHelp(sumbitPorkerList, searchHolderMap, parameter, CardColorEnum.CARD_COLOR_CLUB);
		if(0 != nSearchResult){
			return nSearchResult;
		}
		
		int nErrorResult = 0;
		return nErrorResult;
	}
	
	
	protected int searchSameColorFlushByColorHelp(List<Integer> sumbitPorkerList, Map<Integer, SearchHelperHolder> searchHolderMap, ParameterUtils parameter, int nCardColor){
		parameter.setCardColor(nCardColor);
		
		SameColorMethod sameColorFlush = new  SameColorMethod();
		int nFlushPattern = sameColorFlush.searchSameColorFlushByColorHelp(sumbitPorkerList, searchHolderMap, parameter);
		return nFlushPattern;
	}
	
	protected int searchMinFlush(List<Integer> sumbitPorkerList, Map<Integer, SearchHelperHolder> searchHolderMap,  ParameterUtils parameter){
		parameter.setCardColor(CardColorEnum.CARD_COLOR_ERROR);
		
		FlushMethod flushMethod = new FlushMethod();
		int nFlushPattern = flushMethod.searchSameColorFlushByColorHelp(sumbitPorkerList, searchHolderMap, parameter);
		return nFlushPattern;
	}
	
	
	protected int searchMinFlushExceptSameColor(List<Integer> sumbitPorkerList, Map<Integer, SearchHelperHolder> searchHolderMap,  ParameterUtils parameter){
		parameter.setCardColor(CardColorEnum.CARD_COLOR_ERROR);
		
		DiffColorMethod diffColorMethod = new DiffColorMethod();
		int nFlushPattern = diffColorMethod.searchSameColorFlushByColorHelp(sumbitPorkerList, searchHolderMap, parameter);
		return nFlushPattern;
	}
}

	


