package com.marakana.yamba;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;
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

    private YambaClient client;

    private TextView textCount;
    private EditText editText;
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

        client = new YambaClient("student", "password");

        toast = Toast.makeText(this, null, Toast.LENGTH_LONG);
    }

    /**
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateStatusLen();
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
        String msg = editText.getText().toString();
        clearText();

        //!!! This implementation is broken!
        // Do not use in production!
        if (!TextUtils.isEmpty(msg)) {
            done(Integer.valueOf(post(msg)));
        }
    }

    void clearText() {
        editText.setText("");
        updateStatusLen();
    }

    int post(String status) {
        try {
            Log.d(TAG, "posting status: " + status);
            client.postStatus(status);
            // Emulate a slow network
            try { Thread.sleep(60 * 1000); }
            catch (InterruptedException e) { }
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
    }
}
