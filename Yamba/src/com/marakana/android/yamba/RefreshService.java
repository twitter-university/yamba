package com.marakana.android.yamba;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class RefreshService extends IntentService {
	private static final String TAG = RefreshService.class.getSimpleName();

	/** Required default constructor. */
	public RefreshService() {
		super(TAG);
		Log.d(TAG, "RefreshService instantiated");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// Schedule possible future refreshes
		((YambaApp)getApplication()).setupRefreshAlarm(); 
	}

	/**
	 * Called when service is started to handle the intent. Runs on a separate,
	 * non-UI thread.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		// Get the friends timeline
		((YambaApp) getApplication()).fetchTimeline();
		Log.d(TAG, "onHandleIntent at "+ System.currentTimeMillis());
	}
}
