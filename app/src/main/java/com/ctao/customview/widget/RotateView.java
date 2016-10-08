package com.ctao.customview.widget;

import com.ctao.customview.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class RotateView extends TagImageView {
	public final static int CLOCKWISE = 1;
	public final static int COUNTERCLOCKWISE = -1;
	private int mDegree; //角度
	private int mRate; //速率
	private int mDirection = CLOCKWISE; //方向
	private int isNotChange;
	private Bitmap mTempBitmap;

	public RotateView(Context context) {
		this(context, null);
	}

	public RotateView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RotateView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}
	
	private void init(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RotateView);
		mDegree = ta.getInteger(R.styleable.RotateView_rotateDegree, 0);
		mRate = ta.getInteger(R.styleable.RotateView_rotateRate, 0);
		mDirection = ta.getInteger(R.styleable.RotateView_rotateDirection, CLOCKWISE);
		ta.recycle();
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		if(mRate == 0 && mDegree == 0){
			super.onDraw(canvas);
		}else if(mRate == 0){
			canvas.save();
			canvas.rotate(mDegree * mDirection, getMeasuredWidth()/2, getMeasuredHeight()/2);
			super.onDraw(canvas);
			canvas.restore();
		}else{
			long startTime = System.currentTimeMillis();
			
			canvas.save();
			canvas.rotate(mDegree * mDirection, getMeasuredWidth()/2, getMeasuredHeight()/2);
			if(isNotChange-- > 0){
				if(mTempBitmap != null){
					canvas.drawBitmap(mTempBitmap, 0, 0, null);
				}
			}else{
				mTempBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
				Canvas tmpCanvas = new Canvas(mTempBitmap);
				super.onDraw(tmpCanvas);
				
				canvas.drawBitmap(mTempBitmap, 0, 0, null);
			}
			canvas.restore();
			mDegree += mRate;
			
			long stopTime = System.currentTimeMillis();
			long runTime = stopTime - startTime;
			
			// 16毫秒执行一次
			invalidateDelayed(Math.abs(runTime - 16));
		}
	}
	
	private void invalidateDelayed(long delayMilliseconds) {
		isNotChange = 1;
		postInvalidateDelayed(delayMilliseconds);
	}
}
