package com.szw.easyquotation.aop.advice;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.szw.easyquotation.bean.ExceptionEnum;
import com.szw.easyquotation.bean.Result;
import com.szw.easyquotation.util.ResultUtil;


@ControllerAdvice
public class ExceptionHandle {
	private final Logger log = Logger.getLogger(getClass());

	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public Result exceptionGet(Exception e) {

		log.error("【系统异常】", e);
		return ResultUtil.error(ExceptionEnum.UNKNOW_ERROR);
	}

}
