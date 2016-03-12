package com.lbwan.game.room.roomGameTimer;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.lbwan.game.proto.WhippedEgg.TaskType;
import com.lbwan.game.room.roomGame.RoomGame;
import com.lbwan.game.spring.SpringUtils;
import com.lbwan.game.utils.GDPropertiesUtils;
import com.lbwan.game.utils.TaskManager;

public class RoomGameTimer {
	// 游戏切换到开始状态需要的定时器
	private final int gameStartTimerSec  = GDPropertiesUtils.getPropertyAsInteger(GDPropertiesUtils.GAME_START_TIMER);            
	
	// 客户端表现需要的定时器
    private final int clientPerformanceTimerSec  = GDPropertiesUtils.getPropertyAsInteger(GDPropertiesUtils.CLIENT_OPERATION_TIMER);      
    
    // 倒计时的秒数
    private int userOperationTimeSec = GDPropertiesUtils.getPropertyAsInteger(GDPropertiesUtils.USEROPERATION_TIMER);

    // 进贡定时器
    private int payTributeTimeSec = GDPropertiesUtils.getPropertyAsInteger(GDPropertiesUtils.PAY_TRIBUTE_TIMER);
    
    // 退贡定时器
    private int backTributeTimeSec = GDPropertiesUtils.getPropertyAsInteger(GDPropertiesUtils.BACK_TRIBUTE_TIMER);
    
    private Logger logger = Logger.getLogger(getClass());
    private RoomGame roomGame = null;
    private ScheduledThreadPoolExecutor scheduledExecutor = null;
    private ScheduledFuture<?> scheduledFuture = null;
    
    private Date currentDate = null;
    private TaskType currentTask = TaskType.TASK_NULL;
    
    @Autowired
    private TaskManager taskMgr = (TaskManager) SpringUtils.getBeanByName("taskManager");
   
    
    public RoomGameTimer(RoomGame currRoomGame){
        this.roomGame = currRoomGame;
    }
    
    public void startGameTimer(){
        
    }
    
    public void startGameBeginTimer(){
    	
        this.cancelTimer();  
        if(null == this.roomGame){
            logger.error("RoomGameTimer::startGameBeginTimer this.gameRound null");
            return ;
        }
        
        scheduledExecutor = taskMgr.getScheduledThread();
        scheduledFuture = scheduledExecutor.schedule(new GameBeginTask(this.roomGame), gameStartTimerSec, TimeUnit.SECONDS);
    }
    
    
    
    public void startUserOperationTimer(){
    
      this.cancelTimer();
      if(null == this.roomGame){
          logger.error("GameRoundTimer::startUserOperationTimer this.gameRound null");
          return ;
      }
      
      currentDate = new Date();
      currentTask = TaskType.TASK_USER_OPERATION;
      
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String dateStr = sdf.format(currentDate);
      System.out.println("控制权反转" + dateStr);
      
      scheduledExecutor = taskMgr.getScheduledThread();
      scheduledFuture = scheduledExecutor.schedule(new UserOperationTask(this.roomGame), userOperationTimeSec, TimeUnit.SECONDS);
      
    }
    
    public void startClientPerformanceTimer(){
    	
        this.cancelTimer();
        if(null == this.roomGame){
            logger.error("GameRoundTimer::startClientPerformanceTimer this.gameRound null");
            return ;
        }
        
        scheduledExecutor = taskMgr.getScheduledThread();
        scheduledFuture = scheduledExecutor.schedule(new ClientPerformanceTask(this.roomGame), clientPerformanceTimerSec, TimeUnit.SECONDS);
        
    }
    
    
    public void startPayTributeTimer(){
    	this.cancelTimer();
        if(null == this.roomGame){
            logger.error("GameRoundTimer::startPayTributeTimer this.gameRound null");
            return ;
        }
        
        currentDate = new Date();
        currentTask = TaskType.TASK_PAY_TRIBUTE;
        
        scheduledExecutor = taskMgr.getScheduledThread();
        scheduledFuture = scheduledExecutor.schedule(new PayTributeTask(this.roomGame), payTributeTimeSec, TimeUnit.SECONDS);
    }
    
    public void startBackTributeTimer(){
    	this.cancelTimer();
        if(null == this.roomGame){
            logger.error("GameRoundTimer::startBackTributeTimer this.gameRound null");
            return ;
        }
        
        currentDate = new Date();
        currentTask = TaskType.TASK_BACK_TRIBUTE;
        
        scheduledExecutor = taskMgr.getScheduledThread();
        scheduledFuture = scheduledExecutor.schedule(new BackTributeTask(this.roomGame), backTributeTimeSec, TimeUnit.SECONDS);
    }

    
    public void cancelTimer(){
        currentDate = null;
        currentTask = TaskType.TASK_NULL;
        
        if((null != scheduledExecutor) && (null != scheduledFuture)){
            boolean bStopRunningTask = false;
            scheduledFuture.cancel(bStopRunningTask);
            scheduledFuture = null;
            scheduledExecutor = null;
        }
    }
    
    // 当为用户操作任务时, 取剩余的时间
    public int getUserOperationSec(){ 
    	if(TaskType.TASK_USER_OPERATION != currentTask){
    		return 0;
        }
    	 
    	int nUseSeconds = this.helpGetSecondsOfOperation();
    	int nSecsDiff = GDPropertiesUtils.getPropertyAsInteger(GDPropertiesUtils.SERVER_CLIENTSECS, 2);
    	int nNeedSeconds = userOperationTimeSec - (nUseSeconds + nSecsDiff);
    	if(nNeedSeconds <= 0){
    		nNeedSeconds = 0;
    	}
    	
    	return nNeedSeconds;
    }
    
   
    public int getPayTributeSec(){
    	if(TaskType.TASK_PAY_TRIBUTE != currentTask){
    		return 0;
        }
    	 
    	int nUseSeconds = this.helpGetSecondsOfOperation();
    	int nSecsDiff = GDPropertiesUtils.getPropertyAsInteger(GDPropertiesUtils.SERVER_CLIENTSECS, 2);
    	int nNeedSeconds = payTributeTimeSec - (nUseSeconds + nSecsDiff);
    	if(nNeedSeconds <= 0){
    		nNeedSeconds = 0;
    	}
    	
    	return nNeedSeconds;
    }

    public int getBackTributeSec(){
    	if(TaskType.TASK_BACK_TRIBUTE != currentTask){
    		return 0;
        }
    	 
    	int nUseSeconds = this.helpGetSecondsOfOperation();
    	int nSecsDiff = GDPropertiesUtils.getPropertyAsInteger(GDPropertiesUtils.SERVER_CLIENTSECS, 2);
    	int nNeedSeconds = backTributeTimeSec - (nUseSeconds + nSecsDiff);
    	if(nNeedSeconds <= 0){
    		nNeedSeconds = 0;
    	}
    	
    	return nNeedSeconds;
    }
    
    
    private int helpGetSecondsOfOperation(){
    	if(null == currentDate){
            return 0;
        }
        
        Date nowDate = new Date();
        int nSecs = (int)( (nowDate.getTime() - currentDate.getTime())/1000);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String recordStr = sdf.format(currentDate);
        String dateStr = sdf.format(nowDate);
        System.out.println("断线重连  上次时间 :" + recordStr + "  现在时间:" + dateStr);
        
        return nSecs;
    }
    
    
    
    public void finalize(){
    	this.cancelTimer();
    }
}
