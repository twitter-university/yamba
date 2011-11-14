package com.marakana.yamba;

import java.util.List;

import winterwell.jtwitter.Twitter.Status;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class UpdaterService extends IntentService {
  private static final String TAG = "UpdaterService";
  public static final String NEW_STATUS_INTENT = "com.marakana.yamba.NEW_STATUS";
  public static final String NEW_STATUS_EXTRA_COUNT = "NEW_STATUS_EXTRA_COUNT";
  public static final String RECEIVE_TIMELINE_NOTIFICATIONS = "com.marakana.yamba.RECEIVE_TIMELINE_NOTIFICATIONS";

  YambaApp yamba;
  Toast toastNewTweets, toastNoNewTweets;

  public UpdaterService() {
    super(TAG);

    Log.d(TAG, "UpdaterService constructed");
  }

  @Override
  public void onCreate() {
    super.onCreate();

    yamba = (YambaApp) getApplication();

    toastNewTweets = Toast.makeText(this.getApplicationContext(),
        "You've got new tweets", Toast.LENGTH_LONG);
    toastNoNewTweets = Toast.makeText(this.getApplicationContext(),
        "No new tweets", Toast.LENGTH_SHORT);
  }

  @Override
  protected void onHandleIntent(Intent inIntent) {
    Log.d(TAG, "onHandleIntent");
    int newTweets = 0;

    List<Status> timeline = yamba.twitter.getFriendsTimeline();
    ContentValues values = new ContentValues();
    Uri uri;
    for (Status status : timeline) {
      values.clear();
      values.put(StatusProvider.C_ID, status.id.intValue());
      values.put(StatusProvider.C_CREATED_AT, status.createdAt.getTime());
      values.put(StatusProvider.C_USER, status.user.name);
      values.put(StatusProvider.C_SCREEN_NAME, status.user.screenName);
      values.put(StatusProvider.C_PROFILE_IMAGE_URL,
          status.user.profileImageUrl.toString());
      values.put(StatusProvider.C_TEXT, status.text);
      if (status.inReplyToStatusId != null) {
        values.put(StatusProvider.C_REPLY_TO_ID,
            status.inReplyToStatusId.intValue());
      }
      uri = getContentResolver().insert(StatusProvider.CONTENT_URI, values);
      if (StatusProvider.getId(uri) != -1) {
        newTweets++;
        Log.d(TAG,
            String.format("New tweet: %s: %s", status.user.name, status.text));
      }
    }

    // Notify user of new tweets
    if (newTweets > 0) {
      toastNewTweets.show();
    } else {
      toastNoNewTweets.show();
    }

    Log.d(TAG, "onHandleIntent done with new tweets: " + newTweets);
  }

}
