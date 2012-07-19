package com.marakana.yamba;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.yamba.data.TimelineDao;
import com.marakana.yamba.svc.UpdaterService;

/**
 * YambaApplication
 */
public class YambaApplication extends Application
    implements SharedPreferences.OnSharedPreferenceChangeListener
{
    public static final String TAG = "YambaApplication";

    public static final String KEY_USERNAME = "PREFS_USER";
    public static final String KEY_PASSWORD = "PREFS_PWD";
    public static final String KEY_API_ROOT = "PREFS_URL";
    public static final String DEFAULT_API_ROOT = "http://yamba.marakana.com/api";

    private YambaClient client;
    private TimelineDao timelineDao;

    /**
     * @see android.app.Application#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application up!");

        timelineDao = new TimelineDao(this);

        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(
            AlarmManager.RTC,
            System.currentTimeMillis() + 100,
            UpdaterService.POLL_INTERVAL,
            PendingIntent.getService(
                this,
                1,
                new Intent(this, UpdaterService.class),
                PendingIntent.FLAG_UPDATE_CURRENT));
    }


    /**
     * Don't use an anonymous class to handle this event!
     * http://stackoverflow.com/questions/3799038/onsharedpreferencechanged-not-fired-if-change-occurs-in-separate-activity
     *
     * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener#onSharedPreferenceChanged(android.content.SharedPreferences, java.lang.String)
     */
    @Override
    public synchronized void onSharedPreferenceChanged(SharedPreferences key, String val) {
        client = null;
    }

    /**
     * @return the singleton DAO object
     */
    public TimelineDao getDao() { return timelineDao; }

    /**
     * @return a current, valid, twitter object
     */
    public synchronized YambaClient getYambaClient() {
        if (client == null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            client = new YambaClient(
                prefs.getString(KEY_USERNAME, "student"),
                prefs.getString(KEY_PASSWORD, "password"),
                prefs.getString(KEY_API_ROOT, YambaClient.DEFAULT_API_ROOT));
        }

        return client;
    }
}
