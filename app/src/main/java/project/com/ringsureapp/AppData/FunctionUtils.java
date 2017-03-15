package project.com.ringsureapp.AppData;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by Sahil on 9/6/2016.
 */
public class FunctionUtils {

    private static Context context;
    private static FunctionUtils mInstance;

    public FunctionUtils(Context context) {
        this.context = context;
        mInstance = this;
    }

    public static FunctionUtils getInstance() {
        return mInstance;
    }

    public ArrayList<ContactData> getContacts() {
        HashMap<String, ContactData> contactList = new HashMap<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String phone = "";
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                while (pCur.moveToNext()) {
                    phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                pCur.close();
                if (!TextUtils.isEmpty(phone)) {
                    phone = getFormattedNumber(phone);
                    if (phone.length() > 8) {
                        ContactData data = new ContactData(id, name, phone, "General", "General", "Silent", "Silent", "", 0, 0);
                        if (!contactList.containsKey(phone)) {
                            contactList.put(phone, data);
                            Log.d("ss", cur.getPosition() + ": " + phone);
                        }
                    }
                }
            }
        }
        return new ArrayList<ContactData>(contactList.values());
    }

    public ArrayList<ContactData> getSortedList(ArrayList<ContactData> contactDatas) {
        Collections.sort(contactDatas, new Comparator<ContactData>() {
            @Override
            public int compare(ContactData lhs, ContactData rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        return contactDatas;
    }

    public ArrayList<ContactData> getRecentContacts() {
        HashMap<String, ContactData> contactList = new HashMap<>();

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

                if (phoneNumber != null && !TextUtils.isEmpty(phoneNumber)) {
                    phoneNumber = getFormattedNumber(phoneNumber);
                    if (title == null)
                        title = phoneNumber;

                    ContactData data = new ContactData(phoneNumber, title, phoneNumber, "General", "General", "Silent", "Silent", "", 0, 0);
                   // if (!contactList.containsKey(phoneNumber)) {
                        contactList.put(phoneNumber, data);
                   // }
                }
            }
        }

        return new ArrayList<ContactData>(contactList.values());
    }

    public ArrayList<ContactData> fetchInbox(Context context) {
        HashMap<String, ContactData> contactList = new HashMap<>();

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

            if (!TextUtils.isEmpty(phoneNumber)) {
                phoneNumber = getFormattedNumber(phoneNumber);
                if (displayName == null)
                    displayName = phoneNumber;

                ContactData data = new ContactData(phoneNumber, displayName, phoneNumber, "General", "General", "Silent", "Silent", "", 0, 0);
                contactList.put(phoneNumber, data);

            }
        }
        return new ArrayList<ContactData>(contactList.values());

    }

    public String getFormattedNumber(String number) {
        number = number.replaceAll(" ", "").replace("+91", "0").replace("-", "").replace("(", "").replace(")", "").trim();
        if (number.length() < 11) {
            number = "0" + number;
        }
        return number;
    }

    public JSONObject getJSONObject(ContactData contactData) {
        try {
            return new JSONObject().put("ContactMobileNumber", contactData.getPhoneNumber())
                    .put("ContactName", getEncodedName(contactData.getName()))
                    .put("ContactEmail", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getEncodedName(String name) {
        try {
            byte[] data = name.getBytes("UTF-8");
            return Base64.encodeToString(data, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean b = cm.getActiveNetworkInfo() != null;
        return b;
    }

    /*public void showToast(String send) {
        Toast.makeText(context, send, Toast.LENGTH_SHORT).show();
    }*/
}
