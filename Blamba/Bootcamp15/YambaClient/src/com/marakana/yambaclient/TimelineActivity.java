/* $Id: $
 */
package com.marakana.yambaclient;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

import com.marakana.yamba.data.TimelineContract;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
public class TimelineActivity extends ListActivity
    implements LoaderManager.LoaderCallbacks<Cursor>
{
    public static final String NEW_STATUS_INTENT = "com.marakana.yamba.NEW_STATUS";

    private static final String TAG = "TimelineActivity";

    private static final int LOADER_ID = 37;


    private static final String[] FROM = new String[] {
        TimelineContract.Columns.USER,
        TimelineContract.Columns.CREATED_AT,
        TimelineContract.Columns.TEXT
    };

    private static final int[] TO = new int[] {
        R.id.textUser,
        R.id.textTime,
        R.id.textStatus
    };

    class TimelineReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
            Log.d("TAG", "Notification Received");
        }
    }

    static class TimelineBinder implements ViewBinder {

        @Override
        public boolean setViewValue(View view, Cursor cursor, int colIndex) {
            if (view.getId() != R.id.textTime) { return false; }

            String tStr = "ages ago";
            long t = cursor.getLong(colIndex);
            if (0 < t) {
                tStr = DateUtils.getRelativeTimeSpanString(t, System.currentTimeMillis(), 0)
                    .toString();
            }
            ((TextView) view).setText(tStr);
            return true;
        }
    }

    private SimpleCursorAdapter listAdapter;
    private TimelineReceiver receiver;
    private IntentFilter filter;

    /**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLoaderManager().initLoader(LOADER_ID, null, this);

        listAdapter = new SimpleCursorAdapter(this, R.layout.row, null, FROM, TO, 0);
        listAdapter.setViewBinder(new TimelineBinder());
        setListAdapter(listAdapter);

        receiver = new TimelineReceiver();
        filter = new IntentFilter(NEW_STATUS_INTENT);
    }

    /**
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, filter);
    }

    /**
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        refresh();
    }
    /**
     * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
            this,
            TimelineContract.CONTENT_URI,
            null,
            null,
            null,
            null);
    }

    /**
     * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.support.v4.content.Loader, java.lang.Object)
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        listAdapter.swapCursor(cursor);
        listAdapter.notifyDataSetChanged();
    }

    /**
     * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.support.v4.content.Loader)
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        listAdapter.swapCursor(null);
    }

    void refresh() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }
}
