package com.marakana.yamba.svc;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.Status;
import winterwell.jtwitter.TwitterException;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.provider.BaseColumns;
import android.util.Log;

import com.marakana.yamba.YambaApplication;
import com.marakana.yamba.data.TimelineContract;
import com.marakana.yamba.data.TimelineDao;


/**
 * UpdaterService
 */
public class UpdaterService extends IntentService {
    public static final long POLL_INTERVAL = 3 * 60 * 1000;

    public static final String NEW_STATUS_INTENT = "com.marakana.yamba.NEW_STATUS";
    public static final String NEW_STATUS_COUNT = "com.marakana.yamba.extra.NEW_STATUS_COUNT";
    public static final String NEW_STATUS_PERM = "com.marakana.yamba.permission.NEW_STATUS";

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
        if (0 < nUpdates) { notifyNewStatus(nUpdates); }
    }

    private int addAll(List<Status> statuses) {
        TimelineDao dao = ((YambaApplication) getApplication()).getDao();
        long mostRecentStatus = dao.getLatestStatusCreatedAtTime();
        int i = 0;
        ContentValues values = new ContentValues();
        for (Status status: statuses) {
            long t = status.getCreatedAt().getTime();
            if (mostRecentStatus <= t) {
                values.clear();
                values.put(BaseColumns._ID, Long.valueOf(status.getId()));
                values.put(TimelineContract.Columns.CREATED_AT, Long.valueOf(t));
                values.put(TimelineContract.Columns.TEXT, status.getText());
                values.put(TimelineContract.Columns.USER, status.getUser().getName());
                if (dao.insertOrIgnore(values)) { i++; }
             }
        }

        return i;
    }

    private void notifyNewStatus(int count) {
        Log.d(TAG, "Notify: " + count);
        Intent broadcast = new Intent(NEW_STATUS_INTENT);
        broadcast.putExtra(NEW_STATUS_COUNT, count);
        sendBroadcast(broadcast, NEW_STATUS_PERM);
    }
}
