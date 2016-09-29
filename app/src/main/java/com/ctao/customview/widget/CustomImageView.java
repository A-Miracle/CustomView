package com.ctao.customview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.ctao.customview.R;
import com.ctao.customview.utils.BitmapUtils;
import com.ctao.customview.utils.PathFactory;

/**
 * Created by A Miracle on 2016/9/29.
 */
public class CustomImageView extends ImageView {

	protected static final String TAG = CustomImageView.class.getSimpleName();

	private float[] mRadius; // [LeftTop, RightTop, RightBottom, LeftBottom]
	private Paint mBitmapPaint;
	
	protected PathFactory mPathFactory;
	private Path mPath;
	
	public CustomImageView(Context context) {
		this(context, null);
	}

	public CustomImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CustomImageView);
		float radius = ta.getDimension(R.styleable.CustomImageView_roundRadius, 0);
		float radiusLeftTop = ta.getDimension(R.styleable.CustomImageView_roundRadiusLeftTop, 0);
		float radiusRightTop = ta.getDimension(R.styleable.CustomImageView_roundRadiusRightTop, 0);
		float radiusRightBottom = ta.getDimension(R.styleable.CustomImageView_roundRadiusRightBottom, 0);
		float radiusLeftBottom = ta.getDimension(R.styleable.CustomImageView_roundRadiusLeftBottom, 0);
		ta.recycle();

		if (radius != 0) {
			mRadius = new float[] { radius, radius, radius, radius };
		}

		boolean falg = (radiusLeftTop != 0) ? true : false;
		falg = (radiusRightTop != 0) ? true : falg;
		falg = (radiusRightBottom != 0) ? true : falg;
		falg = (radiusLeftBottom != 0) ? true : falg;

		if (falg) {
			mRadius = new float[] { radiusLeftTop, radiusRightTop, radiusRightBottom, radiusLeftBottom };
		}

		mPathFactory = new PathFactory();
	}
	
	public float[] getRadius() {
		return mRadius;
	}

	/** [LeftTop, RightTop, RightBottom, LeftBottom] */
	public void setRadius(float[] radius) {
		this.mRadius = radius;
		invalidate();
	}
	
	public Paint getBitmapPaint() {
		return mBitmapPaint;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mRadius == null) {
			super.onDraw(canvas);
		}else{
			drawToCanvas(canvas);
		}
	}

	protected void drawToCanvas(Canvas canvas) {
		if (getDrawable() == null) {
			return;
		}
		
		if(mPath == null){
			mPath = new Path();
		}

		RectF rect = getRectF();
		setupBitmapPaint();
		if(mRadius == null){
			canvas.drawPaint(mBitmapPaint);
		}else{
			if (mRadius[0] == mRadius[1] && mRadius[0] == mRadius[2] && mRadius[0] == mRadius[3]) {
				canvas.drawRoundRect(rect, mRadius[0], mRadius[0], mBitmapPaint);
			}else{
				mPath.reset();
				mPathFactory.planRoundPath(mRadius, rect, mPath, 0);
				canvas.drawPath(mPath, mBitmapPaint);
			}
		}
	}
	
	protected RectF getRectF(){
		RectF rect = new RectF(getPaddingLeft(), getPaddingTop(), getRight() - getLeft() - getPaddingRight(),
				getBottom() - getTop() - getPaddingBottom());
		return rect;
	}

	protected void setupBitmapPaint() {
		if (getDrawable() == null) {
			return;
		}
		
		Bitmap bitmap = getBitmapFromDrawable();
		if(bitmap == null){
			return;
		}
		
		BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

		if(getScaleType() != ScaleType.FIT_XY){
            Log.w(TAG,String.format("Now scale type just support fitXY,other type invalid"));
        }
		
        //now scale type just support fitXY
        //todo support all scale type
        Matrix mMatrix = new Matrix();
        mMatrix.setScale(getWidth() * 1.0f / bitmap.getWidth(), getHeight() * 1.0f / bitmap.getHeight());
        bitmapShader.setLocalMatrix(mMatrix);
        
		if(mBitmapPaint == null){
			mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mBitmapPaint.setDither(false);
		}
        
		mBitmapPaint.setShader(bitmapShader);
	}
	
	protected Bitmap getBitmapFromDrawable() {
		return BitmapUtils.getBitmapFromDrawable(getDrawable());
	}
}