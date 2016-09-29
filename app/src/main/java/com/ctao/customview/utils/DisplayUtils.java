package com.ctao.customview.utils;

import android.content.res.Resources;

/**
 * Created by A Miracle on 2016/9/29.
 */
public class DisplayUtils {
	private static final float DENSITY = Resources.getSystem().getDisplayMetrics().densityDpi / 160.0f;

    public static int converDip2px(float dpValue) {
        return Math.round(dpValue * DENSITY);
    }

    public static int converPx2dip(float pxValue) {
        return Math.round(pxValue / DENSITY);
    }

}
