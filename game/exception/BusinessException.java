package com.lbwan.game.exception;

public class BusinessException  extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2657680776466297500L;
	
	private int errorCode = 0;
	
	public BusinessException(int errorCode){
		this.errorCode = errorCode;
	}
	
	public BusinessException(Throwable e){
		super(e);
	}
	
	public BusinessException(String message) {
        super(message);
    }
	
	public BusinessException(String message, Throwable e) {
	    super(message, e);
	}
	
	/**
	 * 是否是Protobuf协议对应的错误
	 * @return
	 */
	public boolean isProtobufErrorResult(){
		return errorCode > 0;
	}
}
