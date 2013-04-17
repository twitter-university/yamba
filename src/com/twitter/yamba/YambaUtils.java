package com.twitter.yamba;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.marakana.android.yamba.clientlib.YambaClient;

public class YambaUtils {

	public static YambaClient getCloud(Context context) {
		// Get the username and password
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		String username = prefs.getString("username", "");
		String password = prefs.getString("password", "");

		// Test if username&password are set
		if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
			return null;
		}
		return new YambaClient(username, password);
	}
}
