package com.bo.android.crime;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import com.bo.R;

import java.util.UUID;

public class CrimePagerActivity extends FragmentActivity {

    private CrimeLab store;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        store = CrimeLab.getInstance();

        ViewPager pager = new ViewPager(this);
        pager.setId(R.id.crime_view_pager);
        pager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return store.getItems().size();
            }

            @Override
            public Fragment getItem(int pos) {
                Crime crime = store.getItems().get(pos);
                return CrimeFragment.newInstance(crime.getId());
            }

        });
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            public void onPageSelected(int pos) {
                setTitle(store.getItems().get(pos).getTitle());
            }
        });

        UUID itemId = (UUID) getIntent().getSerializableExtra(CrimeFragment.ITEM_ID);
        pager.setCurrentItem(store.indexOfId(itemId));

        setContentView(pager);
    }
}