package com.lbwan.game.porkerComparer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lbwan.game.cardTypeChecker.Checker;
import com.lbwan.game.cardTypeSearch.CardTypeCheckerMgr;
import com.lbwan.game.porkerEnumSet.CardTypeEnum;
import com.lbwan.game.spring.SpringUtils;


@Service
public class ComparerManager {
	private Map<Integer, Comparer> allComparer = new HashMap<Integer, Comparer>();
	
	// first为张数   second为张数对应的校验 
	private Map<Integer, List<Integer>> allPieceAndCarTypeMap = new HashMap<Integer, List<Integer>>();
	
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	protected CardTypeCheckerMgr checkerManager;
	
    private List<Integer> morePorkerBomb = new ArrayList<>();
	
	private List<Integer> lessPorkerBomb = new ArrayList<>();
	
	@PostConstruct
	public void initExceptionComparer(){
		this.initPieceAndTypeMap();
		
		this.initBombList();
	}
	
	private synchronized void initComparer(){
		if(false == allComparer.isEmpty()){
			return ;
		}
		
		// 普通比较器
		allComparer.put(CardTypeEnum.CARD_TYPE_ONE_PIECE_VALUE, new CommonComparer());
		allComparer.put(CardTypeEnum.CARD_TYPE_ONE_COUPLE_VALUE, new CommonComparer());
		allComparer.put(CardTypeEnum.CARD_TYPE_THREE_PIECE_VALUE, new CommonComparer());
		allComparer.put(CardTypeEnum.CARD_TYPE_THREE_BRING_TWO_VALUE, new CommonComparer());
		
		// 顺子比较器
		allComparer.put(CardTypeEnum.CARD_TYPE_COMMON_FLUSH_VALUE, new FlushComparer());
		allComparer.put(CardTypeEnum.CARD_TYPE_FLY_OF_THERE_PAIR, new FlushComparer());
		allComparer.put(CardTypeEnum.CARD_TYPE_TWO_CONTINUE_THREE_PIECES_VALUE, new FlushComparer());
		
		allComparer.put(CardTypeEnum.CARD_TYPE_SMALL_COMMON_BOMB_VALUE, new SmallCommonBombComparer());
		allComparer.put(CardTypeEnum.CARD_TYPE_SAME_COLOR_FLUSH_VALUE, new SameColorFlushComparer());
		allComparer.put(CardTypeEnum.CARD_TYPE_BIG_COMMON_BOMB_VALUE, new BigCommonBombComparer());
		allComparer.put(CardTypeEnum.CARD_TYPE_KING_OF_BOMB_VALUE, new KingBombComparer());
	}
	
	
	private void initPieceAndTypeMap(){
		// 一张牌的情况
		List<Integer> cardTypeSetOfPieces_1 = new ArrayList<Integer>();
		cardTypeSetOfPieces_1.add(CardTypeEnum.CARD_TYPE_ONE_PIECE_VALUE);
		allPieceAndCarTypeMap.put(1, cardTypeSetOfPieces_1);
		
		// 两张牌的情况
		List<Integer> cardTypeSetOfPieces_2 = new ArrayList<Integer>();
		cardTypeSetOfPieces_2.add(CardTypeEnum.CARD_TYPE_ONE_COUPLE_VALUE);
		allPieceAndCarTypeMap.put(2, cardTypeSetOfPieces_2);
		
		// 三张牌的情况
		List<Integer> cardTypeSetOfPieces_3 = new ArrayList<Integer>();
		cardTypeSetOfPieces_3.add(CardTypeEnum.CARD_TYPE_THREE_PIECE_VALUE);
		allPieceAndCarTypeMap.put(3, cardTypeSetOfPieces_3);
		
		// 四张牌的情况
		List<Integer> cardTypeSetOfPieces_4 = new ArrayList<Integer>();
		cardTypeSetOfPieces_4.add(CardTypeEnum.CARD_TYPE_KING_OF_BOMB_VALUE);
		cardTypeSetOfPieces_4.add(CardTypeEnum.CARD_TYPE_SMALL_COMMON_BOMB_VALUE);
		allPieceAndCarTypeMap.put(4, cardTypeSetOfPieces_4);
		
		// 五张牌的情况
		List<Integer> cardTypeSetOfPieces_5 = new ArrayList<Integer>();
		cardTypeSetOfPieces_5.add(CardTypeEnum.CARD_TYPE_SAME_COLOR_FLUSH_VALUE);
		cardTypeSetOfPieces_5.add(CardTypeEnum.CARD_TYPE_SMALL_COMMON_BOMB_VALUE);
		cardTypeSetOfPieces_5.add(CardTypeEnum.CARD_TYPE_COMMON_FLUSH_VALUE);
		cardTypeSetOfPieces_5.add(CardTypeEnum.CARD_TYPE_THREE_BRING_TWO_VALUE);
		allPieceAndCarTypeMap.put(5, cardTypeSetOfPieces_5);
		
		// 六张牌的情况
		List<Integer> cardTypeSetOfPieces_6 = new ArrayList<Integer>();
		cardTypeSetOfPieces_6.add(CardTypeEnum.CARD_TYPE_BIG_COMMON_BOMB_VALUE);
		cardTypeSetOfPieces_6.add(CardTypeEnum.CARD_TYPE_FLY_OF_THERE_PAIR);
		cardTypeSetOfPieces_6.add(CardTypeEnum.CARD_TYPE_TWO_CONTINUE_THREE_PIECES_VALUE);
		allPieceAndCarTypeMap.put(6, cardTypeSetOfPieces_6);
		
		// 七张牌的时候
		List<Integer> cardTypeSetOfPieces_7 = new ArrayList<Integer>();
		cardTypeSetOfPieces_7.add(CardTypeEnum.CARD_TYPE_BIG_COMMON_BOMB_VALUE);
		allPieceAndCarTypeMap.put(7, cardTypeSetOfPieces_7);
		
		// 八张牌的时候
		List<Integer> cardTypeSetOfPieces_8 = new ArrayList<Integer>();
		cardTypeSetOfPieces_8.add(CardTypeEnum.CARD_TYPE_BIG_COMMON_BOMB_VALUE);
		allPieceAndCarTypeMap.put(8, cardTypeSetOfPieces_8);
		
		// 九张牌的时候
		List<Integer> cardTypeSetOfPieces_9 = new ArrayList<Integer>();
		cardTypeSetOfPieces_9.add(CardTypeEnum.CARD_TYPE_BIG_COMMON_BOMB_VALUE);
		allPieceAndCarTypeMap.put(9, cardTypeSetOfPieces_9);
		
		// 十张牌的时候
		List<Integer> cardTypeSetOfPieces_10 = new ArrayList<Integer>();
		cardTypeSetOfPieces_10.add(CardTypeEnum.CARD_TYPE_BIG_COMMON_BOMB_VALUE);
		allPieceAndCarTypeMap.put(10, cardTypeSetOfPieces_10);
	}
	
	private void initBombList(){
		// 6, 7, 8, 9, 10 张牌的情况
		morePorkerBomb.add(CardTypeEnum.CARD_TYPE_BIG_COMMON_BOMB_VALUE);
				
				
		// 4, 5 张牌的情况
		lessPorkerBomb.add(CardTypeEnum.CARD_TYPE_KING_OF_BOMB_VALUE);
		lessPorkerBomb.add(CardTypeEnum.CARD_TYPE_SAME_COLOR_FLUSH_VALUE);
		lessPorkerBomb.add(CardTypeEnum.CARD_TYPE_SMALL_COMMON_BOMB_VALUE);
	}
	
	private List<Integer> getCarTypeOfPiecesList(int nPiecesNum){
	
		if((nPiecesNum <= 0) || (nPiecesNum > 10)){
			return null;
		}
		
		List<Integer> searchCheckerList = allPieceAndCarTypeMap.get(nPiecesNum);
		if(null == searchCheckerList){
			logger.error("PorkerCompareLogic::getCarTypeOfPiecesList searchCheckerList null error");
			return null;
		}
		
		return searchCheckerList;
	}
	
	private Comparer getComparerByCardType(int nCardType){
		if(true == allComparer.isEmpty()){
			this.initComparer();
		}
		
		Comparer searchComparer = allComparer.get(nCardType);
		if(null == searchComparer){
			logger.error("PorkerCompareLogic::getComparerByCardType searchCheckerList null error");
			return null;
		}
		
		return searchComparer;
	}
	
	public int compareMaxPorkerWithSumbitPorker(boolean bOnlyCheckCardType, int nLastMaxHandPorker, List<Integer> sumbitPorkerList, int nCurrentMajorCard){
		// 不需要进行比较  出任意牌都是最大的
		int nHandPorkerResult = 0;
		if(true == bOnlyCheckCardType){
			nHandPorkerResult = this.checkTheCardTypeByPorker(sumbitPorkerList, nCurrentMajorCard);
			return nHandPorkerResult;
		}
		
		// 进入比较的环节
		int nLastPorkerCardType = HandPatternCalculator.getPorkCardTypeByLastPorker(nLastMaxHandPorker);
		Comparer specficComparer = this.getComparerByCardType(nLastPorkerCardType);
		if(null == specficComparer){
			logger.error("PorkerCompareLogic::getComparerByCardType specficComparer null error");
			return nHandPorkerResult;
		}
		
		int nNewHandPattern = specficComparer.biggerThanLastMaxPorker(sumbitPorkerList, nLastMaxHandPorker, nCurrentMajorCard);
		return nNewHandPattern;
	}
	
	
	public int checkTheCardTypeByPorker(List<Integer> sumbitPorkerList, int nCurrentMajorCard){
		int nErrorResult = 0;
		int nSumbitPorkerNum = sumbitPorkerList.size();
		List<Integer> searchCheckerList = this.getCarTypeOfPiecesList(nSumbitPorkerNum);
		if(null == searchCheckerList){
			return nErrorResult;
		}
		
		int nCheckerListNum = searchCheckerList.size();
		for(int i = 0; i < nCheckerListNum; ++i){
			int nCheckerCardType = searchCheckerList.get(i);
			Checker checker = checkerManager.getSpecficCheckerByValue(nCheckerCardType);
			if(null == checker){
				logger.error("PorkerCompareLogic::checkTheCardTypeByPorker checker null error");
				continue;
			}
			
			int nFaceValue = checker.isBelongToSpecficType(sumbitPorkerList, nCurrentMajorCard);
			if(0 == nFaceValue){
				continue;
			}
			
			int nNewHandPattern = HandPatternCalculator.makeNewHandPattern(nCheckerCardType, nSumbitPorkerNum, nFaceValue);
			return nNewHandPattern;
		}
		
		return nErrorResult;
	}
	
	
	// 当前 sumbitPorkerList 是否属于炸牌   如果不属于 返回0   如果属于 则返回最大的炸牌类型
	public int isBelongToBombGroup(List<Integer> sumbitPorkerList, int nCurrentMajorCard){
		if(null == sumbitPorkerList){
			logger.error("CardTypeCheckerMgr::isBelongToBombGroup sumbitPorkerList Null error");
			return 0;
		}
		
		int nPorkerNum = sumbitPorkerList.size();
		if((nPorkerNum < 4) || (nPorkerNum > 10)){
			return 0;
		}
		
		List<Integer> listPointerArray = morePorkerBomb;
		if((nPorkerNum >= 4) && (nPorkerNum <= 5)){
			listPointerArray = lessPorkerBomb;
		}
		
		int nListNum = listPointerArray.size();
		for(int i = 0; i < nListNum; ++i){
			int nCheckerTag = listPointerArray.get(i);
			Checker bombChecker = checkerManager.getSpecficCheckerByValue(nCheckerTag);
			if(null == bombChecker){
				logger.error("CardTypeCheckerMgr::isBelongToBombGroup bombChecker Null error");
				continue;
			}
			
			int nFaceValue = bombChecker.isBelongToSpecficType(sumbitPorkerList, nCurrentMajorCard);
			if(0 == nFaceValue){
				continue;
			}
			
			int nNewHandPattern = HandPatternCalculator.makeNewHandPattern(nCheckerTag, sumbitPorkerList.size(), nFaceValue);
			return nNewHandPattern;
		}
		
		int nErrorResult = 0;
		return  nErrorResult;
	}
}
