package com.cs371m.theselfiestudio;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by laurenmckenna on 5/5/15.
 */
public class Settings extends PreferenceActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName("ttt_prefs");
        addPreferencesFromResource(R.xml.preferences);
    }
}
