/* $Id: $
   Copyright 2012, G. Blake Meike

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.marakana.yamba;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
public class ActionBarMgr {
    private static final String TAG = "ActionBarMgr";

    private final Activity activity;

    /**
     * @param activity
     */
    public ActionBarMgr(Activity activity) { this.activity = activity; }

    /**
     * @param menu
     * @return true
     */
    public boolean populateActionBar(Menu menu) {
        MenuInflater inflater = activity.getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * @param item
     * @return true
     */
    public boolean handleSelection(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemTimeline:
                activity.startActivity(new Intent(activity, TimelineActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                break;

            case R.id.itemStatus:
                activity.startActivity(new Intent(activity, StatusActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                break;

            case R.id.itemPrefs:
                activity.startActivity(new Intent(activity, PrefsActivity.class));
                break;

            default:
                Log.d(TAG, "Unrecognized menu item: " + item);
                return false;
        }

        return true;
    }
}
