package com.github.nitrico.mvp;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

public class Utils {

    public static boolean isPortrait(Context context) {
        Configuration config = context.getResources().getConfiguration();
        return config.orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public static int dp(Context context, int dp) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
    }

    public static void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public static boolean hasNavigationBar(Context context) {
        if (Build.VERSION.SDK_INT < 19) return false; // could have but won't be translucent
        return !ViewConfiguration.get(context).hasPermanentMenuKey();
    }

    public static int getNavigationBarHeight(Context context) {
        if (!hasNavigationBar(context)) return 0;
        Resources r = context.getResources();
        DisplayMetrics dm = r.getDisplayMetrics();
        Configuration config = r.getConfiguration();
        boolean canMove = dm.widthPixels != dm.heightPixels && config.smallestScreenWidthDp < 600;
        if (canMove && config.orientation == Configuration.ORIENTATION_LANDSCAPE) return 0;
        String s = isPortrait(context) ? "navigation_bar_height" : "navigation_bar_height_landscape";
        int id = r.getIdentifier(s, "dimen", "android");
        if (id > 0) return r.getDimensionPixelSize(id);
        return 0;
    }

    public static int getNavigationBarWidth(Context context) {
        if (!hasNavigationBar(context)) return 0;
        Resources r = context.getResources();
        DisplayMetrics dm = r.getDisplayMetrics();
        Configuration config = r.getConfiguration();
        boolean canMove = dm.widthPixels != dm.heightPixels && config.smallestScreenWidthDp < 600;
        if (canMove && config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            int id = r.getIdentifier("navigation_bar_width", "dimen", "android");
            if (id > 0) return r.getDimensionPixelSize(id);
        }
        return 0;
    }

}
