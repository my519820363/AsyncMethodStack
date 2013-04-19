package com.huyunfeng.asyncmethodstack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.huyunfeng.asyncmethodstack.Fen.FThreradMode;
import com.huyunfeng.asyncmethodstack.Fen.ThreadMode;

public class MethodUtil {
	
	public static Object invokeMethod(Object obj, Method method, Object... params) {
		Object result = null;
		try {
			if (params != null) {
				Object[] objects = new Object[0];
				if (params[0].getClass().getName().equals(objects.getClass().getName())) {
					objects = (Object[]) params[0];
					result = method.invoke(obj, objects);
				} else {
					result = method.invoke(obj, params[0]);
				}
			} else {
				result = method.invoke(obj);
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static ThreadMode getThreadModeFromMethod(Method method, ThreadMode mode) {
		if (mode == ThreadMode.None) {
			FThreradMode tMode = method.getAnnotation(FThreradMode.class);
			if (tMode != null && tMode.mode() != ThreadMode.None) {
				return tMode.mode();
			}
			
			return ThreadMode.Main;
		}
		
		return mode;
	}
	
	public static Method getMethodFromObject(Object obj, String methodName, Class<?>... paramTypes) throws FenException {
		Method method = null;
		try {
			method = obj.getClass().getDeclaredMethod(
					methodName, paramTypes);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			Method[] methods = obj.getClass()
					.getDeclaredMethods();
			int index = -1, count = 0;
			for (int i = 0; i < methods.length; i++) {
				if (methods[i].getName().equals(methodName)) {
					index = i;
					count++;
				}
			}

			if (count == 1 && index >= 0) {
				method = methods[index];
			} else {
				throw new FenException("Can't Find Method By MethodName:" + methodName);
			}
		}

		if (!method.isAccessible()) {
			method.setAccessible(true);
		}
		
		return method;
	}
}
