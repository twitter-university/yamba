package com.marakana.yamba;

import winterwell.jtwitter.OAuthSignpostClient;
import winterwell.jtwitter.Twitter;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class YambaApp extends Application {
  public static final String TAG = "YambaApp";

  SharedPreferences prefs;
  Twitter twitter;
  String username;

  @Override
  public void onCreate() {
    super.onCreate();

    // Get preferences
    prefs = PreferenceManager.getDefaultSharedPreferences(this);
    username = prefs.getString("username", "");
    String token = prefs.getString("token", null);
    String tokenSecret = prefs.getString("tokenSecret", null);

    // Make a Twitter object
    if (token != null && tokenSecret != null) {
      OAuthSignpostClient oauthClient = new OAuthSignpostClient(
          OAuthFragment.OAUTH_KEY, OAuthFragment.OAUTH_SECRET, token,
          tokenSecret);
      twitter = new Twitter("MarkoGargenta", oauthClient);
    } else {
      startActivity(new Intent(this, OAuthFragment.class)
          .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
  }

}
