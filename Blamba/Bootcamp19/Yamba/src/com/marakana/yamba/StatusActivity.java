package com.marakana.yamba;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClientException;


/**
 * StatusActivity
 */
public class StatusActivity extends Activity {
    /** Debug tag */
    public static final String TAG = "StatusActivity";

    /** Max status update len */
    public static final int MAX_TEXT = 140;
    /** Long update warning */
    public static final int YELLOW_LEVEL = 7;
    /** Update over max len */
    public static final int RED_LEVEL = 0;

    // if two different threads share access to the same mutable state
    // they must do it holding the same lock.
    static class Poster extends AsyncTask<String, Void, Integer> {
        private volatile StatusActivity activity;

        public void setActivity(StatusActivity activity) {
            this.activity = activity;
        }

        @Override
        protected Integer doInBackground(String... args) {
            int status = R.string.statusFail;
            if (null != activity) { status = activity.post(args[0]); }
            return Integer.valueOf(status);
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (null != activity) { activity.done(result); }
        }
    }

    private ActionBarMgr actionBar;
    private TextView textCount;
    private EditText editText;
    private Poster poster;
    private Toast toast;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        textCount = (TextView) findViewById(R.id.countText);

        editText = (EditText) findViewById(R.id.statusText);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                updateStatusLen();
                return false;
            }
        });

        findViewById(R.id.statusButton).setOnClickListener(
            new Button.OnClickListener() {
                @Override public void onClick(View v) { update(); }
            } );

        actionBar = new ActionBarMgr(this);

        toast = Toast.makeText(this, null, Toast.LENGTH_LONG);
    }

    /**
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return actionBar.populateActionBar(menu);
    }

    /**
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return (R.id.itemStatus == item.getItemId())
            ? false
            : actionBar.handleSelection(item);
    }

    /**
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (null != poster) { poster.setActivity(null); }
    }

    /**
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateStatusLen();
        if (null != poster) { poster.setActivity(this); }
    }

    void updateStatusLen() {
        int remaining = MAX_TEXT - editText.getText().length();

        int color;
        if (remaining <= RED_LEVEL) { color = Color.RED; }
        else if (remaining <= YELLOW_LEVEL) { color = Color.YELLOW; }
        else { color = Color.GREEN; }

        textCount.setText(String.valueOf(remaining));
        textCount.setTextColor(color);
    }

    void update() {
        // only allow one post in flight
        if (poster != null) { return; }

        String msg = editText.getText().toString();
        clearText();

        if (!TextUtils.isEmpty(msg)) {
            poster = new Poster();
            poster.setActivity(this);
            poster.execute(msg);
        }
    }

    void clearText() {
        editText.setText("");
        updateStatusLen();
    }

    // !!! run on a different thread!
    int post(String status) {
        try {
            Log.d(TAG, "posting status: " + status);
            ((YambaApplication) getApplication()).getYambaClient().postStatus(status);
            return R.string.statusSuccess;
        }
        catch (YambaClientException e) {
            Log.e(TAG, "Failed to post message", e);
        }

        return R.string.statusFail;
    }

    void done(Integer result) {
        Log.d(TAG, "status posted!");
        toast.setText(result.intValue());
        toast.show();
        poster = null;
    }
}
