package com.marakana.yambaclient;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * ContactDetailFragment
 */
public class TimelineDetailFragment extends Fragment {

    /**
     * @param txt
     * @return a new TimelineDetailFragment
     */
    public static final TimelineDetailFragment newInstance(String txt) {
        Bundle init = new Bundle();
        init.putString(TimelineActivity.TAG_TEXT, txt);
        return newInstance(init);
    }

    /**
     * @param init
     * @return a new ContactDetailFragment
     */
    public static final TimelineDetailFragment newInstance(Bundle init) {
        TimelineDetailFragment frag = new TimelineDetailFragment();
        frag.setArguments(init);
        return frag;
    }

    private String text;

    /**
     * @see android.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        if (null == state) { state = getArguments(); }

        text = state.getString(TimelineActivity.TAG_TEXT);
    }

    /**
     * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        View view = inflater.inflate(
            R.layout.timeline_detail,
            container,
            false);  //!!! this is important

        ((TextView) view).setText(text);

        return view;
    }

    /**
     * @see android.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString(TimelineActivity.TAG_TEXT, text);
    }
}
