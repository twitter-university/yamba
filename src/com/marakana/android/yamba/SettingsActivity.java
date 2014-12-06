package com.marakana.android.yamba;

import android.os.Bundle;

public class SettingsActivity extends SubActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Check if this activity was created before
		if (savedInstanceState == null) {
			// Create a fragment
			SettingsFragment fragment = new SettingsFragment();
			getFragmentManager()
					.beginTransaction()
					.add(android.R.id.content, fragment,
							fragment.getClass().getSimpleName()).commit();
		}
	};
}
