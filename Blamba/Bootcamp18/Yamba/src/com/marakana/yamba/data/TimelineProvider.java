package com.marakana.yamba.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

/**
 * TimelineProvider
 */
public class TimelineProvider extends ContentProvider {

    // Constants to help differentiate between the URI requests
    private static final int STATUS_DIR = 1;
    private static final int STATUS_ITEM = 2;

    // Static initializer, allocating a UriMatcher object. A URI ending in "/status" is a
    // request for all statuses, and a URI ending in "/status/<id>" refers to a single status.
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(TimelineContract.AUTHORITY, TimelineContract.TABLE, STATUS_DIR);
        uriMatcher.addURI(TimelineContract.AUTHORITY, TimelineContract.TABLE + "/#", STATUS_ITEM);
    }

    // cached local reference to the DAO object
    private TimelineDao dao;

    /**
     * Identify the MIME types we provide for a given URI
     *
     * @see android.content.ContentProvider#getType(android.net.Uri)
     */
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case STATUS_DIR:
                return TimelineContract.TYPE_DIR;
            case STATUS_ITEM:
                return TimelineContract.TYPE_ITEM;
            default:
                return null;
        }
    }

    /**
     * @see android.content.ContentProvider#onCreate()
     */
    @Override
    public boolean onCreate() {
        Context context = getContext();
        dao = new TimelineDao(context);
        return dao != null;
    }

    /**
     * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
     */
    @Override
    public Cursor query(
        Uri uri, String[] projection,
        String selection,
        String[] selectionArgs,
        String sort)
    {
        switch (uriMatcher.match(uri)) {
            case STATUS_DIR:
                break;
            case STATUS_ITEM:
                // If this is a request for an individual status, limit the result set to that ID
                selection = selectId(ContentUris.parseId(uri), selection);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Cursor c = dao.query(
            projection,
            selection,
            selectionArgs,
            (!TextUtils.isEmpty(sort)) ? sort : TimelineContract.Columns.DEFAULT_SORT_ORDER);

        // Notify the context's ContentResolver if the cursor result set changes
        c.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor to the result set
        return c;
    }

    /**
     * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
     */
    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        throw new IllegalArgumentException("Unsupported URI: " + uri);
    }

    /**
     * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
     */
    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        throw new IllegalArgumentException("Unsupported URI: " + uri);
    }

    /**
     * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
     */
    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        throw new IllegalArgumentException("Unsupported URI: " + uri);
    }

    private String selectId(long id, String sel) {
        if (0 > id) { throw new IllegalArgumentException("id must be >= 0"); }

        StringBuilder selection = new StringBuilder();
        selection.append(BaseColumns._ID).append("=").append(id);
        if (sel != null) { selection.insert(0, "(").append(") AND (").append(sel).append(")"); }

        return selection.toString();
    }
}