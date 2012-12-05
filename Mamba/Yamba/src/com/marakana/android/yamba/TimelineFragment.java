package com.marakana.android.yamba;

import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class TimelineFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {
	private static final String[] FROM = { StatusContract.Columns.USER,
			StatusContract.Columns.TEXT, StatusContract.Columns.CREATED_AT };
	private static final int[] TO = { R.id.text_user, R.id.text_text,
			R.id.text_created_at };
	private SimpleCursorAdapter adapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setEmptyText("Loading data...");

		adapter = new SimpleCursorAdapter(getActivity(), R.layout.row, null,
				FROM, TO, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		adapter.setViewBinder(new TimelineViewBinder());

		setListAdapter(adapter);

		getLoaderManager().initLoader(0, null, this);
	}

	/** Handles custom binding of data to view. */
	class TimelineViewBinder implements ViewBinder {

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if (view.getId() != R.id.text_created_at)
				return false;

			// Convert timestamp to relative time
			long timestamp = cursor.getLong(columnIndex);
			CharSequence relativeTime = DateUtils
					.getRelativeTimeSpanString(timestamp);
			((TextView) view).setText(relativeTime);

			return true;
		}
	}

	// --- LoaderManager.LoaderCallbacks ---

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), StatusContract.CONTENT_URI,
				null, null, null, StatusContract.SORT_ORDER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

}
