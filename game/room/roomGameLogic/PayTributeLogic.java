package com.lbwan.game.room.roomGameLogic;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.lbwan.game.payTributeChecker.PayTributeCheckerHolder;
import com.lbwan.game.payTributeHandler.PayTributeHandlerHolder;
import com.lbwan.game.porker.PorkerMgr;
import com.lbwan.game.porkerEnumSet.FaceValueEnum;
import com.lbwan.game.porkerEnumSet.PorkerValueEnum;
import com.lbwan.game.proto.CmdProtocol.CmdType;
import com.lbwan.game.proto.WhippedEgg.GameResultCode;
import com.lbwan.game.proto.WhippedEgg.SBackTributeResponse;
import com.lbwan.game.proto.WhippedEgg.SBackTributeToAllUser;
import com.lbwan.game.proto.WhippedEgg.SPayTributeResult;
import com.lbwan.game.proto.WhippedEgg.SPayTributeToAllUser;
import com.lbwan.game.proto.WhippedEgg.SServerNofityBackTribute;
import com.lbwan.game.room.gameStatus.GameStatusChecker;
import com.lbwan.game.room.gameStatus.GameStatusEnum;
import com.lbwan.game.room.gameTeam.TeamGroup;
import com.lbwan.game.room.payTributeData.PayTributeData;
import com.lbwan.game.room.roomGame.GamePlayer;
import com.lbwan.game.room.roomGame.RoomGame;
import com.lbwan.game.room.roomGameTimer.RoomGameTimer;
import com.lbwan.game.spring.SpringUtils;
import com.lbwan.game.utils.DataSendUtils;
import com.lbwan.game.utils.GDPropertiesUtils;

public class PayTributeLogic {
	
	protected Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	protected PayTributeCheckerHolder payTributeCheckerHolder = (PayTributeCheckerHolder) SpringUtils.getBeanByName("payTributeCheckerHolder");
	
	@Autowired
	protected PayTributeHandlerHolder payTributeHandlerHolder = (PayTributeHandlerHolder) SpringUtils.getBeanByName("payTributeHandlerHolder");
	
	@Autowired
	protected PorkerMgr porkerManager = (PorkerMgr) SpringUtils.getBeanByName("porkerMgr");
	
	
	public PayTributeLogic(){
		
	}
	
	// 进贡
    public boolean payTributePorkerValue(RoomGame currentRoom, String strPayTributeUserId, int nPayTributePorkerValue){
    	//1. 增加状态的判断 即当前时间是否为进贡状态
    	boolean bPayTributeResult = false;
    	if(null == currentRoom){
    		logger.error("PayTributeLogic::payTributePorkerValue currentRoom Null Error");
    		return bPayTributeResult;
    	}
    	
    	GameStatusChecker statusChecker = currentRoom.getGameStatusChecker();
    	if(null == statusChecker){
    		logger.error("PayTributeLogic::payTributePorkerValue statusChecker Null Error");
    		return bPayTributeResult;
    	}
    	
    	PayTributeData payTributeData = currentRoom.getPayTributeData();
    	if(null == payTributeData){
    		logger.error("PayTributeLogic::payTributePorkerValue payTributeData Null Error");
    		return bPayTributeResult;
    	}
    	
    	TeamGroup teamGroup = currentRoom.getTeamGroup();
    	if(null == teamGroup){
    		logger.error("PayTributeLogic::payTributePorkerValue teamGroup Null Error");
    		return bPayTributeResult;
    	}
    	
    	RoomGameTimer roomGameTimer = currentRoom.getRoomGameTimer();
    	if(null == roomGameTimer){
    		logger.error("PayTributeLogic::payTributePorkerValue roomGameTimer Null Error");
    		return bPayTributeResult;
    	}
    	
    	GameStatusEnum status = statusChecker.getGameStatus();
    	if(GameStatusEnum.GameStatus_PayTribute != status){
    		return bPayTributeResult;
    	}

    	// 2. 是否为进贡人   不是返回
    	boolean bIsPayTributeUser = payTributeData.isPayTributerUser(strPayTributeUserId);
    	if(false == bIsPayTributeUser){
    		return bPayTributeResult;
    	}

    	// 3. 是否已经进贡过了.
    	boolean bIsPayTributed = payTributeData.isPayTributed(strPayTributeUserId);
    	if(true == bIsPayTributed){
    		return bPayTributeResult;
    	}

    	// 4. 进贡的牌值 是否符合条件
    	int nSumbitTributeFaceValue = PorkerValueEnum.getFaceValueByPorkerValueOf(nPayTributePorkerValue);
    	int nTributeFaceValue = payTributeData.getPayTributePorkerFaceValue(strPayTributeUserId);
    	if(nSumbitTributeFaceValue != nTributeFaceValue){
    		return bPayTributeResult;
    	}

    	GamePlayer payTributedPlayer = teamGroup.searchGamePlayerByUserId(strPayTributeUserId);
    	if(null == payTributedPlayer){
    		logger.error("RoomGame::payTributePorkerValue payTributedPlayer Null Server Error UserId: " + strPayTributeUserId);
    		return bPayTributeResult;
    	}

    	// 取出接收者  和 牌值
    	String strReceiveUser = payTributeData.getReceivePayTributeUser(strPayTributeUserId);
    	GamePlayer receiveTributePlayer = teamGroup.searchGamePlayerByUserId(strReceiveUser);
    	if(null == receiveTributePlayer){
    		logger.error("RoomGame::payTributePorkerValue receiveTributePlayer Null Server Error UserId: " + strReceiveUser);
    		return bPayTributeResult;
    	}


    	// 5. 执行进贡流程
    	payTributeData.payTributed(strPayTributeUserId);

    	// 发消息通知当前的客户端  进贡成功
    	SPayTributeResult.Builder tributeResult = SPayTributeResult.newBuilder();
    	tributeResult.setPayTributeResult(GameResultCode.GRC_SUCCEED);
    	DataSendUtils.sendData(strPayTributeUserId, CmdType.CMD_PAY_TRIBUTE_RESULT_VALUE, tributeResult.build().toByteArray());

    	// 从进贡方的手上 删除该手牌
    	payTributedPlayer.delPorkerValueFromHandPorker(nPayTributePorkerValue);
    	// 从接受方的手上增加该手牌
    	receiveTributePlayer.addPoerkValueToHandPorker(nPayTributePorkerValue);


    	//进贡过程通知所有玩家
    	SPayTributeToAllUser.Builder tributeToAll = SPayTributeToAllUser.newBuilder();
    	tributeToAll.setStrPayTributeUserId(strPayTributeUserId);
    	tributeToAll.setNTributePorkerValue(nPayTributePorkerValue);
    	tributeToAll.setStrReceivePayTributeUserId(strReceiveUser);
    	teamGroup.nofityAllOnLineUser(CmdType.CMD_PAY_TRIBUTE_NOTIFY_ALL_VALUE, tributeToAll.build().toByteArray());
    	//System.out.println("进贡完成------>" + strPayTributeUserId + "  向 "+ strReceiveUser + "进贡" + PorkerValueEnum.getColorByPorkValue(nPayTributePorkerValue) + PorkerValueEnum.getFaceValueByPorkerValueOf(nPayTributePorkerValue));

    	// 通知退贡
    	int nBackTributeTimeSec = GDPropertiesUtils.getPropertyAsInteger(GDPropertiesUtils.BACK_TRIBUTE_TIMER);
    	int nSecsDiff = GDPropertiesUtils.getPropertyAsInteger(GDPropertiesUtils.SERVER_CLIENTSECS, 2);

    	SServerNofityBackTribute.Builder backTribute = SServerNofityBackTribute.newBuilder();
    	backTribute.setStrBackTributerId(strPayTributeUserId);
    	backTribute.setNBackTributeNeedSecs(nBackTributeTimeSec-nSecsDiff);
    	DataSendUtils.sendData(strReceiveUser, CmdType.CMD_SERVER_NOTIFY_BACK_TRIBUTE_VALUE, backTribute.build().toByteArray());

    	
    	// 全部进贡后再还贡
    	// 判断是否都已经进贡完成 是的话 将定时器停掉
    	boolean bCompleteAll = payTributeData.isCompletePayTributeActivity();
    	if(true == bCompleteAll){
    		// 启动还贡的定时器
    		roomGameTimer.startBackTributeTimer();
    	}


    	bPayTributeResult = true;
    	return bPayTributeResult;
    }
    
    // 还贡的过程
    public boolean backTributePorkerValue(RoomGame currentRoom, String strBackTributeUserId, int nBackTributePorkerValue){
    	boolean bBackTributeResult = false;
    	if(null == currentRoom){
    		logger.error("PayTributeLogic::backTributePorkerValue currentRoom Null Error");
    		return bBackTributeResult;
    	}
    	
    	GameStatusChecker statusChecker = currentRoom.getGameStatusChecker();
    	if(null == statusChecker){
    		logger.error("PayTributeLogic::backTributePorkerValue statusChecker Null Error");
    		return bBackTributeResult;
    	}
    	
    	PayTributeData payTributeData = currentRoom.getPayTributeData();
    	if(null == payTributeData){
    		logger.error("PayTributeLogic::backTributePorkerValue payTributeData Null Error");
    		return bBackTributeResult;
    	}
    	
    	TeamGroup teamGroup = currentRoom.getTeamGroup();
    	if(null == teamGroup){
    		logger.error("PayTributeLogic::backTributePorkerValue teamGroup Null Error");
    		return bBackTributeResult;
    	}
    	
    	RoomGameTimer roomGameTimer = currentRoom.getRoomGameTimer();
    	if(null == roomGameTimer){
    		logger.error("PayTributeLogic::backTributePorkerValue roomGameTimer Null Error");
    		return bBackTributeResult;
    	}
    	
    	GameStatusEnum status = statusChecker.getGameStatus();
    	if(GameStatusEnum.GameStatus_PayTribute != status){
    		return bBackTributeResult;
    	}
    	
    	
    	// 2. 是否为退贡人   不是返回
    	boolean bIsBackTributeUser = payTributeData.isBackTributerUser(strBackTributeUserId);
    	if(false == bIsBackTributeUser){
    		return bBackTributeResult;
    	}
    	
    	// 3. 是否已经退贡过了.
    	boolean bIsBackTributed = payTributeData.isBackTributed(strBackTributeUserId);
    	if(true == bIsBackTributed){
    		return bBackTributeResult;
    	}
    	
    	// 4. 退贡的牌值 是否符合条件
    	int nBackTributeMaxFaceValue = GDPropertiesUtils.getPropertyAsInteger(GDPropertiesUtils.BACK_TRIBUTE_MAX_VALUE);
    	boolean bCompareResult = this.isSmallerThanMaxFaceValue(nBackTributePorkerValue, nBackTributeMaxFaceValue, teamGroup.getCurrentMajorFaceValue());
    	if(false == bCompareResult){
    		return bBackTributeResult;
    	}
    	
    	// 5. 符合条件的前提下
    	GamePlayer backTributedPlayer = teamGroup.searchGamePlayerByUserId(strBackTributeUserId);
    	if(null == backTributedPlayer){
    		logger.error("RoomGame::payTributePorkerValue payTributedPlayer Null Server Error UserId: " + strBackTributeUserId);
    		return bBackTributeResult;
    	}
    	
    	// 取出接收者  和 牌值
    	String strPayTributeUser = payTributeData.getPayTributeUserByBackTributer(strBackTributeUserId);
    	GamePlayer payTributePlayer = teamGroup.searchGamePlayerByUserId(strPayTributeUser);
    	if(null == payTributePlayer){
    		logger.error("RoomGame::payTributePorkerValue receiveTributePlayer Null Server Error UserId: " + strPayTributeUser);
    		return bBackTributeResult;
    	}
    	
    	// 执行退贡流程
    	payTributeData.backTributeToPayPlayer(strBackTributeUserId);
    	
    	// 发消息通知当前的客户端  进贡成功
    	SBackTributeResponse.Builder tributeResult = SBackTributeResponse.newBuilder();
		tributeResult.setBackTributeResult(GameResultCode.GRC_SUCCEED);
		DataSendUtils.sendData(strBackTributeUserId, CmdType.CMD_BACK_TRIBUTE_RESPONSE_VALUE, tributeResult.build().toByteArray());
		
    	// 两个玩家牌的变化
    	backTributedPlayer.delPorkerValueFromHandPorker(nBackTributePorkerValue);
    	payTributePlayer.addPoerkValueToHandPorker(nBackTributePorkerValue);
    	
    	// 退贡的过程 发消息到客户端
    	SBackTributeToAllUser.Builder backTributeToAll = SBackTributeToAllUser.newBuilder();
    	backTributeToAll.setStrBackTributeUserId(strBackTributeUserId);
    	backTributeToAll.setNBackTributePorkerValue(nBackTributePorkerValue);
    	backTributeToAll.setStrReceiveBackTributeUserId(strPayTributeUser);
    	teamGroup.nofityAllOnLineUser(CmdType.CMD_BACK_TRIBUTE_NOTIFY_ALL_VALUE, backTributeToAll.build().toByteArray());
    	//System.out.println("退贡完成------>" + strBackTributeUserId + "  向 "+ strPayTributeUser + "退贡" + PorkerValueEnum.getColorByPorkValue(nBackTributePorkerValue) + PorkerValueEnum.getFaceValueByPorkerValueOf(nBackTributePorkerValue));
    	
    	// 空操作
    	// backTributedPlayer.setTributeToNullOperation();
    	
    	
    	boolean bCompleteResult = payTributeData.isCompleteAllTributeActivity();
    	if(true == bCompleteResult){
    		
    		// 定时器禁用
    		roomGameTimer.cancelTimer();
    		
    		// 选取起牌者
    		String strNewControlUser = payTributeData.getNewGameFristPlayer(); 
    		
    		// 清除进贡数据
    		payTributeData.clearAllData();
    		
    		// 开始游戏
    		payTributeHandlerHolder.startGameByEndTributeCallBack(currentRoom, strNewControlUser);
    	}
    	
    	bBackTributeResult = true;
    	return bBackTributeResult;
    }
    
    
    private boolean isSmallerThanMaxFaceValue(int nSumbitPorkerValue, int nBackTributeMaxFaceValue, int nMajorFaceValue){
    	// 如果主牌等于最大的牌 则牌的类型+1
    	if(nMajorFaceValue == nBackTributeMaxFaceValue){
    		nBackTributeMaxFaceValue = FaceValueEnum.getNextCardType(nBackTributeMaxFaceValue);
    	}
    	
    	boolean bSmallerResult = false;
    	int nSumbitFaceValue = PorkerValueEnum.getFaceValueByPorkerValueOf(nSumbitPorkerValue);
    	// 如果是大小王  或者 主牌 则返回错误
    	boolean bIsBigKing = porkerManager.isBelongToBigKing(nSumbitPorkerValue);
    	boolean bIsSmallKing = porkerManager.isBelongToSmallKing(nSumbitPorkerValue);
    	boolean bIsMajorFaceValue = (nSumbitFaceValue == nMajorFaceValue);
    	if((true == bIsBigKing) || (true == bIsSmallKing) || (true == bIsMajorFaceValue)){
    		return bSmallerResult;
    	}
    	
    	int nCompareResult = FaceValueEnum.compareTwoFaceValue(nSumbitFaceValue, nBackTributeMaxFaceValue);
    	if(FaceValueEnum.Less_Result != nCompareResult){
    		return bSmallerResult;
    	}
    	
    	bSmallerResult = true;
    	return bSmallerResult;
    }
}



