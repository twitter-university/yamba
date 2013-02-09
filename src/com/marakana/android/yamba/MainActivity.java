package com.marakana.android.yamba;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.activity_main);
	}

	private static MenuItem statusItem;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		statusItem = menu.findItem(R.id.item_status);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (statusItem == null)
			return true;

		Fragment frag = getFragmentManager().findFragmentById(R.id.fragment_status);
		if (frag!=null && frag.isAdded()) {
			Log.d("Yamba", "onPrepareOptionsMenu false");
			statusItem.setVisible(false);
		} else {
			Log.d("Yamba", "onPrepareOptionsMenu true");
			statusItem.setVisible(true);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_status:
			startActivity(new Intent("com.marakana.android.yamba.action.STATUS"));
			return true;
		case R.id.item_prefs:
			startActivity(new Intent(this, PrefsActivity.class));
		case R.id.item_refresh:
			startService(new Intent(this, RefreshService.class));
			return true;
		case R.id.item_purge:
			getContentResolver().delete(StatusContract.CONTENT_URI, null, null);
			return true;
		default:
			return false;
		}
	}

}
