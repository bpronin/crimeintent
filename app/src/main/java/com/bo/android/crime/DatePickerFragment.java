package com.bo.android.crime;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import com.bo.android.R;
import com.bo.android.crime.util.DatePickerUtils;

import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {

    public static final String DATE_VALUE = "date_value";

    public static DatePickerFragment newInstance(Date value) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(DATE_VALUE, value);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_date, null);

        Date date = (Date) getArguments().getSerializable(DATE_VALUE);
        DatePicker datePicker = (DatePicker) view.findViewById(R.id.dialog_date_date_picker);
        DatePickerUtils.init(datePicker, date, new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker view, int year, int month, int day) {
                Date date = new GregorianCalendar(year, month, day).getTime();
                getArguments().putSerializable(DATE_VALUE, date);
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent data = new Intent();
                        data.putExtras(getArguments());

                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
                    }
                })
                .create();
    }

}
