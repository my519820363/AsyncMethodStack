package com.huyunfeng.asyncmethodstack;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncPoster {
	private ExecutorService threadPool;
	private ThreadFactory sThreadFactory;

	public AsyncPoster() {
		// 线程工厂
		sThreadFactory = new ThreadFactory() {
			private final AtomicInteger mCount = new AtomicInteger(1);

			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r, "ThreadManager #"
						+ mCount.getAndIncrement());
				thread.setPriority(Thread.MAX_PRIORITY);
				return thread;
			}
		};
		
		reStart();
	}

	public void invokeMethod(Fen fen) {
		if(threadPool != null) {
			threadPool.execute(new FenRunnable(fen));
		}
	}

	/**
	 * 重启线程池,开启服务
	 */
	public void reStart() {
		// 初始化线程池
		if (threadPool == null) {
			threadPool = Executors.newCachedThreadPool(sThreadFactory);
		}
	}

	/**
	 * 关闭线程池,停止服务
	 */
	public void shutDown() {
		if (threadPool != null) {
			threadPool.shutdown();
			threadPool = null;
		}
	}

	private static class FenRunnable implements Runnable {
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
