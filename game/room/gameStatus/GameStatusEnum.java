package com.lbwan.game.room.gameStatus;

public enum GameStatusEnum {
	GameStatus_Ready(0, "准备开始"),
	GameStatus_Start(1, "开始状态"),
	GameStatus_Calculate_Winner(2, "获取省着"),
	GameStatus_End(3, "结束状态"),
	GameStatus_PayTribute(4, "在进贡状态中"),
	;
	
	public static final int GAME_STAUTS_OF_READY = 0;
	public static final int GAME_STATUS_OF_START = 1;
	public static final int GAME_STATUS_OF_CALCULATE_WINNER    = 2;
	public static final int GAME_STATUS_OF_END  = 3;
	public static final int GAME_STATUS_PAY_TRIBUTE = 4;
	
	private final int value;
    private final String desc;
    
    private GameStatusEnum(int code, String desc) {
        this.value = code;
        this.desc = desc;
    }
    
    public String getStatusDesc(){
    	return this.desc;
    }
    
    public int getStatusEnumValue(){
    	return this.value;
    }
}
