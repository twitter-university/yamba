package com.twitter.yamba;

import android.app.Fragment;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailsFragment extends Fragment {
	private TextView textUser, textMessage, textCreatedAt;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragments_details, null);

		textUser = (TextView) view.findViewById(R.id.text_user);
		textMessage = (TextView) view.findViewById(R.id.text_message);
		textCreatedAt = (TextView) view.findViewById(R.id.text_createdAt);

		updateDetails(-1);
		
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		Intent intent = getActivity().getIntent();
		long id = intent.getLongExtra("id", -1);

		updateDetails(id);
		
		Log.d("Details", "onResume with id: " + id);
	}
	
	public void updateDetails(long id) {
		// Data not set
		if (id == -1) {
			textUser.setText("");
			textMessage.setText("");
			textCreatedAt.setText("");
			return;
		}
		
		// Get the data from StatusProvider
		Uri uri = ContentUris.withAppendedId(StatusContract.CONTENT_URI, id);
		Cursor cursor = getActivity().getContentResolver().query(uri, null,
				null, null, null);
		if( !cursor.moveToFirst() ) return;
		
		// Update the views
		textUser.setText(cursor.getString(cursor
				.getColumnIndex(StatusContract.Column.USER)));
		textMessage.setText(cursor.getString(cursor
				.getColumnIndex(StatusContract.Column.MESSAGE)));
		textCreatedAt.setText(DateUtils.getRelativeTimeSpanString(
				getActivity(), cursor.getLong(cursor
						.getColumnIndex(StatusContract.Column.CREATED_AT))));
	}
}
