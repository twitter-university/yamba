package com.marakana.android.yamba;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class StatusProvider extends ContentProvider {
	private static final String TAG = StatusProvider.class.getSimpleName();

	public static final String AUTHORITY = "com.marakana.android.yamba.statusprovider";
	public static final Uri CONTENT_URI = Uri.parse("content://com.marakana.android.yamba.statusprovider/status");
	public static final String URI_TYPE_STATUS = "status";
	public static final String TYPE_SINGLE_STATUS = "vnd.android.cursor.item/vnd.marakana.yamba.status";
	public static final String TYPE_ALL_STATUS = "vnd.android.cursor.dir/vnd.marakana.yamba.status";
	private static final int ALL_STATUS = 1;
	private static final int SINGLE_STATUS = 2;

	private static UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, URI_TYPE_STATUS, ALL_STATUS);
		uriMatcher.addURI(AUTHORITY, URI_TYPE_STATUS + "/#", SINGLE_STATUS);
	}

	private StatusData statusData;

	@Override
	public boolean onCreate() {
		statusData = new StatusData(getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case ALL_STATUS:
			return TYPE_ALL_STATUS;

		case SINGLE_STATUS:
			return TYPE_SINGLE_STATUS;

		default:
			throw new IllegalArgumentException("Unknown mime type: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = statusData.getDbHelper().getWritableDatabase();
		try {
			long id = db.insertWithOnConflict(StatusData.TABLE, null, values,
					SQLiteDatabase.CONFLICT_IGNORE);
			if (id != -1) {
				Uri newUri = ContentUris.withAppendedId(uri, id);

				// Notify ContentResolver of any changes
				getContext().getContentResolver().notifyChange(newUri, null);

				return newUri;
			} else {
				return uri;
			}
		} finally {
			db.close();
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count = 0;
		SQLiteDatabase db = statusData.getDbHelper().getWritableDatabase();

		try {
			switch (uriMatcher.match(uri)) {
			case ALL_STATUS:
				count = db.update(StatusData.TABLE, values, selection,
						selectionArgs);
				break;

			case SINGLE_STATUS:
				count = db.update(StatusData.TABLE, values, StatusData.C_ID
						+ "=" + ContentUris.parseId(uri), null);
				break;

			default:
				Log.d(TAG, "Unrecognized URI: " + uri);
				break;
			}
		} finally {
			db.close();
		}

		// Notify ContentResolver of changes
		getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count = 0;
		SQLiteDatabase db = statusData.getDbHelper().getWritableDatabase();

		try {
			switch (uriMatcher.match(uri)) {
			case ALL_STATUS:
				count = db.delete(StatusData.TABLE, selection, selectionArgs);
				break;

			case SINGLE_STATUS:
				count = db.delete(StatusData.TABLE, StatusData.C_ID + "="
						+ ContentUris.parseId(uri), null);
				break;

			default:
				Log.d(TAG, "Unrecognized URI: " + uri);
				break;
			}
		} finally {
			db.close();
		}

		// Notify ContentResolver of changes
		getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = statusData.getDbHelper().getReadableDatabase();

		Cursor cursor = null;

		switch (uriMatcher.match(uri)) {
		case ALL_STATUS:
			cursor = db.query(StatusData.TABLE, projection, selection,
					selectionArgs, null, null, sortOrder);
			Log.d(TAG, "query for ALL_STATUS uri: "+uri);
			break;

		case SINGLE_STATUS:
			cursor = db.query(StatusData.TABLE, projection, StatusData.C_ID
					+ "=" + ContentUris.parseId(uri), null, null, null, null);
			Log.d(TAG, "query for SINGLE_STATUS id: "+ContentUris.parseId(uri));
			break;

		default:
			Log.d(TAG, "Unknown URI: " + uri);
			break;
		}

		// Notify ContentResolver of any changes
		if (null != cursor) {
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
		}

		return cursor;
	}

}
