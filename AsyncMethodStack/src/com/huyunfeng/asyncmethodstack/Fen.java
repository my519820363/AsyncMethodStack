package com.huyunfeng.asyncmethodstack;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.LinkedBlockingQueue;

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

	private LinkedBlockingQueue<MethodElement> methodElementQueue;
	private boolean insertFlag = true;

	public Fen() throws FenException {
		methodElementQueue = new LinkedBlockingQueue<MethodElement>();
	}

	public Fen and(MethodPoint mp) throws FenException {
		return and(ThreadMode.None, mp);
	}

	public Fen and(ThreadMode mode, MethodPoint mp) throws FenException {
		if (mp == null) {
			throw new FenException("MethodHost is Null!");
		}

		if (insertFlag) {
			paraseToQueue(mode, mp);
		}
		return this;
	}

	public Fen first(MethodPoint mp, Object... params) throws FenException {
		return first(ThreadMode.None, mp, params);
	}

	public Fen first(ThreadMode mode, MethodPoint mp, Object... params)
			throws FenException {
		if (insertFlag) {
			paraseToQueue(mode, mp, params);
		}
		return this;
	}

	public Fen first(Object... params) {
		if (params != null && !methodElementQueue.isEmpty() && insertFlag) {
			getFirstMethodElement().params = new Object[] { params };
		}
		return this;
	}

	public void start() {
		insertFlag = false;
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

	private void paraseToQueue(ThreadMode mode, MethodPoint mp,
			Object... params) throws FenException {
		MethodElement methodElement = new MethodElement();
		methodElement.methodPoint = mp;
		if (mode == ThreadMode.None) {
			if (mp.getMode() == ThreadMode.None) {
				methodElement.mode = ThreadMode.Main;
			} else {
				methodElement.mode = mp.getMode();
			}
		} else {
			methodElement.mode = mode;
		}
		methodElement.params = new Object[] { params };
		addMethodElement(methodElement);
	}

	/**
	 * 获取方法栈的第一个元素
	 */
	MethodElement getFirstMethodElement() {
		return methodElementQueue.peek();
	}

	/**
	 * 获取方法栈的第一个元素并从栈中删除
	 */
	MethodElement pollMethodElement() {
		return methodElementQueue.poll();
	}

	/**
	 * 获取方法栈的第一个元素并从栈中删除
	 */
	void addMethodElement(MethodElement methodElement) {
		methodElementQueue.offer(methodElement);
	}

	/**
	 * 运行Fen对象方法栈中的下一个方法
	 */
	void next() {
		if (!methodElementQueue.isEmpty()) {
			MethodElement methodElement = getFirstMethodElement();
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
		MethodElement methodElement = fen.pollMethodElement();
		if (methodElement != null) {
			Object result = null;
			result = methodElement.methodPoint.runMethod(methodElement.params);
			if (result != null) {
				fen.getFirstMethodElement().params = new Object[] { result };
			}
			fen.next();
		}
	}

	static class MethodElement {
		public MethodPoint methodPoint;// 方法宿主对象
		public ThreadMode mode;// 运行线程环境
		public Object[] params;// 参数
	}

	public static enum ThreadMode {
		Async, Main, BackGround, Current, None
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface FThreradMode {
		public ThreadMode mode() default ThreadMode.None;
	}
}
