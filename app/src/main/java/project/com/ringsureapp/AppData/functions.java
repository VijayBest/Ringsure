package project.com.ringsureapp.AppData;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by apple on 04/07/16.
 */
public class functions {

    private static ArrayList<ContactData> listData = new ArrayList<>();

    // Read Contact from contact list method
    public static ArrayList<ContactData> ReadContact(Context context) {
        listData.clear();
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String phone = "";
                String finalPhone = "";
                String finalPhone1 = "";
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                while (pCur.moveToNext()) {
                    phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                pCur.close();

                if (!phone.equals("")) {

                    String phone2 = phone.replaceAll(" ", "");
                    if (phone2.contains("+91")) {
                        finalPhone = phone2.replace("+91", "0");
                    } else if (phone2.contains("-")) {
                        finalPhone = phone2.replaceAll("-", "");
                    } else {
                        finalPhone = phone2;
                    }

                    String num = finalPhone.replace("(", "");
                    String num1 = num.replace(")", "");

                    if (num1.trim().length() < 11) {
                        finalPhone1 = "0" + num1;
                    } else {
                        finalPhone1 = num1;
                    }

                    if (finalPhone1.trim().length() > 8) {
                        ContactData data = new ContactData(id, name, finalPhone1.replaceAll(" ", ""), "General", "General", "Silent", "Silent", "", 0, 0);
                        listData.add(data);
                    }
                }
            }
        }
        return listData;
    }

    public static ArrayList<ContactData> RemoveDuplicat(ArrayList<ContactData> data, boolean sort) {
        ArrayList<ContactData> dataList = new ArrayList<>();
        Set<ContactData> hs = new HashSet<>();
        hs.addAll(data);
        dataList.clear();
        dataList.addAll(hs);
        if (sort) {
            Collections.sort(dataList, new Comparator<ContactData>() {
                @Override
                public int compare(ContactData lhs, ContactData rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });
        }
        return dataList;
    }

    public static ArrayList<ContactData> CustomRemoveDuplicat(ArrayList<ContactData> data, boolean sort) {
        List<ContactData> allEvents = data;
        ArrayList<ContactData> noRepeat = new ArrayList<ContactData>();
        Log.e("crash", "" + allEvents.size());

        if (allEvents.size() > 0) {
            for (int j = 0; j < allEvents.size(); j++) {
                ContactData event = allEvents.get(j);

                boolean isFound = false;
                // check if the event name exists in noRepeat
                for (ContactData e : noRepeat) {
                    if (e.getName().equals(event.getName()) || e.getName().equals(event.getPhoneNumber())) {
                        isFound = true;
                    }
                }
                if (!isFound) noRepeat.add(event);
            }
            if (sort) {
                Collections.sort(noRepeat, new Comparator<ContactData>() {
                    @Override
                    public int compare(ContactData lhs, ContactData rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });
            }
        }
        return noRepeat;
    }

    // Get  call Logs of User mobile
    public static ArrayList<ContactData> getRecentContacts(Context context) {
        listData.clear();
        String finalPhone = "", finalPhone1 = "";
        Uri queryUri = android.provider.CallLog.Calls.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                CallLog.Calls._ID,
                CallLog.Calls.NUMBER,
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.DATE};

        String sortOrder = String.format("%s limit 500 ", CallLog.Calls.DATE + " DESC");
        Cursor cursor = null;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            cursor = context.getContentResolver().query(queryUri, projection, null, null, sortOrder);
            while (cursor.moveToNext()) {
                String phoneNumber = cursor.getString(cursor
                        .getColumnIndex(CallLog.Calls.NUMBER));

                String title = (cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)));
                if (phoneNumber == null)
                    continue;

                if (!phoneNumber.equals("")) {
                    String phone2 = phoneNumber.replaceAll(" ", "");
                    if (phone2.contains("+91")) {
                        finalPhone = phone2.replace("+91", "0");
                    } else if (phone2.contains("-")) {
                        finalPhone = phone2.replaceAll("-", "");
                    } else {
                        finalPhone = phone2;
                    }

                    String num = finalPhone.replace("(", "");
                    String num1 = num.replace(")", "");

                    if (num1.trim().length() < 11) {
                        finalPhone1 = "0" + num1;
                    } else {
                        finalPhone1 = num1;
                    }
                    if (title == null)
                        title = finalPhone1;
                    ContactData data = new ContactData(finalPhone1.replaceAll(" ", ""), title, finalPhone1.replaceAll(" ", ""), "General", "General", "Silent", "Silent", "", 0, 0);
                    listData.add(data);
                }
            }
        }
        return listData;
    }
    // Get all SMS from SMS Inbox
    public static ArrayList<ContactData> fetchInbox(Context context) {
        listData.clear();
        String finalPhone = "", finalPhone1 = "";
        Uri uriSms = Uri.parse("content://sms/inbox");
        Cursor cursor = context.getContentResolver().query(uriSms, new String[]{"_id", "address", "date", "body"}, null, null, null);

        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            String phoneNumber = cursor.getString(1);
            String body = cursor.getString(3);

            String displayName = null;
            Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
            Cursor c = context.getContentResolver().query(lookupUri, new String[]{ContactsContract.Data.DISPLAY_NAME}, null, null, null);
            try {
                c.moveToFirst();
                displayName = c.getString(0);
            } catch (Exception e) {
            }
            c.close();

            if (!phoneNumber.equals("")) {
                String phone2 = phoneNumber.replaceAll(" ", "");
                if (phone2.contains("+91")) {
                    finalPhone = phone2.replace("+91", "0");
                } else if (phone2.contains("-")) {
                    finalPhone = phone2.replaceAll("-", "");
                } else {
                    finalPhone = phone2;
                }
                String num = finalPhone.replace("(", "");
                String num1 = num.replace(")", "");

                if (num1.trim().length() < 11) {
                    finalPhone1 = "0" + num1;
                } else {
                    finalPhone1 = num1;
                }
                if (displayName == null)
                    displayName = finalPhone1;

                ContactData data = new ContactData(finalPhone1.replaceAll(" ", ""), displayName, finalPhone1.replaceAll(" ", ""), "General", "General", "Silent", "Silent", "", 0, 0);
                listData.add(data);
            }
        }
        return listData;
    }

}
