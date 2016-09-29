package com.ctao.customview.widget;

import com.ctao.customview.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * Created by A Miracle on 2016/9/29.
 */
public class BorderImageView extends CustomImageView {
	
	private int mBorderColor = -5832960;
	private float mBorderSize = 3;
	private Paint mBorderPaint;
	private Path mPath;
	private boolean mIsSetupBorderSize;
	
	public BorderImageView(Context context) {
		this(context, null);
	}

	public BorderImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BorderImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}
	
	private void init(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BorderImageView);
		mBorderColor = ta.getColor(R.styleable.BorderImageView_borderColor, mBorderColor);
		float size = ta.getDimension(R.styleable.BorderImageView_borderSize, 0);
		if(size != 0){
			mBorderSize = size;
			mIsSetupBorderSize = true;
		}
		ta.recycle();
	}
	
	public boolean isSetupBorderSize(){
		return mIsSetupBorderSize;
	}
	
	public int getBorderColor() {
		return mBorderColor;
	}

	public void setBorderColor(int mBorderColor) {
		this.mBorderColor = mBorderColor;
		invalidate();
	}

	public float getBorderSize() {
		return mBorderSize;
	}

	public void setBorderSize(float mBorderSize) {
		this.mBorderSize = mBorderSize;
		invalidate();
	}

	public Paint getBorderPaint() {
		return mBorderPaint;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(mBorderSize == 0){
			super.onDraw(canvas);
		}else{
			drawToCanvas(canvas);
		}
	}

	@Override
	protected void drawToCanvas(Canvas canvas) {
		if (getDrawable() == null) {
			return;
		}
		
		if(mPath == null){
			mPath = new Path();
		}

		RectF rect = getRectF();
		mPath.reset();
		if(getRadius() == null){
			setRadius(new float[] { 0f, 0f, 0f, 0f }); //为null, mPathFactory.planRoundPath直接return;
		}
		mPathFactory.planRoundPath(getRadius(), rect, mPath, mBorderSize / 2); //规划Path
		setupBitmapPaint();
		canvas.drawPath(mPath, getBitmapPaint());
		
		//draw border
		if(mBorderSize > 0){
			setupBorderPaint();
			canvas.drawPath(mPath, mBorderPaint);
		}
	}
	
	protected void setupBorderPaint(){
		if(mBorderPaint == null){
			mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mBorderPaint.setDither(false);
		}
		mBorderPaint.setColor(mBorderColor);
		mBorderPaint.setStrokeWidth(mBorderSize);
		mBorderPaint.setStyle(Paint.Style.STROKE);
	}
}
