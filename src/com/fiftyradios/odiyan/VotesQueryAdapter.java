package com.fiftyradios.odiyan;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class VotesQueryAdapter extends BaseAdapter {

	private Activity mActivity;
	private LayoutInflater mInflater;
	private FeedQueryAdapter.OnLoadListener mListener;
	private Bundle mArgs;
	private ArrayList<ParseObject> mData;
	
	public VotesQueryAdapter(Activity act, Bundle args, FeedQueryAdapter.OnLoadListener listener){
		mActivity = act;
		mArgs = args;
		mData = new ArrayList<ParseObject>();
		mInflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mListener = listener;
		
		queryData();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void queryData(){
		
		if(mListener != null){
			mListener.onLoading();
		}
		
		ParseQuery query = new ParseQuery("Vote");
	    query.whereEqualTo("question", ParseObject.createWithoutData("Question", mArgs.getString("qid")));
	    if(mArgs.containsKey("ans")){
	    	int ans = mArgs.getInt("ans");
	    	query.whereEqualTo("ans", ans);
	    }
	    query.orderByDescending("createdAt");
	    query.include("user");
	    
	    query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> results, ParseException exp) {
				if(mListener != null){
					mListener.onLoaded();
				}
				
				mData.clear();
				
				if(exp != null){
					Toast toast = Toast.makeText(mActivity, "Network Error", Toast.LENGTH_SHORT);
	            	toast.show();
				}else{
					if(results != null){
						for(ParseObject vObj : results){
							mData.add(vObj);
						}
					}
					notifyDataSetChanged();
				}
			}
		});
	}
	
	@Override
	public int getCount() {
		
		return mData.size();
	}

	@Override
	public Object getItem(int pos) {
		
		return mData.get(pos);
	}

	@Override
	public long getItemId(int pos) {

		return pos;
	}

	@Override
	public View getView(int pos, View v, ViewGroup vg) {

		if (v == null) {
		    v = mInflater.inflate(R.layout.vote_item, null);
		}
		
		ParseObject vote = mData.get(pos);
		
		ImageView img = (ImageView)v.findViewById(R.id.vote_pic_img);
		ImageView img2 = (ImageView)v.findViewById(R.id.vote_pic_img2);
		Drawable fallback = mActivity.getResources().getDrawable(R.drawable.unknown);
		
		ParseUser user = vote.getParseUser("user");
		ParseFile file = user.getParseFile("img");
		
		if(file != null){
			img2.setVisibility(View.GONE);
			img.setVisibility(View.VISIBLE);
			
			ImageLoader imageLoader = ImageLoader.getInstance();
			DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
							.cacheOnDisk(true).resetViewBeforeLoading(true)
							.showImageForEmptyUri(fallback)
							.showImageOnFail(fallback)
							.showImageOnLoading(fallback).build();
			
			imageLoader.displayImage(file.getUrl(), img, options);
		}else{
			img.setVisibility(View.GONE);
			img2.setVisibility(View.VISIBLE);
			img2.setImageDrawable(fallback);
		}
		
		TextView userTxt = (TextView)v.findViewById(R.id.voted_user);
		userTxt.setText(user.getString("name"));
		TextView ansTxt = (TextView)v.findViewById(R.id.voted_ans);
		ansTxt.setText(mArgs.getString("ans" + vote.getInt("ans")));						

		return v;
	}

}
