package com.fiftyradios.odiyan;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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
			
			holder = this.fillHolder(vi);
			
			vi.setTag(holder);
		}
		else{
			holder=(ViewHolder)vi.getTag();
		}
		
		final QuestionData questionObj = mData.questions.get(position);
		final ParseUser user = ParseUser.getCurrentUser();
		
		holder.user.setText(questionObj.userName);
		holder.question.setText(questionObj.data);
		
		this.prepareAnswerView(questionObj, questionObj.answer1, holder.answer1, user, holder, 1);
		this.prepareAnswerView(questionObj, questionObj.answer2, holder.answer2, user, holder, 2);
		this.prepareAnswerView(questionObj, questionObj.answer3, holder.answer3, user, holder, 3);
		this.prepareAnswerView(questionObj, questionObj.answer4, holder.answer4, user, holder, 4);
		this.prepareAnswerView(questionObj, questionObj.answer5, holder.answer5, user, holder, 5);
		
		return vi;
	}
	
	private void prepareAnswerView(final QuestionData qData, final QuestionData.AnswerData ansObj, ViewHolder.AnswerView ansView, final ParseUser user, final ViewHolder holder, final int ans){
		if(ansObj != null){
			if(qData.votedByMe){
				ansView.ans_btn.setVisibility(View.GONE);
				ansView.ans_vote_layout.setVisibility(View.VISIBLE);
				
				ansView.ans_data.setText(ansObj.data);
				float totalVotes = this.getTotalVoteCount(qData);
				float ansVote = ansObj.voteCount;
				long votePer = Math.round(((ansVote * 100.0) / totalVotes));
				float votePerDouble = (float) (((float)votePer)/ 100.0);
				float votePerDoubleRem = (float) (((float)(100 - votePer))/ 100.0);
				
				ViewGroup.LayoutParams oldParam1 = ansView.ans_vote_per1.getLayoutParams();
				LinearLayout.LayoutParams newParam1 = new LinearLayout.LayoutParams(
						oldParam1.width, oldParam1.height, votePerDouble);
				ansView.ans_vote_per1.setLayoutParams(newParam1);
				
				ViewGroup.LayoutParams oldParam2 = ansView.ans_vote_per2.getLayoutParams();
				LinearLayout.LayoutParams newParam2 = new LinearLayout.LayoutParams(
						oldParam2.width, oldParam2.height, votePerDoubleRem);
				ansView.ans_vote_per2.setLayoutParams(newParam2);
				
				ansView.ans_vote_per.setText(votePer + "%");
				
			}else{
				ansView.ans_btn.setVisibility(View.VISIBLE);
				ansView.ans_vote_layout.setVisibility(View.GONE);
				ansView.ans_btn.setText(ansObj.data);
				ansView.ans_btn.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View clickedView) {
						ParseObject vote = new ParseObject("Vote");
						vote.put("user", user);
						vote.put("isAnonymous", false);
						vote.put("ans", 1);
						
						ParseObject qObj = ParseObject.createWithoutData("Question", qData.id);
						qObj.increment("count");
						qObj.increment("ans1_count");
						vote.put("question", qObj);	
						vote.saveEventually();
						
						ansObj.voteCount++;
						qData.votedByMe = true;
						qData.myAnswerId = ans;
						
						prepareAfterVote(qData, holder);
					}
				});
			}			
		}else{
			ansView.ans_layout.setVisibility(View.GONE);
		}
	}

	private void prepareAfterVote(QuestionData qData, ViewHolder holder){
		ParseUser user = ParseUser.getCurrentUser();
		
		this.prepareAnswerView(qData, qData.answer1, holder.answer1, user, holder, 1);
		this.prepareAnswerView(qData, qData.answer2, holder.answer2, user, holder, 2);
		this.prepareAnswerView(qData, qData.answer3, holder.answer3, user, holder, 3);
		this.prepareAnswerView(qData, qData.answer4, holder.answer4, user, holder, 4);
		this.prepareAnswerView(qData, qData.answer5, holder.answer5, user, holder, 5);
	}
	
	private int getTotalVoteCount(QuestionData qData){
		int count = 0;
		
		if(qData.answer1 != null){
			count += qData.answer1.voteCount;
		}
		if(qData.answer2 != null){
			count += qData.answer2.voteCount;
		}
		if(qData.answer3 != null){
			count += qData.answer3.voteCount;
		}
		if(qData.answer4 != null){
			count += qData.answer4.voteCount;
		}
		if(qData.answer5 != null){
			count += qData.answer5.voteCount;
		}
		
		return count;
	}
	
	private ViewHolder fillHolder(View parent){
		ViewHolder holder = new ViewHolder();
		
		holder.user = (TextView)parent.findViewById(R.id.user_text);
		holder.question = (TextView)parent.findViewById(R.id.question_data);
		
		holder.answer1 = new ViewHolder.AnswerView();		
		holder.answer1.ans_layout = parent.findViewById(R.id.ans1_btn);
		holder.answer1.ans_btn = (Button)parent.findViewById(R.id.ans_btn1);
		holder.answer1.ans_border = parent.findViewById(R.id.ans_border1);
		holder.answer1.ans_vote_layout = parent.findViewById(R.id.ans1_afterVote);
		holder.answer1.ans_vote_per1 = parent.findViewById(R.id.ans1_per1);
		holder.answer1.ans_vote_per2 = parent.findViewById(R.id.ans1_per2);
		holder.answer1.ans_data = (TextView)parent.findViewById(R.id.ans1_data);
		holder.answer1.ans_vote_per = (TextView)parent.findViewById(R.id.ans1_per);
		
		holder.answer2 = new ViewHolder.AnswerView();		
		holder.answer2.ans_layout = parent.findViewById(R.id.ans2_btn);
		holder.answer2.ans_btn = (Button)parent.findViewById(R.id.ans_btn2);
		holder.answer2.ans_border = parent.findViewById(R.id.ans_border2);
		holder.answer2.ans_vote_layout = parent.findViewById(R.id.ans2_afterVote);
		holder.answer2.ans_vote_per1 = parent.findViewById(R.id.ans2_per1);
		holder.answer2.ans_vote_per2 = parent.findViewById(R.id.ans2_per2);
		holder.answer2.ans_data = (TextView)parent.findViewById(R.id.ans2_data);
		holder.answer2.ans_vote_per = (TextView)parent.findViewById(R.id.ans2_per);
		
		holder.answer3 = new ViewHolder.AnswerView();		
		holder.answer3.ans_layout = parent.findViewById(R.id.ans3_btn);
		holder.answer3.ans_btn = (Button)parent.findViewById(R.id.ans_btn3);
		holder.answer3.ans_border = parent.findViewById(R.id.ans_border3);
		holder.answer3.ans_vote_layout = parent.findViewById(R.id.ans3_afterVote);
		holder.answer3.ans_vote_per1 = parent.findViewById(R.id.ans3_per1);
		holder.answer3.ans_vote_per2 = parent.findViewById(R.id.ans3_per2);
		holder.answer3.ans_data = (TextView)parent.findViewById(R.id.ans3_data);
		holder.answer3.ans_vote_per = (TextView)parent.findViewById(R.id.ans3_per);
		
		holder.answer4 = new ViewHolder.AnswerView();		
		holder.answer4.ans_layout = parent.findViewById(R.id.ans4_btn);
		holder.answer4.ans_btn = (Button)parent.findViewById(R.id.ans_btn4);
		holder.answer4.ans_border = parent.findViewById(R.id.ans_border4);
		holder.answer4.ans_vote_layout = parent.findViewById(R.id.ans4_afterVote);
		holder.answer4.ans_vote_per1 = parent.findViewById(R.id.ans4_per1);
		holder.answer4.ans_vote_per2 = parent.findViewById(R.id.ans4_per2);
		holder.answer4.ans_data = (TextView)parent.findViewById(R.id.ans4_data);
		holder.answer4.ans_vote_per = (TextView)parent.findViewById(R.id.ans4_per);
		
		holder.answer5 = new ViewHolder.AnswerView();		
		holder.answer5.ans_layout = parent.findViewById(R.id.ans5_btn);
		holder.answer5.ans_btn = (Button)parent.findViewById(R.id.ans_btn5);
		holder.answer5.ans_border = parent.findViewById(R.id.ans_border5);
		holder.answer5.ans_vote_layout = parent.findViewById(R.id.ans5_afterVote);
		holder.answer5.ans_vote_per1 = parent.findViewById(R.id.ans5_per1);
		holder.answer5.ans_vote_per2 = parent.findViewById(R.id.ans5_per2);
		holder.answer5.ans_data = (TextView)parent.findViewById(R.id.ans5_data);
		holder.answer5.ans_vote_per = (TextView)parent.findViewById(R.id.ans5_per);
		
		return holder;
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
			qData.myAnswerId = (Integer)qMap.get("ma");
			
			if(qMap.containsKey("a1")){
				HashMap<String, Object> ans = (HashMap<String, Object>)qMap.get("a1");
				qData.answer1 = new QuestionData.AnswerData();
				qData.answer1.data = (String)ans.get("t");
				qData.answer1.voteCount = (Integer)ans.get("c");
			}
			if(qMap.containsKey("a2")){
				HashMap<String, Object> ans = (HashMap<String, Object>)qMap.get("a2");
				qData.answer2 = new QuestionData.AnswerData();
				qData.answer2.data = (String)ans.get("t");
				qData.answer2.voteCount = (Integer)ans.get("c");
			}
			if(qMap.containsKey("a3")){
				HashMap<String, Object> ans = (HashMap<String, Object>)qMap.get("a3");
				qData.answer3 = new QuestionData.AnswerData();
				qData.answer3.data = (String)ans.get("t");
				qData.answer3.voteCount = (Integer)ans.get("c");
			}
			if(qMap.containsKey("a4")){
				HashMap<String, Object> ans = (HashMap<String, Object>)qMap.get("a4");
				qData.answer4 = new QuestionData.AnswerData();
				qData.answer4.data = (String)ans.get("t");
				qData.answer4.voteCount = (Integer)ans.get("c");
			}
			if(qMap.containsKey("a5")){
				HashMap<String, Object> ans = (HashMap<String, Object>)qMap.get("a5");
				qData.answer5 = new QuestionData.AnswerData();
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
		public int myAnswerId;
		
		public AnswerData answer1;
		public AnswerData answer2;
		public AnswerData answer3;
		public AnswerData answer4;
		public AnswerData answer5;
		
		public static class AnswerData{
			public String data;
			public int voteCount;
		}
	}
	
	public static class ViewHolder{
        
		public TextView user;
        public TextView votes;
        public TextView question;
        
        public AnswerView answer1;
        public AnswerView answer2;
        public AnswerView answer3;
        public AnswerView answer4;
        public AnswerView answer5;
        
        public static class AnswerView{
        	public View ans_layout;
        	public Button ans_btn;
            public View ans_border;
            
            public View ans_vote_layout;
            public View ans_vote_per1;
            public View ans_vote_per2;
            public TextView ans_vote_per;
            public TextView ans_data;
        }
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
