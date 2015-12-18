package ru.spbau.banksms;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SMSOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "SMSOpenHelper";

    private static final int VERSION = 1;

    private static final String DATABASE_NAME = "sms_db";

    private static final String SMS_TABLE = "sms";
    private static final String SMS_ID = "id";
    private static final String SMS_DATE = "date";
    private static final String SMS_DELTA = "delta";

    private static final String CREATE_TABLE = "CREATE TABLE " + SMS_TABLE + " ("
            + SMS_ID + " INTEGER, " + SMS_DATE + " INTEGER, " + SMS_DELTA + " INTEGER);";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + SMS_TABLE + ";";
    private static final String CLEAR_TABLE = "DELETE FROM "+SMS_TABLE + ";";

    private static final String ADD_SMS = "INSERT INTO " + SMS_TABLE + "(" + SMS_ID + ", "
            + SMS_DATE + ", " + SMS_DELTA + ")" + " VALUES (?, ?, ?);";
    private static final String GET_SMS_BY_ID = "SELECT " + SMS_DATE + ", " + SMS_DELTA
            + " FROM " + SMS_TABLE + " WHERE " + SMS_ID + " = ?;";
    private static final String GET_ALL_SMS = "SELECT " + SMS_ID + ", " + SMS_DATE + ", " + SMS_DELTA
            + " FROM " + SMS_TABLE + " ORDER BY " + SMS_ID + " DESC;";
    private static final String GET_ALL_SMS_ASC = "SELECT " + SMS_ID + ", " + SMS_DATE + ", " + SMS_DELTA
            + " FROM " + SMS_TABLE + " ORDER BY " + SMS_ID + " ASC;";

    public SMSOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public void clear() {
        getWritableDatabase().execSQL(CLEAR_TABLE);
    }

    public void addSms(String id, String date, int delta) {
        Log.d(TAG, String.format("I add %s %s %d", id, date, delta));
        getWritableDatabase().execSQL(ADD_SMS, new String[]{id, date, Integer.toString(delta)});
    }

    public Cursor getSmsById(String id) {
        return getReadableDatabase().rawQuery(GET_SMS_BY_ID, new String[]{id});
    }

    public Cursor getAllSms() {
        return getReadableDatabase().rawQuery(GET_ALL_SMS, null);
    }
    public Cursor getAllSmsAsc() {
        return getReadableDatabase().rawQuery(GET_ALL_SMS_ASC, null);
    }
}