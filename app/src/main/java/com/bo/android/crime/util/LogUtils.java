package com.bo.android.crime.util;

import android.util.Log;
import com.bo.android.crime.CrimeJsonSerializer;

public class LogUtils {

    public static void debug(Object owner, String message) {
        Log.d(owner.getClass().getName(), message);
    }

    public static void info(Object owner, String message) {
        Log.i(owner.getClass().getName(), message);
    }

    public static void warn(Object owner, String message) {
        Log.w(owner.getClass().getName(), message);
    }

    public static void error(Object owner, Exception x) {
        error(owner, x.getMessage(), x);
    }

    public static void error(Object owner, String message, Exception x) {
        Log.e(owner.getClass().getName(), message, x);
    }

}
