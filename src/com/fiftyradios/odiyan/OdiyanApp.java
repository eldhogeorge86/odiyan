package com.fiftyradios.odiyan;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;
import com.parse.PushService;

import android.app.Application;

public class OdiyanApp extends Application {

	@Override
	public void onCreate (){
		super.onCreate();
		
		Parse.enableLocalDatastore(this);
		
		Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));
		
		ParseFacebookUtils.initialize(getString(R.string.facebook_app_id));

	    ParseTwitterUtils.initialize(getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_secret));
		
		// Specify an Activity to handle all pushes by default.
		PushService.setDefaultPushCallback(this, MainActivity.class);
	}
}
