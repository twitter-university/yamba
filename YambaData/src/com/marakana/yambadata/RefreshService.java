package com.marakana.yambadata;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.Status;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class RefreshService extends IntentService {
	static final String TAG = "RefreshService";
	Twitter twitter;
	DbHelper dbHelper;

	public RefreshService() {
		super(TAG);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		twitter = new Twitter("student", "password");
		twitter.setAPIRootUrl("http://yamba.marakana.com/api");
		dbHelper = new DbHelper(this);
		Log.d(TAG, "onCreate");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		List<Status> timeline = twitter.getPublicTimeline();
		int count = dbHelper.insert(timeline);
		if (count > 0) {
			sendBroadcast(new Intent(
					"com.marakana.yamba.TimelineReceiver.NEW_STATUS").putExtra(
					"count", count));
			getContentResolver().notifyChange(StatusProvider.CONTENT_URI, null);
		}
		Log.d(TAG, "onHandleIntent with count: "+count);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		dbHelper.close();
		Log.d(TAG, "onDestroy");
	}

}
