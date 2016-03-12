package com.lbwan.game.testServerLogicCode;

import java.util.ArrayList;
import java.util.List;

import com.lbwan.game.cardTypeSearch.SearcherManager;
import com.lbwan.game.porker.PorkerMgr;
import com.lbwan.game.porkerComparer.HandPatternCalculator;
import com.lbwan.game.porkerEnumSet.CardColorEnum;
import com.lbwan.game.porkerEnumSet.CardTypeEnum;
import com.lbwan.game.porkerEnumSet.FaceValueEnum;
import com.lbwan.game.porkerEnumSet.PorkerValueEnum;
import com.lbwan.game.spring.SpringUtils;

public class TestSearcherCode {
	
	public TestSearcherCode(){
		
	}
	
	public void testSearchLogic(){
    	List<Integer> playerHandPorker = this.makeHandPorker();
    	    	
    	List<Integer> sumbitPorkerList = new ArrayList<>();
    	SearcherManager searcherMgr = (SearcherManager) SpringUtils.getBeanByName("searcherManager");

    	int nLastMaxHandPattern = HandPatternCalculator.makeNewHandPattern(CardTypeEnum.CARD_TYPE_COMMON_FLUSH_VALUE, 5, FaceValueEnum.FACE_VALUE_2_VALUE);
    	int nSearchPorkerResult = searcherMgr.searchIsBiggerPorkerThanLastMax(playerHandPorker, nLastMaxHandPattern, FaceValueEnum.FACE_VALUE_6_VALUE, sumbitPorkerList);
    	
    	System.out.println("ResultHandPattern: " + nSearchPorkerResult);
    	for(int i = 0; i < sumbitPorkerList.size(); ++i){
    		int nTempPorkerValue = sumbitPorkerList.get(i);
    		System.out.println("PorkerValue: " + nTempPorkerValue + " Color: " + PorkerValueEnum.getColorByPorkValue(nTempPorkerValue) + " FaceValue: " + PorkerValueEnum.getFaceValueByPorkerValueOf(nTempPorkerValue));
    	}
    	
    	System.out.println("End");
    }
	
	
	private List<Integer> makeHandPorker(){
    	
    	List<Integer> sumbitPorkerList = new ArrayList<>();
    	PorkerMgr porkerManager = (PorkerMgr) SpringUtils.getBeanByName("porkerMgr");
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_3_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_4_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_5_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_6_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_7_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_10_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_10_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_10_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_10_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_10_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_10_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_J_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	return sumbitPorkerList;
    }

}
