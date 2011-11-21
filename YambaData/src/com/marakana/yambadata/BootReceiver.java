package com.marakana.yambadata;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	static final String TAG = "BootReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {

		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		PendingIntent operation = PendingIntent.getService(context, -1,
				new Intent("com.marakana.yambadata.RefreshService"),
				PendingIntent.FLAG_UPDATE_CURRENT);

		alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
				System.currentTimeMillis(),
				AlarmManager.INTERVAL_FIFTEEN_MINUTES, operation);

		Log.d(TAG, "onReceive");
	}
}
