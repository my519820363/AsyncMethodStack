package com.huyunfeng.asyncmethodstack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.huyunfeng.asyncmethodstack.Fen.FThreradMode;
import com.huyunfeng.asyncmethodstack.Fen.ThreadMode;

public class MethodUtil {
	
	/**
	 * 运行方法
	 * @param obj 调用此方法的对象
	 * @param method 方法
	 * @param params 参数
	 * @return
	 */
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
	
	/**
	 * 获取方法注解中的线程模式，默认为Main
	 * @param method 方法
	 * @return
	 */
	public static ThreadMode getThreadModeFromMethod(Method method) {
		FThreradMode tMode = method.getAnnotation(FThreradMode.class);
		if (tMode != null && tMode.mode() != ThreadMode.None) {
			return tMode.mode();
		}
		
		return ThreadMode.Main;
	}
	
	/**
	 * 根据方法名和参数类型从对象中提取方法
	 * @param obj 方法宿主对象
	 * @param methodName 方法名
	 * @param paramTypes 参数类型
	 * @return 获取到的方法
	 * @throws FenException 未找到方法异常
	 */
	public static Method getMethodFromObject(Class<?> cls, String methodName, Class<?>... paramTypes) throws FenException {
		Method method = null;
		try {
			method = cls.getDeclaredMethod(
					methodName, paramTypes);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			Method[] methods = cls.getDeclaredMethods();
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
