package com.bo.android.crime;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.bo.android.R;

public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_master_detail;
    }

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    public void onItemSelected(Crime crime) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = new Intent(this, CrimePagerActivity.class);
            intent.putExtra(CrimeFragment.EXTRA_ITEM_ID, crime.getId());
            startActivity(intent);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            Fragment oldDetail = fm.findFragmentById(R.id.detail_fragment_container);
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());

            FragmentTransaction transaction = fm.beginTransaction();
            if (oldDetail != null) {
                transaction.remove(oldDetail);
            }
            transaction.add(R.id.detail_fragment_container, newDetail);
            transaction.commit();
        }
    }

    @Override
    public void onItemUpdated(Crime crime) {
        FragmentManager fm = getSupportFragmentManager();
        CrimeListFragment listFragment = (CrimeListFragment) fm.findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }

}

