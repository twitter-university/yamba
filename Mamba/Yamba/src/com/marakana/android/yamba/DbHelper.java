package com.marakana.android.yamba;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {

	public DbHelper(Context context) {
		super(context, StatusContract.DB_NAME, null, StatusContract.DB_VERSION);
	}

	/** Called only once to create the database first time. */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = String.format("create table %s (%s int primary key, %s int, %s text, %s text)",
				StatusContract.TABLE, StatusContract.Columns._ID, 
				StatusContract.Columns.CREATED_AT, StatusContract.Columns.USER,
				StatusContract.Columns.TEXT);
		Log.d("DbHelper", "SQL: "+sql);
		db.execSQL(sql);
	}

	/** Called every time oldVersion != newVersion. */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Typically you do ALTER TABLE ..., but we don't have previous userbase
		db.execSQL("drop table if exists " + StatusContract.TABLE);
		this.onCreate(db);
	}


}
