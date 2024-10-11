package com.systex.lottery.exception;

public class LotteryException extends Exception{
	public static final String SETS_INVALID="組數不得為空";
	public static final String EXCLUDE_STRING_INVALID="排除號碼只能填介於1~49的整數";
	public static final String EXCLUDE_STRING_OUT_OF_BOUND="排出號碼不應超過43個";
	
	public LotteryException() {}
	public LotteryException(String msg) {
		super(msg);
	}
}
