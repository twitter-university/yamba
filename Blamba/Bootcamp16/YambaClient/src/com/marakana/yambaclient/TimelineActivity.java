package com.marakana.yambaclient;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;


/**
 * TimelineActivity
 */
public class TimelineActivity extends Activity {
    /** Bundle tag for Id */
    public static final String TAG_TEXT = "TEXT";
    private static final String FRAG_TAG = "Client.TimelineActivity";

    /**
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);

        installDetailsFragment();
   }

    /**
     * @param text the text to be put into the fragment
     */
    public void launchDetailFragment(String text) {
        FragmentTransaction xact = getFragmentManager().beginTransaction();

        xact.replace(
            R.id.timelineDetail,
            TimelineDetailFragment.newInstance(text),
            FRAG_TAG);

        xact.addToBackStack(null);
        xact.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        xact.commit();
    }

    private void installDetailsFragment() {
        FragmentManager fragMgr = getFragmentManager();

        if (null != fragMgr.findFragmentByTag(FRAG_TAG)) { return; }

        FragmentTransaction xact = fragMgr.beginTransaction();
        xact.add(
            R.id.timelineDetail,
            TimelineDetailFragment.newInstance("nothing yet"),
            FRAG_TAG);
        xact.commit();
    }
}
