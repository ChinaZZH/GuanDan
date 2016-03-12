package com.lbwan.game.room.roomGameLogic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.lbwan.game.porkerEnumSet.PorkerValueEnum;
import com.lbwan.game.room.roomGame.GamePlayer;
import com.lbwan.game.room.roomGame.RoomGame;
import com.lbwan.game.utils.RandomUtils;

public class PorkerInitLogic {
	
	private List<Integer> initPorkerList = new ArrayList<Integer>();
	
	private List<Integer> playersPorkerList = null;
	
	private Log logger = LogFactory.getLog(getClass());
	
	private RoomGame roomGame = null;
	
	
	public PorkerInitLogic(RoomGame roomGameParam){
		this.roomGame = roomGameParam;
		this.initPorkerArray();
	}
	
	
	private void initPorkerArray(){
		// 两副牌
		for(int nPorkerValue = PorkerValueEnum.PORKER_MIN_TYPE_VALUE; nPorkerValue <= PorkerValueEnum.PORKER_MAX_TYPE_VALUE; ++nPorkerValue){
			initPorkerList.add(nPorkerValue);
			initPorkerList.add(nPorkerValue);
		}
	}
	
	
	// 洗牌
	public boolean shuttleTheCard(){
		int nPorkerNum = initPorkerList.size();
		playersPorkerList = RandomUtils.getNotRepeatRandomValues(initPorkerList, nPorkerNum);
		if(null == playersPorkerList){
			logger.error("PorkerInitLogic::shuttleTheCard playersPorkerList null Error");
			return false;
		}
		
		return true;
		
	}
	
	// 测试时候使用
	// 测试时候使用
	public boolean testForShuttleCard(){
		int nTestPorkerArray[] = {2  ,2  ,8  ,17  ,18  ,22  ,26  ,27  ,28  ,29  ,30  ,31  ,36  ,37  ,38  ,39  ,40  ,43  ,44  ,45  ,46  ,47  ,50  ,50  ,51  ,53  ,54  ,
			4  ,4  ,5  ,6  ,8  ,12  ,14  ,15  ,15  ,20  ,22  ,25  ,25  ,30  ,32  ,33  ,34  ,35  ,41  ,43  ,44  ,47  ,48  ,49  ,52  ,52  ,53  ,
			1  ,3  ,6  ,9  ,9  ,10  ,11  ,11  ,16  ,16  ,17  ,19  ,21  ,23  ,24  ,28  ,29  ,31  ,33  ,34  ,37  ,38  ,42  ,42  ,45  ,46  ,49  ,
			1  ,3  ,5  ,7  ,7  ,10  ,12  ,13  ,13  ,14  ,18  ,19  ,20  ,21  ,23  ,24  ,26  ,27  ,32  ,35  ,36  ,39  ,40  ,41  ,48  ,51  ,54  };
			
		playersPorkerList = new ArrayList<>();
		for(int i = 0; i < 108; i++){
			playersPorkerList.add(nTestPorkerArray[i]);
		}
		
		/*
		int nPorkerNum = initPorkerList.size();
		playersPorkerList = RandomUtils.getNotRepeatRandomValues(initPorkerList, nPorkerNum);
		if(null == playersPorkerList){
			logger.error("PorkerInitLogic::shuttleTheCard playersPorkerList null Error");
			return false;
		}
		*/
		
		return true;
		
	}
	
	
	public boolean gainInitPorker(GamePlayer player){
		if(null == player){
			logger.error("PorkerInitLogic::gainInitPorker player null Error");
			return false;
		}
		
		
		List<Integer> playerHandPorker = this.getHandPorkerBySort();
		player.initPlayerHandPorker(playerHandPorker);
		
		// 日志  显示牌型
		StringBuffer printBuffer = new StringBuffer();
		printBuffer.append(player.getGamePlayerId()+" 最开始的牌组:");
		
		/*
		StringBuffer porkerValueBuffer = new StringBuffer();
		porkerValueBuffer.append(player.getGamePlayerId()+"____PorkerValueList:");
		*/
		
		for(int i = 0; i < playerHandPorker.size(); ++i){
			int nPorkerValue = playerHandPorker.get(i);
			
			String strPorkerColor = PorkerValueEnum.getColorByPorkValue(nPorkerValue);
			int nPorkerFaceValue = PorkerValueEnum.getFaceValueByPorkerValueOf(nPorkerValue);
			printBuffer.append("   " + strPorkerColor.toString() + "  " + nPorkerFaceValue + "  ;");
			
			//porkerValueBuffer.append("   "  + nPorkerValue + "  ;");
		}
		
		System.out.println(printBuffer.toString());
		
		//System.out.println(porkerValueBuffer.toString());
		return true;
	}
	
	private List<Integer> getHandPorkerBySort(){
		int nPorkerNum = initPorkerList.size();
		int nGamePlayerMinCount = 4; //PropertiesUtils.getPropertyAsInteger(PropertiesUtils.GAME_PLAYER_MIN_COUNT);
		int nPorkerNumEachPlayer = nPorkerNum / nGamePlayerMinCount;
		
		List<Integer> targetValue = new ArrayList<Integer>(nPorkerNumEachPlayer);
		if(playersPorkerList.size() < nPorkerNumEachPlayer){
			logger.error("PorkerInitLogic::getHandPorkerBySort size() server Error");
			return null;
		}
		
		for(int i = 0; i < nPorkerNumEachPlayer; ++i){
			targetValue.add(playersPorkerList.get(i));
		}
		
		
		for(int i = 0; i < nPorkerNumEachPlayer; ++i){
			int nFirstIndex = 0;
			playersPorkerList.remove(nFirstIndex);
		}
		
		/*
		Collections.sort(targetValue);
		StringBuffer buffer = new StringBuffer();
		for(int i = 0; i < targetValue.size(); ++i){
			buffer.append(targetValue.get(i));
			buffer.append("  ,");
		}
		
		System.out.println(buffer.toString());
		*/
		return targetValue;
	}
}
	
	


