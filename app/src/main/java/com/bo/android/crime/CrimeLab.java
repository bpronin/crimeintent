package com.bo.android.crime;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {

    private static CrimeLab instance;
    private List<Crime> items = new ArrayList<>();

    private CrimeLab() {
        for (int i = 0; i < 100; i++) {
            Crime crime = new Crime();
            crime.setTitle("Crime #" + i);
            crime.setSolved(i % 2 == 0);

            items.add(crime);
        }
    }

    public static CrimeLab getInstance() {
        if (instance == null) {
            instance = new CrimeLab();
        }
        return instance;
    }

    public List<Crime> getItems() {
        return items;
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

}
