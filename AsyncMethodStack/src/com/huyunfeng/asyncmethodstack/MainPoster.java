package com.huyunfeng.asyncmethodstack;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class MainPoster extends Handler {
	private static final int WHAT_INVOKE = 0;
	
	private MainLooperHandler mHandler;

	public MainPoster() {
		mHandler = new MainLooperHandler();
	}
	
	public void invokeMethod(Fen fen) {
		mHandler.sendMessage(mHandler.obtainMessage(WHAT_INVOKE, fen));
	}

	private static class MainLooperHandler extends Handler {

		public MainLooperHandler() {
			super(Looper.getMainLooper());
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == WHAT_INVOKE)
				Fen.runStackMethod((Fen)msg.obj);
		}
	}
}
