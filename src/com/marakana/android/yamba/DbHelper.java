package com.marakana.android.yamba;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {

	public DbHelper(Context context) {
		super(context, StatusContract.DB_NAME, null, StatusContract.DB_VERSION);
	}

	/** Gets called ONLY if the database file does not exist. */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = String.format("create table %s ("
				+ "%s int primary key, %s text, %s text, %s int )",
				StatusContract.TABLE, StatusContract.Columns.ID,
				StatusContract.Columns.USER, StatusContract.Columns.MESSAGE,
				StatusContract.Columns.CREATED_AT);
		Log.d("Yamba", "DbHelper onCreate SQL: "+sql);
		db.execSQL(sql);
	}

	/** Called ONLY if oldVersion != newVersion */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// alter table status add column ..., BUT for now:
		db.execSQL("drop table if exists " + StatusContract.TABLE);
		this.onCreate(db);
	}

}
