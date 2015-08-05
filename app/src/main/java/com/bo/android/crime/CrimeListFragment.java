package com.bo.android.crime;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.*;
import android.widget.Button;
import android.widget.ListView;
import com.bo.android.R;
import com.bo.android.crime.util.ActionBarUtil;

public class CrimeListFragment extends ListFragment {

    private CrimeLab store;
    private boolean subtitleVisible;
    private CrimeListViewItemAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        store = CrimeLab.getInstance(getActivity());
        adapter = new CrimeListViewItemAdapter(this, store);

        setListAdapter(adapter);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setupActionBar();

        /* return super.onCreateView(inflater, container, savedInstanceState); */

        View view = inflater.inflate(R.layout.list_content, container, false);
        Button emptyListButton = (Button) view.findViewById(R.id.emptyListButton);
        emptyListButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onNewItem();
            }

        });
        return view;
    }

    private void setupActionBar() {
        if (subtitleVisible) {
            ActionBarUtil.setSubtitle(getActivity(), R.string.subtitle);
        }
    }

/*
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setEmptyText("List is empty. Please add something.");
        super.onViewCreated(view, savedInstanceState);
    }
*/

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Crime crime = adapter.getItem(position);

        Intent intent = new Intent(getActivity(), CrimePagerActivity.class);
        intent.putExtra(CrimeFragment.ITEM_ID, crime.getId());
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem showSubtitle = menu.findItem(R.id.menu_item_show_subtitle);
        if (subtitleVisible && showSubtitle != null) {
            showSubtitle.setTitle(R.string.hide_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                onNewItem();
                return true;
            case R.id.menu_item_show_subtitle:
                onSwitchSubtitle(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onNewItem() {
        Crime crime = new Crime();
        store.addItem(crime);

        Intent intent = new Intent(getActivity(), CrimePagerActivity.class);
        intent.putExtra(CrimeFragment.ITEM_ID, crime.getId());
        startActivityForResult(intent, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void onSwitchSubtitle(MenuItem item) {
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            if (actionBar.getSubtitle() == null) {
                actionBar.setSubtitle(R.string.subtitle);
                item.setTitle(R.string.hide_subtitle);
                subtitleVisible = true;
            } else {
                actionBar.setSubtitle(null);
                item.setTitle(R.string.show_subtitle);
                subtitleVisible = false;
            }
        }
    }

}
