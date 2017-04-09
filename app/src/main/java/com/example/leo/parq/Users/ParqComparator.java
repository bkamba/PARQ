package com.example.leo.parq.Users;

import java.util.Comparator;

/**
 * Created by cmaso on 4/8/2017.
 */

public class ParqComparator implements Comparator {
    public int compare(Object o1, Object o2) {
        long x = (long)o1;
        long y = (long)o2;

        if (x < y) {
            return -1;

        } else if (x > y) {
            return 1;

        } else {
            return 0;

        }
    }
}
