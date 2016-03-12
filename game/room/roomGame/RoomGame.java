package com.lbwan.game.room.roomGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.lbwan.game.cardTypeSearch.SearcherManager;
import com.lbwan.game.payTributeChecker.PayTributeCheckerHolder;
import com.lbwan.game.payTributeHandler.PayTributeHandlerHolder;
import com.lbwan.game.porker.PorkerMgr;
import com.lbwan.game.porkerComparer.GameComparer;
import com.lbwan.game.proto.WhippedEgg.LostConnectionStatus;
import com.lbwan.game.proto.WhippedEgg.SEnterGameSuccess;
import com.lbwan.game.room.GameBase;
import com.lbwan.game.room.Player;
import com.lbwan.game.room.RoomManager;
import com.lbwan.game.room.gameStatus.GameStatusChecker;
import com.lbwan.game.room.gameStatus.GameStatusEnum;
import com.lbwan.game.room.gameTeam.TeamGroup;
import com.lbwan.game.room.payTributeData.PayTributeData;
import com.lbwan.game.room.roomGameLogic.GameEndLogic;
import com.lbwan.game.room.roomGameLogic.HostingLogic;
import com.lbwan.game.room.roomGameLogic.NotifyClientLogic;
import com.lbwan.game.room.roomGameLogic.OperationTag;
import com.lbwan.game.room.roomGameLogic.PayTributeLogic;
import com.lbwan.game.room.roomGameLogic.PorkerInitLogic;
import com.lbwan.game.room.roomGameLogic.StartGameLogic;
import com.lbwan.game.room.roomGameLogic.SumbitPorkerLogic;
import com.lbwan.game.room.roomGameTimer.RoomGameTimer;
import com.lbwan.game.spring.SpringUtils;
import com.lbwan.game.userLoginDateDao.AllLoginUserService;
import com.lbwan.game.utils.DataSendUtils;
import com.lbwan.game.utils.GDPropertiesUtils;
import com.lbwan.game.utils.PropertiesUtils;

public class RoomGame extends GameBase {

	@Autowired
	protected PayTributeCheckerHolder payTributeCheckerHolder = (PayTributeCheckerHolder) SpringUtils
			.getBeanByName("payTributeCheckerHolder");

	@Autowired
	protected PayTributeHandlerHolder payTributeHandlerHolder = (PayTributeHandlerHolder) SpringUtils
			.getBeanByName("payTributeHandlerHolder");

	@Autowired
	protected PorkerMgr porkerManager = (PorkerMgr) SpringUtils
			.getBeanByName("porkerMgr");

	@Autowired
	protected SearcherManager searcherMgr = (SearcherManager) SpringUtils
			.getBeanByName("searcherManager");

	@Autowired
	protected AllLoginUserService allUserService = (AllLoginUserService) SpringUtils
			.getBeanByName("allLoginUserService");

	private String gameTag = PropertiesUtils
			.getPropertyAsString(PropertiesUtils.ZK_GAME_TAG);
	
	private GameComparer roomGameComparer = null;

	private TeamGroup teamGroup = null;

	private RoomGameTimer roomGameTimer = null;

	private NotifyClientLogic nofityClientLogic = null;

	private GameControlUser controlUser = null;

	private GameStatusChecker statusChecker = null;

	private PayTributeData payTributeData = null;

	private OperationTag canOperationTag = null;

	private PayTributeLogic payTributeLogic = null;

	private SumbitPorkerLogic sumbitPorkerLogic = null;

	private HostingLogic hostingClientLogic = null;

	private StartGameLogic startGameLogic = null;

	private GameEndLogic gameEndLogic = null;

	public RoomGame() {

		roomGameComparer = new GameComparer(this);
		if (null == roomGameComparer) {
			logger.error("RoomGame::RoomGame roomGameComparer Null Error");
		}

		teamGroup = new TeamGroup(this);
		if (null == teamGroup) {
			logger.error("RoomGame::RoomGame teamGroup Null Error");
		}

		roomGameTimer = new RoomGameTimer(this);
		if (null == roomGameTimer) {
			logger.error("RoomGame::RoomGame roomGameTimer Null Error");
		}

		nofityClientLogic = new NotifyClientLogic(this);
		if (null == nofityClientLogic) {
			logger.error("RoomGame::RoomGame nofityClientLogic Null Error");
		}

		controlUser = new GameControlUser(this);
		if (null == controlUser) {
			logger.error("RoomGame::RoomGame controlUser Null Error");
		}

		statusChecker = new GameStatusChecker(this);
		if (null == statusChecker) {
			logger.error("RoomGame::RoomGame statusChecker Null Error");
		}

		payTributeData = new PayTributeData();
		if (null == payTributeData) {
			logger.error("RoomGame::RoomGame payTributeData Null Error");
		}

		canOperationTag = new OperationTag();
		if (null == canOperationTag) {
			logger.error("RoomGame::RoomGame canOperationTag Null Error");
		}

		payTributeLogic = new PayTributeLogic();
		if (null == payTributeLogic) {
			logger.error("RoomGame::RoomGame payTributeLogic Null Error");
		}

		sumbitPorkerLogic = new SumbitPorkerLogic(this);
		if (null == sumbitPorkerLogic) {
			logger.error("RoomGame::RoomGame sumbitPorkerLogic Null Error");
		}

		hostingClientLogic = new HostingLogic(this);
		if (null == hostingClientLogic) {
			logger.error("RoomGame::RoomGame hostingClientLogic Null Error");
		}

		startGameLogic = new StartGameLogic(this);
		if (null == startGameLogic) {
			logger.error("RoomGame::RoomGame startGameLogic Null Error");
		}

		gameEndLogic = new GameEndLogic(this);
		if (null == gameEndLogic) {
			logger.error("RoomGame::RoomGame gameEndLogic Null Error");
		}
	}

	@Override
	public boolean startGame(String token, List<Player> players) {

		// TODO Auto-generated method stub
		this.gameToken = token;
		boolean bStartResult = startGameLogic.startGamebyPlayerList(token, players);
		return bStartResult;
	}

	public void runGameStartAction() {
		startGameLogic.runGameStartAction();
	}

	@Override
	public void stopGame() {
		if (null != roomGameTimer) {
			roomGameTimer.cancelTimer();
		}
	}

	@Override
	public boolean checkPlayerIsExist(String strUserId) {
		GamePlayer player = teamGroup.searchGamePlayerByUserId(strUserId);
		if (null == player) {
			return false;
		}

		return true;
	}

	@Override
	public void sendToAllClient(int cmd, byte[] datas) {
		// TODO Auto-generated method stub
		teamGroup.nofityAllOnLineUser(cmd, datas);
	}
	
	public String getGameTag() {
		return this.gameTag;
	}

	public String getRoomToken(){
		return this.gameToken;
	}
	
	// 校验游戏状态
	@Override
	public boolean checkGameState() {
		GameStatusEnum status = statusChecker.getGameStatus();
		if (GameStatusEnum.GameStatus_End == status) {
			return false;
		}
		if (GameStatusEnum.GameStatus_Ready == status) {
			return false;
		}

		return true;
	}

	@Override
	public void deletePlayer(String strDeleteUser) {
		GamePlayer player = teamGroup.searchGamePlayerByUserId(strDeleteUser);
		if (null == player) {
			logger.error("RoomGame::deletePlayer player Null Error");
			return;
		}

		if (true == player.isAutoExitBySelf()) {
			return;
		}

		player.setPlayerIsAutoExitGame();
		return;
	}

	public GameComparer getGameComparer() {
		return roomGameComparer;
	}

	public TeamGroup getTeamGroup() {
		return this.teamGroup;
	}

	public RoomGameTimer getRoomGameTimer() {
		return this.roomGameTimer;
	}

	public NotifyClientLogic getNotifyClientLogic() {
		return this.nofityClientLogic;
	}

	public GameStatusChecker getGameStatusChecker() {
		return this.statusChecker;
	}

	public PayTributeData getPayTributeData() {
		return this.payTributeData;
	}

	public String getCurrentControlUser() {
		return controlUser.getControlUser();
	}

	public GameControlUser getGameControlUser() {
		return this.controlUser;
	}

	public OperationTag getOperationTag() {
		return this.canOperationTag;
	}

	public HostingLogic getHostingClientLogic() {
		return this.hostingClientLogic;
	}

	public GamePlayer searchGamePlayerByUserId(String strControlUser){
		return teamGroup.searchGamePlayerByUserId(strControlUser);
	}
	
	
	// 过牌
	public boolean sumbitNullPorkerToServer(String strUserId) {
		boolean bSumbitResult = sumbitPorkerLogic.sumbitNullPorkerToServer(strUserId);
		return bSumbitResult;
	}

	public void nofityAllOnLineUser(int nCmd, byte[] datas){
		teamGroup.nofityAllOnLineUser(nCmd, datas);
	}
	
	public int getCurrentMajorCard() {
		return teamGroup.getCurrentMajorFaceValue();
	}

	public int getOtherMajorCard() {
		return teamGroup.getOtherMajorFaceValue();
	}

	public boolean sumbitSomePorker(String strUserId,
			List<Integer> sumbitPorkerList) {
		boolean bSumbitResult = sumbitPorkerLogic.sumbitSomePorker(strUserId,
				sumbitPorkerList);
		return bSumbitResult;
	}

	public boolean comparePorkerAction(GamePlayer currentPlayer,
			List<Integer> sumbitPorkerList) {
		boolean bCompareResult = gameEndLogic.comparePorkerAction(
				currentPlayer, sumbitPorkerList);
		return bCompareResult;
	}

	public void gameRoundEndAction(GamePlayer currentPlayer,
			List<Integer> sumbitPorkerList) {
		gameEndLogic.gameRoundEndAction(currentPlayer, sumbitPorkerList);
	}

	public void hostingClientToServer(String strHostingUser) {
		hostingClientLogic.hostingClientToServer(strHostingUser);
	}

	public void cancelHostingToServer(String strCancelHostingUser) {
		hostingClientLogic.cancelHostingToServer(strCancelHostingUser);
	}

	// 进贡
	public boolean payTributePorkerValue(String strPayTributeUserId,
			int nPayTributePorkerValue) {
		// 1. 增加状态的判断 即当前时间是否为进贡状态
		boolean bPayTributeResult = payTributeLogic.payTributePorkerValue(this,
				strPayTributeUserId, nPayTributePorkerValue);
		return bPayTributeResult;
	}

	// 还贡的过程
	public boolean backTributePorkerValue(String strBackTributeUserId,
			int nBackTributePorkerValue) {
		boolean bBackTributeResult = payTributeLogic.backTributePorkerValue(
				this, strBackTributeUserId, nBackTributePorkerValue);
		return bBackTributeResult;
	}

	public int getPayTributeSeconds(String strUserId) {
		int nErrorSeconds = 0;
		if (null == roomGameTimer) {
			logger.error("RoomGame::getPayTributeSeconds roomGameTimer Null Server Error UserId: ");
			return nErrorSeconds;
		}

		return roomGameTimer.getPayTributeSec();
	}

	public int getBackTributeSeconds(String strUserId) {
		int nErrorSeconds = 0;
		if (null == roomGameTimer) {
			logger.error("RoomGame::getBackTributeSeconds roomGameTimer Null Server Error UserId: ");
			return nErrorSeconds;
		}

		return roomGameTimer.getBackTributeSec();
	}

	public String getLastMaxPorkerUser(){
		if(null == roomGameComparer) {
			logger.error("RoomGame::getLastMaxPorkerUser() roomGameComparer Null Error");
			return null;
		}
		
		String strMaxPorkerUser = roomGameComparer.getMaxPorkerPlayerId();
		return strMaxPorkerUser;
	}
	
	public List<Integer> getLastMaxPorkerValueList(){
		if(null == roomGameComparer) {
			logger.error("RoomGame::getLastMaxPorkerValueList() roomGameComparer Null Error");
			return null;
		}
		
		List<Integer> maxPorkerValueList = roomGameComparer.getMaxPorkerValueList();
		return maxPorkerValueList;
	}
	
	public SEnterGameSuccess.Builder getReConnectionInfo(String strPlayerId) {
		SEnterGameSuccess.Builder enterGame = nofityClientLogic.getReConnectionInfo(strPlayerId);
		if (null == strPlayerId) {
			logger.error("RoomGame::getReConnectionInfo strPlayerId Null Error");
			return null;
		}

		GameStatusEnum status = statusChecker.getGameStatus();
		if (GameStatusEnum.GameStatus_PayTribute != status) {
			enterGame.setLostConnection(LostConnectionStatus.LOST_CONNECTION_PLAYING_NOW);
			return enterGame;
		}

		// 是否是进贡者
		boolean bPayTributer = payTributeData.isPayTributeByUserId(strPlayerId);
		if (true == bPayTributer) {
			// 是否完成 未完成
			LostConnectionStatus connectionStatus = LostConnectionStatus.LOST_CONNECTION_WAIT_START;
			if (false == payTributeData.isPayTributed(strPlayerId)) {
				connectionStatus = LostConnectionStatus.LOST_CONNECTION_PAY_TRIBUTING;
			}

			enterGame.setLostConnection(connectionStatus);
			return enterGame;
		}

		// 是否是退贡者
		boolean bBackTributer = payTributeData.isBackTributeByUserId(strPlayerId);
		if (true == bBackTributer) {
			// 是否完成 未完成
			LostConnectionStatus connectionStatus = LostConnectionStatus.LOST_CONNECTION_WAIT_START;
			if (false == payTributeData.isBackTributed(strPlayerId)) {
				connectionStatus = LostConnectionStatus.LOST_CONNECTION_BACK_TRIBUTING;
			}

			enterGame.setLostConnection(connectionStatus);
			return enterGame;
		}

		enterGame.setLostConnection(LostConnectionStatus.LOST_CONNECTION_WAIT_START);
		return enterGame;
	}

	
}
