package com.twitter.yamba;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {

	public DbHelper(Context context) {
		super(context, StatusContract.DB_NAME, null, StatusContract.DB_VERSION);
	}

	// Runs run only once, first time you install the app
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = String
				.format("create table %s (%s int primary key, %s text, %s text, %s int)",
						StatusContract.RESOURCE, StatusContract.Column.ID,
						StatusContract.Column.USER,
						StatusContract.Column.MESSAGE,
						StatusContract.Column.CREATED_AT);

		Log.d("DbHelper", "onCreate with SQL: " + sql);
		db.execSQL(sql);
	}

	// Runs if oldVersion != newVersion
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Typically use alter table ...

		// Temporary for debug purposes
		db.execSQL("drop table if exists " + StatusContract.RESOURCE);
		onCreate(db);
	}

}
