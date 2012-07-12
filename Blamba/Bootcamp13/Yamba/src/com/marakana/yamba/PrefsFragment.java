package com.marakana.yamba;

import android.os.Bundle;
import android.preference.PreferenceFragment;


/**
 * PrefsFragment
 */
public class PrefsFragment extends PreferenceFragment {

    /**
     * @see android.preference.PreferenceFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
    }

}
