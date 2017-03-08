package com.ctao.customview.svg.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import com.ctao.customview.R;
import com.ctao.customview.svg.SVG;
import com.ctao.customview.svg.SVGParser;

import java.lang.ref.WeakReference;

/**
 * reference https://github.com/MostafaGazar/CustomShapeImageView
 * @author Miracle
 */
public class SvgImageView extends android.support.v7.widget.AppCompatImageView {
	
	private static final String TAG = SvgImageView.class.getSimpleName();
	private static final Xfermode sXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
	private Paint mPaint;
	private Bitmap mMaskBitmap;
	private WeakReference<Bitmap> mWeakBitmap;
	private int mSvgRawResourceId;

	public SvgImageView(Context context) {
		this(context, null);
	}

	public SvgImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SvgImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SvgImageView);
		mSvgRawResourceId = ta.getResourceId(R.styleable.SvgImageView_svgRawResourceId, 0);
		ta.recycle();
		
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
        if (isInEditMode()) {
        	super.onDraw(canvas);
        	return;
        }
        drawSvg(canvas);
	}

	private void drawSvg(Canvas canvas) {
		int i = canvas.saveLayer(0.0f, 0.0f, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        try {
            Bitmap bitmap = mWeakBitmap != null ? mWeakBitmap.get() : null;
          
            // Bitmap not loaded.
            if (bitmap == null || bitmap.isRecycled()) {
                Drawable drawable = getDrawable();
                if (drawable != null) {
					bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
					Canvas bitmapCanvas = new Canvas(bitmap);
					drawable.setBounds(0, 0, getWidth(), getHeight());
					drawable.draw(bitmapCanvas);

                    // If mask is already set, skip and use cached mask.
					if (mMaskBitmap == null || mMaskBitmap.isRecycled()) {
                        mMaskBitmap = getBitmap(getContext(), getWidth(), getHeight(), mSvgRawResourceId);
					}

                    // Draw Bitmap.
                    mPaint.reset();
                    mPaint.setFilterBitmap(false);
                    mPaint.setXfermode(sXfermode);
                    bitmapCanvas.drawBitmap(mMaskBitmap, 0.0f, 0.0f, mPaint);

                    mWeakBitmap = new WeakReference<>(bitmap);
                }
            }

            // Bitmap already loaded.
            if (bitmap != null) {
                mPaint.setXfermode(null);
                canvas.drawBitmap(bitmap, 0.0f, 0.0f, mPaint);
                return;
            }
        } catch (Exception e) {
            System.gc();

            Log.e(TAG, String.format("Failed to draw, Id :: %s. Error occurred :: %s", getId(), e.toString()));
        } finally {
            canvas.restoreToCount(i);
        }
	}
	
	public static Bitmap getBitmap(Context context, int width, int height, int svgRawResourceId) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);

        if (svgRawResourceId > 0) {
            SVG svg = SVGParser.getSVGFromInputStream(context.getResources().openRawResource(svgRawResourceId), width, height);
            canvas.drawPicture(svg.getPicture());
        } else {
            canvas.drawRect(new RectF(0.0f, 0.0f, width, height), paint);
        }
        return bitmap;
    }
}
