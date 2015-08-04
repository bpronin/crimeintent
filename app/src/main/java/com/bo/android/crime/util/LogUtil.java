package com.bo.android.crime.util;

import android.util.Log;
import com.bo.android.crime.CrimeJsonSerializer;

public class LogUtil {

    public static void debug(Object owner, String message) {
        Log.d(owner.getClass().getName(), message);
    }

    public static void info(Object owner, String message) {
        Log.i(owner.getClass().getName(), message);
    }

    public static void error(Object owner, Exception x) {
        Log.e(owner.getClass().getName(), x.getMessage(), x);
    }

}
