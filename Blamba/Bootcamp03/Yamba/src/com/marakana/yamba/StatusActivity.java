package com.marakana.yamba;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


/**
 * StatusActivity
 */
public class StatusActivity extends Activity {

    /** Max status update len */
    public static final int MAX_TEXT = 140;
    /** Long update warning */
    public static final int YELLOW_LEVEL = 7;
    /** Update over max len */
    public static final int RED_LEVEL = 0;

    private TextView textCount;
    private EditText editText;

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
}
