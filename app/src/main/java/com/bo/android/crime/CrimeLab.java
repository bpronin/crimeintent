package com.bo.android.crime;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CrimeLab {

    private static final String TAG = "CrimeLab";
    private static CrimeLab instance;

    private List<Crime> items = new ArrayList<>();
    private CrimeJsonSerializer serializer;

    private CrimeLab(Context context) {
        serializer = new CrimeJsonSerializer(this, context);
        load();
/*
        for (int i = 0; i < 100; i++) {
            Crime crime = new Crime();
            crime.setTitle("Crime #" + i);
            crime.setSolved(i % 2 == 0);

            items.add(crime);
        }
*/
    }

    public static CrimeLab getInstance(Context context) {
        if (instance == null) {
            instance = new CrimeLab(context);
        }
        return instance;
    }

    public void add(Crime item) {
        items.add(item);
    }

    public void remove(Crime item) {
        items.remove(item);
    }

    public List<Crime> getAll() {
        return Collections.unmodifiableList(items);
    }

    public void clear() {
        items.clear();
    }

    public Crime getById(UUID itemId) {
        return items.get(indexOfId(itemId));
    }

    public int indexOfId(UUID itemId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(itemId)) {
                return i;
            }
        }
        return -1;
    }

    public void load() {
        try {
            serializer.load();
        } catch (Exception x) {
            Log.e(TAG, x.getMessage(), x);
        }
    }

    public void save() {
        try {
            serializer.save();
        } catch (Exception x) {
            Log.e(TAG, x.getMessage(), x);
        }
    }

}
