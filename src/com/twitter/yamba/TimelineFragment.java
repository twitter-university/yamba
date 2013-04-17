package com.twitter.yamba;

import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
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
	private static final String[] FROM = { StatusContract.Column.USER,
			StatusContract.Column.MESSAGE, StatusContract.Column.CREATED_AT };
	private static final int[] TO = { R.id.text_user, R.id.text_message,
			R.id.text_createdAt };
	private SimpleCursorAdapter adapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setEmptyText("Loading...");

		// Create the adapter
		adapter = new SimpleCursorAdapter(getActivity(), R.layout.row, null,
				FROM, TO, 0);
		adapter.setViewBinder(VIEW_BINDER);

		// Connect adapter to list
		setListAdapter(adapter);

		// Init the cursor loader
		getLoaderManager().initLoader(LOADER_ID, null, loaderCallbacks);
	}

	private static final int LOADER_ID = 42;
	private final LoaderCallbacks<Cursor> loaderCallbacks = new LoaderCallbacks<Cursor>() {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			// Assert
			assert (id == LOADER_ID);

			// Get the data: select * from statuses;
			CursorLoader loader = new CursorLoader(getActivity(),
					StatusContract.CONTENT_URI, null, null, null,
					StatusContract.SORT_BY);

			Log.d("Yamba", "onCreateLoader: " + loader);
			return loader;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			((SimpleCursorAdapter) getListAdapter()).swapCursor(cursor);
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			((SimpleCursorAdapter) getListAdapter()).swapCursor(null);
		}
	};

	private final ViewBinder VIEW_BINDER = new ViewBinder() {

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if (view.getId() != R.id.text_createdAt)
				return false;

			// Custom binding
			long timestamp = cursor.getLong(columnIndex);
			CharSequence relTime = DateUtils.getRelativeTimeSpanString(
					getActivity(), timestamp);
			((TextView) view).setText(relTime);

			return true;
		}
	};

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		// Find the fragment from the fragment manager
		DetailsFragment detailsFragment = (DetailsFragment) getFragmentManager()
				.findFragmentById(R.id.fragment_details);

		if (detailsFragment != null && detailsFragment.isVisible()) {
			detailsFragment.updateDetails(id);
		} else {
			// Create an intent to start DetailsActivity
			Intent intent = new Intent(getActivity(), DetailsActivity.class);

			// Put extra id into that intent
			intent.putExtra("id", id);

			startActivity(intent);
		}
	}

}
