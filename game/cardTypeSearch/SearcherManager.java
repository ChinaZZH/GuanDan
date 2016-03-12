package com.lbwan.game.cardTypeSearch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.lbwan.game.porkerComparer.HandPatternCalculator;
import com.lbwan.game.porkerEnumSet.CardTypeEnum;

@Service
public class SearcherManager {
	private Map<Integer, Searcher> allSearcherMap = new HashMap<Integer, Searcher>();

	private Logger logger = Logger.getLogger(getClass());
	
	private synchronized void init(){
		if(false == allSearcherMap.isEmpty()){
			return ;
		}
		
		allSearcherMap.put(CardTypeEnum.CARD_TYPE_ONE_PIECE_VALUE, new SinglePorkerSearcher());
		allSearcherMap.put(CardTypeEnum.CARD_TYPE_ONE_COUPLE_VALUE, new DoublePorkerSearcher());
		allSearcherMap.put(CardTypeEnum.CARD_TYPE_THREE_PIECE_VALUE, new ThreePorkerSearcher());
		
		allSearcherMap.put(CardTypeEnum.CARD_TYPE_COMMON_FLUSH_VALUE, new CommonFlushSearcher());
		allSearcherMap.put(CardTypeEnum.CARD_TYPE_THREE_BRING_TWO_VALUE, new ThreeBringTwoSearcher());
		
		allSearcherMap.put(CardTypeEnum.CARD_TYPE_FLY_OF_THERE_PAIR, new FlyPorkerSearcher());
		allSearcherMap.put(CardTypeEnum.CARD_TYPE_TWO_CONTINUE_THREE_PIECES_VALUE, new ThreeFlushPorkerSearcher());
		allSearcherMap.put(CardTypeEnum.CARD_TYPE_SMALL_COMMON_BOMB_VALUE, new SamllBombPorkerSearcher());
		allSearcherMap.put(CardTypeEnum.CARD_TYPE_SAME_COLOR_FLUSH_VALUE, new SameColorPorkerSearcher());
		allSearcherMap.put(CardTypeEnum.CARD_TYPE_BIG_COMMON_BOMB_VALUE, new BigBombPorkerSearcher());
		
		allSearcherMap.put(CardTypeEnum.CARD_TYPE_KING_OF_BOMB_VALUE, new KingPorkerSearcher());
	}
	
	private Searcher getSearcherByCardType(int nCardType){
		if(true == allSearcherMap.isEmpty()){
			this.init();
		}
		
		Searcher specficSearcher = allSearcherMap.get(nCardType);
		return specficSearcher;
	}
	
	public int searchIsBiggerPorkerThanLastMax(List<Integer> playerHandPorker, int nMaxHandPattern, int nMajorFaceValue, List<Integer> sumbitPorkerList){
		int nErrorResult = 0;
		if(null == playerHandPorker){
			logger.error("SearcherManager::searchIsBiggerPorkerThanLastMax playerHandPorker Valid Error");
			return nErrorResult;
		}
		
		if(null == sumbitPorkerList){
			logger.error("SearcherManager::searchIsBiggerPorkerThanLastMax sumbitPorkerList Valid Error");
			return nErrorResult;
		}
		
		int nCardType = HandPatternCalculator.getPorkCardTypeByLastPorker(nMaxHandPattern);
		Searcher porkerSearcher = this.getSearcherByCardType(nCardType);
		if(null == porkerSearcher){
			logger.error("SearcherManager::searchIsBiggerPorkerThanLastMax porkerSearcher Error nCardType: " + nCardType);
			return nErrorResult;
		}
		
		int nResultHandPorker = porkerSearcher.searchBiggerPorkerThanMax(playerHandPorker, nMaxHandPattern, nMajorFaceValue, sumbitPorkerList);
		return nResultHandPorker;
	}
}
