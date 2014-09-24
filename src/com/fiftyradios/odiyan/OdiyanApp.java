package com.fiftyradios.odiyan;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
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
		
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
					.cacheOnDisk(true).cacheInMemory(true)
					.imageScaleType(ImageScaleType.EXACTLY)
					.displayer(new FadeInBitmapDisplayer(300)).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.defaultDisplayImageOptions(defaultOptions)
				.memoryCache(new WeakMemoryCache())
				.diskCacheSize(100 * 1024 * 1024).build();

		ImageLoader.getInstance().init(config);
	}
}
