package com.marakana.yambaclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.marakana.yamba.data.TimelineContract;


/**
 * TimelineFragment
 */
public class TimelineFragment extends ListFragment
    implements LoaderManager.LoaderCallbacks<Cursor>
{
    /** Status update intent */
    public static final String NEW_STATUS_INTENT = "com.marakana.yamba.NEW_STATUS";

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

    // Receiver to wake up when UpdaterService gets a new status
    // It refreshes the timeline list by requerying the cursor
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        View view = super.onCreateView(inflater, container, b);

        getLoaderManager().initLoader(LOADER_ID, null, this);

        listAdapter = new SimpleCursorAdapter(getActivity(), R.layout.row, null, FROM, TO, 0);
        listAdapter.setViewBinder(new TimelineBinder());
        setListAdapter(listAdapter);

        receiver = new TimelineReceiver();
        filter = new IntentFilter(NEW_STATUS_INTENT);

        return view;
    }

    /**
     * @see android.app.Activity#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    /**
     * @see android.app.Activity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        refresh();
        getActivity().registerReceiver(receiver, filter);
    }

    /**
     * @see android.app.ListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        Cursor cursor = (Cursor) getListAdapter().getItem(pos);
        Intent intent = new Intent();
        intent.setClass(getActivity(), TimelineDetailActivity.class);
        intent.putExtra(
            TimelineActivity.TAG_TEXT,
            cursor.getString(cursor.getColumnIndex(TimelineContract.Columns.TEXT)));
        startActivity(intent);
    }

    /**
     * @see android.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
            getActivity().getApplicationContext(),
            TimelineContract.CONTENT_URI,
            null,
            null,
            null,
            null);
    }

    /**
     * @see android.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.content.Loader, java.lang.Object)
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        listAdapter.swapCursor(cursor);
        listAdapter.notifyDataSetChanged();
    }

    /**
     * @see android.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.content.Loader)
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        listAdapter.swapCursor(null);
    }

    void refresh() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }
}
