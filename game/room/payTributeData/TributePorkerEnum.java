package com.lbwan.game.room.payTributeData;

public enum TributePorkerEnum {
	TributeStatus_NullOperation(0, "不操作"),
	TributeStatus_PayTribute(1, "进贡"),
	TributeStatus_BackTribute(2, "退贡"),
	;
	
	public static final int NULL_OPERATION = 0;
	public static final int PAY_TRIBUTE    = 1;
	public static final int BACK_TRIBUTE   = 2;
	
	
	private final int value;
	private final String desc;

	private TributePorkerEnum(int code, String desc) {
	    this.value = code;
	    this.desc = desc;
	}

	public int getValue(){
		return this.value;
	}

	public String getDesc(){
		return this.desc;
	}
}
