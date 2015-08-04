package com.bo.android.crime.util;

import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

import static android.widget.DatePicker.*;

public abstract class DatePickerUtils {

    private DatePickerUtils() {
    }

    public static void init(DatePicker datePicker, Date date, OnDateChangedListener listener){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        datePicker.init(year, month, day, listener);
    }

}
