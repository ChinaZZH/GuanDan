package com.lbwan.game.porkerComparer;

import java.util.List;
import com.lbwan.game.porkerEnumSet.CardTypeEnum;

public class SameColorFlushComparer extends AbstractComparer{
	
	public int biggerThanLastMaxPorker(List<Integer> sumbitPorkerList, int nLastMaxHandPorker, int nCurrentMajorCard){
		int nErrorResult = 0;
		// 如果是王炸 则返回
		int nSumbitSize = sumbitPorkerList.size();
		int nFaceValue = this.getFaceValueBySpecficCardType(CardTypeEnum.CARD_TYPE_KING_OF_BOMB_VALUE, sumbitPorkerList, nCurrentMajorCard);
		if(0 != nFaceValue){
			int nSuccessResult = HandPatternCalculator.makeNewHandPattern(CardTypeEnum.CARD_TYPE_KING_OF_BOMB_VALUE, nSumbitSize, nFaceValue);
			return nSuccessResult;
		}
		
		// 是否是大的普通炸
		if(sumbitPorkerList.size() > 5){
			nFaceValue = this.getFaceValueBySpecficCardType(CardTypeEnum.CARD_TYPE_BIG_COMMON_BOMB_VALUE, sumbitPorkerList, nCurrentMajorCard);
			if(0 != nFaceValue){
				int nSuccessResult = HandPatternCalculator.makeNewHandPattern(CardTypeEnum.CARD_TYPE_BIG_COMMON_BOMB_VALUE, nSumbitSize, nFaceValue);
				return nSuccessResult;
			}
		}
		
		// 是否是同花顺
		nFaceValue = this.getFaceValueBySpecficCardType(CardTypeEnum.CARD_TYPE_SAME_COLOR_FLUSH_VALUE, sumbitPorkerList, nCurrentMajorCard);
		if(0 == nFaceValue){
			return nErrorResult;
		}
		
		int nLastMaxValue = HandPatternCalculator.getFaceValueByLastPorker(nLastMaxHandPorker);
		boolean bBiggerResult = this.compareFlushByCardType(nFaceValue, nLastMaxValue);
		if(true == bBiggerResult){
			int nSuccessResult = HandPatternCalculator.makeNewHandPattern(CardTypeEnum.CARD_TYPE_SAME_COLOR_FLUSH_VALUE, nSumbitSize, nFaceValue);
			return nSuccessResult;
		}
		
		return nErrorResult;
	}
}
