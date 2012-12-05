package com.marakana.android.yamba;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Debug;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClient.TimelineProcessor;
import com.marakana.android.yamba.clientlib.YambaClient.TimelineStatus;

public class RefreshService extends IntentService {

	private static final String TAG = "RefreshService";
	private YambaClient yambaClient;
	private FriendsTimeline friendsTimeline;

	public RefreshService() {
		super(TAG);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		Debug.startMethodTracing("RefreshService.trace");

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String username = prefs.getString("username", null);
		String password = prefs.getString("password", null);
		String server = prefs.getString("server", null);
		yambaClient = new YambaClient(username, password);
		if (server != null && server.length() > 0)
			yambaClient.setApiRoot(server);

		friendsTimeline = new FriendsTimeline();

		Log.d(TAG, String.format("onCreate() yambaClient with %s:%s@%s",
				username, password, server));
	}

	@Override
	public void onHandleIntent(Intent intent) {
		Log.d(TAG, "onStartCommand");

		// Fetches friends timeline from the cloud
		try {
			yambaClient.fetchFriendsTimeline(friendsTimeline); // could take
																// awhile
		} catch (Exception e) {
			Log.e(TAG, "Failed to fetch timeline", e);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		
		Debug.stopMethodTracing();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/** Processes callbacks from YambaClientLib for available statuses. */
	class FriendsTimeline implements TimelineProcessor {

		@Override
		public void onStartProcessingTimeline() {
			Log.d(TAG, "onStartProcessingTimeline");
		}

		@Override
		public void onTimelineStatus(TimelineStatus status) {
			Log.d(TAG,
					String.format("%s: %s", status.getUser(),
							status.getMessage()));

			ContentValues values = new ContentValues();
			values.put(StatusContract.Columns._ID, status.getId());
			values.put(StatusContract.Columns.CREATED_AT, status.getCreatedAt()
					.getTime());
			values.put(StatusContract.Columns.USER, status.getUser());
			values.put(StatusContract.Columns.TEXT, status.getMessage());

			Uri uri = getContentResolver().insert(StatusContract.CONTENT_URI,
					values);

			// Do we have a new status?
			if (uri != null) {
				Intent intent = new Intent("com.marakana.android.yamba.NEW_STATUS");
				intent.putExtra(StatusContract.Columns.USER, status.getUser())
						.putExtra(StatusContract.Columns.TEXT, status.getMessage());
				sendBroadcast(intent);
			}
		}

		@Override
		public boolean isRunnable() {
			return true;
		}

		@Override
		public void onEndProcessingTimeline() {
			Log.d(TAG, "onEndProcessingTimeline");
		}

	}
}
