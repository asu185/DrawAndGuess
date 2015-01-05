package com.game.drawandguess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DrawingView extends SurfaceView implements SurfaceHolder.Callback {
	
	private Bitmap bitmap;
	private SurfaceHolder holder;
	private Bitmap mbitmap;
	private Context context;
	private MyThread mythread;
	
	
	public DrawingView(Context ctx, AttributeSet attrSet) {
		super(ctx);
		
		context = ctx;

		//the bitmap we wish to draw

		mbitmap =  Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);

		holder = getHolder();

		holder.addCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mythread = new MyThread(holder, context,this);

		mythread.setRunning(true);

		mythread.start();
		
	}


	@Override
	public void draw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		canvas.drawBitmap(mbitmap, 50, 50, null);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mythread.setRunning(false);
		boolean retry = true;
		while (retry)

		{
			try

			{

				mythread.join();

				retry = false;

			}

			catch (Exception e)

			{
				Log.v("Exception Occured", e.getMessage());

			}

		}

	}
	
	public class MyThread extends Thread {

		boolean mRun;

		Canvas mcanvas;

		SurfaceHolder surfaceHolder;

		Context context;

		DrawingView msurfacePanel;

		public MyThread(SurfaceHolder sholder, Context ctx, DrawingView spanel)
		{
			surfaceHolder = sholder;

			context = ctx;

			mRun = false;

			msurfacePanel = spanel;
		}

		void setRunning(boolean bRun)
		{
			mRun = bRun;
		}

		@Override
		public void run()
		{
			super.run();

			while (mRun)
			{

				mcanvas = surfaceHolder.lockCanvas();

				if (mcanvas != null)

				{

					msurfacePanel.draw(mcanvas);

					surfaceHolder.unlockCanvasAndPost(mcanvas);

				}

			}

		}

	}
}
