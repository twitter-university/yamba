package com.marakana.yamba;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class PrefsFragment extends PreferenceFragment {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.prefs);
  }
  
  static PrefsFragment newInstance() {
    PrefsFragment f = new PrefsFragment();

    return f;
  }

}
