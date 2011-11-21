package com.marakana.yambadata;

import java.util.List;

import winterwell.jtwitter.Twitter.Status;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.marakana.yambacommon.StatusColumns;

public class DbHelper extends SQLiteOpenHelper {
	static final String TAG = "DbHelper";
	static final String DB_NAME = "timeline.db";
	static final int DB_VERSION = 7;
	static final String TABLE = "status";
	ContentValues values;

	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		values = new ContentValues();
		Log.d(TAG, "constructed");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// CREATE TABLE ...
		String sql = String.format("CREATE TABLE %s (%s INT primary key, "
				+ "%s TEXT, %s TEXT, %s INT)", TABLE, StatusColumns.ID,
				StatusColumns.USER, StatusColumns.TEXT,
				StatusColumns.CREATED_AT);
		Log.d(TAG, "onCreate sql: " + sql);
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// ALTER TABLE
		db.execSQL("drop table if exists " + TABLE);
		Log.d(TAG, "onUpgrade");
		onCreate(db);
	}

	// Helper methods
	public long insert(Status status) {
		values.clear();
		values.put(StatusColumns.ID, status.id);
		values.put(StatusColumns.USER, status.user.name);
		values.put(StatusColumns.TEXT, status.text);
		values.put(StatusColumns.CREATED_AT, status.createdAt.getTime());

		return getWritableDatabase().insertWithOnConflict(TABLE, null, values,
				SQLiteDatabase.CONFLICT_IGNORE);
	}

	/** @return Number of new records */
	public int insert(List<Status> timeline) {
		int count = 0;
		SQLiteDatabase db = getWritableDatabase();
		try {
			db.beginTransaction();
			for (Status status : timeline) {
				values.clear();
				values.put(StatusColumns.ID, status.id);
				values.put(StatusColumns.USER, status.user.name);
				values.put(StatusColumns.TEXT, status.text);
				values.put(StatusColumns.CREATED_AT, status.createdAt.getTime());

				if (getWritableDatabase().insertWithOnConflict(TABLE, null,
						values, SQLiteDatabase.CONFLICT_IGNORE) != -1) {
					count++;
				}
				Log.d(TAG,
						String.format("%s: %s", status.user.name, status.text));
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		return count;
	}
}
