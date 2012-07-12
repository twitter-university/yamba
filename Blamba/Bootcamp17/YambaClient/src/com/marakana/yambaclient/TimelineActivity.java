package com.marakana.yambaclient;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;


/**
 * TimelineActivity
 */
public class TimelineActivity extends Activity {
    /** Bundle tag for Id */
    public static final String TAG_TEXT = "TEXT";
    private static final String FRAG_TAG = "Client.TimelineActivity";

    boolean useFrag;

    /**
     * @see android.app.Activity#startActivityFromFragment(android.app.Fragment, android.content.Intent, int)
     */
    @Override
    public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode) {
        if (!useFrag) { startActivity(intent); }
        else if (fragment instanceof TimelineFragment) {
            launchDetailFragment(intent.getExtras());
        }
    }

    /**
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);

        useFrag = null != findViewById(R.id.timelineDetail);

        if (useFrag) { installDetailsFragment(); }
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

    private void launchDetailFragment(Bundle xtra) {
        FragmentTransaction xact = getFragmentManager().beginTransaction();

        xact.replace(
            R.id.timelineDetail,
            TimelineDetailFragment.newInstance(xtra),
            FRAG_TAG);

        xact.addToBackStack(null);
        xact.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        xact.commit();
    }
}
