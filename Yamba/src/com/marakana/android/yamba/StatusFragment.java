package com.marakana.android.yamba;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;

public class StatusFragment extends Fragment implements OnClickListener,
		LocationListener {
	private static final int MAX_LENGTH = 140;
	private static final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
	private TextView statusText, counterText;
	private ImageButton updateButton;
	private int defaultColor;
	private LocationManager locationManager;
	private Location location;

	/** Called by activity when it's ready for fragment to create its view. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.status, container, false);

		// Find the views
		statusText = (TextView) view.findViewById(R.id.status_update_text);
		statusText.addTextChangedListener(new MyTextWatcher());
		counterText = (TextView) view
				.findViewById(R.id.status_update_counter_text);
		counterText.setText(Integer.toString(MAX_LENGTH - statusText.length()));
		defaultColor = counterText.getTextColors().getDefaultColor();
		updateButton = (ImageButton) view
				.findViewById(R.id.status_update_button);
		updateButton.setOnClickListener(this);

		// Get location
		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		location = locationManager.getLastKnownLocation(LOCATION_PROVIDER);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(LOCATION_PROVIDER, 10000, 100,
				this);
	}

	@Override
	public void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	/** Called when update button is clicked on. */
	public void onClick(View v) {
		final String status = statusText.getText().toString();

		new StatusUpdateTask().execute(status);
	}

	/** Posts the status update in a separate task. */
	class StatusUpdateTask extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new Dialog(getActivity());
			dialog.setTitle("Posting status update...");
			dialog.show();
		}

		/** Task that happens on a separate thread. */
		@Override
		protected String doInBackground(String... params) {
			try {
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(getActivity());
				String username = prefs.getString("username", null);
				String password = prefs.getString("password", null);
				String server = prefs.getString("server", null);
				YambaClient yambaClient = new YambaClient(username, password);
				if (server != null && server.length() > 0)
					yambaClient.setApiRoot(server);

				if (location != null) {
					yambaClient.updateStatus(params[0], location.getLatitude(),
							location.getLongitude()); // could take some time
				} else {
					yambaClient.updateStatus(params[0]);
				}

				// Send broadcast that there may be new data on the server
				getActivity().sendBroadcast(
						new Intent("com.marakana.android.yamba.REFRESH_ACTION"));

				return "Status update posted successfully";
			} catch (Exception e) {
				Log.e("StatusActivity", "CRASHED!", e);
				e.printStackTrace();
				return "Failed to post the status update";
			}
		}

		/** Called once doInBackground() is complete. */
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dialog.dismiss();
			Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
		}

	}

	// --- Part of being TextWatcher --- //
	class MyTextWatcher implements TextWatcher {
		@Override
		public void afterTextChanged(Editable s) {
			int count = MAX_LENGTH - s.length();
			counterText.setText(Integer.toString(count));

			// Change the color
			if (count < 30) {
				counterText.setTextColor(Color.RED);
				counterText.setTextScaleX(2);
			} else {
				counterText.setTextColor(defaultColor);
				counterText.setTextScaleX(1);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
	}

	// --- Callbacks for LocatoinManager
	@Override
	public void onLocationChanged(Location location) {
		this.location = location;
		Log.d("StatusFragment", String.format("location: %f, %f",
				location.getLatitude(), location.getLongitude()));
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

}