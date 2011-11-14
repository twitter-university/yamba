package com.marakana.yamba;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class StatusProvider extends ContentProvider {
  private static final String TAG = StatusProvider.class.getSimpleName();

  public static final String AUTHORITY = "content://com.marakana.yamba.statusprovider";
  public static final Uri CONTENT_URI = Uri.parse(AUTHORITY);
  public static final String SINGLE_RECORD_MIME_TYPE = "vnd.android.cursor.item/vnd.marakana.yamba.status";
  public static final String MULTIPLE_RECORDS_MIME_TYPE = "vnd.android.cursor.dir/vnd.marakana.yamba.status";

  static final int VERSION = 103;
  static final String DATABASE = "yamba.db";
  static final String T_STATUSES = "statuses";

  // Database columns
  static final String C_ID = "_id";
  static final String C_CREATED_AT = "status_createdAt";
  static final String C_TEXT = "status_text";
  static final String C_USER = "status_user";
  static final String C_REPLY_TO_ID = "status_inReplyToStatusId";
  static final String C_SCREEN_NAME = "user_screenName";
  static final String C_PROFILE_IMAGE_URL = "user_profileImageUrl";

  // Uri matches
  private static final UriMatcher sUriMatcher = new UriMatcher(
      UriMatcher.NO_MATCH);
  private static final int TWEETS = 1000;
  private static final int TWEETS_ID = 1001;
  private static final int MENTIONS = 2000;
  private static final int MENTIONS_ID = 2001;
  static {
    final UriMatcher matcher = sUriMatcher;
    matcher.addURI(AUTHORITY, "tweets", TWEETS);
    matcher.addURI(AUTHORITY, "tweets/#", TWEETS_ID);
    matcher.addURI(AUTHORITY, "mentions", MENTIONS);
    matcher.addURI(AUTHORITY, "mentions/#", MENTIONS_ID);
  }

  // DbHelper implementations
  class DbHelper extends SQLiteOpenHelper {

    public DbHelper(Context context) {
      super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      Log.i(TAG, "Creating database: " + DATABASE);
      db.execSQL("create table " + T_STATUSES + " (" + C_ID
          + " int primary key, " + C_REPLY_TO_ID + " int, " + C_CREATED_AT
          + " int, " + C_USER + " text, " + C_SCREEN_NAME + " text, "
          + C_PROFILE_IMAGE_URL + " text, " + C_TEXT + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      Log.i(TAG, "Upgrading database: " + DATABASE + " from " + oldVersion
          + " to " + newVersion);
      db.execSQL("drop table if exists " + T_STATUSES);
      this.onCreate(db);
    }
  }

  DbHelper dbHelper;

  @Override
  public String getType(Uri uri) {
    return StatusProvider.getId(uri) < 0 ? MULTIPLE_RECORDS_MIME_TYPE
        : SINGLE_RECORD_MIME_TYPE;
  }

  @Override
  public boolean onCreate() {
    dbHelper = new DbHelper(this.getContext());
    return true;
  }

  @Override
  public Uri insert(Uri uri, ContentValues values) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    // try {
    long id = db.insertWithOnConflict(T_STATUSES, null, values,
        SQLiteDatabase.CONFLICT_IGNORE);
    Log.d(TAG, "inserting: " + values.toString() );
    if (id == -1) {
//      throw new RuntimeException(String.format(
//          "%s: Failed to insert [%s] to [%s] for unknown reasons.", TAG,
//          values, uri));
      // TODO not sure why I'm getting -1 here.
      return null;
    } else {
      Uri newUri = ContentUris.withAppendedId(uri, id);
      // Notify the Context's ContentResolver of the change
      getContext().getContentResolver().notifyChange(newUri, null);
      Log.d(TAG, "inserted " + newUri.toString());
      return newUri;
    }
    // }
    // finally {
    // db.close();
    // }
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection,
      String[] selectionArgs) {
    long id = StatusProvider.getId(uri);
    int count;
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    try {
      if (id < 0) {
        count = db.update(T_STATUSES, values, selection, selectionArgs);
      } else {
        count = db.update(T_STATUSES, values, C_ID + "=" + id, null);
      }
    } finally {
      db.close();
    }

    // Notify the Context's ContentResolver of the change
    getContext().getContentResolver().notifyChange(uri, null);

    return count;
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    long id = StatusProvider.getId(uri);
    int count;
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    try {
      if (id < 0) {
        count = db.delete(T_STATUSES, selection, selectionArgs);
      } else {
        count = db.delete(T_STATUSES, C_ID + "=" + id, null);
      }
    } finally {
      db.close();
    }

    // Notify the Context's ContentResolver of the change
    getContext().getContentResolver().notifyChange(uri, null);

    return count;
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection,
      String[] selectionArgs, String sortOrder) {
    Cursor c = null;
    SQLiteDatabase db = dbHelper.getReadableDatabase();

    Log.d(TAG, "querying uri: " + uri.toString());

    // long id = StatusProvider.getId(uri);

    c = db.query(T_STATUSES, projection, selection, selectionArgs, null, null,
        sortOrder);

    // Notify the context's ContentResolver if the cursor result set changes
    c.setNotificationUri(getContext().getContentResolver(), uri);

    Log.d(TAG, "queried records: " + c.getCount());

    return c;
  }

  // Helper method to extract ID from Uri
  public static long getId(Uri uri) {
    if (uri == null)
      return -1;

    String lastPathSegment = uri.getLastPathSegment();
    if (lastPathSegment != null) {
      try {
        return Long.parseLong(lastPathSegment);
      } catch (NumberFormatException e) {
        // at least we tried
      }
    }
    return -1;
  }

}
