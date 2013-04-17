package com.twitter.yamba;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_settings);

		FragmentManager fragmentManager = getFragmentManager();
		SettingsFragment fragment = new SettingsFragment();
		fragmentManager.beginTransaction().
			add(android.R.id.content, fragment,SettingsFragment.class.getSimpleName()).
			commit();
	}
}
