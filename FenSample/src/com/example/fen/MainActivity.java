package com.example.fen;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.huyunfeng.asyncmethodstack.Fen;
import com.huyunfeng.asyncmethodstack.MethodPoint;
import com.huyunfeng.asyncmethodstack.Fen.FThreradMode;
import com.huyunfeng.asyncmethodstack.Fen.ThreadMode;
import com.huyunfeng.asyncmethodstack.FenException;

/**
 * 示例讲解： new Fen(this).pop(view,
 * "setImageBitmap").pop("CutBitmap").end("getBitmap", R.drawable.ic_launcher);
 * 从左到右，每一个pop将会使用反射找到对象和方法并压入一个方法栈中。 当end发生时，将触发方法栈中的方法开始执行。
 * 
 * 因为是基于栈，所以方法的实际执行过程是从右至左。模拟顺序执行，不管是在异步线程还是主线程。
 * 
 * 示例的具体过程是：
 * 1：最先执行了activity对象下的getBitmap方法，提供给getBitmap方法的参数是R.drawable.ic_launcher
 * ，返回值是一张图片。 2：将1方法中获取到的返回值（Bitmap）传递给activity对象下的CutBitmap方法。
 * 3：将2方法中获取到的返回值（Bitmap）传递给view对象的setImageBitmap方法。
 * 注意：每个方法将在注解指定的线程中运行，或默认为主线程。
 * 
 * @author Huyunfeng E-mail:my519820363@gmail.com
 * 
 */
public class MainActivity extends Activity {
	private ImageView view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		view = (ImageView) findViewById(R.id.img);
		Log.d("demo", "主线程ID：" + Thread.currentThread().getId());
		
		try {
//			MethodPoint getBitmap = new MethodPoint(this, "getBitmap", Integer.class, Integer.class);
//			MethodPoint cutBitmap = new MethodPoint(this, "CutBitmap", Bitmap.class);
//			MethodPoint setImageBitmap = new MethodPoint(view, "setImageBitmap", Bitmap.class);
			
			MethodPoint getBitmap = new MethodPoint(this, "getBitmap");
			MethodPoint cutBitmap = new MethodPoint(MainActivity.class, "CutBitmap");
			MethodPoint setImageBitmap = new MethodPoint(view, "setImageBitmap");
			
			for (int i = 0; i < 10; i++) {
				new Fen().first(getBitmap, R.drawable.ic_launcher, i).and(cutBitmap)
						.and(setImageBitmap).start();
			}
		} catch (FenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FThreradMode(mode = ThreadMode.BackGround)
	public static Bitmap CutBitmap(Bitmap bitmap, int taskId) {
		// 模仿切图过程，在背景线程中
		Log.d("demo", "任务 : " + taskId + " CutBitmap 所在线程ID：" + Thread.currentThread().getId());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bitmap;
	}

	@FThreradMode(mode = ThreadMode.Async)
	public Object[] getBitmap(int resId, int taskId) {
		// 模仿下载过程，在异步线程中
		Log.d("demo", "任务 : " + taskId + " getBitmap 所在线程ID：" + Thread.currentThread().getId());
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Object[] {
				((BitmapDrawable) getResources().getDrawable(resId))
						.getBitmap(), taskId };
	}
}
