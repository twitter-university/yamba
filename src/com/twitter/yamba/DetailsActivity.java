package com.twitter.yamba;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

public class DetailsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FragmentManager fragmentManager = getFragmentManager();
		DetailsFragment fragment = new DetailsFragment();
		fragmentManager.beginTransaction().
			add(android.R.id.content, fragment,DetailsFragment.class.getSimpleName()).
			commit();
	}
}
