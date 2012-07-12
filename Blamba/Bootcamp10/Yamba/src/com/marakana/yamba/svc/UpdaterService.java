package com.marakana.yamba.svc;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import winterwell.jtwitter.Twitter.Status;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.marakana.yamba.YambaApplication;


/**
 * UpdaterService
 */
public class UpdaterService extends IntentService {
    public static final long POLL_INTERVAL = 3 * 1000;

    private static final String TAG = "UpdaterService";

    /** Ctor */
    public UpdaterService() { super(TAG); }

    /**
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent arg0) {
        Log.v(TAG, "onHandleIntent() invoked");
        fetchStatusUpdates();
    }

    private void fetchStatusUpdates() {
        Log.d(TAG, "Fetching status updates");

        Twitter twit = ((YambaApplication) getApplication()).getTwitter();
        if (twit == null) {
            Log.d(TAG, "Twitter connection info not initialized");
            return;
        }

        // http://www.wherever.com/foo/bar?this=that&time=now
        List<Status> statuses = null;
        Exception fail = null;
        try { statuses = twit.getFriendsTimeline(); }
        catch (NullPointerException e) { fail = e; }
        catch (TwitterException e) { fail = e; }

        if (null != fail) {
            Log.e(TAG, "Failed to fetch status updates", fail);
            return;
        }

        int nUpdates = addAll(statuses);
        Log.d(TAG, "New records added: " + nUpdates);
    }

    private int addAll(List<Status> statuses) {
        for (Status status: statuses) {
            Log.v(
                TAG,
                status.user.name
                    + "#" + status.id
                    + " @" + status.createdAt
                    + ": " + status.text);
        }

        return statuses.size();
    }
}
