package com.marakana.android.yamba;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class TimelineFragment extends ListFragment {
	private static final String TAG = TimelineFragment.class.getSimpleName();
	private boolean dualPane;
	int currentCheckPosition = 0;

	private String[] FROM = { StatusData.C_USER, StatusData.C_TEXT,
			StatusData.C_CREATED_AT };
	private int[] TO = { R.id.text_user, R.id.text_text, R.id.text_createdAt };

	private SimpleCursorAdapter adapter;
	private Cursor cursor;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setEmptyText("Loading data...");

		// Get the timeline
		cursor = getActivity().getContentResolver().query(
				StatusProvider.CONTENT_URI, null, null, null,
				StatusData.SORT_BY);

		// Setup the adapter
		adapter = new SimpleCursorAdapter(getActivity(), R.layout.row, cursor,
				FROM, TO);
		adapter.setViewBinder(VIEW_BINDER);
		setListAdapter(adapter);

		// Check to see if we have a frame in which to embed the details
		// fragment directly in the containing UI.
		View detailsFrame = getActivity().findViewById(R.id.details);
		dualPane = detailsFrame != null
				&& detailsFrame.getVisibility() == View.VISIBLE;

		if (savedInstanceState != null) {
			// Restore last state for checked position.
			currentCheckPosition = savedInstanceState.getInt("position", 0);
		}

		if (dualPane) {
			// In dual-pane mode, the list view highlights the selected item.
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			// Make sure our UI is in the correct state.
			showDetails(currentCheckPosition, -1);
		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("currentChoice", currentCheckPosition);
	}

	@Override
	public void onListItemClick(ListView list, View view, int position, long id) {
		showDetails(position, id);
	}

	/**
	 * Helper function to show the details of a selected item, either by
	 * displaying a fragment in-place in the current UI, or starting a whole new
	 * activity in which it is displayed.
	 */
	void showDetails(int position, long id) {
		currentCheckPosition = position;
		Log.d(TAG, String.format("showDetails position: %d, id: %d, dualPane: %b",
				position, id, dualPane));

		if (dualPane) {
			// We can display everything in-place with fragments, so update
			// the list to highlight the selected item and show the data.
			getListView().setItemChecked(position, true);

			// Check what fragment is currently shown, replace if needed.
			DetailsFragment details = (DetailsFragment) getFragmentManager()
					.findFragmentById(R.id.details);
			if (details == null || details.getStatusId() != id) {
				// Make new fragment to show this selection.
				details = DetailsFragment.newInstance(id);

				// Execute a transaction, replacing any existing fragment
				// with this one inside the frame.
				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				ft.replace(R.id.details, details);
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.commit();
			}

		} else {
			// Otherwise we need to launch a new activity to display
			// the dialog fragment with selected text.
			Intent intent = new Intent(getActivity(), DetailsActivity.class);
			intent.putExtra("id", id);
			startActivity(intent);
		}
	}

	/** Custom ViewBinder to convert timestamp to relative time. */
	static final ViewBinder VIEW_BINDER = new ViewBinder() {
		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			// Ignore custom binding for user and text values
			if (view.getId() != R.id.text_createdAt)
				return false;

			// Custom binding for timestamp to relative time
			long timestamp = cursor.getLong(columnIndex);
			CharSequence relativeTime = DateUtils
					.getRelativeTimeSpanString(timestamp);
			((TextView) view).setText(relativeTime);
			return true;
		}

	};

	// --- Timeline Receiver related code
	TimelineReceiver receiver = new TimelineReceiver();
	IntentFilter filter = new IntentFilter(YambaApp.NEW_STATUS_BROADCAST);

	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(receiver, filter);
	}

	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(receiver);
	}

	/**
	 * Called when there are new statuses in the database so we can update the
	 * list.
	 */
	class TimelineReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Update list from new timeline
			cursor = getActivity().getContentResolver().query(
					StatusProvider.CONTENT_URI, null, null, null,
					StatusData.SORT_BY);
			adapter.changeCursor(cursor);
			Log.d(TAG, "TimelineReceiver refreshing the list");
		}
	}
}
