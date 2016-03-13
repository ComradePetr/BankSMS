package ru.spbau.banksms;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SMSProvider {
    public class SMSThread {
        public int id;
        public String address;

        public SMSThread(int id, String address) {
            this.id = id;
            this.address = address;
        }
    }

    public class SMS {
        public int id;
        public long date;
        public String body;
        public Double delta;

        public SMS(int id, long date, String body) {
            this.id = id;
            this.date = date;
            this.body = body;
        }
    }

    private Activity activity;

    public SMSProvider(Activity activity) {
        this.activity = activity;
    }

    public ArrayList<SMSThread> getSMSThreadList() {
        Cursor cursor = activity.getContentResolver().query(Uri.parse("content://sms/inbox"),
                new String[]{"thread_id", "address"}, null, null, "date DESC");

        ArrayList<SMSThread> list = new ArrayList<SMSThread>();
        HashSet<Integer> used = new HashSet<Integer>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String address = cursor.getString(1);
                if (!used.contains(id)) {
                    list.add(new SMSThread(id, address));
                    used.add(id);
                }
            }
            cursor.close();
        }
        return list;
    }

    public ArrayList<SMS> getSMSByThreadId(int threadId) {
        Cursor cursor = activity.getContentResolver().
                query(Uri.parse("content://sms/inbox"),
                        new String[]{"_id", "date_sent", "body"},
                        "thread_id = ?", new String[]{String.valueOf(threadId)},
                        "date DESC");

        ArrayList<SMS> list = new ArrayList<SMS>();
        if (cursor == null)
            return list;
        while (cursor.moveToNext()) {
            list.add(new SMS(cursor.getInt(0), cursor.getLong(1), cursor.getString(2)));
        }
        cursor.close();
        return list;
    }

    public static void applyRules(List<SMS> smsList, List<Rule> rules) {
        for (SMS sms : smsList) {
            for (Rule rule : rules) {
                Double delta = rule.apply(sms.body);
                if (delta != null) {
                    sms.delta = delta;
                    break;
                }
            }
        }
    }
}