package com.bo.android.crime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.DateFormat;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import com.bo.R;

public class CrimeListFragment extends ListFragment {

    private static final String DATE_PATTERN = "yyy-MM-dd";
    private CrimeLab store = CrimeLab.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new CrimeAdapter(store));
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.crimes_title);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter().notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Crime crime = adapter().getItem(position);

        Intent intent = new Intent(getActivity(), CrimePagerActivity.class);
        intent.putExtra(CrimeFragment.ITEM_ID, crime.getId());
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        adapter().notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                Crime crime = new Crime();
                store.addItem(crime);

                Intent intent = new Intent(getActivity(), CrimePagerActivity.class);
                intent.putExtra(CrimeFragment.ITEM_ID, crime.getId());
                startActivityForResult(intent, 0);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected CrimeAdapter adapter() {
        return (CrimeAdapter) getListAdapter();
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
