package com.bo.android.crime;

import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.bo.android.R;

class CrimeListViewItemAdapter extends ArrayAdapter<Crime> {

    private static final String DATE_PATTERN = "yyy-MM-dd";

    private CrimeListFragment fragment;

    public CrimeListViewItemAdapter(CrimeListFragment fragment, CrimeLab store) {
        super(fragment.getActivity(), 0, store.getAll());
        this.fragment = fragment;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = fragment.getActivity().getLayoutInflater().inflate(R.layout.list_item_crime, parent, false);
        }

        Crime item = getItem(position);

        TextView titleText = (TextView) view.findViewById(R.id.list_item_title);
        titleText.setText(item.getTitle());

        TextView dateText = (TextView) view.findViewById(R.id.list_item_date);
        dateText.setText(DateFormat.format(DATE_PATTERN, item.getDate()));

        CheckBox solvedCheckBox = (CheckBox) view.findViewById(R.id.list_item_solved_check_box);
        solvedCheckBox.setChecked(item.isSolved());

        return view;
    }

}
