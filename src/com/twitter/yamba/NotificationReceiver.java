package com.twitter.yamba;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {
	public static final int NOTIFICATION_ID = 47;
	private static final String TAG = NotificationReceiver.class
			.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		int newStatusCount = intent.getIntExtra("newStatusCount", -1);

		// Get the notification manager
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent operation = PendingIntent.getActivity(context, 0,
				new Intent(context, MainActivity.class),
				PendingIntent.FLAG_UPDATE_CURRENT);

		// Create a new notification object or update existing one
		String msg = "You got " + newStatusCount;
		msg += (newStatusCount > 1) ? " new statuses." : "new status";
		Notification notification = new NotificationCompat.Builder(context)
				.setContentTitle("New Yamba Status!").setContentText(msg)
				.setContentIntent(operation)
				// .setAutoCancel(true)
				.setSmallIcon(android.R.drawable.ic_dialog_email).build();

		// Notify user
		notificationManager.notify(NOTIFICATION_ID, notification);
		
		// Vibrate the device
		Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
		if(vibrator.hasVibrator()) {
			vibrator.vibrate(1000);
		}
		
		// Play a sound
		Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Ringtone ringtone = RingtoneManager.getRingtone(context, ringtoneUri);
		ringtone.play();

		Log.d(TAG, "onReceived new statuses: " + newStatusCount);
	}
}
