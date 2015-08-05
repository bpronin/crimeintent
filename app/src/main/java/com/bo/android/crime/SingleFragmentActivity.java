package com.bo.android.crime;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.bo.android.R;

public abstract class SingleFragmentActivity extends SherlockFragmentActivity {

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_fragment);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = createFragment();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(R.id.fragment_container, fragment).commit();
        }

    }

    protected abstract Fragment createFragment();

}
