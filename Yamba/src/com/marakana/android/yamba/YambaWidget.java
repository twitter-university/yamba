package com.marakana.android.yamba;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;

public class YambaWidget extends AppWidgetProvider {
	private static final String TAG = YambaWidget.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if (intent.getAction().equals(YambaApp.NEW_STATUS_BROADCAST)) {
			AppWidgetManager appWidgetManager = AppWidgetManager
					.getInstance(context);
			this.onUpdate(context, appWidgetManager, appWidgetManager
					.getAppWidgetIds(new ComponentName(context,
							YambaWidget.class)));
			Log.d(TAG, "onReceived");
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		// Get the data
		Cursor cursor = context.getContentResolver().query(
				StatusProvider.CONTENT_URI, null, null, null,
				StatusData.SORT_BY);

		// Do we have any data?
		if (cursor!=null && cursor.moveToFirst()) {
			String user = cursor.getString(cursor
					.getColumnIndex(StatusData.C_USER));
			String text = cursor.getString(cursor
					.getColumnIndex(StatusData.C_TEXT));

			// Loop thru all the widget instances
			for (int appWidgetId : appWidgetIds) {
				RemoteViews views = new RemoteViews(context.getPackageName(),
						R.layout.row);
				views.setTextViewText(R.id.text_user, user);
				views.setTextViewText(R.id.text_text, text);
				views.setTextViewText(R.id.text_createdAt, "");
				appWidgetManager.updateAppWidget(appWidgetId, views);
			}
		}
		Log.d(TAG, "onUpdated");
	}

}
