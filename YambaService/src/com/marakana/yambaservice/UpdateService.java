package com.marakana.yambaservice;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.Status;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

public class UpdateService extends Service {
	static final String TAG = UpdateService.class.getSimpleName();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand at timestamp: "
				+ intent.getExtras().getLong("timestamp"));

		new Thread("Pulling from Twitter") {
			public void run() {
				Twitter twitter = new Twitter("student", "password");
				twitter.setAPIRootUrl("http://yamba.marakana.com/api");
				List<Status> timeline = twitter.getFriendsTimeline();
				for (Status status : timeline) {
					Log.d(TAG, String.format("%s: %s", status.user.name,
							status.text));
				}

			}
		}.start();

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// Check for stop permission
		if (this.checkCallingOrSelfPermission("com.marakana.yambaservice.permission.STOP_SERVICE") 
				!= PackageManager.PERMISSION_GRANTED) {
			throw new SecurityException("Can't stop this service!");
		}

		Log.d(TAG, "onDestroy");
	}
}
