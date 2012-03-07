package com.marakana.android.yamba;

import winterwell.jtwitter.TwitterException;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends Activity implements OnClickListener, TextWatcher {
	private static final String TAG = StatusActivity.class.getSimpleName();
	private Button buttonUpdate;
	private EditText editStatus;
	private TextView textCount;

	// --- Activity Lifecycle Callbacks
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Debug.startMethodTracing("Yamba.trace");

		setContentView(R.layout.status);

		// Find views
		buttonUpdate = (Button) findViewById(R.id.button_update);
		editStatus = (EditText) findViewById(R.id.edit_status);
		textCount = (TextView) findViewById(R.id.text_count);

		// Add listeners
		buttonUpdate.setOnClickListener(this);
		editStatus.addTextChangedListener(this);
		
		Log.d(TAG, "onCreated");
	}

	/** Called when we leave this activity. */
	@Override
	protected void onStop() {
		super.onStop();
		// Debug.stopMethodTracing();
	}


	/** Called when the update button is clicked. */
	@Override
	public void onClick(View v) {
		String status = editStatus.getText().toString();

		new PostToTwitter().execute(status);

		Log.d(TAG, "onClicked with status: " + status);
	}

	
	private ProgressDialog dialog;
	/** Called as result of showDialog() call. */
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		dialog = new ProgressDialog(this);
		dialog.setMessage("Posting...");
		dialog.setCancelable(true);
		return dialog;
	}


	/** AsyncTask responsible for posting to twitter. */
	class PostToTwitter extends AsyncTask<String, Void, Integer> {
		
		/** Called before the background job starts. */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Create progress bar dialog here
			showDialog(-1, null);
		}

		/** Work executed on a background thread. */
		@Override
		protected Integer doInBackground(String... status) {
			try {
				YambaApp yambaApp = (YambaApp)getApplication();
				yambaApp.getTwitter().setStatus(status[0]);
				return R.string.update_successful;
			} catch (TwitterException e) {
				Log.e(TAG, "Failure to post", e);
				return R.string.update_failed;
			}
		}

		/** Called when we are done with the background job. */
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);

			// Cancel progress bar dialog here
			dialog.cancel();
		
			Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG)
					.show();
		}

	}


	// --- TextWatcher Callbacks 
	
	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int c) {
		// Update count
		int lenght = editStatus.getText().length();
		int count = 140 - lenght;
		textCount.setText(Integer.toString(count));
		
		// Update color
		if(count<20) {
			textCount.setTextColor(Color.WHITE);
			textCount.setBackgroundColor(Color.RED);
		} else {
			textCount.setTextColor(Color.BLACK);
			textCount.setBackgroundColor(Color.WHITE);
		}
	}

}