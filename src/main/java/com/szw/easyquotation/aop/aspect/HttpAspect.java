package com.szw.easyquotation.aop.aspect;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Aspect
@Component
public class HttpAspect {
	private final Logger log = Logger.getLogger(getClass());

	@Pointcut("execution(public * com.szw.easyquotation.*.*(..))")
	public void log() {

	}

	@Before("log()")
	public void doBefore(JoinPoint joinPoint) {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();

		// url
		log.info("url=" + request.getRequestURL());
		// method
		log.info("method=" + request.getMethod());
		// ip
		log.info("id=" + request.getRemoteAddr());
		// class_method
		log.info("class_method=" + joinPoint.getSignature().getDeclaringTypeName() + "," + joinPoint.getSignature().getName());
		// args[]
		log.info("args=" + joinPoint.getArgs());
	}

	@Around("log()")
	public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		return proceedingJoinPoint.proceed();
	}

	@AfterReturning(pointcut = "log()", returning = "object")// 打印输出结果
	public void doAfterReturing(Object object) {
		log.info("response=" + object.toString());
	}
}
