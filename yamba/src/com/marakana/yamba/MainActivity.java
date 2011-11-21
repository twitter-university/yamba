package com.marakana.yamba;

import winterwell.jtwitter.TwitterException;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity implements TabListener {
	private static final String TAG = "MainActivity";
	private int themeId = -1;
	static final String FRAGMENT_COMPOSE = "FRAGMENT_COMPOSE";
	static final String FRAGMENT_PREFS = "FRAGMENT_PREFS";

	FragmentTransaction fragmentTransaction;
	PrefsFragment prefsFragment;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.main);

		// Update theme, if previously set
		if (savedInstanceState != null) {
			themeId = savedInstanceState.getInt("theme");
			super.setTheme(themeId);
		}

		// Set the action bar
		this.setActionBar();

		// Do we have twitter object?
		if (((YambaApp) getApplication()).twitter == null) {
			startActivity( new Intent(this, OAuthActivity.class));
		}
	}

	// -- Options Menu methods
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemAuthorize:
			showFragment("com.marakana.yamba.OAuthFragment");
			return true;
		case R.id.toggleTheme:
			if (themeId == android.R.style.Theme_Holo_Light) {
				themeId = android.R.style.Theme_Holo;
			} else {
				themeId = android.R.style.Theme_Holo_Light;
			}
			Log.d(TAG, "themeId=" + themeId);
			this.recreate();
			return true;
		case R.id.itemRefresh:
			startService(new Intent(this, UpdaterService.class));
			return true;
		case R.id.itemCompose:
			showFragment("com.marakana.yamba.ComposeFragment");
			return true;
		case R.id.itemPrefs:
			showFragment("com.marakana.yamba.PrefsFragment");
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/** Saves state of the activity before it gets recreated */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("theme", themeId);
	}

	/* TabListener callback when current tab was re-selected */
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		TimelineFragment timelineFragment = (TimelineFragment) getFragmentManager()
				.findFragmentById(R.id.list);
		timelineFragment.getLoaderManager().initLoader((Integer) tab.getTag(),
				null, timelineFragment);

	}

	/* TabListener callback when tab was selected */
	public void onTabSelected(Tab tab, FragmentTransaction ft) {

	}

	/* TabListener callback was unselected */
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {

	}

	
	// --- Other methods

	public void postToTwitter(String status) {
		// Remove old compose fragment
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();

		Fragment prev = getFragmentManager()
				.findFragmentByTag(FRAGMENT_COMPOSE);
		if (prev != null) {
			fragmentTransaction.remove(prev);
		}
		transaction.commit();

		(new PostToTwitterTask()).execute(status);
	}

	class PostToTwitterTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... status) {
			String ret = null;

			try {
				((YambaApp) getApplication()).twitter.setStatus(status[0]);
				ret = "Successfully posted";
			} catch (TwitterException e) {
				Log.e(TAG, "Failed to post to twitter", e);
				ret = "Failed to post to Twitter";
			}

			return ret;
		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(getApplication(), result, Toast.LENGTH_LONG).show();
		}

	}

	// --- Private helper methods
	private void showFragment(String tag) {
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		getFragmentManager().popBackStackImmediate();
		Fragment fragment = getFragmentManager().findFragmentByTag(tag);

		if (fragment == null) {
			fragment = Fragment.instantiate(this, tag);
			transaction.add(R.id.container, fragment, tag);
			transaction.addToBackStack(tag);
			Log.d(TAG, "Added " + tag);
		}

		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.commit();
		Log.d(TAG, "Showed " + tag);
	}

	private void setActionBar() {
		ActionBar bar = getActionBar();
		bar.addTab(bar.newTab().setText("Timeline")
				.setTag(TimelineFragment.LOADER_TIMELINE).setTabListener(this));
		bar.addTab(bar.newTab().setText("@Mentions")
				.setTag(TimelineFragment.LOADER_MENTIONS).setTabListener(this));

		bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_USE_LOGO);
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		bar.setDisplayShowHomeEnabled(true);
	}
}