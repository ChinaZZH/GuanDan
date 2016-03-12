package com.lbwan.game.porker;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.lbwan.game.porkerEnumSet.CardColorEnum;
import com.lbwan.game.porkerEnumSet.FaceValueEnum;
import com.lbwan.game.porkerEnumSet.PorkerValueEnum;


@Service
public class PorkerMgr {
	private Map<Integer, Porker> allPorkerMap = new HashMap<Integer, Porker>();
	private Logger logger = Logger.getLogger(getClass());
	 
	@PostConstruct
	public void init(){
		for(int nPorkValue = PorkerValueEnum.PORKER_MIN_TYPE_VALUE; nPorkValue <= PorkerValueEnum.PORKER_MAX_TYPE_VALUE; ++nPorkValue){
			Porker newPorker = new Porker(nPorkValue);
			allPorkerMap.put(nPorkValue, newPorker);
		}
	}
	
	
	public Porker getSpecficPorkerByValue(int nPorkerValue){
		Porker searchPorker = allPorkerMap.get(nPorkerValue);
		if(null == searchPorker){
			logger.error("PorkerMgr::getSpecficPorkerByValue searchPorker Null error");
			return null;
		}
		
		return searchPorker;
	}
	
	public boolean isBelongToBigKing(int nPorkerValue){
		if(PorkerValueEnum.PORKER_BIG_JOKER_VALUE == nPorkerValue){
			return true;
		}
		
		return false;
	}
	
	public boolean isBelongToSmallKing(int nPorkerValue){
		if(PorkerValueEnum.PORKER_SMALL_JOKER_VALUE == nPorkerValue){
			return true;
		}
		
		return false;
	}
	
	public int getFaceValue(int nPorkerValue){
		int nErrorResult = 0;
		Porker porker = this.getSpecficPorkerByValue(nPorkerValue);
		if(null == porker){
			return nErrorResult;
		}
		
		return porker.getFaceValue();
	}
	
	public int getColor(int nPorkerValue){
		int nErrorResult = 0;
		Porker porker = this.getSpecficPorkerByValue(nPorkerValue);
		if(null == porker){
			return nErrorResult;
		}
		
		return porker.getColor();
	}
	
	public int getPorkerValue(int nFaceValue, int nColor){
		if(FaceValueEnum.FACE_VALUE_SMALL_JORKER == nFaceValue){
			return PorkerValueEnum.PORKER_SMALL_JOKER_VALUE;
		}
		
		if(FaceValueEnum.FACE_VALUE_BIG_JORKER == nFaceValue){
			return PorkerValueEnum.PORKER_BIG_JOKER_VALUE;
		}
		
		int nTempPorkActor = (nFaceValue - 2) * 4;
		if(CardColorEnum.CARD_COLOR_HEART == nColor){
			return (nTempPorkActor + 1);
		}
		
		if(CardColorEnum.CARD_COLOR_DIAMOND == nColor){
			return (nTempPorkActor + 2);
		}
		
		if(CardColorEnum.CARD_COLOR_SPADE == nColor){
			return (nTempPorkActor + 3);
		}
		
		if(CardColorEnum.CARD_COLOR_CLUB == nColor){
			return (nTempPorkActor + 4);
		}
		
		return 0;
	}
}
