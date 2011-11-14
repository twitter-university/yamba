package com.marakana.yamba;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class TimelineFragment extends ListFragment implements
    LoaderManager.LoaderCallbacks<Cursor> {
  static final String TAG = "TimelineFragment";

  static final String[] FROM = new String[] { StatusProvider.C_USER,
      StatusProvider.C_TEXT, StatusProvider.C_PROFILE_IMAGE_URL };
  static final int[] TO = new int[] { R.id.status_username, R.id.status_text,
      R.id.status_image };

  static final int LOADER_TIMELINE = 1;
  static final int LOADER_MENTIONS = 2;

  // This is the Adapter being used to display the list's data.
  SimpleCursorAdapter adapter;

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    // Give some text to display if there is no data. In a real
    // application this would come from a resource.
    setEmptyText("No timeline yet...");

    // Create an empty adapter we will use to display the loaded data.
    adapter = new SimpleCursorAdapter(getActivity(), R.layout.status_row, null,
        FROM, TO, 0);
    adapter.setViewBinder(VIEW_BINDER);
    setListAdapter(adapter);

    // Prepare the loader. Either re-connect with an existing one,
    // or start a new one.
    getLoaderManager().initLoader(LOADER_TIMELINE, null, this);
    Log.d(TAG, "onActivityCreated");
  }

  // These are the status rows that we will retrieve.
  static final String[] PROJECTION = new String[] { StatusProvider.C_ID,
      StatusProvider.C_CREATED_AT, StatusProvider.C_USER,
      StatusProvider.C_SCREEN_NAME, StatusProvider.C_PROFILE_IMAGE_URL,
      StatusProvider.C_TEXT, StatusProvider.C_REPLY_TO_ID };

  /* Implementation of LoaderManager.LoaderCallbacks */
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    // This is called when a new Loader needs to be created.
    // We only have one Loader, so we don't care about the ID.
    Log.d(TAG, "onCreateLoader");

    switch (id) {
    case LOADER_TIMELINE:
      Log.d(TAG, "onCreateLoader for LOADER_TIMELINE");
      return new CursorLoader(getActivity(), Uri.withAppendedPath(
          StatusProvider.CONTENT_URI, "tweets"), null, null, null, null);
    case LOADER_MENTIONS:
      Log.d(TAG, "onCreateLoader for LOADER_MENTIONS");
      return new CursorLoader(getActivity(), Uri.withAppendedPath(
          StatusProvider.CONTENT_URI, "mentions"), null, null, null, null);
    }
    return null; // Unimplemented loader
  }

  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    // Swap the new cursor in. (The framework will take care of closing the
    // old cursor once we return.)
    Log.d(TAG, "onLoadFinished with " + data.getCount());
    adapter.swapCursor(data);
  }

  public void onLoaderReset(Loader<Cursor> loader) {
    // This is called when the last Cursor provided to onLoadFinished()
    // above is about to be closed. We need to make sure we are no
    // longer using it.
    Log.d(TAG, "onLoaderReset");
    adapter.swapCursor(null);
  }

  // Used to convert the url string to the profile image to the actual image
  // view. It is attached the adapter.
  static final ViewBinder VIEW_BINDER = new ViewBinder() {
    int IMAGE_COLUMN_INDEX = -1;
    DrawableManager drawableManager = new DrawableManager();

    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
      if (view.getId() != R.id.status_image)
        return false;

      // Initialize the index, so we don't do it every single time
      if (IMAGE_COLUMN_INDEX == -1)
        IMAGE_COLUMN_INDEX = cursor
            .getColumnIndex(StatusProvider.C_PROFILE_IMAGE_URL);
      String imageUrlString = cursor.getString(IMAGE_COLUMN_INDEX);
      ImageView image = (ImageView) view;
      drawableManager.fetchDrawableOnThread(imageUrlString, image);
      Log.d(TAG, "IMAGE: " + cursor.getString(IMAGE_COLUMN_INDEX));
      return true;

    }
  };
}
