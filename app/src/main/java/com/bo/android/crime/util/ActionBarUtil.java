package com.bo.android.crime.util;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;

public abstract class ActionBarUtil {

    private ActionBarUtil() {
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static ActionBar getActionBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return activity.getActionBar();
        } else {
            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void setSubtitle(Activity activity, String text) {
        ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(text);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void setSubtitle(Activity activity, int resId) {
        ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(resId);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void setDisplayHomeAsUpEnabled(Activity activity, boolean value) {
        ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(value);
        }
    }

}
