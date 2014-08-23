package com.fiftyradios.odiyan;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;

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

	private ArrayList<ParseObject> mQuestionList;
	private Activity mActivity;
	private LayoutInflater mInflater;
	private OnLoadListener mLoadListner;
	
	public FeedQueryAdapter(Activity act, OnLoadListener listner){
		
		mLoadListner = listner;
		mActivity = act;
		mQuestionList = new ArrayList<ParseObject>();
		
		mInflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		queryData();
	}
	
	public FeedQueryAdapter(Activity act, ArrayList<ParseObject> qList, OnLoadListener listner){
		
		mLoadListner = listner;
		mActivity = act;
		mQuestionList = qList;
		mInflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		
		return mQuestionList.size();
	}

	@Override
	public Object getItem(int pos) {
		
		return mQuestionList.get(pos);
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
		
		ParseObject questionObj = mQuestionList.get(position);
		ParseObject user = questionObj.getParseObject("user");
		String name = user.getString("name");
		String data = questionObj.getString("data");
		
		holder.user.setText(name);
		holder.question.setText(data);
		
		if(questionObj.has("answer1")){
			ParseObject answer = questionObj.getParseObject("answer1");
			holder.answer1.setText(answer.getString("text"));
			
			holder.answer1.setVisibility(View.VISIBLE);
			holder.answer_view1.setVisibility(View.VISIBLE);
		}else{
			holder.answer1.setVisibility(View.GONE);
			holder.answer_view1.setVisibility(View.GONE);
		}
		
		if(questionObj.has("answer2")){
			ParseObject answer = questionObj.getParseObject("answer2");
			holder.answer2.setText(answer.getString("text"));
			
			holder.answer2.setVisibility(View.VISIBLE);
			holder.answer_view2.setVisibility(View.VISIBLE);
		}else{
			holder.answer2.setVisibility(View.GONE);
			holder.answer_view2.setVisibility(View.GONE);
		}

		if(questionObj.has("answer3")){
			ParseObject answer = questionObj.getParseObject("answer3");
			holder.answer3.setText(answer.getString("text"));
			
			holder.answer3.setVisibility(View.VISIBLE);
			holder.answer_view3.setVisibility(View.VISIBLE);
		}else{
			holder.answer3.setVisibility(View.GONE);
			holder.answer_view3.setVisibility(View.GONE);
		}
		
		if(questionObj.has("answer4")){
			ParseObject answer = questionObj.getParseObject("answer4");
			holder.answer4.setText(answer.getString("text"));
			
			holder.answer4.setVisibility(View.VISIBLE);
			holder.answer_view4.setVisibility(View.VISIBLE);
		}else{
			holder.answer4.setVisibility(View.GONE);
			holder.answer_view4.setVisibility(View.GONE);
		}
		
		if(questionObj.has("answer5")){
			ParseObject answer = questionObj.getParseObject("answer5");
			holder.answer5.setText(answer.getString("text"));
			
			holder.answer5.setVisibility(View.VISIBLE);
			holder.answer_view5.setVisibility(View.VISIBLE);
		}else{
			holder.answer5.setVisibility(View.GONE);
			holder.answer_view5.setVisibility(View.GONE);
		}		
		
		return vi;
	}

	public ArrayList<ParseObject> getData(){
		return mQuestionList;
	}
	
	private void queryData(){
		
		if(mLoadListner != null){
			mLoadListner.onLoading();
		}
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Question");
		query.include("user");
		query.include("answer1");
		query.include("answer2");
		query.include("answer3");
		query.include("answer4");
		query.include("answer5");
		query.orderByDescending("updatedAt");
		query.findInBackground(new FindCallback<ParseObject>() {
		    public void done(List<ParseObject> qList, ParseException e) {
		        if (e == null) {
		        	fillData(qList);
		        } else {
		        	Toast toast = Toast.makeText(mActivity, "Network Error", Toast.LENGTH_SHORT);
                	toast.show();
		        }
		        
		        if(mLoadListner != null){
					mLoadListner.onLoaded();
				}
		    }
		});
	}
	
	private void fillData(List<ParseObject> qList){
		
		mQuestionList.clear();
		
		for(ParseObject qObj : qList){
			mQuestionList.add(qObj);
		}
		notifyDataSetChanged();
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
}
