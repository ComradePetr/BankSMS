package ru.spbau.banksms;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private CardsOpenHelper dbHelper;
    private Preferences preferences;
    private SMSProvider smsProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new CardsOpenHelper(this);
        preferences = new Preferences(this);
        smsProvider = new SMSProvider(this);

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
    }

    private void setListItems(List<String> list) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                list.toArray(new String[list.size()]));
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

    private Card getChosenCard() {
        int id = preferences.getChosenCardId();
        if (id == -1)
            return null;
        return dbHelper.getCardById(id);
    }

    private List<String> makeListItems(ArrayList<SMSProvider.SMS> smsList) {
        ArrayList<String> list = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        for (SMSProvider.SMS sms : smsList) {
            if (sms.delta != null)
                list.add(String.format("%+.2f   %s", sms.delta, simpleDateFormat.format(new Date(sms.date))));
        }
        return list;
    }


    @Override
    protected void onResume() {
        super.onResume();
        Card card = getChosenCard();
        if (card == null) {
            setListItems(Arrays.asList("No card has been chosen"));
        } else {
            List<Rule> rules = dbHelper.getRulesByCardId(card.id);
            ArrayList<SMSProvider.SMS> smsList = smsProvider.getSMSByThreadId(card.threadId);
            SMSProvider.applyRules(smsList, rules);
            setListItems(makeListItems(smsList));
        }
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

        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, ItemListActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
