package com.marakana.android.yamba;

import java.util.List;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClient.Status;
import com.marakana.android.yamba.clientlib.YambaClientException;

public class RefreshService extends IntentService {

	private static final String TAG = "RefreshService";
	private YambaClient yamba;

	public RefreshService() {
		super(TAG);
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// Get the login info from the preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String username = prefs.getString("username", null);
		String password = prefs.getString("password", null);
		if (username == null || password == null) {
			yamba = null;
		} else {
			yamba = new YambaClient(username, password);
		}

		Log.d(TAG, "onCreated");
	}

	/** Executes on a worker thread. */
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onHandleIntent");

		// Do we have yamba client?
		if (yamba == null) {
			noYamba();
			return;
		}

		// Pull the data from the cloud
		try {
			ContentValues values = new ContentValues();

			List<Status> timeline = yamba.getTimeline(20);
			for (Status status : timeline) {
				// Insert into db
				values.clear();
				values.put(StatusContract.Columns.ID, status.getId());
				values.put(StatusContract.Columns.USER, status.getUser());
				values.put(StatusContract.Columns.MESSAGE, status.getMessage());
				values.put(StatusContract.Columns.CREATED_AT, status
						.getCreatedAt().getTime());
				getContentResolver().insert(StatusContract.CONTENT_URI, values);

				Log.d(TAG,
						String.format("%s: %s", status.getUser(),
								status.getMessage()));
			}
		} catch (YambaClientException e) {
			e.printStackTrace();
			noYamba();
		}
	}

	private void noYamba() {
		Log.e(TAG, "Wrong username/password");
		startActivity(new Intent(this, PrefsActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroyed");
	}

}
