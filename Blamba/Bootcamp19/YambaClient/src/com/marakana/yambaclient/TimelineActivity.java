package com.marakana.yambaclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


/**
 * TimelineActivity
 */
public class TimelineActivity extends FragmentActivity {
    /** Bundle tag for Id */
    public static final String TAG_TEXT = "TEXT";
    private static final String FRAG_TAG = "Client.TimelineActivity";

    boolean useFrag;

    /**
     * @see android.support.v4.app.FragmentActivity#startActivityFromFragment(android.support.v4.app.Fragment, android.content.Intent, int)
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
        FragmentManager fragMgr = getSupportFragmentManager();

        if (null != fragMgr.findFragmentByTag(FRAG_TAG)) { return; }

        FragmentTransaction xact = fragMgr.beginTransaction();
        xact.add(
            R.id.timelineDetail,
            TimelineDetailFragment.newInstance("nothing yet"),
            FRAG_TAG);
        xact.commit();
    }

    private void launchDetailFragment(Bundle xtra) {
        FragmentTransaction xact = getSupportFragmentManager().beginTransaction();

        xact.replace(
            R.id.timelineDetail,
            TimelineDetailFragment.newInstance(xtra),
            FRAG_TAG);

        xact.addToBackStack(null);
        xact.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        xact.commit();
    }
}
