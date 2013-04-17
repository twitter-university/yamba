package com.twitter.yamba;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	public static final long DEFAULT_INTERVAL = AlarmManager.INTERVAL_HOUR;

	@Override
	public void onReceive(Context context, Intent intent) {

		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		PendingIntent operation = PendingIntent.getService(context, 0,
				new Intent(context, RefreshService.class),
				PendingIntent.FLAG_UPDATE_CURRENT);

		alarmManager.setInexactRepeating(AlarmManager.RTC,
				System.currentTimeMillis(), DEFAULT_INTERVAL, operation);

		Log.d("BootReceiver", "onReceived");
	}
}
