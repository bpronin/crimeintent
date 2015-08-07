package com.bo.android.crime;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.bo.android.R;

public abstract class SingleFragmentActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(getLayoutId());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = createFragment();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(R.id.fragment_container, fragment).commit();
        }

    }

    protected int getLayoutId() {
        return R.layout.activity_fragment;
    }

    protected abstract Fragment createFragment();

}
