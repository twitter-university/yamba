package com.marakana.yamba.data;

import android.net.Uri;
import android.provider.BaseColumns;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
public final class TimelineContract {

    // Prevent instantiation
    private TimelineContract() {}

    /** The data for which we are the authority */
    public static final String AUTHORITY = "com.marakana.yamba.timeline";

    /** The permission necessary to read this data */
    public static final String PERMISSION = "com.marakana.yamba.timeline.READ_TIMELINE";

    /** Our table */
    public static final String TABLE_TIMELINE = "timeline";

    /** Our base URI */
    public static final Uri CONTENT_URI
        = Uri.parse("content://" + AUTHORITY + "/" + TABLE_TIMELINE);

    /** the MIME type of our data */
    public static final String TYPE_ITEM
        = "vnd.android.cursor.item/vnd.com.marakana.yamba.timeline";

    /** The MIME type of lists of our data */
    public static final String TYPE_DIR
        = "vnd.android.cursor.dir/vnd.com.marakana.yamba.timeline";

    /**
     * Column definitions for status information.
     */
    public final static class Columns implements BaseColumns {

        // Prevent instantiation
        private Columns() {}

        /** */
        public static final String CREATED_AT = "created_at";
        /** */
        public static final String TEXT = "txt";
        /** */
        public static final String USER = "user";
    }
}
