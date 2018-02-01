package com.szw.easyquotation.bean;

public enum ExceptionEnum {
	ZMQ_CLOSE_ERROR(-1, "zmq关闭异常"), UNKNOW_ERROR(9999, "未知错误"),;
	private Integer code;

	private String msg;

	ExceptionEnum(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public Integer getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}
}
