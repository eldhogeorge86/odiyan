package com.fiftyradios.odiyan;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FeedQueryAdapter extends BaseAdapter {

	private FeedData mData;
	private Activity mActivity;
	private LayoutInflater mInflater;
	private OnLoadListener mLoadListner;
	private OnMoreLoadListener mMoreLoadListner;
	private boolean mIsRefreshing;
	
	public FeedQueryAdapter(Activity act, OnLoadListener listner){
		
		mLoadListner = listner;
		mActivity = act;
		mData = new FeedData();
		mData.skip = 0;
		mData.count = 20;
		mData.questions = new ArrayList<FeedQueryAdapter.QuestionData>();
		
		mInflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		refreshData();
	}
	
	public FeedQueryAdapter(Activity act, FeedQueryAdapter.FeedData data, OnLoadListener listner){
		
		mLoadListner = listner;
		mActivity = act;
		mData = data;
		mInflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		
		return mData.questions.size();
	}

	@Override
	public Object getItem(int pos) {
		
		return mData.questions.get(pos);
	}

	@Override
	public long getItemId(int pos) {

		return pos;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View vi = convertView;
        ViewHolder holder;
        
		if (vi == null){
			vi = mInflater.inflate(R.layout.question_item, null);
			
			holder = new ViewHolder();
			holder.user = (TextView)vi.findViewById(R.id.user_text);
			holder.question = (TextView)vi.findViewById(R.id.question_data);
			
			holder.answer1 = (Button)vi.findViewById(R.id.ans_btn1);
			holder.answer_view1 = vi.findViewById(R.id.ans_border1);
			
			holder.answer2 = (Button)vi.findViewById(R.id.ans_btn2);
			holder.answer_view2 = vi.findViewById(R.id.ans_border2);
			
			holder.answer3 = (Button)vi.findViewById(R.id.ans_btn3);
			holder.answer_view3 = vi.findViewById(R.id.ans_border3);
			
			holder.answer4 = (Button)vi.findViewById(R.id.ans_btn4);
			holder.answer_view4 = vi.findViewById(R.id.ans_border4);
			
			holder.answer5 = (Button)vi.findViewById(R.id.ans_btn5);
			holder.answer_view5 = vi.findViewById(R.id.ans_border5);
			
			vi.setTag(holder);
		}
		else{
			holder=(ViewHolder)vi.getTag();
		}
		
		QuestionData questionObj = mData.questions.get(position);
		
		holder.user.setText(questionObj.userName);
		holder.question.setText(questionObj.data);
		
		if(questionObj.answer1 != null){
			holder.answer1.setText(questionObj.answer1.data);
			
			holder.answer1.setVisibility(View.VISIBLE);
			holder.answer_view1.setVisibility(View.VISIBLE);
		}else{
			holder.answer1.setVisibility(View.GONE);
			holder.answer_view1.setVisibility(View.GONE);
		}
		
		if(questionObj.answer2 != null){
			holder.answer2.setText(questionObj.answer2.data);
			
			holder.answer2.setVisibility(View.VISIBLE);
			holder.answer_view2.setVisibility(View.VISIBLE);
		}else{
			holder.answer2.setVisibility(View.GONE);
			holder.answer_view2.setVisibility(View.GONE);
		}
		
		if(questionObj.answer3 != null){
			holder.answer3.setText(questionObj.answer3.data);
			
			holder.answer3.setVisibility(View.VISIBLE);
			holder.answer_view3.setVisibility(View.VISIBLE);
		}else{
			holder.answer3.setVisibility(View.GONE);
			holder.answer_view3.setVisibility(View.GONE);
		}
		
		if(questionObj.answer4 != null){
			holder.answer4.setText(questionObj.answer4.data);
			
			holder.answer4.setVisibility(View.VISIBLE);
			holder.answer_view4.setVisibility(View.VISIBLE);
		}else{
			holder.answer4.setVisibility(View.GONE);
			holder.answer_view4.setVisibility(View.GONE);
		}
		
		if(questionObj.answer5 != null){
			holder.answer5.setText(questionObj.answer5.data);
			
			holder.answer5.setVisibility(View.VISIBLE);
			holder.answer_view5.setVisibility(View.VISIBLE);
		}else{
			holder.answer5.setVisibility(View.GONE);
			holder.answer_view5.setVisibility(View.GONE);
		}
		
		return vi;
	}

	public FeedQueryAdapter.FeedData getData(){
		return mData;
	}
	
	public void refreshData(){
		if(mIsRefreshing){
			return;
		}
		
		if(mLoadListner != null){
			mLoadListner.onLoading();
		}
		
		mIsRefreshing = true;
		
		ParseUser user = ParseUser.getCurrentUser();
		HashMap<String, Object> args = new HashMap<String, Object>();
		args.put("limit", 20);
		args.put("skip", 0);
		args.put("userId", user.getObjectId());
		ParseCloud.callFunctionInBackground("queryFeed", args, new FunctionCallback<ArrayList<Object>>() {
		  public void done(ArrayList<Object> result, ParseException e) {
		    if (e == null) {
		    	mData.questions.clear();
		    	mData.skip = 0;
		    	mData.count = 20;
		    	fillData(result);
		    }else{
		    	Toast toast = Toast.makeText(mActivity, "Network Error", Toast.LENGTH_SHORT);
            	toast.show();
		    }
		    
		    if(mLoadListner != null){
				mLoadListner.onLoaded();
			}
		    
		    mIsRefreshing = false;
		  }
		});
	}
	
	public void loadMore(){
		int skip = mData.skip + mData.count;
		queryData(skip, mData.count);
	}
	
	public void setMoreListner(OnMoreLoadListener listner){
		mMoreLoadListner = listner;
	}
	
	private void queryData(int skip, int limit){
		
		if(mIsRefreshing){
			return;
		}
		
		if(mMoreLoadListner != null){
			mMoreLoadListner.onMoreLoading();
		}		
		
		ParseUser user = ParseUser.getCurrentUser();
		HashMap<String, Object> args = new HashMap<String, Object>();
		args.put("limit", limit);
		args.put("skip", skip);
		args.put("userId", user.getObjectId());
		ParseCloud.callFunctionInBackground("queryFeed", args, new FunctionCallback<ArrayList<Object>>() {
		  public void done(ArrayList<Object> result, ParseException e) {
		    if (e == null) {
		    	mData.skip = mData.skip + result.size();
		    	fillData(result);
		    }else{
		    	Toast toast = Toast.makeText(mActivity, "Network Error", Toast.LENGTH_SHORT);
            	toast.show();
            	if(mMoreLoadListner != null){
            		mMoreLoadListner.onMoreLoadFailed();
            	}
		    }
		    
		    if(mMoreLoadListner != null){
		    	mMoreLoadListner.onMoreLoaded();
			}
		  }
		});
	}
	
	@SuppressWarnings("unchecked")
	private void fillData(ArrayList<Object> qList){
		
		for(Object qObj : qList){
			QuestionData qData = new QuestionData();
			
			HashMap<String, Object> qMap = (HashMap<String, Object>)qObj;
			qData.id = (String)qMap.get("i");
			qData.createdAt = (Date)qMap.get("c");
			qData.updatedAt = (Date)qMap.get("u");
			qData.data = (String)qMap.get("q");
			
			qData.userId = (String)qMap.get("ui");
			qData.userName = (String)qMap.get("un");
			
			qData.votedByMe = (Boolean)qMap.get("v");
			qData.myAnswerId = (String)qMap.get("ma");
			
			if(qMap.containsKey("a1")){
				HashMap<String, Object> ans = (HashMap<String, Object>)qMap.get("a1");
				qData.answer1 = new QuestionData.AnswerData();
				qData.answer1.id = (String)ans.get("i");
				qData.answer1.data = (String)ans.get("t");
				qData.answer1.voteCount = (Integer)ans.get("c");
			}
			if(qMap.containsKey("a2")){
				HashMap<String, Object> ans = (HashMap<String, Object>)qMap.get("a2");
				qData.answer2 = new QuestionData.AnswerData();
				qData.answer2.id = (String)ans.get("i");
				qData.answer2.data = (String)ans.get("t");
				qData.answer2.voteCount = (Integer)ans.get("c");
			}
			if(qMap.containsKey("a3")){
				HashMap<String, Object> ans = (HashMap<String, Object>)qMap.get("a3");
				qData.answer3 = new QuestionData.AnswerData();
				qData.answer3.id = (String)ans.get("i");
				qData.answer3.data = (String)ans.get("t");
				qData.answer3.voteCount = (Integer)ans.get("c");
			}
			if(qMap.containsKey("a4")){
				HashMap<String, Object> ans = (HashMap<String, Object>)qMap.get("a4");
				qData.answer4 = new QuestionData.AnswerData();
				qData.answer4.id = (String)ans.get("i");
				qData.answer4.data = (String)ans.get("t");
				qData.answer4.voteCount = (Integer)ans.get("c");
			}
			if(qMap.containsKey("a5")){
				HashMap<String, Object> ans = (HashMap<String, Object>)qMap.get("a5");
				qData.answer5 = new QuestionData.AnswerData();
				qData.answer5.id = (String)ans.get("i");
				qData.answer5.data = (String)ans.get("t");
				qData.answer5.voteCount = (Integer)ans.get("c");
			}
			
			mData.questions.add(qData);
		}
		
		notifyDataSetChanged();
	}
	
	public static class FeedData{
		public ArrayList<QuestionData> questions;
		public int skip;
		public int count;
	}
	
	public static class QuestionData{
		public String id;
		public Date createdAt;
		public Date updatedAt;
		public String data;
		public String userId;
		public String userName;
		public boolean votedByMe;
		public String myAnswerId;
		
		public AnswerData answer1;
		public AnswerData answer2;
		public AnswerData answer3;
		public AnswerData answer4;
		public AnswerData answer5;
		
		public static class AnswerData{
			public String id;
			public String data;
			public int voteCount;
		}
	}
	
	public static class ViewHolder{
        
		public TextView user;
        public TextView votes;
        public TextView question;
        public Button answer1;
        public View answer_view1;
        public Button answer2;
        public View answer_view2;
        public Button answer3;
        public View answer_view3;
        public Button answer4;
        public View answer_view4;
        public Button answer5;
        public View answer_view5;
    }	
	
	public abstract static interface OnLoadListener{
		
		public abstract void onLoading();
		public abstract void onLoaded();
	}
	
	public abstract static interface OnMoreLoadListener{
		
		public abstract void onMoreLoading();
		public abstract void onMoreLoaded();
		public abstract void onMoreLoadFailed();
	}
}
