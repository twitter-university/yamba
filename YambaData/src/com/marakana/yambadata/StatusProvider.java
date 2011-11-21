package com.marakana.yambadata;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.marakana.yambacommon.StatusColumns;

public class StatusProvider extends ContentProvider {
	static final String TAG = "StatusProvider";
	public static final Uri CONTENT_URI = Uri
			.parse("content://com.marakana.yambadata");
	DbHelper dbHelper;

	@Override
	public boolean onCreate() {
		dbHelper = new DbHelper(this.getContext());

		Log.d(TAG, "onCreate");
		return true;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	static final String[] COLUMNS = { StatusColumns.ID, StatusColumns.TEXT,
			StatusColumns.USER, StatusColumns.CREATED_AT };

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor cursor;

		// Batch insert into the db
		cursor = dbHelper.getReadableDatabase().query(DbHelper.TABLE,
				projection, selection, selectionArgs, null, null, sortOrder);
		Log.d(TAG, "query");
		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

}
