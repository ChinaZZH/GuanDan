package com.lbwan.game.room.gameTeam;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lbwan.game.proto.WhippedEgg.GameResultCode;
import com.lbwan.game.room.Player;
import com.lbwan.game.room.roomGame.GamePlayer;
import com.lbwan.game.room.roomGame.RoomGame;
import com.lbwan.game.spring.SpringUtils;
import com.lbwan.game.utils.DataSendUtils;
import com.lbwan.game.utils.GDPropertiesUtils;
import com.lbwan.game.utils.PropertiesUtils;
import com.lbwan.gamecenter.remoting.entity.GameInfo;
import com.lbwan.gamecenter.remoting.enums.GameResult;
import com.lbwan.gamecenter.remoting.spi.GameDataService;

public class TeamGroup {
	
	@Autowired
	private GameDataService gameDataService = (GameDataService)SpringUtils.getBeanByName("gameDataService");
	
	private final static int TEAM_GROUP_MIN_COUNT = PropertiesUtils.getPropertyAsInteger(PropertiesUtils.GAME_PLAYER_MIN_COUNT);
	
	// first 为UserId  Second为Team
	private Map<String, Team> teamGroupMap = new HashMap<String, Team>();
	
	private Set<String> allOfTeamUser = new HashSet<>();
	
	private Set<Team> teamMap = new HashSet<>();
	
	private Log logger = LogFactory.getLog(getClass());
	
	private boolean initTeamData = false;
	
	private RoomGame game = null;
	
	// 主场
	private Team majorSceneTeam = null;
	
	// 当前庄家的主牌
	private int dealerMajorFaceValue = 2;
	
	// 当前从牌者的主牌
	private int leafMajorFaceValue = 2;
	
	// 当前第几个出完牌
	private int m_nOutPorkerPlayerRank = 1;
	
	
	public TeamGroup(RoomGame currentGameParam){
		this.game = currentGameParam;
		m_nOutPorkerPlayerRank = 1;
	}
	

	public void incHandOutRank(){
		this.m_nOutPorkerPlayerRank = this.m_nOutPorkerPlayerRank + 1;
	}
	

	 public int getHandOutPorkerGameRank(){
		 return this.m_nOutPorkerPlayerRank;
	 }
	 
	public boolean initOfTeamGroup(List<Player> players){
		boolean bInitResult = false;
		if(null == players){
			logger.error("TeamGroup::initOfTeamGroup players Null Error");
			return bInitResult;
		}
		
		
		if(false == initTeamData){
			// 进行分配和初始化
			bInitResult = this.helpInitTeamData(players);
		}else{
			// 进行校验  如何不符合  则失败
			bInitResult = this.checkInitData(players);
		}
		
		if(true == bInitResult){
			this.initTeamData = true;
		}
		
		return bInitResult;
	}
	
	// 进行内存的分配
	private boolean helpInitTeamData(List<Player> players){
		boolean bCheckResult = false;
		int nPlayersCount = players.size();
		
		// 判断校验  并且设置set变量
		for(int i = 0; i < nPlayersCount; i++){
			Player listPlayer = players.get(i);
			if(null == listPlayer){
				logger.error("TeamGroup::checkInitData listPlayer Null Error");
				return bCheckResult;
			}
			
			String strPlayerId = listPlayer.getUserId();
			if(null == strPlayerId){
				logger.error("TeamGroup::checkInitData strPlayerId Null Error");
				return bCheckResult;
			}
			
			allOfTeamUser.add(strPlayerId);
		}
		
		// 初始化队伍
		String strPlayer_0 = players.get(0).getUserId(), strPlayer_2 = players.get(2).getUserId();
		String strPlayer_1 = players.get(1).getUserId(), strPlayer_3 = players.get(3).getUserId();
		
		// 如何初始化控制链 和 队友  并将其放入同一个队伍中
		Team leftTeam  = new Team(strPlayer_0, strPlayer_2, this);
		leftTeam.initControlNextPlayer(strPlayer_0, strPlayer_1);
		leftTeam.initControlNextPlayer(strPlayer_2, strPlayer_3);
		teamGroupMap.put(strPlayer_0, leftTeam);
		teamGroupMap.put(strPlayer_2, leftTeam);
		
		
		Team rightTeam = new Team(strPlayer_1, strPlayer_3, this);
		rightTeam.initControlNextPlayer(strPlayer_1, strPlayer_2);
		rightTeam.initControlNextPlayer(strPlayer_3, strPlayer_0);
		teamGroupMap.put(strPlayer_1, rightTeam);
		teamGroupMap.put(strPlayer_3, rightTeam);
		
		teamMap.add(leftTeam);
		teamMap.add(rightTeam);
		
		// 庄家牌初始化
		this.dealerMajorFaceValue = 2;
		this.leafMajorFaceValue = 2;
		
		bCheckResult = true;
		return bCheckResult;
	}
		
	// 校验初始化的数据
	private boolean checkInitData(List<Player> players){
		boolean bCheckResult = false;
		int nPlayersNum = players.size();
		for(int i = 0; i < nPlayersNum; ++i){
			Player listPlayer = players.get(i);
			if(null == listPlayer){
				logger.error("TeamGroup::checkInitData listPlayer Null Error");
				return bCheckResult;
			}
			
			String strPlayerId = listPlayer.getUserId();
			if(null == strPlayerId){
				logger.error("TeamGroup::checkInitData strPlayerId Null Error");
				return bCheckResult;
			}
			
			boolean bContainResult = allOfTeamUser.contains(strPlayerId);
			if(false == bContainResult){
				return bCheckResult;
			}
		}
		
		bCheckResult = true;
		return bCheckResult;
	}
	
	
	public GamePlayer searchGamePlayerByUserId(String strSearchUser){
    	if(null == strSearchUser){
    		logger.error("GamePlayerMgr::searchGamePlayerByUserId strSearchUser Null Error");
			return null;
    	}
    	
 
    	Team userTeam = teamGroupMap.get(strSearchUser);
    	if(null == userTeam){
    		logger.error("GamePlayerMgr::searchGamePlayerByUserId searchGamePlayer Null Error");
			return null;
    	}
    	
    	GamePlayer searchGamePlayer = userTeam.searchPlayer(strSearchUser);
    	if(null == searchGamePlayer){
    		logger.error("GamePlayerMgr::searchGamePlayerByUserId searchGamePlayer Null Error");
			return null;
    	}
    	
    	return searchGamePlayer;
    }
    
    public GamePlayer searchGamePlayerByPlayer(Player currentPlayer){
    	String strGameUserId = currentPlayer.getUserId();
		if(null == strGameUserId){
			logger.error("GamePlayerMgr::searchGamePlayerByPlayer strGameUserId null Error");
			return null;
		}
		
		return this.searchGamePlayerByUserId(strGameUserId);
    }
	
    
    public boolean nofityAllOnLineUser(int cmd, byte[] datas){
    	Iterator<String> iter = allOfTeamUser.iterator();//先迭代出来  
        while(true == iter.hasNext()){//遍历  
        	String strUserId = iter.next();
        	if(null == strUserId){
            	logger.error("GamePlayerMgr::nofityAllOnLineUser player null Error");
            	continue;
            }
        	
        	GamePlayer player = this.searchGamePlayerByUserId(strUserId);
        	if(null == player){
            	logger.error("GamePlayerMgr::nofityAllOnLineUser player null Error");
            	continue;
            }
        	
        	 boolean bOnLine = player.getOnLinePlayStatus();
             if(false == bOnLine){
                 continue;
             }
             
             // 根据String strUserId 发送消息
             DataSendUtils.sendData(strUserId, cmd, datas, true);
        }  
        
        boolean bResult = true;
    	return bResult;
    }
    
    
    public Set<String> getAllUserSet(){
    	return this.allOfTeamUser;
    }
    
    public int getCurrentMajorFaceValue(){
    	return this.dealerMajorFaceValue;
    }
    
    
    public int getOtherMajorFaceValue(){
    	return this.leafMajorFaceValue;
    }
    
    public boolean isEndOfRoundGame(){
    	boolean bEndGame = false;
    	String  strControlUser = game.getCurrentControlUser();
    	if(null == strControlUser){
    		logger.error("RoomGame::isEndOfRoundGame strControlUser null Error");
     	    return bEndGame;
    	}
    	
    	Team searchTeam = teamGroupMap.get(strControlUser);
    	if(null == searchTeam){
    		logger.error("RoomGame::getCurrentMajorCard searchTeam null Error");
    		return bEndGame;
    	}
    	
    	int nTeamMemberCount = searchTeam.teamPlayerNum();
    	int nHandOutPlayerCount = searchTeam.getHandOutPorkerNumOfTeam();
    	if(nTeamMemberCount == nHandOutPlayerCount){
    		bEndGame = true;
    		return bEndGame;
    	}
    	
    	return bEndGame;
    }
    
  

    public Team getTeamByUserId(String strUserId){
    	if(null == strUserId){
    		logger.error("RoomGame::getTeamByUserId strUserId null Error");
     	    return null;
    	}
    	
    	Team findTeam = teamGroupMap.get(strUserId);
    	if(null == findTeam){
    		logger.error("RoomGame::getTeamByUserId findTeam null Error");
     	    return null;
    	}
    	
    	return findTeam;
    }
    
    public Team getTeamOfControlUser(){
    	String  strControlUser = game.getCurrentControlUser();
    	if(null == strControlUser){
    		logger.error("RoomGame::getTeamOfControlUser strControlUser null Error");
     	    return null;
    	}
    	
    	Team controlTeam = teamGroupMap.get(strControlUser);
    	if(null == controlTeam){
    		logger.error("RoomGame::getTeamOfControlUser controlTeam null Error");
    		return null;
    	}
    	
    	return controlTeam;
    }
    
    private boolean countHandOutPorkerPlayerRank(){
		Team failTeam = this.getTeamOfFailGame();
		if(null == failTeam){
    		logger.error("RoomGame::countHandOutPorkerPlayerRank failTeam null Error");
    		return false;
    	}
		failTeam.calculateRank(this);
		
		Team winnerTeam = this.getTeamOfWinner();
		if(null == winnerTeam){
    		logger.error("RoomGame::countHandOutPorkerPlayerRank winnerTeam null Error");
    		return false;
    	}
		winnerTeam.calculateRank(this);
		return true;
    }
    
    private boolean gameEndUpMajorFaceValue(){
    	Team winnerTeam = this.getTeamOfWinner();
		if(null == winnerTeam){
    		logger.error("RoomGame::gameEndUpMajorFaceValue winnerTeam null Error");
    		return false;
    	}
		
		winnerTeam.upMajorFaceValue();
		return true;
    }
    
    public Team getTeamOfFailGame(){
    	Iterator<Team> iter = teamMap.iterator();
		while(true == iter.hasNext()){
			Team theInitTeam = iter.next();
			if(null == theInitTeam){
	    		logger.error("RoomGame::countHandOutPorkerPlayerRank theInitTeam null Error");
	    		continue;
	    	}
			
			boolean bWinTheGame = theInitTeam.isTeamWinTheGame();
			if(false == bWinTheGame){
				return theInitTeam;
			}
		}
		
		return null;
    }
    
    public Team getTeamOfWinner(){
    	Iterator<Team> iter = teamMap.iterator();
		while(true == iter.hasNext()){
			Team theInitTeam = iter.next();
			if(null == theInitTeam){
	    		logger.error("RoomGame::countHandOutPorkerPlayerRank theInitTeam null Error");
	    		continue;
	    	}
			
			boolean bWinTheGame = theInitTeam.isTeamWinTheGame();
			if(true == bWinTheGame){
				Team winnerTeam = theInitTeam;
				return winnerTeam;
			}
		}
		
		return null;
    }
    
    public void clearLastRoundData(){
    	m_nOutPorkerPlayerRank = 1;
    	Iterator<Team> iter = teamMap.iterator();
		while(true == iter.hasNext()){
			Team theInitTeam = iter.next();
			if(null == theInitTeam){
	    		logger.error("RoomGame::clearLastRoundData theInitTeam null Error");
	    		continue;
	    	}
			
			theInitTeam.initNewRound();
		}
    }
    
    // 取上游者
    public GamePlayer getFirstOutPorkerUser(){
    	//Set<String> allOfTeamUser
    	return this.getPlayerByRankNumber(1);
    }
    
    // 取下游者
    public GamePlayer getLastOutPorkerUser(){
    	
    	return this.getPlayerByRankNumber(4); 
    }
    
    private GamePlayer getPlayerByRankNumber(int nRankNumber){
    	Iterator<String> iter = allOfTeamUser.iterator();
    	while(true == iter.hasNext()){
    		String strPorkerUser = iter.next();
    		GamePlayer player = this.searchGamePlayerByUserId(strPorkerUser);
    		if(null == player){
	    		logger.error("TeamGroup::getPlayerByRankNumber player null Error");
	    		continue;
	    	}
    		
    		if(nRankNumber == player.getOutAllPorkerRank()){
    			return player;
    		}
    	}
    	
    	return null;
    }
    
    public void reSetNewMajorFaceValue(int nDealerMajorFaceValue, int nLeafMajorFaceValue, Team majorSceneTeamParam){
    	majorSceneTeam = majorSceneTeamParam;
    	this.dealerMajorFaceValue = nDealerMajorFaceValue;
    	this.leafMajorFaceValue = nLeafMajorFaceValue;
    }
    
    public void processForGameEnd(){
    	// 计算排位
		this.countHandOutPorkerPlayerRank();
    	
    	// 游戏结束 升级相对应的主牌
		this.gameEndUpMajorFaceValue();
    	

    	// 设置下一局的主牌
		this.setNewMajorFaceValueOfGameEnd();
		
		// 获取金币
		this.awardPlayerGoldForGameEnd();
		
		// 修改胜场 和 负场
		this.calculateGameRate();
    }
    
    private boolean calculateGameRate(){
    	boolean bCalculateResult = false;
    	List<GameInfo> gameInfosList = new ArrayList<>();
    	Iterator<String> iter = allOfTeamUser.iterator();
    	while(true == iter.hasNext()){
    		String strSearchUser = iter.next();
    		GamePlayer player = this.searchGamePlayerByUserId(strSearchUser);
    		if(null == player){
    			logger.error("RoomGame::calculateGameRate() player null Error");
        		continue;
    		}
    		
    		GameResult gameRoundResult = GameResult.EXIT;
    		boolean bExitGame = player.isAutoExitBySelf();
    		GameResultCode resultCode = player.getGameResult();
    		if(true == bExitGame){
    			player.addEscapeRound();
    			gameRoundResult = GameResult.EXIT;
    		}else if(GameResultCode.GRC_SUCCEED == resultCode){
    			player.addWinRound();
    			gameRoundResult = GameResult.WIN;
    		}else if(GameResultCode.GRC_FAIL == resultCode){
    			player.addFailRound();
    			gameRoundResult = GameResult.LOSE;
    		}
    		
    		GameInfo newGameInfo = new GameInfo(player.getGamePlayerId(), game.getGameTag(), gameRoundResult, new Date());
    		gameInfosList.add(newGameInfo);
    		
    	}
    	
    	//gameDataService.uploadGameInfo(gameInfosList);
    	bCalculateResult = true;
    	return bCalculateResult;
    }
    
    private boolean awardPlayerGoldForGameEnd(){
    	Map<Integer, Integer> playerGoldMap = new HashMap<Integer, Integer>();
    	playerGoldMap.put(1, 125);
    	playerGoldMap.put(2, 50);
    	playerGoldMap.put(3, 25);
    	playerGoldMap.put(4, 0);
    	
    	Set<String> playersSet = this.getAllUserSet();
    	Iterator<String> iter = playersSet.iterator();
    	while(true == iter.hasNext()){
    		String strPlayerUser = iter.next();
    		GamePlayer player = this.searchGamePlayerByUserId(strPlayerUser);
    		if(null == player){
        		logger.error("TeamGroup::awardPlayerGoldForGameEnd player null Error");
        		continue;
        	}
    		
    		int nRankNo = player.getOutAllPorkerRank();
    		int nAwardGold = playerGoldMap.get(nRankNo);
    		player.addPlayerGold(nAwardGold);
    	}
    	
    	boolean bAddResult = true;
    	return bAddResult;
    }
    
    
    public boolean setNewMajorFaceValueOfGameEnd(){
    	Team winnerTeam = this.getTeamOfWinner();
		if(null == winnerTeam){
    		logger.error("RoomGame::setNewMajorFaceValueOfGameEnd winnerTeam null Error");
    		return false;
    	}
		
		Team failerTeam = this.getTeamOfFailGame();
		if(null == failerTeam){
    		logger.error("RoomGame::setNewMajorFaceValueOfGameEnd failerTeam null Error");
    		return false;
    	}
		
		this.reSetNewMajorFaceValue(winnerTeam.getMajorFace(), failerTeam.getMajorFace(), winnerTeam);
		return true;
    }
    
    public void setMajorSceneForRoomInit(String strUserId){
    	Team majorTeam = this.getTeamByUserId(strUserId);
    	if(null == majorTeam){
    		logger.error("RoomGame::setNewMajorFaceValueOfGameEnd majorTeam null Error");
    		return ;
    	}
    	
    	this.majorSceneTeam = majorTeam;
    }
    
    public boolean isBelongToMajorScene(String strUserId){
    	if(null == this.majorSceneTeam){
    		logger.error("TeamGroup::isBelongToMajorScene majorTeam null Error");
    		return false;
    	}
    	
    	GamePlayer player = majorSceneTeam.searchPlayer(strUserId);
    	if(null == player){
    		return false;
    	}
    	
    	return true;
    }
    
}


