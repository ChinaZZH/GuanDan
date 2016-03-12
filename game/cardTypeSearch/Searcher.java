package com.lbwan.game.cardTypeSearch;

import java.util.List;

public interface Searcher {
	public int searchBiggerPorkerThanMax(List<Integer> playerHandPorker, int nMaxHandPattern, int nMajorFaceValue, List<Integer> sumbitPorkerList);
}
