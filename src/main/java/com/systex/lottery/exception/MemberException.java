package com.systex.lottery.exception;

public class MemberException extends Exception {
	public static final String PARAMETER_INVALID="json 傳入參數不合法";
	public static final String PARAMETER_NULL="傳入參數不得為空";
	public static final String USERNAME_INVALID="帳號不應包含空格，且長度介於5~30";
	public static final String USERNAME_EXISTED="帳號已存在";
	public static final String USERNAME_NOT_EXISTED="帳號不存在";
	public static final String PASSWORD_INVALID="密碼不應包含空格，且長度介於5~30";
	public static final String PASSWORD_ERROR="密碼錯誤";
	public static final String CONFIRMPASSWORD_INVALID="兩次輸入密碼不一致";
	
	public MemberException(){}
	public MemberException(String msg){
		super(msg);
	}
}
