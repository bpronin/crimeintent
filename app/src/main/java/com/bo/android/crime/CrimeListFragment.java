package com.bo.android.crime;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bo.R;

import java.util.List;

public class CrimeListFragment extends ListFragment {

    private static final String DATE_PATTERN = "yyy-MM-dd";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.crimes_title);

        setListAdapter(new CrimeAdapter(CrimeLab.getInstance()));
    }

    @Override
    public void onResume() {
        super.onResume();
        getAdapter().notifyDataSetChanged();
    }

    protected CrimeAdapter getAdapter() {
        return (CrimeAdapter)getListAdapter();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Crime item = getAdapter().getItem(position);

        Intent intent = new Intent(getActivity(), CrimePagerActivity.class);
        intent.putExtra(CrimeFragment.ITEM_ID, item.getId());
        startActivity(intent);
    }

    private class CrimeAdapter extends ArrayAdapter<Crime> {

        public CrimeAdapter(CrimeLab store) {
            super(getActivity(), 0, store.getItems());
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = getActivity().getLayoutInflater().inflate(R.layout.list_item_crime, parent, false);
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
}
