package com.lbwan.game.room.payTributeData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PayTributeData {
	// 进贡者   和  进贡数据 隐射
	private Map<String, PayTributeDataUnit> tributeDataMap = new HashMap<String, PayTributeDataUnit>();
	
	// 接受进贡者  和  进贡数据 隐射
	private Map<String, PayTributeDataUnit> backTributePlayerMap = new HashMap<String, PayTributeDataUnit>();
	
	private Log logger = LogFactory.getLog(getClass());
	
	private String newGameFirstPlayer = null;
	
	public PayTributeData(){
		
	}
	
	public void addNewPayTributeData(String strPayTributeUser, int nPayTributePorkerFaceValue, String strReceivePayTributeUser){
		if((null == strPayTributeUser) || (null == strReceivePayTributeUser)){
			logger.error("PayTributeData::addNewPayTributeData strPayTributeUser: " + strPayTributeUser + " strReceivePayTributeUser: "+ strReceivePayTributeUser);
			return ;
		}
		
		//System.out.println(strPayTributeUser + "需要向" + strReceivePayTributeUser + "进贡" + nPayTributePorkerFaceValue);
		
		PayTributeDataUnit tributeDataUnit = new PayTributeDataUnit(strPayTributeUser, nPayTributePorkerFaceValue, strReceivePayTributeUser);
		tributeDataUnit.clearTributedStatus();
		
		tributeDataMap.put(strPayTributeUser, tributeDataUnit);
		backTributePlayerMap.put(strReceivePayTributeUser, tributeDataUnit);
	}
	
	// 设置新一局游戏的起牌者
	public void initNewGameFirstPlayer(String strNewGameFirstPlayer){
		this.newGameFirstPlayer = strNewGameFirstPlayer;
	}
	
	public String getNewGameFristPlayer(){
		return this.newGameFirstPlayer;
	}
	
	public void clearAllData(){
		tributeDataMap.clear();
		backTributePlayerMap.clear();
		newGameFirstPlayer = null;
	}
	
	public boolean isPayTributeByUserId(String strPayTributeUser){
		PayTributeDataUnit payUnit = this.getPayTributeDataUnitByUser(strPayTributeUser);
		if(null != payUnit){
			return true;
		}
		
		return false;
	}
	
	public boolean isBackTributeByUserId(String strBackTributeUser){
		PayTributeDataUnit payUnit = this.getBackTributeDataByBackTributer(strBackTributeUser);
		if(null != payUnit){
			return true;
		}
		
		return false;
	}
	
	private PayTributeDataUnit getPayTributeDataUnitByUser(String strPayTributeUser){
		PayTributeDataUnit tributeData = tributeDataMap.get(strPayTributeUser);
		return tributeData;
	}
	
	private PayTributeDataUnit getBackTributeDataByBackTributer(String strBackTributeUser){
		PayTributeDataUnit tributeData = backTributePlayerMap.get(strBackTributeUser);
		return tributeData;
	}
	
	public int getPayTributePorkerFaceValue(String strPayTributeUser){
		PayTributeDataUnit dataUnit = this.getPayTributeDataUnitByUser(strPayTributeUser);
		if(null == dataUnit){
			logger.error("PayTributeData::initPlayerHandPorker playerPorkerArray Null Error");
			return 0;
		}
		
		return dataUnit.getPayTributeFaceValue();
	}
	
	public String getReceivePayTributeUser(String strPayTributeUser){
		PayTributeDataUnit dataUnit = this.getPayTributeDataUnitByUser(strPayTributeUser);
		if(null == dataUnit){
			logger.error("PayTributeData::getReceivePayTributeUser playerPorkerArray Null Error");
			return null;
		}
		
		return dataUnit.getReceivePayTributeUser();
	}
	
	public String getPayTributeUserByBackTributer(String strBackTributeUser){
		PayTributeDataUnit dataUnit = this.getBackTributeDataByBackTributer(strBackTributeUser);
		if(null == dataUnit){
			logger.error("PayTributeData::getPayTributeUserByBackTributer playerPorkerArray Null Error");
			return null;
		}
		
		return dataUnit.getPayTributeUser();
	}
	
	public boolean isPayTributerUser(String strPayTributeUser){
		PayTributeDataUnit dataUnit = this.getPayTributeDataUnitByUser(strPayTributeUser);
		if(null == dataUnit){
			return false;
		}
		
		return true;
	}
	
	public boolean isBackTributerUser(String strBackTributeUser){
		PayTributeDataUnit dataUnit = this.getBackTributeDataByBackTributer(strBackTributeUser);
		if(null == dataUnit){
			return false;
		}
		
		return true;
	}
	
	// 是否已经进贡过
	public boolean isPayTributed(String strPayTributeUser){
		boolean bResult = false;
		PayTributeDataUnit dataUnit = this.getPayTributeDataUnitByUser(strPayTributeUser);
		if(null == dataUnit){
			logger.error("PayTributeData::isPayTributed playerPorkerArray Null Error");
			return bResult;
		}
		
		bResult = dataUnit.isPayTributed();
		return bResult;
	}
	
	// 是否已经退贡过
	public boolean isBackTributed(String strBackTributeUser){
		boolean bResult = false;
		PayTributeDataUnit dataUnit = this.getBackTributeDataByBackTributer(strBackTributeUser);
		if(null == dataUnit){
			logger.error("PayTributeData::isBackTributed playerPorkerArray Null Error");
			return bResult;
		}
		
		bResult = dataUnit.isBackTributeToPayPlayer();
		return bResult;
	}
	
	
	// 执行进贡
	public void payTributed(String strPayTributeUser){
		PayTributeDataUnit dataUnit = this.getPayTributeDataUnitByUser(strPayTributeUser);
		if(null == dataUnit){
			logger.error("PayTributeData::payTributed playerPorkerArray Null Error");
			return ;
		}
		
		dataUnit.payTributed();
	}
	
	// 是否已经完成进贡过程
	public boolean isCompletePayTributeActivity(){
		Iterator<Map.Entry<String, PayTributeDataUnit>> iter = tributeDataMap.entrySet().iterator();
		while(true == iter.hasNext()){
			Map.Entry<String, PayTributeDataUnit> entry = iter.next();
			PayTributeDataUnit tributeData = entry.getValue();
			if(false == tributeData.isPayTributed()){
				return false;
			}
		}
		
		return true;
	}
	
	// 是否已经 完成  进贡/退贡 的所有活动
	public boolean isCompleteAllTributeActivity(){
		Iterator<Map.Entry<String, PayTributeDataUnit>> iter = backTributePlayerMap.entrySet().iterator();
		while(true == iter.hasNext()){
			Map.Entry<String, PayTributeDataUnit> entry = iter.next();
			PayTributeDataUnit tributeData = entry.getValue();
			if(false == tributeData.isBackTributeToPayPlayer()){
				return false;
			}
		}
		
		return true;
	}
	
	public void backTributeToPayPlayer(String strBackTributer){
		if(null == strBackTributer){
			logger.error("PayTributeData::backTributeToPayPlayer strBackTributer Null Error");
			return ;
		}
		
		PayTributeDataUnit payTributeUnit = backTributePlayerMap.get(strBackTributer);
		if(null == payTributeUnit){
			logger.error("PayTributeData::backTributeToPayPlayer payTributeUnit Null Error");
			return ;
		}
		
		payTributeUnit.backTributeToPayPlayer();
	}
	
	public List<String> getPlayerNotCompletePayTribute(){
		List<String> noPayTributer = new ArrayList<>();
		
		Iterator<Map.Entry<String, PayTributeDataUnit>> iter = tributeDataMap.entrySet().iterator();
		while(true == iter.hasNext()){
			Map.Entry<String, PayTributeDataUnit> entry = iter.next();
			PayTributeDataUnit tributeData = entry.getValue();
			if(true == tributeData.isPayTributed()){
				continue;
			}
			
			String strTributePlayer = entry.getKey();
			noPayTributer.add(strTributePlayer);
		}
		
		return noPayTributer;
	}
	
	
	public List<String> getPlayerNotCompleteBackTribute(){
		List<String> noBackTributer = new ArrayList<>();
		
		Iterator<Map.Entry<String, PayTributeDataUnit>> iter = backTributePlayerMap.entrySet().iterator();
		while(true == iter.hasNext()){
			Map.Entry<String, PayTributeDataUnit> entry = iter.next();
			PayTributeDataUnit tributeData = entry.getValue();
			if(true == tributeData.isBackTributeToPayPlayer()){
				continue;
			}
			
			String strTributePlayer = entry.getKey();
			noBackTributer.add(strTributePlayer);
		}
		
		return noBackTributer;
	}
}
