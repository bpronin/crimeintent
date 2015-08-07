package com.bo.android.crime.util;

import java.util.Comparator;

public abstract class PriorityComparator<T> implements Comparator<T> {

    @Override
    public int compare(T o1, T o2) {
        int p1 = getPriority(o1);
        int p2 = getPriority(o2);
        if (p1 < 0) {
            return 1;
        } else if (p2 < 0) {
            return -1;
        } else {
            return Integer.compare(p1, p2);
        }
    }

    protected abstract int getPriority(T entry);
}
