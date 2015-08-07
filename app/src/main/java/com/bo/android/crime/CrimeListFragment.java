package com.bo.android.crime;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.ListView;
import com.bo.android.R;

import static android.widget.AbsListView.MultiChoiceModeListener;
import static android.widget.AdapterView.AdapterContextMenuInfo;

public class CrimeListFragment extends ListFragment {

    private static final String TAG = "CrimeListFragment";

    private CrimeLab store;
    /* private boolean subtitleVisible; */
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
        /* return super.onCreateView(inflater, container, savedInstanceState); */
        View view = inflater.inflate(R.layout.list_content, container, false);

        setupActionBar();
        setupListView(view);
        setupEmptyListView(view);

        return view;
    }

    private void setupListView(View view) {
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            registerForContextMenu(listView);
        } else {
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new ListMultiChoiceModeListener());
        }
    }

    private void setupEmptyListView(View view) {
        Button emptyListButton = (Button) view.findViewById(R.id.emptyListButton);
        emptyListButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addItem();
            }

        });
    }

    private void setupActionBar() {
/*
        if (subtitleVisible) {
            ActionBarUtils.setSubtitle(getActivity(), R.string.subtitle);
        }
*/
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
        intent.putExtra(CrimeFragment.EXTRA_ITEM_ID, crime.getId());
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

/*
        MenuItem showSubtitle = menu.findItem(R.id.menu_item_show_subtitle);
        if (subtitleVisible && showSubtitle != null) {
            showSubtitle.setTitle(R.string.hide_subtitle);
        }
*/
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.list_item_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                addItem();
                return true;
/*
            case R.id.menu_item_show_subtitle:
                switchSubtitle(item);
                return true;
*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_remove_crime:
                removeItem(item);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void addItem() {
        Crime crime = new Crime();
        store.add(crime);

        Intent intent = new Intent(getActivity(), CrimePagerActivity.class);
        intent.putExtra(CrimeFragment.EXTRA_ITEM_ID, crime.getId());
        startActivityForResult(intent, 0);
    }

    private void removeItem(MenuItem item) {
        int position = ((AdapterContextMenuInfo) item.getMenuInfo()).position;
        store.remove(adapter.getItem(position));
        adapter.notifyDataSetChanged();
    }

/*
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void switchSubtitle(MenuItem item) {
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
*/

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private class ListMultiChoiceModeListener implements MultiChoiceModeListener {

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.list_item_crime, menu);
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_item_remove_crime:
                    for (int i = adapter.getCount() - 1; i >= 0; i--) {
                        if (getListView().isItemChecked(i)) {
                            store.remove(adapter.getItem(i));
                        }
                    }

                    mode.finish();
                    adapter.notifyDataSetChanged();

                    return true;
                default:
                    return false;
            }
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            Log.i(TAG, "onPrepareActionMode");
            return false;
        }

        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            /* do nothing */
            Log.i(TAG, "onItemCheckedStateChanged");
        }

        public void onDestroyActionMode(ActionMode mode) {
            /* do nothing */
            Log.i(TAG, "onDestroyActionMode");
        }
    }

}
