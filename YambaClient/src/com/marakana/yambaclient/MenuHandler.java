package com.marakana.yambaclient;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MenuHandler {
	static final String TAG = "MenuHandler";

	public static boolean onCreateOptionsMenu(Activity activity, Menu menu) {
		activity.getMenuInflater().inflate(R.menu.menu, menu);
		Log.d(TAG, "onCreateOptionsMenu");
		return true;
	}

	public static boolean onOptionsItemSelected(Activity activity, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_timeline:
			activity.startActivity(new Intent(activity, TimelineActivity.class)
					.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			return true;
		case R.id.item_update:
			activity.startActivity(new Intent(activity, StatusActivity.class)
					.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			return true;
		case R.id.item_start_service:
			try {
				activity.startService(new Intent(
						"com.marakana.yambaservice.UpdateService").putExtra(
						"timestamp", System.currentTimeMillis()));
			} catch (Exception e) {
				Log.e(TAG, "Can't start service", e);
			}
			return true;
		case R.id.item_stop_service:
			try {
				activity.stopService(new Intent(
						"com.marakana.yambaservice.UpdateService"));
			} catch (Exception e) {
				Log.e(TAG, "Can't stop service", e);
			}
			return true;
		case R.id.item_refresh:
			activity.startService(new Intent("com.marakana.yambadata.RefreshService"));
			return true;
		case R.id.item_prefs:
			activity.startActivity(new Intent(activity, PrefsActivity.class));
			return true;
		default:

			return false;
		}
	}
}
