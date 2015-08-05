package com.bo.android.crime.util;

import android.support.annotation.Nullable;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;

public abstract class ActionBarUtil {

    private ActionBarUtil() {
    }

    /*
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
            ActionBar actionBar = getActionBar(activity);
            if (actionBar != null) {
                actionBar.setSubtitle(text);
            }
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public static void setSubtitle(Activity activity, int resId) {
            ActionBar actionBar = getActionBar(activity);
            if (actionBar != null) {
                actionBar.setSubtitle(resId);
            }
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public static void setDisplayHomeAsUpEnabled(Activity activity, boolean value) {
            ActionBar actionBar = getActionBar(activity);
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(value);
            }
        }
    */


    public static void setSubtitle(@Nullable ActionBar actionBar, String text) {
        if (actionBar != null) {
            actionBar.setSubtitle(text);
        }
    }

    public static void setSubtitle(@Nullable ActionBar actionBar, int resId) {
        if (actionBar != null) {
            actionBar.setSubtitle(resId);
        }
    }

    public static void setDisplayHomeAsUpEnabled(@Nullable ActionBar actionBar, boolean value) {
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(value);
        }
    }

}
