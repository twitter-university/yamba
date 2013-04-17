package com.twitter.yamba;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Cancel any notifications
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(NotificationReceiver.NOTIFICATION_ID);
	}

	// Called only once to lazily initialize the action bar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// Called each time a menu item is clicked on
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_status_update:
			startActivity(new Intent(this, StatusActivity.class));
			return true;
		case R.id.action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		case R.id.action_refresh:
			startService(new Intent(this, RefreshService.class));
			return true;
		case R.id.action_purge:
			getContentResolver().delete(StatusContract.CONTENT_URI, null, null);
			return true;
		default:
			return false;
		}
	}
}
