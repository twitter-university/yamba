/* $Id: $
 */
package com.marakana.yamba;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

import com.marakana.yamba.data.TimelineContract;
import com.marakana.yamba.data.TimelineDao;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
public class TimelineActivity extends ListActivity
implements LoaderManager.LoaderCallbacks<Cursor>
{
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

    static class TimelineLoader extends AsyncTaskLoader<Cursor> {
        private final TimelineDao db;
        private volatile boolean loaded;

        public TimelineLoader(Context context, TimelineDao db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor c = db.getTimeline();
            loaded = true;
            return c;
        }

        // see bug: http://code.google.com/p/android/issues/detail?id=14944
        @Override
        protected void onStartLoading() {
            if (!loaded) { forceLoad(); }
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

    /**
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * @param item
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemTimeline:
                startActivity(new Intent(this, TimelineActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                break;

            case R.id.itemStatus:
                startActivity(new Intent(this, StatusActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                break;

            case R.id.itemPrefs:
                startActivity(new Intent(this, PrefsActivity.class));
                return true;

            default:
                Log.d(TAG, "Unrecognized menu item: " + item);
                return false;
        }

        return true;
    }

    /**
     * @see android.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new TimelineLoader(this, ((YambaApplication) getApplication()).getDao());
    }

    /**
     * @see android.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.content.Loader, java.lang.Object)
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        listAdapter.swapCursor(data);
    }

    /**
     * @see android.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.content.Loader)
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        listAdapter.swapCursor(null);
    }

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
    }
}
