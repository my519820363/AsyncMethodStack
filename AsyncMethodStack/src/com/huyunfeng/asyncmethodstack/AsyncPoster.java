package com.huyunfeng.asyncmethodstack;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncPoster {
	private static final LinkedBlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(
			10);
	private static final int CORE_POOL_SIZE = 5;
	private static final int MAXIMUM_POOL_SIZE = 64;
	private static final int KEEP_ALIVE = 1;

	private ThreadPoolExecutor threadPool;

	public AsyncPoster() {
		// 线程工厂
		ThreadFactory sThreadFactory = new ThreadFactory() {
			private final AtomicInteger mCount = new AtomicInteger(1);

			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r, "ThreadManager #"
						+ mCount.getAndIncrement());
				thread.setPriority(Thread.MAX_PRIORITY);
				return thread;
			}
		};

		// 初始化线程池
		threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
				KEEP_ALIVE, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
	}

	public void invokeMethod(Fen fen) {
		threadPool.execute(new FenRunnable(fen));
	}
	
	private class FenRunnable implements Runnable{
		private Fen fen;
		public FenRunnable(Fen fen) {
			this.fen = fen;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Fen.runStackMethod(fen);
		}
	}
}
