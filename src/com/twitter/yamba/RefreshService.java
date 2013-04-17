package com.twitter.yamba;

import java.util.List;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClient.Status;
import com.marakana.android.yamba.clientlib.YambaClientException;

public class RefreshService extends IntentService {
	private static final String TAG = "RefreshService";

	public RefreshService() {
		super(TAG);
	}

	// UI thread
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreated");
	}

	// Worker thread
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onStarted");

		// Get the timeline
		int newStatusCount = 0;
		try {
			ContentValues values = new ContentValues();

			// Get the data from the cloud
			YambaClient cloud = YambaUtils.getCloud(this);
			List<Status> timeline = cloud.getTimeline(20);
			for (Status status : timeline) {
				// ORM
				values.clear();
				values.put(StatusContract.Column.ID, status.getId());
				values.put(StatusContract.Column.USER, status.getUser());
				values.put(StatusContract.Column.MESSAGE, status.getMessage());
				values.put(StatusContract.Column.CREATED_AT, status
						.getCreatedAt().getTime());
				Uri ret = getContentResolver().insert(
						StatusContract.CONTENT_URI, values);
				// Do we have a new status?
				if (ret != null) {
					newStatusCount++;
					Log.d(TAG,
							String.format("%s: %s", status.getUser(),
									status.getMessage()));
//					sendBroadcast(new Intent(StatusContract.NEW_STATUS_ACTION)
//							.putExtra("newStatusUri", ret));
				}
			}
		} catch (YambaClientException e) {
			Log.e(TAG, "Failed to get the timeline", e);
			e.printStackTrace();
		}

		// Send a broacast about new tweets
		if (newStatusCount > 0) {
			sendBroadcast(new Intent(StatusContract.NEW_STATUS_ACTION)
					.putExtra("newStatusCount", newStatusCount));
		}
	}

	// UI thread
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroyed");
	}

}
