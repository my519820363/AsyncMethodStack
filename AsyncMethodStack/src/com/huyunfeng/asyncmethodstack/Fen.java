package com.huyunfeng.asyncmethodstack;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import android.os.Looper;

/**
 * 
 * @author huyunfeng E-mail:my519820363@gmail.com
 * @version CreateTime：2013-4-19
 * 
 */
public class Fen {
	private static final MainPoster mainPoster = new MainPoster();
	private static final AsyncPoster asyncPoster = new AsyncPoster();
	private static final BackgroundPoster backgroundPoster = new BackgroundPoster();

	private Stack<MethodElement> methodElementStack;
	private Object defaultMethodHostObject;
	private boolean insertFlag = true;

	/**
	 * 
	 * @param methodHost
	 *            默认的方法宿主
	 * @throws FenException
	 *             异常
	 */
	public Fen(Object methodHost) throws FenException {
		if (methodHost == null) {
			throw new FenException("Host isn't Null!");
		}

		defaultMethodHostObject = methodHost;
		methodElementStack = new Stack<MethodElement>();
	}
	
	public Fen() throws FenException {
		methodElementStack = new Stack<MethodElement>();
	}

	/**
	 * 将默认宿主对象中的指定方法压入栈，默认的线程模式是主线程
	 * 
	 * @param methodName
	 *            方法名，在默认宿主对象中查找此方法
	 * @param paramTypesCls
	 *            方法参数类型列表（仅在指定方法有多个重载实现时需要指定）,若指定类型，也可加快反射获取方法的速度。
	 * @throws FenException
	 */
	public Fen pop(String methodName, Class<?>... paramTypesCls)
			throws FenException {
		return pop(ThreadMode.None, defaultMethodHostObject, methodName,
				paramTypesCls);
	}

	/**
	 * 将指定宿主对象中的指定方法压入栈，默认的线程模式是主线程
	 * 
	 * @param methodHost
	 *            方法的宿主对象
	 * @param methodName
	 *            方法名，在宿主对象中查找此方法
	 * @param paramsTypesCls
	 *            方法参数类型列表（仅在指定方法有多个重载实现时需要指定）,若指定类型，也可加快反射获取方法的速度。
	 * @throws FenException
	 */
	public Fen pop(Object methodHost, String methodName, Class<?>... paramsTypesCls)
			throws FenException {
		return pop(ThreadMode.None, methodHost, methodName, paramsTypesCls);
	}

	/**
	 * 将默认宿主对象中的指定方法压入栈
	 * 
	 * @param mode
	 *            调用此方法时使用的线程模式，会覆盖注解指定的模式
	 * @param methodName
	 *            方法名，在默认宿主对象中查找此方法
	 * @param paramsTypesCls
	 *            方法参数类型列表（仅在指定方法有多个重载实现时需要指定）,若指定类型，也可加快反射获取方法的速度。
	 * @throws FenException
	 */
	public Fen pop(ThreadMode mode, String methodName,
			Class<?>... paramsTypesCls) throws FenException {
		return pop(mode, defaultMethodHostObject, methodName, paramsTypesCls);
	}

	/**
	 * 将指定宿主对象中的指定方法压入栈
	 * 
	 * @param mode
	 *            调用此方法时使用的线程模式，会覆盖注解指定的模式
	 * @param methodHost
	 *            方法的宿主对象
	 * @param methodName
	 *            方法名，在宿主对象中查找此方法
	 * @param paramsTypesCls
	 *            方法参数类型列表（仅在指定方法有多个重载实现时需要指定）,若指定类型，也可加快反射获取方法的速度。
	 * @throws FenException
	 */
	public Fen pop(ThreadMode mode, Object methodHost, String methodName,
			Class<?>... paramsTypesCls) throws FenException {
		if (methodHost == null) {
			throw new FenException("MethodHost is Null!");
		}
		
		if (insertFlag) {
			paraseToStack(mode, methodHost, methodHost.getClass(), methodName,
					paramsTypesCls);
		}
		return this;
	}

	/**
	 * 将默认宿主对象的类中的指定静态方法压入栈，默认的线程模式是主线程
	 * 
	 * @param methodName
	 *            方法名，在默认宿主对象中查找此方法
	 * @param paramsTypesCls
	 *            方法参数类型列表（仅在指定方法有多个重载实现时需要指定）,若指定类型，也可加快反射获取方法的速度。
	 * @throws FenException
	 */
	public Fen popStatic(String methodName, Class<?>... paramsTypesCls)
			throws FenException {
		if (defaultMethodHostObject == null) {
			throw new FenException("MethodHost is Null!");
		}
		
		return popStatic(ThreadMode.None, defaultMethodHostObject.getClass(),
				methodName, paramsTypesCls);
	}

	/**
	 * 将默认宿主对象的类中的指定静态方法压入栈，默认的线程模式是主线程
	 * 
	 * @param methodName
	 *            方法名，在默认宿主对象中查找此方法
	 * @param paramsTypesCls
	 *            方法参数类型列表（仅在指定方法有多个重载实现时需要指定）,若指定类型，也可加快反射获取方法的速度。
	 * @throws FenException
	 */
	public Fen popStatic(ThreadMode mode, String methodName,
			Class<?>... paramsTypesCls) throws FenException {
		if (defaultMethodHostObject == null) {
			throw new FenException("MethodHost is Null!");
		}
		
		return popStatic(mode, defaultMethodHostObject.getClass(), methodName,
				paramsTypesCls);
	}

	/**
	 * 将指定类中的指定静态方法压入栈，默认的线程模式是主线程
	 * 
	 * @param methodHost
	 *            类，在此类中查找静态方法
	 * @param methodName
	 *            方法名，在默认宿主对象中查找此方法
	 * @param paramsTypesCls
	 *            方法参数类型列表（仅在指定方法有多个重载实现时需要指定）,若指定类型，也可加快反射获取方法的速度。
	 * @throws FenException
	 */
	public Fen popStatic(Class<?> methodHost, String methodName,
			Class<?>... paramsTypesCls) throws FenException {
		return popStatic(ThreadMode.None, methodHost, methodName, paramsTypesCls);
	}

	/**
	 * 将默认宿主对象中的指定方法压入栈
	 * 
	 * @param mode
	 *            调用此方法时使用的线程模式，会覆盖注解指定的模式
	 * @param methodName
	 *            方法名，在默认宿主对象中查找此方法
	 * @param paramTypesCls
	 *            方法参数类型列表（仅在指定方法有多个重载实现时需要指定）,若指定类型，也可加快反射获取方法的速度。
	 * @throws FenException
	 */
	public Fen popStatic(ThreadMode mode, Class<?> methodHost, String methodName,
			Class<?>... paramTypesCls) throws FenException {
		if (methodHost == null) {
			throw new FenException("MethodHost is Null!");
		}
		
		if (insertFlag) {
			paraseToStack(mode, null, methodHost, methodName, paramTypesCls);
		}
		return this;
	}

	/**
	 * 压入最后一个方法，代码将从这个方法开始执行并向前推进。 若此方法需要参数，你可以将参数传进这个方法。
	 * 
	 * @param methodName
	 *            方法名
	 * @param params
	 *            参数列表
	 * @throws FenException
	 */
	public void end(String methodName, Object... params) throws FenException {
		endToHost(ThreadMode.None, defaultMethodHostObject, methodName, params);
	}

	/**
	 * 压入最后一个方法，代码将从这个方法开始执行并向前推进。 若此方法需要参数，你可以将参数传进这个方法。
	 * 
	 * @param methodHost
	 *            方法宿主对象
	 * @param methodName
	 *            方法名
	 * @param params
	 *            参数
	 * @throws FenException
	 */
	public void endToHost(Object methodHost, String methodName, Object... params)
			throws FenException {
		endToHost(ThreadMode.None, methodHost, methodName, params);
	}

	/**
	 * 压入最后一个方法，代码将从这个方法开始执行并向前推进。 若此方法需要参数，你可以将参数传进这个方法。
	 * 
	 * @param mode
	 *            运行此方法的线程模式
	 * @param methodName
	 *            方法名
	 * @param params
	 *            参数
	 * @throws FenException
	 */
	public void end(ThreadMode mode, String methodName, Object... params)
			throws FenException {
		endToHost(mode, defaultMethodHostObject, methodName, params);
	}

	/**
	 * 压入最后一个方法，代码将从这个方法开始执行并向前推进。 若此方法需要参数，你可以将参数传进这个方法。
	 * 
	 * @param mode
	 *            线程模式
	 * @param methodHost
	 *            方法宿主对象
	 * @param methodName
	 *            方法名
	 * @param params
	 *            参数
	 * @throws FenException
	 */
	public void endToHost(ThreadMode mode, Object methodHost, String methodName,
			Object... params) throws FenException {
		if (methodHost == null) {
			throw new FenException("MethodHost is Null!");
		}
		
		insertFlag = false;
		paraseToStack(mode, methodHost, methodHost.getClass(), methodName, params);
		next();
	}

	/**
	 * 压入最后一个方法，代码将从这个方法开始执行并向前推进。 若此方法需要参数，你可以将参数传进这个方法。
	 * 
	 * @param methodName
	 *            方法名
	 * @param params
	 *            参数列表
	 * @throws FenException
	 */
	public void endStatic(String methodName, Object... params)
			throws FenException {
		if (defaultMethodHostObject == null) {
			throw new FenException("MethodHost is Null!");
		}
		
		endToHostStatic(ThreadMode.None, defaultMethodHostObject.getClass(),
				methodName, params);
	}

	/**
	 * 压入最后一个方法，代码将从这个方法开始执行并向前推进。 若此方法需要参数，你可以将参数传进这个方法。
	 * 
	 * @param methodHost
	 *            方法宿主对象
	 * @param methodName
	 *            方法名
	 * @param params
	 *            参数
	 * @throws FenException
	 */
	public void endToHostStatic(Class<?> methodHost, String methodName,
			Object... params) throws FenException {
		if (methodHost == null) {
			throw new FenException("MethodHost is Null!");
		}
		
		endToHostStatic(ThreadMode.None, methodHost, methodName, params);
	}

	/**
	 * 压入最后一个方法，代码将从这个方法开始执行并向前推进。 若此方法需要参数，你可以将参数传进这个方法。
	 * 
	 * @param mode
	 *            运行此方法的线程模式
	 * @param methodName
	 *            方法名
	 * @param params
	 *            参数
	 * @throws FenException
	 */
	public void endStatic(ThreadMode mode, String methodName, Object... params)
			throws FenException {
		if (defaultMethodHostObject == null) {
			throw new FenException("MethodHost is Null!");
		}
		
		endToHostStatic(mode, defaultMethodHostObject.getClass(), methodName,
				params);
	}

	/**
	 * 压入最后一个方法，代码将从这个方法开始执行并向前推进。 若此方法需要参数，你可以将参数传进这个方法。
	 * 
	 * @param mode
	 *            线程模式
	 * @param host
	 *            方法宿主对象
	 * @param methodName
	 *            方法名
	 * @param params
	 *            参数
	 * @throws FenException
	 */
	public void endToHostStatic(ThreadMode mode, Class<?> methodHost,
			String methodName, Object... params) throws FenException {
		if (methodHost == null) {
			throw new FenException("MethodHost is Null!");
		}
		
		insertFlag = false;
		paraseToStack(mode, null, methodHost, methodName, params);
		next();
	}

	public void end(Object... params) {
		insertFlag = false;
		if (params != null && !methodElementStack.isEmpty()) {
			methodElementStack.peek().params = new Object[] { params };
		}
		next();
	}

	/**
	 * 重启后台线程，恢复服务
	 * 
	 */
	public static void reStart() {
		asyncPoster.reStart();
		backgroundPoster.reStart();
	}

	/**
	 * 关闭后台线程，停止服务
	 */
	public static void shutDown() {
		asyncPoster.shutDown();
		backgroundPoster.shutDown();
	}

	/**
	 * 通过反射获取方法并入栈
	 * 
	 * @param mode
	 * @param obj
	 * @param methodName
	 * @param params
	 * @throws FenException
	 */
	private void paraseToStack(ThreadMode mode, Object obj, Class<?> cls,
			String methodName, Object... params) throws FenException {
		MethodElement methodElement = new MethodElement();
		if (obj == null) {
			methodElement.isStaticMethod = true;
		} else {
			methodElement.isStaticMethod = false;
			methodElement.object = obj;
		}
		methodElement.cls = cls;

		// 获取方法
		if (params != null) {
			Class<?>[] paramTypesCls = new Class<?>[params.length];
			for (int i = 0; i < params.length; i++) {
				paramTypesCls[i] = params[i].getClass();
			}
			methodElement.method = MethodUtil.getMethodFromObject(
					methodElement.cls, methodName, paramTypesCls);
			methodElement.params = new Object[] { params };
		} else {
			methodElement.method = MethodUtil.getMethodFromObject(
					methodElement.cls, methodName);
		}
		
		if (mode == ThreadMode.None) {
			methodElement.mode = MethodUtil.getThreadModeFromMethod(
					methodElement.method, mode);
		} else {
			methodElement.mode = mode;
		}
		methodElementStack.push(methodElement);
	}

	/**
	 * 通过反射获取方法并入栈
	 * 
	 * @param mode
	 * @param obj
	 * @param cls
	 * @param methodName
	 * @param paramTypesCls
	 * @throws FenException
	 */
	private void paraseToStack(ThreadMode mode, Object obj, Class<?> cls,
			String methodName, Class<?>... paramTypesCls) throws FenException {
		MethodElement methodElement = new MethodElement();
		// 设置方法体的宿主
		if (obj == null) {
			methodElement.isStaticMethod = true;
		} else {
			methodElement.isStaticMethod = false;
			methodElement.object = obj;
		}
		methodElement.cls = cls;
		
		// 获取方法
		methodElement.method = MethodUtil.getMethodFromObject(
				methodElement.cls, methodName, paramTypesCls);
		if (mode == ThreadMode.None) {
			methodElement.mode = MethodUtil.getThreadModeFromMethod(
					methodElement.method, mode);
		} else {
			methodElement.mode = mode;
		}
		methodElementStack.push(methodElement);
	}

	/**
	 * 获取方法栈的第一个元素
	 */
	MethodElement getFirstMethodElement() {
		return methodElementStack.peek();
	}

	/**
	 * 获取方法栈的第一个元素并从栈中删除
	 */
	MethodElement popMethodElement() {
		return methodElementStack.pop();
	}
	
	/**
	 * 运行Fen对象方法栈中的下一个方法
	 */
	void next() {
		if (!methodElementStack.isEmpty()) {
			MethodElement methodElement = methodElementStack.peek();
			if (Looper.getMainLooper() == Looper.myLooper()) {// 当前在主线程
				if (methodElement.mode == ThreadMode.Current
						|| methodElement.mode == ThreadMode.Main) {
					runStackMethod(this);// 在当前线程（主线程）执行
				} else if (methodElement.mode == ThreadMode.Async) {
					asyncPoster.invokeMethod(this);// 在异步线程执行
				} else if (methodElement.mode == ThreadMode.BackGround) {
					backgroundPoster.invokeMethod(this);// 在背景线程运行
				}
			} else {
				// 异步线程
				if (Thread.currentThread().getName()
						.equals(backgroundPoster.getThreadName())) {
					// 在背景线程中
					if (methodElement.mode == ThreadMode.Current
							|| methodElement.mode == ThreadMode.BackGround) {
						runStackMethod(this);// 在当前线程（背景线程）执行
					} else if (methodElement.mode == ThreadMode.Async) {
						asyncPoster.invokeMethod(this);// 在异步线程执行
					} else if (methodElement.mode == ThreadMode.Main) {
						mainPoster.invokeMethod(this);// 在主线程执行
					}
				} else {
					if (methodElement.mode == ThreadMode.Current
							|| methodElement.mode == ThreadMode.Async) {
						runStackMethod(this);// 在当前线程（异步程）执行
					} else if (methodElement.mode == ThreadMode.Main) {
						mainPoster.invokeMethod(this);// 在主线程执行
					} else if (methodElement.mode == ThreadMode.BackGround) {
						backgroundPoster.invokeMethod(this);// 在背景线程执行
					}
				}
			}
		} else {
			insertFlag = true;
		}
	}

	/**
	 * 从栈中取得方法并运行
	 * 
	 * @param fen
	 */
	static void runStackMethod(Fen fen) {
		MethodElement methodElement = fen.popMethodElement();
		if (methodElement != null) {
			Object result = null;
			if (!methodElement.isStaticMethod) {
				result = MethodUtil.invokeMethod(methodElement.object,
						methodElement.method, methodElement.params);
			} else {
				result = MethodUtil.invokeMethod(methodElement.cls,
						methodElement.method, methodElement.params);
			}

			if (result != null) {
				fen.getFirstMethodElement().params = new Object[] { result };
			}
			fen.next();
		}
	}

	static class MethodElement {
		public Object object;// 方法宿主对象
		public Class<?> cls;// 宿主对象的类
		public Method method;// 方法名
		public boolean isStaticMethod;// 是否是静态方法
		public ThreadMode mode;// 运行线程环境
		public Object[] params;// 参数
	}

	public static enum ThreadMode {
		Async, Main, BackGround, Current, None
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface FThreradMode {
		public ThreadMode mode() default ThreadMode.Main;
	}
}
