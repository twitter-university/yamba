package com.marakana.android.yamba;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AboutActivity extends Activity {
	private static final String TAG = AboutActivity.class.getSimpleName();
	
	WebView webviewAbout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		webviewAbout = (WebView) findViewById(R.id.webview_about);
		
		// Workaround to have your webview client open links instead of Browser
		webviewAbout.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		
		// Load local asset
		webviewAbout.loadUrl("file:///android_asset/about.html");

		Log.d(TAG, "onCreated");
	}

}
