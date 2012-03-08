package com.marakana.android.yamba;

import android.app.Fragment;
import android.content.ContentUris;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailsFragment extends Fragment {
	private static final String TAG = DetailsFragment.class.getSimpleName();

	/**
	 * Create a new instance of DetailsFragment, initialized to show the text at
	 * 'index'.
	 */
	public static DetailsFragment newInstance(long index) {
		DetailsFragment detailsFragment = new DetailsFragment();

		// Supply index input as an argument.
		Bundle args = new Bundle();
		args.putLong("id", index);
		detailsFragment.setArguments(args);

		return detailsFragment;
	}

	public long getStatusId() {
		return getArguments().getLong("id", 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView container: " + container);

		if (container == null) {
			// We have different layouts, and in one of them this
			// fragment's containing frame doesn't exist. The fragment
			// may still be created from its saved state, but there is
			// no reason to try to create its view hierarchy because it
			// won't be displayed. Note this is not needed -- we could
			// just run the code below, where we would create and return
			// the view hierarchy; it would just never be used.

			return null;
		}

		Cursor cursor = getActivity().getContentResolver().query(
				ContentUris.withAppendedId(StatusProvider.CONTENT_URI,
						getStatusId()), null, null, null, null);
		if (cursor==null || cursor.getCount()<1) return null;
		
		cursor.moveToFirst();
		Log.d(TAG, "onCreateView with cursor size: " + cursor.getCount());
		View view = inflater.inflate(R.layout.row, null);
		((TextView) view.findViewById(R.id.text_user)).setText(cursor
				.getString(cursor.getColumnIndex(StatusData.C_USER)));

		((TextView) view.findViewById(R.id.text_text)).setText(cursor
				.getString(cursor.getColumnIndex(StatusData.C_TEXT)));

		long timestamp = cursor.getLong(cursor.getColumnIndex(StatusData.C_CREATED_AT));
		CharSequence relativeTime = DateUtils
				.getRelativeTimeSpanString(timestamp);
		((TextView) view.findViewById(R.id.text_createdAt)).setText(relativeTime);

		return view;
	}
}
