package ru.spbau.banksms;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rule {
    private static final String TAG = "Rule";

    private String regexp;
    private int coeff;
    private Pattern compiledRegexp;

    public Rule(String regexp, int coeff) {
        this.regexp = regexp;
        this.coeff = coeff;
        compiledRegexp = Pattern.compile(regexp);
    }

    public Double apply(String text) {
        Matcher matcher = compiledRegexp.matcher(text);
        Log.d(TAG, regexp + ", " + text);
        if (matcher.find()) {
            String match = matcher.group(1);
            if (match == null)
                return null;
            Log.d(TAG, regexp + ", " + text + ": " + match);
            double delta;
            try {
                delta = Double.parseDouble(match);
                Log.d(TAG, String.valueOf(delta));
            } catch (NumberFormatException e) {
                return null;
            }
            return coeff * delta;
        }
        return null;
    }
}
