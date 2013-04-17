package com.twitter.yamba;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);		
		Log.d("SettingsActivity", "onCreated with bundle: "+savedInstanceState);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		// Check for inbound messages
		String message = getActivity().getIntent().getStringExtra("message");
		
		if(message!=null) {
			Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
		}
	}


}
