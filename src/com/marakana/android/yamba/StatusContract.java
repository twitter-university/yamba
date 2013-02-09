package com.marakana.android.yamba;

import android.net.Uri;
import android.provider.BaseColumns;

public class StatusContract {

  private StatusContract() {
  }

  public static final String DB_NAME = "timeline.db";
  public static final int DB_VERSION = 1;
  public static final String TABLE = "status";

  public static final String AUTHORITY = "com.marakana.android.yamba.provider.timeline";
  public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
      + "/" + TABLE);
  public static final int CONTENT_TYPE_ITEM = 1;
  public static final int CONTENT_TYPE_DIR = 2;

  class Columns {
    public static final String ID = BaseColumns._ID;
    public static final String USER = "user";
    public static final String MESSAGE = "message";
    public static final String CREATED_AT = "created_at";
  }

  public static final String DEFAULT_SORT = Columns.CREATED_AT + " desc";
}
