package project.com.ringsureapp.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

import project.com.ringsureapp.AppData.ContactData;


/**
 * Created by tarun on 24/6/16.
 */

public class MyDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ringsure.db";
    private static final String TABLE_NAME = "Contact";

    private static final String PHONEID = "phone_id";
    private static final String NAME = "name";
    private static final String PHONENO = "number";
    private static final String CALMODEGENERAL = "callGeneral";
    private static final String MESAGEMODEGENERAL = "msggeneral";
    private static final String CALLMODESILENT = "callSilent";
    private static final String MESSAGEMODESILENT = "messageSilent";
    private static final String CATEGORY = "Category";
    private static final String RECENTORDER = "recentOrder";
    private static final String SMSORDER = "smsOrder";


    private static final String CREATE_TABLE = "Create table " + TABLE_NAME + " (" + PHONEID + " text, " + NAME + " text, " +
            PHONENO + " text , " + CALMODEGENERAL + " text, " + MESAGEMODEGENERAL + " text, " + CALLMODESILENT + " text, " + CATEGORY + " text," + MESSAGEMODESILENT + " text, " + RECENTORDER + " int, " + SMSORDER + " int);";

    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public long InsertContact(ContactData data) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PHONEID, data.getId());
        contentValues.put(NAME, data.getName());
        contentValues.put(PHONENO, data.getPhoneNumber());
        contentValues.put(CALMODEGENERAL, data.getCALLMODEGeneral());
        contentValues.put(MESAGEMODEGENERAL, data.getMESSAGEMODEGeneral());
        contentValues.put(CALLMODESILENT, data.getCallModeSilent());
        contentValues.put(MESSAGEMODESILENT, data.getMessageModeSilent());
        contentValues.put(CATEGORY, data.getCategory());
        contentValues.put(RECENTORDER, data.getRecentOrder());
        contentValues.put(SMSORDER, data.getSMSOrder());

        long id = db.insert(TABLE_NAME, null, contentValues);
        return id;
    }

    public boolean UpdateContact(ContactData data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PHONEID, data.getId());
        contentValues.put(NAME, data.getName());
        contentValues.put(PHONENO, data.getPhoneNumber());
        contentValues.put(CALMODEGENERAL, data.getCALLMODEGeneral());
        contentValues.put(MESAGEMODEGENERAL, data.getMESSAGEMODEGeneral());
        contentValues.put(CALLMODESILENT, data.getCallModeSilent());
        contentValues.put(MESSAGEMODESILENT, data.getMessageModeSilent());
        contentValues.put(CATEGORY, data.getCategory());
        contentValues.put(RECENTORDER, data.getRecentOrder());
        contentValues.put(SMSORDER, data.getSMSOrder());
        long id = db.update(TABLE_NAME, contentValues, PHONENO + " = ? ", new String[]{data.getPhoneNumber()});
        return true;
    }


    public int getData(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where " + CATEGORY + " = " + category, null);
        int count = res.getCount();
        res.close();
        return count;
    }

    public String getPhoneMode(String id) {
        String mode = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + CALMODEGENERAL + " from " + TABLE_NAME + " where " + PHONENO + " = '" + id + "'", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            mode = res.getString(res.getColumnIndex(CALMODEGENERAL));
            res.moveToNext();
        }

        res.close();

        return mode;
    }

    public String getPhoneModeSilent(String id) {
        String mode = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + CALLMODESILENT + " from " + TABLE_NAME + " where " + PHONENO + " = '" + id + "'", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            mode = res.getString(res.getColumnIndex(CALLMODESILENT));
            res.moveToNext();
        }

        res.close();

        return mode;
    }


    public ArrayList<ContactData> getAllData(String category, String recentCategory, String smscat, String cat) {
        ArrayList<ContactData> listData = new ArrayList<>();
        HashMap<String, ContactData> contactList = new HashMap<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where " + CATEGORY + " = '" + category + "' OR " + CATEGORY + " = '" + recentCategory + "' OR " + CATEGORY + " = '" + smscat + "' OR " + CATEGORY + " = '" + cat + "'", null);
//        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where " + CATEGORY + " = ?",
//                new String[]{category});
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            ContactData data = new ContactData(res.getString(res.getColumnIndex(PHONEID)), res.getString(res.getColumnIndex(NAME)), res.getString(res.getColumnIndex(PHONENO)), res.getString(res.getColumnIndex(CALMODEGENERAL)), res.getString(res.getColumnIndex(MESAGEMODEGENERAL)), res.getString(res.getColumnIndex(CALLMODESILENT)), res.getString(res.getColumnIndex(MESSAGEMODESILENT)), res.getString(res.getColumnIndex(CATEGORY)), res.getInt(res.getColumnIndex(RECENTORDER)), res.getInt(res.getColumnIndex(SMSORDER)));
            if (!contactList.containsKey( res.getString(res.getColumnIndex(PHONENO))))
                contactList.put( res.getString(res.getColumnIndex(PHONENO)), data);
           // listData.add(data);
            res.moveToNext();
        }
        res.close();
        return new ArrayList<ContactData>(contactList.values());
    }
    public ArrayList<ContactData> getAllDataMessage(String category, String recentCategory, String smscat, String cat) {
        ArrayList<ContactData> listData = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where " + CATEGORY + " = '" + category + "' OR " + CATEGORY + " = '" + recentCategory + "' OR " + CATEGORY + " = '" + smscat + "' OR " + CATEGORY + " = '" + cat + "' ORDER BY " + SMSORDER + " ASC", null);
//        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where " + CATEGORY + " = ?",
//                new String[]{category});
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            ContactData data = new ContactData(res.getString(res.getColumnIndex(PHONEID)), res.getString(res.getColumnIndex(NAME)), res.getString(res.getColumnIndex(PHONENO)), res.getString(res.getColumnIndex(CALMODEGENERAL)), res.getString(res.getColumnIndex(MESAGEMODEGENERAL)), res.getString(res.getColumnIndex(CALLMODESILENT)), res.getString(res.getColumnIndex(MESSAGEMODESILENT)), res.getString(res.getColumnIndex(CATEGORY)), res.getInt(res.getColumnIndex(RECENTORDER)), res.getInt(res.getColumnIndex(SMSORDER)));
            listData.add(data);
            res.moveToNext();
        }
        res.close();
        return listData;
    }
    public ArrayList<ContactData> getAllDataRecent(String category, String recentCategory, String smscat, String cat) {
        ArrayList<ContactData> listData = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where " + CATEGORY + " = '" + category + "' OR " + CATEGORY + " = '" + recentCategory + "' OR " + CATEGORY + " = '" + smscat + "' OR " + CATEGORY + " = '" + cat + "' ORDER BY " + RECENTORDER + " ASC", null);
//        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where " + CATEGORY + " = ?",
//                new String[]{category});
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            ContactData data = new ContactData(res.getString(res.getColumnIndex(PHONEID)), res.getString(res.getColumnIndex(NAME)), res.getString(res.getColumnIndex(PHONENO)), res.getString(res.getColumnIndex(CALMODEGENERAL)), res.getString(res.getColumnIndex(MESAGEMODEGENERAL)), res.getString(res.getColumnIndex(CALLMODESILENT)), res.getString(res.getColumnIndex(MESSAGEMODESILENT)), res.getString(res.getColumnIndex(CATEGORY)), res.getInt(res.getColumnIndex(RECENTORDER)), res.getInt(res.getColumnIndex(SMSORDER)));
            listData.add(data);
            res.moveToNext();
        }
        res.close();
        return listData;
    }
    public int getData2(String id, String cat, String recentcat, String smscat, String cats) {
        String mode = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where " + PHONENO + " = '" + id + "' AND (" + CATEGORY + "= '" + cat + "' OR " + CATEGORY + " = '" + recentcat + "' OR " + CATEGORY + " = '" + smscat + "' OR " + CATEGORY + " = '" + cats + "')", null);
        int size = res.getCount();
        res.close();

        return size;
    }
    public String getMessageMode(String id) {
        String mode = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + MESAGEMODEGENERAL + " from " + TABLE_NAME + " where " + PHONENO + " = '" + id + "'", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            mode = res.getString(res.getColumnIndex(MESAGEMODEGENERAL));
            res.moveToNext();
        }
        res.close();
        return mode;
    }
    public String getMessageModeSilent(String id) {
        String mode = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + MESSAGEMODESILENT + " from " + TABLE_NAME + " where " + PHONENO + " = '" + id + "'", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            mode = res.getString(res.getColumnIndex(MESSAGEMODESILENT));
            res.moveToNext();
        }
        res.close();
        return mode;
    }
    public int getDataFromNumber(String number, String cat) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where " + PHONENO + " = '" + number + "' AND " + CATEGORY + " = '" + cat + "'", null);
        int count = res.getCount();
        res.close();
        return count;
    }
    public ContactData getDataFromNumberSilent(String number) {
        String mode = "";
        ContactData data = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where " + PHONENO + " like '%" + number + "%'", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            data = new ContactData(res.getString(res.getColumnIndex(PHONEID)), res.getString(res.getColumnIndex(NAME)), res.getString(res.getColumnIndex(PHONENO)), res.getString(res.getColumnIndex(CALMODEGENERAL)), res.getString(res.getColumnIndex(MESAGEMODEGENERAL)), res.getString(res.getColumnIndex(CALLMODESILENT)), res.getString(res.getColumnIndex(MESSAGEMODESILENT)), res.getString(res.getColumnIndex(CATEGORY)), res.getInt(res.getColumnIndex(RECENTORDER)), res.getInt(res.getColumnIndex(SMSORDER)));
            res.moveToNext();
        }

        res.close();
        return data;
    }
    public String getDataFromNumberSilent1(String number) {
        String mode = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + CALLMODESILENT + " from " + TABLE_NAME + " where " + PHONENO + " = '" + number + "'", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            mode = res.getString(res.getColumnIndex(CALLMODESILENT));
            res.moveToNext();
        }
        res.close();
        return mode;
    }

    public String getDataFromNumber1(String number) {
        String mode = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + CALMODEGENERAL + " from " + TABLE_NAME + " where " + PHONENO + " = '" + number + "'", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            mode = res.getString(res.getColumnIndex(CALMODEGENERAL));
            res.moveToNext();
        }
        return mode;
    }


    public String getDataFromNumber2(String number) {
        String mode = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + MESAGEMODEGENERAL + " from " + TABLE_NAME + " where " + PHONENO + " = '" + number + "'", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            mode = res.getString(res.getColumnIndex(MESAGEMODEGENERAL));
            res.moveToNext();
        }
        return mode;
    }
    public String getDataFromNumberSilent2(String number) {
        String mode = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + MESSAGEMODESILENT + " from " + TABLE_NAME + " where " + PHONENO + " = '" + number + "'", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            mode = res.getString(res.getColumnIndex(MESSAGEMODESILENT));
            res.moveToNext();
        }
        return mode;
    }
    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
         db.delete(TABLE_NAME,null,null);
    }
}
