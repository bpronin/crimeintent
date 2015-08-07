package com.bo.android.crime.util;

import android.app.Activity;
import android.database.Cursor;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.provider.ContactsContract.CommonDataKinds.Phone;
import static android.provider.ContactsContract.Contacts;

public abstract class ContactUtils {

    private static final String TAG = "ContactUtils";

    private ContactUtils() {
    }

    public static String getContactPreferredPhoneNumber(@NonNull Activity activity, @NonNull String contactId, Integer... phoneTypePriority) {
        List<PhoneNumber> numbers = new ArrayList<>();

        Cursor phones = activity.getContentResolver().query(Phone.CONTENT_URI, null,
                Phone.CONTACT_ID + " = " + contactId, null, null);
        while (phones.moveToNext()) {
            numbers.add(new PhoneNumber(
                    phones.getInt(phones.getColumnIndex(Phone.TYPE)),
                    phones.getString(phones.getColumnIndex(Phone.NUMBER))
            ));
        }
        phones.close();

        if (!numbers.isEmpty()) {
            final List<Integer> priorities = Arrays.asList(phoneTypePriority);
            Collections.sort(numbers, new PriorityComparator<PhoneNumber>() {

                @Override
                protected int getPriority(PhoneNumber entry) {
                    return priorities.indexOf(entry.type);
                }
            });

            return numbers.get(0).number;
        } else {
            return null;
        }
    }

    public static String getContactDisplayName(@NonNull Activity activity, @NonNull String contactId) {
        String result = "<Unknown>";
        Cursor contacts = activity.getContentResolver().query(Contacts.CONTENT_URI, null, "_ID = '" + contactId + "'", null, null);
        if (contacts.moveToFirst()) {
            result = contacts.getString(contacts.getColumnIndex(Contacts.DISPLAY_NAME));
        }
        contacts.close();
        return result;
    }

    private static class PhoneNumber {

        private final int type;
        private final String number;

        public PhoneNumber(int type, String number) {
            this.type = type;
            this.number = number;
        }

        @Override
        public String toString() {
            return "PhoneNumber{" +
                    "type=" + type +
                    ", number='" + number + '\'' +
                    '}';
        }
    }
}
