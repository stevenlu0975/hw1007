package com.systex.lottery.model;

import java.io.Serializable;

public class Result<T> implements Serializable{
    private Integer code; // 狀態碼
    private String message; //錯誤訊息
    private T data; // 數據
    public Result() {}
    public static <T> Result<T> success(){
        Result<T> result = new Result<T>();
        result.code=1;
        return result;
    }  
    public static <T> Result<T> success(T obj){
        Result<T> result = new Result<>();
        result.code = 1;
        result.data = obj;
        return result;
    }
    public static <T>Result<T> error(String message){
        Result<T> result = new Result<>();
        result.code = 0;
        result.message = message;
        return result;
    }
    
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
    
}
