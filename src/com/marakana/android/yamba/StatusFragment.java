package com.marakana.android.yamba;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClientException;

public class StatusFragment extends Fragment implements OnClickListener,
		TextWatcher {
	private static final int MAX_TEXT = 140;
	private EditText editStatus;
	private Button buttonUpdate;
	private TextView textCount;
	private int defaultColor;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_status, container, false);

		editStatus = (EditText) view.findViewById(R.id.text_status);
		buttonUpdate = (Button) view.findViewById(R.id.button_update);
		textCount = (TextView) view.findViewById(R.id.text_count);

		textCount.setText(String.valueOf(MAX_TEXT));
		defaultColor = textCount.getTextColors().getDefaultColor();

		buttonUpdate.setOnClickListener(this);
		editStatus.addTextChangedListener(this);
		
		return view;
	}

	// --- OnClickListener callback ---
	@Override
	public void onClick(View v) {

		// Post to cloud
		String status = editStatus.getText().toString();
		new PostStatusTask().execute(status);

	}

	private class PostStatusTask extends AsyncTask<String, Void, String> {
		
		/** Happens before the background task, on UI thread */
		@Override
		protected void onPreExecute() {
			getActivity().setProgressBarIndeterminateVisibility(true);
		}

		/** Executes on a worker thread */
		@Override
		protected String doInBackground(String... param) {
			try {
				// Get the login info from the preferences
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
				String username = prefs.getString("username", null);
				String password = prefs.getString("password", null);
				if( username==null || password==null) {
					return "Please update your login info";
				}
				// Post to the cloud
				YambaClient yamba = new YambaClient(username, password);
				yamba.postStatus(param[0]);
				return "Successfully posted";
			} catch (YambaClientException e) {
				Log.e("Yamba", "Failed to post", e);
				e.printStackTrace();
				return "Failed to post";
			}
		}

		/** Executes on the UI thread */
		@Override
		protected void onPostExecute(String result) {
			getActivity().setProgressBarIndeterminateVisibility(false);
			Toast.makeText( getActivity(), result, Toast.LENGTH_LONG)
					.show();
		}

	}

	// --- TextWateched callbacks ---
	@Override
	public void afterTextChanged(Editable s) {
		int count = 140 - editStatus.length();

		textCount.setText(String.valueOf(count));

		if (count < 50) {
			textCount.setTextColor(Color.RED);
		} else {
			textCount.setTextColor(defaultColor);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}
}