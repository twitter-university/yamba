package com.marakana.yamba.svc;

import java.util.List;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.provider.BaseColumns;
import android.util.Log;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClientException;
import com.marakana.yamba.YambaApplication;
import com.marakana.yamba.data.TimelineContract;
import com.marakana.yamba.data.TimelineDao;


/**
 * UpdaterService
 */
public class UpdaterService extends IntentService {
    /** Polling interval */
    public static final long POLL_INTERVAL = 3 * 1000;

    /** Max timeline length */
    public static final int MAX_TIMELINE_SIZE = 200;

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

        YambaClient client = ((YambaApplication) getApplication()).getYambaClient();
        if (client == null) {
            Log.d(TAG, "Twitter connection info not initialized");
            return;
        }

        // http://www.wherever.com/foo/bar?this=that&time=now
        List<YambaClient.Status> statuses = null;
        try { statuses = client.getTimeline(MAX_TIMELINE_SIZE); }
        catch (YambaClientException e) {
            Log.e(TAG, "Failed to fetch status updates", e);
            return;
        }

        int nUpdates = addAll(statuses);
        Log.d(TAG, "New records added: " + nUpdates);
    }

    private int addAll(List<YambaClient.Status> statuses) {
        TimelineDao dao = ((YambaApplication) getApplication()).getDao();
        long mostRecentStatus = dao.getLatestStatusCreatedAtTime();
        int i = 0;
        ContentValues values = new ContentValues();
        for (YambaClient.Status status: statuses) {
            long t = status.getCreatedAt().getTime();
            if (mostRecentStatus <= t) {
                values.clear();
                values.put(BaseColumns._ID, Long.valueOf(status.getId()));
                values.put(TimelineContract.Columns.CREATED_AT, Long.valueOf(t));
                values.put(TimelineContract.Columns.TEXT, status.getMessage());
                values.put(TimelineContract.Columns.USER, status.getUser());
                if (dao.insertOrIgnore(values)) { i++; }
            }
        }

        return i;
    }
}
