package ru.spbau.banksms;

import android.app.Activity;
import android.content.SharedPreferences;

public class Preferences {
    private static String PREFERENCES_FILE_NAME = "Preferences";
    private static String PREFERENCES_CHOSEN_CARD_KEY = "card_id";

    private Activity activity;

    public Preferences(Activity activity) {
        this.activity = activity;
    }

    public int getChosenCardId() {
        SharedPreferences sPref = activity.getSharedPreferences(PREFERENCES_FILE_NAME, activity.MODE_PRIVATE);
        return sPref.getInt(PREFERENCES_CHOSEN_CARD_KEY, -1);
    }

    public void setChosenCardId(int id) {
        SharedPreferences sPref = activity.getSharedPreferences(PREFERENCES_FILE_NAME, activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt(PREFERENCES_CHOSEN_CARD_KEY, id);
        ed.commit();
    }
}
