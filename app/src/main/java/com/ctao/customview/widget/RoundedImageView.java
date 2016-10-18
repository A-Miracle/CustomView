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
import android.widget.ImageView;

import com.ctao.customview.R;
import com.ctao.customview.utils.BitmapUtils;
import com.ctao.customview.utils.PathFactory;

public class RoundedImageView extends ImageView {

	protected static final String TAG = RoundedImageView.class.getSimpleName();

	protected PathFactory mPathFactory;
	
	private float[] mRadius; // [LeftTop, RightTop, RightBottom, LeftBottom]
	private Paint mBitmapPaint;
	private Path mPath;

	private RectF mRect;
	
	public RoundedImageView(Context context) {
		this(context, null);
	}

	public RoundedImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public RoundedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundedImageView);
		float radius = ta.getDimension(R.styleable.RoundedImageView_roundRadius, 0);
		float radiusLeftTop = ta.getDimension(R.styleable.RoundedImageView_roundRadiusLeftTop, 0);
		float radiusRightTop = ta.getDimension(R.styleable.RoundedImageView_roundRadiusRightTop, 0);
		float radiusRightBottom = ta.getDimension(R.styleable.RoundedImageView_roundRadiusRightBottom, 0);
		float radiusLeftBottom = ta.getDimension(R.styleable.RoundedImageView_roundRadiusLeftBottom, 0);
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
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (mRadius == null) {
			super.onDraw(canvas);
		}else{
			RectF rect = getRectF();
			if(mPath == null){
				mPath = new Path();
			}
			mPath.reset();
			mPathFactory.planRoundPath(mRadius, rect, mPath, 0);
			drawRounded(canvas, rect, mPath);
		}
	}
	
	private void drawRounded(Canvas canvas, RectF rect, Path path) {
		if (getDrawable() == null) {
			return;
		}

		setupRoundePaint();
		
		if(mRadius == null){
			canvas.drawPaint(mBitmapPaint);
		}else{
			if (mRadius[0] == mRadius[1] && mRadius[0] == mRadius[2] && mRadius[0] == mRadius[3]) {
				canvas.drawRoundRect(rect, mRadius[0], mRadius[0], mBitmapPaint);
			}else{
				canvas.drawPath(path, mBitmapPaint);
			}
		}
	}
	
	private void setupRoundePaint() {
		if (getDrawable() == null) {
			return;
		}
		
		Bitmap bitmap = getBitmapFromDrawable();
		if(bitmap == null){
			return;
		}
		
		BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

		bitmapShader.setLocalMatrix(getImageMatrix());
        
		if(mBitmapPaint == null){
			mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mBitmapPaint.setDither(false);
		}
        
		mBitmapPaint.setShader(bitmapShader);
	}
	
	protected RectF getRectF(){
		if(mRect == null){
			mRect = new RectF(getPaddingLeft(), getPaddingTop(), getRight() - getLeft() - getPaddingRight(),
					getBottom() - getTop() - getPaddingBottom());
		}
		return mRect;
	}
	
	protected Bitmap getBitmapFromDrawable() {
		return BitmapUtils.getBitmapFromDrawable(getDrawable());
	}
}
