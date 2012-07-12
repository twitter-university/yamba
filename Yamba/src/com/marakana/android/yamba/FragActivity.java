package com.marakana.android.yamba;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class FragActivity extends Activity {
	public static final int FRAGMENT_PREFS = 1;
	public static final int FRAGMENT_STATUS = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_with_fragment);

		if (savedInstanceState == null) {
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction transaction = fragmentManager
					.beginTransaction();

			switch (getIntent().getIntExtra("fragment_id", -1)) {
			case FRAGMENT_PREFS:
				transaction.add(R.id.fragment_container, new PrefsFragment() );
				this.setTitle(R.string.preferences);
				break;
			case FRAGMENT_STATUS:
				transaction.add(R.id.fragment_container, new StatusFragment() );
				this.setTitle(R.string.status_update);
				break;
			}
			
			transaction.commit();

		}
	}

}
