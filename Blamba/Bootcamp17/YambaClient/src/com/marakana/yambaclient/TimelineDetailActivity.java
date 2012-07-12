/* $Id: $
 */
package com.marakana.yambaclient;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;


/**
 * ContactDetailActivity
 */
public class TimelineDetailActivity extends Activity {
    private TextView detail;

    /**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        setContentView(R.layout.timeline_detail);
        detail = (TextView) findViewById(R.id.timeline_detail);

        if (null == state) { state = getIntent().getExtras(); }

        if (null != state) {
            String text = state.getString(TimelineActivity.TAG_TEXT);
            if (null != text) { detail.setText(text); }
        }
    }

    /**
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        if (null != detail) {
            CharSequence text = detail.getText();
            if (null != text) { state.putString(TimelineActivity.TAG_TEXT, text.toString()); }
        }
    }
}
