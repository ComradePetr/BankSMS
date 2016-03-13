package ru.spbau.banksms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CardsOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "CardsOpenHelper";

    private static final int VERSION = 3;

    private static final String DATABASE_NAME = "sms_db";


    private static final String CARDS_TABLE = "cards";
    private static final String CARD_ID = "id";
    private static final String CARD_NUMBER = "number";
    private static final String CARD_THREAD_ID = "thread_id";
    private static final String CARD_SMS_SENDER = "sender";

    private static final String CREATE_CARDS_TABLE = "CREATE TABLE " + CARDS_TABLE + " ("
            + CARD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CARD_NUMBER + " INTEGER NOT NULL, "
            + CARD_THREAD_ID + " INTEGER NOT NULL, "
            + CARD_SMS_SENDER + " STRING NOT NULL);";
    private static final String DROP_CARDS_TABLE = "DROP TABLE IF EXISTS " + CARDS_TABLE + ";";


    private static final String RULES_TABLE = "rules";
    private static final String RULE_ID = "id";
    private static final String RULE_CARD_ID = "card_id";
    private static final String RULE_REGEXP = "regexp";
    private static final String RULE_COEFF = "coeff";

    private static final String CREATE_RULES_TABLE = "CREATE TABLE " + RULES_TABLE + " ("
            + RULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RULE_CARD_ID + " INTEGER NOT NULL REFERENCES "
            + CARDS_TABLE + "(" + CARD_ID + ") ON UPDATE CASCADE ON DELETE CASCADE,"
            + RULE_REGEXP + " STRING NOT NULL, "
            + RULE_COEFF + " INTEGER NOT NULL);";

    private static final String DROP_RULES_TABLE = "DROP TABLE IF EXISTS " + RULES_TABLE + ";";

    public CardsOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CARDS_TABLE);
        db.execSQL(CREATE_RULES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_RULES_TABLE);
        db.execSQL(DROP_CARDS_TABLE);
        onCreate(db);
    }

    ContentValues cardToContentValues(Card card) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CARD_ID, card.id);
        contentValues.put(CARD_NUMBER, card.number);
        contentValues.put(CARD_THREAD_ID, card.threadId);
        contentValues.put(CARD_SMS_SENDER, card.smsSender);
        return contentValues;
    }

    Card cursorToCard(Cursor cursor) {
        return new Card(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getString(3));
    }

    public void addCard(Card card) throws SQLException {
        ContentValues contentValues = cardToContentValues(card);
        getWritableDatabase().insertOrThrow(CARDS_TABLE, null, contentValues);
    }

    public List<Card> getAllCards() {
        Cursor cursor = getReadableDatabase().query(CARDS_TABLE, null, null, null, null, null, null, null);
        ArrayList<Card> list = new ArrayList<Card>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursorToCard(cursor));
            }
            cursor.close();
        }
        return list;
    }

    public Card getCardById(int id) {
        Cursor cursor = getReadableDatabase().query(CARDS_TABLE, null,
                CARD_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor == null)
            return null;
        if (cursor.isAfterLast()) {
            cursor.close();
            return null;
        }
        cursor.moveToNext();
        Card card = cursorToCard(cursor);
        cursor.close();
        return card;
    }

    Rule cursorToRule(Cursor cursor) {
        return new Rule(cursor.getString(2), cursor.getInt(3));
    }

    public List<Rule> getRulesByCardId(int id) {
        Cursor cursor = getReadableDatabase().query(RULES_TABLE, null,
                RULE_CARD_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null, null);
        ArrayList<Rule> list = new ArrayList<Rule>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursorToRule(cursor));
            }
            cursor.close();
        }
        return list;
    }
}