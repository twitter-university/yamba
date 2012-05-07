package com.marakana.android.yamba;

import winterwell.jtwitter.Twitter.Status;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class StatusData {
	private static final String TAG = StatusData.class.getSimpleName();

	// DB-related constants
	static final String DB_NAME = "timeline.db";
	static final int DB_VERSION = 1;
	static final String TABLE = "statuses";
	static final String C_ID = BaseColumns._ID;
	static final String C_CREATED_AT = "yabma_created_at";
	static final String C_USER = "yamba_user";
	static final String C_TEXT = "yamba_text";

	static final String SORT_BY = C_CREATED_AT + " DESC";

	private DbHelper dbHelper;

	/** Constructor */
	public StatusData(Context context) {
		dbHelper = new DbHelper(context);
	}

	/** Converts Status to ContentValues. */
	public static ContentValues statusToValues(Status status) {
		ContentValues values = new ContentValues();
		values.put(C_ID, status.id);
		values.put(C_CREATED_AT, status.createdAt.getTime());
		values.put(C_USER, status.user.name);
		values.put(C_TEXT, status.text);
		return values;
	}

	/** Inserts the status into the database. */
	public long insert(Status status) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		// Create content values from status object
		ContentValues values = new ContentValues();
		values.put(C_ID, status.id);
		values.put(C_CREATED_AT, status.createdAt.getTime());
		values.put(C_USER, status.user.name);
		values.put(C_TEXT, status.text);

		return db.insertWithOnConflict(TABLE, null, values,
				SQLiteDatabase.CONFLICT_IGNORE);
	}

	/** Returns all statuses in timeline. */
	public Cursor query() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		return db.query(TABLE, null, null, null, null, null, SORT_BY);
	}

	/** Class to help us create and upgrade database. */
	class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
			Log.d(TAG, "DbHelper() instantiated");
		}

		/** Called only once, first time we create the database file. */
		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = String.format("CREATE TABLE %s "
					+ "(%s INT PRIMARY KEY, %s INT," + "%s TEXT, %s TEXT)",
					TABLE, C_ID, C_CREATED_AT, C_USER, C_TEXT);
			Log.d(TAG, "onCreate with SQL: " + sql);
			db.execSQL(sql);
		}

		/** Called when the old schema is different then new schema. */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Typically SQL such as: ALTER TABLE ADD COLUMN ...
			db.execSQL("DROP TABLE IF EXISTS " + TABLE);
			onCreate(db);
		}
	}

	/** Getter. */
	public DbHelper getDbHelper() {
		return dbHelper;
	}
}
