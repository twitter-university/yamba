package com.twitter.yamba;

import android.net.Uri;
import android.provider.BaseColumns;

public final class StatusContract {
	private StatusContract() {
	};

	public static final String NEW_STATUS_ACTION = "com.twitter.yamba.action.NEW_STATUS";

	// Shared
	public static final String RESOURCE = "statuses"; // table or path

	final class Column {
		public static final String ID = BaseColumns._ID;
		public static final String CREATED_AT = "yamba_created_at";
		public static final String USER = "yamba_user";
		public static final String MESSAGE = "yamba_message";
	}

	public static final String SORT_BY = Column.CREATED_AT + " DESC";

	// DB constants
	public static final String DB_NAME = "timeline.db";
	public static final int DB_VERSION = 1;

	// CP constants
	public static final String AUTHORITY = "com.twitter.yamba.provider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + RESOURCE);
	public static final int STATUSES = 1; // All statuses
	public static final int STATUSES_ID = 2; // A specific status
	public static final String TYPE_STATUS = "vnd.android.cursor.item/vnd.twitter.yamba.status";
	public static final String TYPE_STATUSES = "vnd.android.cursor.dir/vnd.twitter.yamba.status";

}
