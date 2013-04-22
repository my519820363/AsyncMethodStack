package com.huyunfeng.asyncmethodstack;

import java.lang.reflect.Method;

import com.huyunfeng.asyncmethodstack.Fen.ThreadMode;

public class MethodPoint {
	private Method method;
	private Object host;
	private boolean isStatic;
	private ThreadMode mode = ThreadMode.None;
	
	public MethodPoint(Object methodHost, String methodName, Class<?>... paramsType) throws FenException {
		if (methodHost == null) {
			throw new FenException("MethodHost is Null!");
		}
		
		this.method = MethodUtil.getMethodFromObject(
				methodHost.getClass(), methodName, paramsType);
		this.mode = MethodUtil.getThreadModeFromMethod(
				method);
		this.host = methodHost;
		this.isStatic = false;
	}
	
	public MethodPoint(Class<?> methodHost, String methodName, Class<?>... paramsType) throws FenException{
		if (methodHost == null) {
			throw new FenException("MethodHost is Null!");
		}
		
		this.method = MethodUtil.getMethodFromObject(
				methodHost, methodName, paramsType);
		this.mode = MethodUtil.getThreadModeFromMethod(
				method);
		this.host = methodHost;
		this.isStatic = true;
	}
	
	public Object runMethod(Object... params) {
		return MethodUtil.invokeMethod(this.host,
				this.method, params);
	}

	public ThreadMode getMode() {
		return mode;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public Object getHost() {
		return host;
	}
}
