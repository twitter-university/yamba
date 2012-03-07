package com.marakana.android.yamba;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.Status;
import winterwell.jtwitter.TwitterException;
import winterwell.jtwitter.URLConnectionHttpClient;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

public class YambaApp extends Application implements
		OnSharedPreferenceChangeListener {
	private static final String TAG = YambaApp.class.getSimpleName();
	public static final String NEW_STATUS_BROADCAST = "com.marakana.broadcast.NEW_STATUS";
	private SharedPreferences prefs;
	private Twitter twitter;

	/** Called when app is created. */
	@Override
	public void onCreate() {
		super.onCreate();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
		Log.d(TAG, "onCreated");
	}

	/** Returns Twitter object, lazily initializing it when needed. */
	public Twitter getTwitter() {
		if (twitter == null) {
			// Read preferences
			String username = prefs.getString("username", "");
			String password = prefs.getString("password", "");
			String server = prefs.getString("server", "");
			Log.d(TAG, String.format("%s/%s@%s", username, password, server));

			// Temporary override of timeout
			URLConnectionHttpClient http = new URLConnectionHttpClient(
					username, password);
			http.setTimeout(60000);

			twitter = new Twitter(username, password);
			twitter.setAPIRootUrl(server);
		}
		return twitter;
	}

	/** Returns the refresh interval preference. */
	public long getInterval() {
		return Long.parseLong(prefs.getString("interval", "0"));
	}

	/** Connects to twitter, gets the timeline, inserts it into DB. */
	public void fetchTimeline() {
		boolean hasNewStatuses = false;

		// Get the friends timeline
		try {
			List<Status> timeline = getTwitter().getHomeTimeline();
			for (Status status : timeline) {
				ContentValues values = StatusData.statusToValues(status);
				Uri uri = getContentResolver().insert(
						StatusProvider.CONTENT_URI, values);

				if (!"-1".equals(uri.getLastPathSegment())) {
					hasNewStatuses = true;
				}
				Log.d(TAG,
						String.format("%s: %s", status.user.name, status.text));
			}
		} catch (TwitterException e) {
			Log.e(TAG, "Failed to pull timeline", e);
		}
		// Send broadcast
		if (hasNewStatuses)
			sendBroadcast(new Intent(NEW_STATUS_BROADCAST));
	}

	/** Called when prefs change. */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		twitter = null;
		setupRefreshAlarm();
	}

	/** Schedule RefreshService to run periodically. */
	public void setupRefreshAlarm() {
		Intent intent = new Intent(this, RefreshService.class);
		PendingIntent pendingIntent = PendingIntent.getService(this, -1,
				intent, PendingIntent.FLAG_ONE_SHOT);

		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		long interval = getInterval() * 1000;
		if (interval == 0) {
			// Cancel pending intents
			alarmManager.cancel(pendingIntent);
		} else {
			// Setup repeating pending intents
			alarmManager.setInexactRepeating(AlarmManager.RTC,
					System.currentTimeMillis() + interval, interval,
					pendingIntent);
		}

		Log.d(TAG, String.format("setupRefreshAlarm for %d s from now",
				getInterval()));
	}
}
