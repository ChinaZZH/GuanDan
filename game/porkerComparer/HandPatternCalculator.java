package com.lbwan.game.porkerComparer;

public class HandPatternCalculator {
	public HandPatternCalculator(){
		
	}
	
	public static int makeNewHandPattern(int nCardType, int nPiecesNum, int nFaceValue){
		int nNewResult = (nCardType * 10000) + (nPiecesNum * 100) + nFaceValue;
		return nNewResult;
	}
	
	public static int getPorkCardTypeByLastPorker(int nLastMaxPorkerSaved){
		int nCalcuCardType = nLastMaxPorkerSaved / 10000;
		return nCalcuCardType;
	}
	
	public static int getFaceValueByLastPorker(int nLastMaxPorkerSaved){
		int nFaceValue = nLastMaxPorkerSaved % 100;
		return nFaceValue;
	}
	
	public static int getPiecesNumByLastPorker(int nLastMaxPorkerSaved){
		int nPiecesNum = ((nLastMaxPorkerSaved % 10000) / 100);
		return nPiecesNum;
	}
}
