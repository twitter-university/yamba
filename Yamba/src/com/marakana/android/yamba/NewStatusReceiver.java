package com.marakana.android.yamba;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NewStatusReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				new Intent(context, MainActivity.class), 0);

		String text = intent.getStringExtra(StatusContract.Columns.USER) + ": "
				+ intent.getStringExtra(StatusContract.Columns.TEXT);

		// Create the notification object
		Notification.Builder builder = new Notification.Builder(context);
		builder.setContentTitle("New Status!").setContentText(text)
				.setAutoCancel(true)
				.setSmallIcon(android.R.drawable.stat_notify_more)
				.setWhen(System.currentTimeMillis())
				.setContentIntent(pendingIntent);
		Notification notification = builder.getNotification();

		// Post the notification
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(0, notification);

		Log.d("NewStatusReceiver", "onReceive with text: " + text);
	}
}
