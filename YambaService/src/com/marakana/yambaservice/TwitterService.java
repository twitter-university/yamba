package com.marakana.yambaservice;

import winterwell.jtwitter.Twitter;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.marakana.yambacommon.ITwitterService;
import com.marakana.yambacommon.IYambaListener;
import com.marakana.yambacommon.YambaStatus;

public class TwitterService extends Service {
	static final String TAG = "TwitterService";
	Twitter twitter;

	@Override
	public void onCreate() {
		super.onCreate();
		twitter = new Twitter("student", "password");
		twitter.setAPIRootUrl("http://yamba.marakana.com/api");
		Log.d(TAG, "onCreate");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		twitter = null;
		Log.d(TAG, "onDestroy");
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");

		return new ITwitterService.Stub() {

			@Override
			public boolean updateStatus(String status) throws RemoteException {
				twitter.setStatus(status);
				Log.d(TAG, "updateStatus: " + status);
				return true;
			}

			@Override
			public boolean update(YambaStatus status) throws RemoteException {
				twitter.setStatus(status.text);
				Log.d(TAG, "update: " + status);
				return true;
			}

			@Override
			public boolean asyncUpdate(String status, final IYambaListener listener)
					throws RemoteException {
				Log.d(TAG, "asyncUpdate");
				new Thread() {
					public void run() {
						try {
							sleep(10000);
							listener.onUpdateResult((int)System.currentTimeMillis());
							Log.d(TAG, "asyncUpdate run() done: ");
						} catch (InterruptedException e) {
							Log.e(TAG, "asyncUpdate InterruptedException", e);
						} catch (RemoteException e) {
							Log.e(TAG, "asyncUpdate RemoteException", e);
						}
					}
				}.start();
				
				return true;
			}
		};
	}

}
