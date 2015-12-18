package ru.spbau.banksms;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private SMSOpenHelper dbHelper;
    private String curTid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "i was created");
        dbHelper = new SMSOpenHelper(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GraphActivity.class);
                startActivity(intent);
            }
        });


        final Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), new String[]{"thread_id", "address"}, null, null, "date DESC");

        if (cursor == null)
            return;

        final ArrayList<String> used = new ArrayList<>(), lst = new ArrayList<>();
        used.add(null);
        lst.add("---");
        while (cursor.moveToNext()) {
            String thread_id = cursor.getString(0);
            if (used.contains(thread_id))
                continue;
            used.add(thread_id);
            lst.add(cursor.getString(1));
        }
        cursor.close();

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, lst.toArray(new String[lst.size()]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    curTid = null;
                else
                    curTid = used.get(position);
                refresh(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        refresh(false);
    }

    private void refresh(boolean needClear) {
        if (curTid == null)
            return;
        if (needClear)
            dbHelper.clear();
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), new String[]{"_id", "date_sent", "body"}, "thread_id = ?", new String[]{curTid}, "date DESC");

        if (cursor == null)
            return;

        String lastBody = "";
        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            Cursor dbCursor = dbHelper.getSmsById(id);
            if (dbCursor != null && dbCursor.moveToFirst())
                continue;

            String msgData = "";
            String date = cursor.getString(1);
            String body = cursor.getString(2);
            if (body.compareTo(lastBody) == 0)
                continue;
            Matcher m = Pattern.compile("([+-]?)(\\d+)\\.((\\d){1,2}) RUR").matcher(body);
            if (m.find()) {
                Log.d(TAG, m.group(0));
                String sgn = m.group(1);
                int delta = Integer.parseInt(m.group(2)) * 100 + Integer.parseInt(m.group(3));
                if (sgn.compareTo("-") == 0 || body.contains("Oplata") || body.contains("snyatie"))
                    delta *= -1;
                dbHelper.addSms(id, date, delta);
            }
        }
        cursor.close();

        Cursor dbCursor = dbHelper.getAllSms();
        if (dbCursor == null)
            return;

        ArrayList<String> lst = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());
        while (dbCursor.moveToNext()) {
            long date = dbCursor.getLong(1);
            int delta = dbCursor.getInt(2);

            lst.add(String.format("%+.2f   %s", delta / 100.0, sdf.format(new Date(date))));
        }
        dbCursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, lst.toArray(new String[lst.size()]));
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
