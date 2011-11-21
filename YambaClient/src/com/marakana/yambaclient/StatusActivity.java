package com.marakana.yambaclient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.marakana.yambacommon.ITwitterService;
import com.marakana.yambacommon.IYambaListener;

public class StatusActivity extends Activity implements OnClickListener,
		ServiceConnection {
	static final String TAG = "StatusActivity";
	EditText editStatus;
	Button buttonUpdate;
	ITwitterService twitterService;
	Handler handler;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		Debug.startMethodTracing("Yamba.trace");

		setContentView(R.layout.status);

		editStatus = (EditText) findViewById(R.id.edit_status);
		buttonUpdate = (Button) findViewById(R.id.button_update);
		buttonUpdate.setOnClickListener(this);

		handler = new Handler();
		Log.d(TAG, "onCreate");
	}

	@Override
	protected void onStart() {
		super.onStart();
		bindService(new Intent("com.marakana.yambacommon.ITwitterService"), this,
				BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();

		unbindService(this);

		Log.d(TAG, "onStop");
//		Debug.stopMethodTracing();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return MenuHandler.onCreateOptionsMenu(this, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return MenuHandler.onOptionsItemSelected(this, item);
	}

	public void onClick(View v) {

		String status = editStatus.getText().toString();
		// new PostToTwitter().execute(status);

		try {
			twitterService.asyncUpdate(status, new IYambaListener.Stub() {

				@Override
				public void onUpdateResult(int result) throws RemoteException {
					handler.post(new Runnable() {
						public void run() {
							Log.d(TAG,
									"onUpdateResult:  thread: "
											+ Thread.currentThread());
							Toast.makeText(
									StatusActivity.this,
									"Posted:  thread: "
											+ Thread.currentThread(),
									Toast.LENGTH_LONG).show();
						}
					});
				}
			});
		} catch (RemoteException e) {
			Log.e(TAG, "Failed onClick()", e);
		}

		Log.d("StatusActivity", "onClick	on thread: " + Thread.currentThread());
	}

	class PostToTwitter extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... status) {
			if (twitterService == null)
				return "No service connection";
			

			try {
				twitterService.updateStatus(status[0]);
			} catch (RemoteException e) {
				Log.e(TAG, "PostToTwitter.doInBackground", e);
				return "Failed: " + e;
			}
			return "Successfully posted " + status[0];
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Toast.makeText(getApplication(), "Successfully posted " + result,
					Toast.LENGTH_LONG).show();
		}

	}

	// --- ServiceConnection interface methods
	@Override
	public void onServiceConnected(ComponentName name, IBinder binder) {
		twitterService = ITwitterService.Stub.asInterface(binder);
		Log.d(TAG, "onServiceConnected");
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		twitterService = null;
		Log.d(TAG, "onServiceDisconnected");
	}

}