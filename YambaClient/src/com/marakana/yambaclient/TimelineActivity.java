package com.marakana.yambaclient;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

import com.marakana.yambacommon.StatusColumns;

public class TimelineActivity extends ListActivity {
	static final String TAG = "TimelineActivity";
	static final Uri URI = Uri.parse("content://com.marakana.yambadata");
	static final String[] FROM = { StatusColumns.USER, StatusColumns.TEXT,
			StatusColumns.CREATED_AT };
	static final int[] TO = { R.id.text_user, R.id.text_text,
			R.id.text_createdAt };
	SimpleCursorAdapter adapter;
	TimelineReceiver receiver;
	TimelineObserver observer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Adapter
		adapter = new SimpleCursorAdapter(this, R.layout.row, null, FROM, TO);
		adapter.setViewBinder(VIEW_BINDER);

		// View
		setListAdapter(adapter);

		// Data
		new AsyncLoader().execute(URI);

		Log.d(TAG, "onCreate");
	}

	static final IntentFilter FILTER = new IntentFilter(
			"com.marakana.yamba.TimelineReceiver.NEW_STATUS");

	@Override
	protected void onResume() {
		super.onResume();
		if (receiver == null)
			receiver = new TimelineReceiver();
		if (observer == null )
			observer = new TimelineObserver();
		
		registerReceiver(receiver, FILTER);
		getContentResolver().registerContentObserver(URI, true, observer);
		
		Log.d(TAG, "onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
		getContentResolver().unregisterContentObserver(observer);
		Log.d(TAG, "onPause");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return MenuHandler.onCreateOptionsMenu(this, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return MenuHandler.onOptionsItemSelected(this, item);
	}

	class AsyncLoader extends AsyncTask<Uri, Void, Cursor> {
		ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress = ProgressDialog.show(TimelineActivity.this, null,
					"Loading data...");
		}

		@Override
		protected Cursor doInBackground(Uri... uri) {
			return managedQuery(uri[0], null, null, null,
					StatusColumns.CREATED_AT + " DESC");
		}

		@Override
		protected void onPostExecute(Cursor cursor) {
			super.onPostExecute(cursor);
			adapter.changeCursor(cursor);
			progress.dismiss();
		}

	}

	static final ViewBinder VIEW_BINDER = new ViewBinder() {

		@Override
		public boolean setViewValue(View view, Cursor row, int index) {
			if (view.getId() != R.id.text_createdAt)
				return false;

			long timestamp = row.getLong(index);
			CharSequence relativeTime = DateUtils
					.getRelativeTimeSpanString(timestamp);
			((TextView) view).setText(relativeTime);

			return true;
		}

	};

	class TimelineReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			int count = intent.getIntExtra("count", 0);
			Toast.makeText(TimelineActivity.this, "Got new items: " + count,
					Toast.LENGTH_LONG).show();
			new AsyncLoader().execute(URI);
		}
	}
	
	class TimelineObserver extends ContentObserver {

		public TimelineObserver() {
			super(new Handler());
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			Log.d(TAG, "TimelineObserver.onChange");
			new AsyncLoader().execute(URI);
		}
		
	}
}
