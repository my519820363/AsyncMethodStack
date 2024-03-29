package com.huyunfeng.asyncmethodstack;

import java.util.concurrent.atomic.AtomicInteger;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

public class BackgroundPoster {
	private static final int WHAT_INVOKE = 0;
	private final AtomicInteger mCount = new AtomicInteger(1);
	private HandlerThread mHandlerThread;
	private BackgroundLooperHandler mHandler;

	public BackgroundPoster() {
		// TODO Auto-generated constructor stub
		reStart();
	}
	
	public String getThreadName() {
		return mHandlerThread.getName();
	}

	public void invokeMethod(Fen fen) {
		if (mHandlerThread != null) {
			mHandler.sendMessage(mHandler.obtainMessage(WHAT_INVOKE, fen));
		}
	}
	
	/**
	 * 重启背景线程，开启服务
	 */
	public void reStart() {
		if (mHandlerThread == null) {
			mHandlerThread = new HandlerThread("BackgroundHandler"
					+ mCount.getAndIncrement());
			mHandlerThread.start();
			mHandler = new BackgroundLooperHandler(mHandlerThread.getLooper());
		}
	}
	
	/**
	 * 关闭背景线程，停止服务
	 */
	public void shutDown() {
		if (mHandlerThread != null) {
			mHandlerThread.getLooper().quit();
			mHandlerThread = null;
		}
	}

	private static class BackgroundLooperHandler extends Handler {

		public BackgroundLooperHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == WHAT_INVOKE)
				Fen.runStackMethod((Fen)msg.obj);
		}
	}
}
