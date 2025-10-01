package com.mcs.inventory.aspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

	private static final Log logger = LogFactory.getLog(LoggingAspect.class);

	// @Before("execution(* com.tata.bookService.*.*.*(..)) ")
	/*
	 * *: Matches any return type. com.tata.bookService.*: Matches any class in the
	 * com.tata.bookService package. *: Matches any class name. *: Matches any
	 * method name. *(..): Matches any number of arguments of any type.
	 * 
	 */
	@Before("execution(* com.mcs.inventory.*.*.*(..)) ")
	public void logBeforeMethodExecution(JoinPoint joinPoint) {
		Signature signature = joinPoint.getSignature();
		String className = signature.getDeclaringType().getSimpleName();
		String methodName = signature.getName();
		String methodArgs = Arrays.toString(joinPoint.getArgs());
		logger.debug(className + " :: " + methodName + " :: start :: with args - " + methodArgs);
	}

	@After("execution(* com.mcs.inventory.*.*.*(..)) ")
	public void logAfterMethodExecution(JoinPoint joinPoint) {
		Signature signature = joinPoint.getSignature();
		String className = signature.getDeclaringType().getSimpleName();
		String methodName = signature.getName();
		logger.debug(className + " :: " + methodName + " :: ends ");
	}

	@AfterThrowing(pointcut = "execution(* com.mcs.inventory.*.*.*(..))", throwing = "ex")
	public void logExceptionInCatchBlock(JoinPoint joinPoint, Exception ex) {
		Signature signature = joinPoint.getSignature();
		String className = signature.getDeclaringType().getSimpleName();
		String methodName = signature.getName();
		logger.error("Exception occurred and caught in method  " + className + " :: " + methodName + " message - "
				+ ex.getMessage());
	}

	@AfterReturning(pointcut = "execution(* com.mcs.inventory.*.*.*(..))", returning = "returnValue")
	public void logAfterReturnedValue(JoinPoint joinPoint, Object returnValue) {
		String methodName = joinPoint.getSignature().getName();
		String className = getRepositoryInterfaceName(joinPoint.getTarget());

		logger.trace("Returned from " + className + "." + methodName + ": " + returnValue);
	}

	private String getRepositoryInterfaceName(Object targetObject) {
		Class<?>[] interfaces = targetObject.getClass().getInterfaces();

		for (Class<?> iface : interfaces) {
			if (hasRepositoryAnnotation(iface)) {
				return iface.getSimpleName();
			}
		}

		// Check if it's a proxy and get the interfaces from the proxy
		if (Proxy.isProxyClass(targetObject.getClass())) {
			Class<?>[] proxyInterfaces = targetObject.getClass().getInterfaces();
			for (Class<?> iface : proxyInterfaces) {
				if (hasRepositoryAnnotation(iface)) {
					return iface.getSimpleName();
				}
			}
		}

		return targetObject.getClass().getSimpleName();
	}

	private boolean hasRepositoryAnnotation(Class<?> clazz) {
		Annotation[] annotations = clazz.getDeclaredAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation instanceof Repository) {
				return true;
			}
		}
		return false;
	}
}
