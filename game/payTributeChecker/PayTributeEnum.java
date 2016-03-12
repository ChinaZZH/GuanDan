package com.lbwan.game.payTributeChecker;

public enum PayTributeEnum {
	Tribute_Null_Error(0, "不满足所有进贡 抗攻条件"),
	
	Tribute_AllLow_React(1, "双下抗贡"),
	Tribute_AllLow_SameTributePorker(2, "双下,并且进贡的牌一样大"),
	Tribute_AllLow_DiffTributePorker(3, "双下,并且进贡的牌不一样大"),
	
	Tribute_SingleLow_React(4, "单下, 抗贡"),
	Tribute_SingleLow_Tribute(5, "单下, 进贡"),
	;
	
	public static final int TRIBUTE_NULL_ERROR = 0;
	public static final int TRIBUTE_ALL_LOW_REACT = 1;
	public static final int TRIBUTE_ALL_LOW_SAME_TRIBUTE_PORKER = 2;
	public static final int TRIBUTE_ALL_LOW_DIFF_TRIBUTE_PORKER = 3;
	
	public static final int TRIBUTE_SINGLE_LOW_REACT = 4;
	public static final int TRIBUTE_SINGLE_LOW_TRIBUTE = 5;
	
	
	public static final int SINGLE_TRIBUTER = 1;
	public static final int DOUBLE_TRIBUTER = 2;
	
	private int  value;
	private String  desc;
	
	private PayTributeEnum(int nValueParam, String strDescParam){
		this.value = nValueParam;
		this.desc = strDescParam;
	}
	
	public int getValue(){
		return this.value;
	}
	
	public String getDescOfPayTribute(){
		return this.desc;
	}
	
	public static boolean isBelongToReactTribute(int nTributeType){
		if((TRIBUTE_ALL_LOW_REACT == nTributeType) || (TRIBUTE_SINGLE_LOW_REACT == nTributeType)){
			return true;
		}
		
		return false;
	}
}
