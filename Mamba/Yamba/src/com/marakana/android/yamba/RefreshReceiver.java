package com.marakana.android.yamba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RefreshReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("RefreshReceiver", "onReceive");
		context.startService( new Intent(context, RefreshService.class));
	}

}
