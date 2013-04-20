package com.huyunfeng.asyncmethodstack;

import android.util.Log;

public class FenException extends Exception {
	private static final long serialVersionUID = 8943661345251055280L;
	
	private String msg;

	public FenException(String msg) {
		// TODO Auto-generated constructor stub
		this.msg = msg;
	}

	@Override
	public void printStackTrace() {
		// TODO Auto-generated method stub
		Log.w("FenException", msg);
	}
}
