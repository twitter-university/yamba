package com.twitter.yamba;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class StatusFragment extends Fragment {
	public static final String TAG = "StatusFragment";
	private EditText statusText;
	private Button updateButton;
	private TextView textCount;
	private int defaultTextColor;
	private PostToTwitterTask postToTwitterTask;
	private YambaClient cloud;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_status, null);

		statusText = (EditText) view.findViewById(R.id.status_text);
		updateButton = (Button) view.findViewById(R.id.status_button_update);
		updateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (cloud == null) {
					String message = "Please set your username&password";
					startActivity(new Intent(getActivity(),
							SettingsActivity.class).addFlags(
							Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("message",
							message));

					return;
				}

				String status = statusText.getText().toString();
				postToTwitterTask = new PostToTwitterTask();
				postToTwitterTask.execute(status);
			}
		});
		textCount = (TextView) view.findViewById(R.id.text_count);
		defaultTextColor = textCount.getTextColors().getDefaultColor();
		statusText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				int count = Integer
						.parseInt(getString(R.string.status_text_counter))
						- s.length();
				textCount.setText(Integer.toString(count));
				if (count < 50) {
					textCount.setTextColor(Color.RED);
				} else {
					textCount.setTextColor(defaultTextColor);
				}
			}
		});

		cloud = YambaUtils.getCloud(getActivity());

		return view;
	}

	class PostToTwitterTask extends AsyncTask<String, Void, String> {
		ProgressDialog dialog;

		// Executed on the UI thread
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(getActivity(), "Posting to the cloud",
					"Please wait...");
		}

		// Executed on a separate thread
		@Override
		protected String doInBackground(String... params) {
			// Get location
			if (location == null) {
				location = locationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}
			Log.d(TAG, "location: " + location);

			// Post to cloud
			try {
				if (location != null) {
					cloud.postStatus(params[0], location.getLatitude(),
							location.getLongitude());
				} else {
					cloud.postStatus(params[0]);
				}

				return "Successfully posted!";
			} catch (YambaClientException e) {
				Log.e("Yamba", "onClick", e);
				e.printStackTrace();
				return "Failed to post";
			}
		}

		// Executed on the UI thread
		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
		}
	}

	private LocationManager locationManager;
	private Location location;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
	}

	@Override
	public void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				60000, 1000, locationListener);
	};

	private LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			StatusFragment.this.location = location;
			Log.d(TAG, "locationListener location: " + location);
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
	};

	@Override
	public void onPause() {
		super.onPause();
		locationManager.removeUpdates(locationListener);
		location=null;
	}

}
