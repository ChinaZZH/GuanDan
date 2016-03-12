package com.lbwan.game.cardTypeSearch;

import java.util.List;

public class KingPorkerSearcher extends AbstractSearcher{
	public KingPorkerSearcher(){
		m_nSearcherPieceNum = 4;
	}
	
	public int getSearcherPieceNum(){
		return this.m_nSearcherPieceNum;
	}
	
	public int searchBiggerPorkerThanMax(List<Integer> playerHandPorker, int nMaxHandPattern, int nMajorFaceValue, List<Integer> sumbitPorkerList){
		int nErrorResult = 0;
		return nErrorResult;
	}
}
