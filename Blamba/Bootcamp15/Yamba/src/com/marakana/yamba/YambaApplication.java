package com.marakana.yamba;

import winterwell.jtwitter.Twitter;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

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

    private Twitter twitter;
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
        twitter = null;
    }

    /**
     * @return the singleton DAO object
     */
    public TimelineDao getDao() { return timelineDao; }

    /**
     * @return a current, valid, twitter object
     */
    public synchronized Twitter getTwitter() {
        if (twitter == null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String user = prefs.getString(KEY_USERNAME, "");
            String pass = prefs.getString(KEY_PASSWORD, "");
            String api = prefs.getString(KEY_API_ROOT, DEFAULT_API_ROOT);

            Twitter t = new Twitter(user, pass);
            t.setAPIRootUrl(api);

            Log.d(TAG, "new twitter: " + user + ", " + pass + ", " + api);
            twitter = t;
        }

        return twitter;
    }
}
