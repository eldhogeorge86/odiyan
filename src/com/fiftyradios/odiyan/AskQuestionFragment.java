package com.fiftyradios.odiyan;

import java.util.ArrayList;

import com.fiftyradios.odiyan.FeedFragment.QuestionStore;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AskQuestionFragment extends Fragment {

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
        View rootView = inflater.inflate(R.layout.ask_q_fragment, container, false);
        
        final FragmentActivity act = getActivity();
        final MoreActionListener listener = (MoreActionListener)act;
        
        final EditText qText = (EditText)rootView.findViewById(R.id.q_txt);
        
        final EditText a1Text = (EditText)rootView.findViewById(R.id.ans1_txt);        
        final EditText a2Text = (EditText)rootView.findViewById(R.id.ans2_txt);
        final EditText a3Text = (EditText)rootView.findViewById(R.id.ans3_txt);
        final EditText a4Text = (EditText)rootView.findViewById(R.id.ans4_txt);
        final EditText a5Text = (EditText)rootView.findViewById(R.id.ans5_txt);
        
        final Button askbtn = (Button)rootView.findViewById(R.id.ask_q_btn);
        askbtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String question = qText.getText().toString().trim();
				if(question.length() == 0){
					Toast toast = Toast.makeText(act, "Empty Question", Toast.LENGTH_SHORT);
	            	toast.show();
	            	return;
				}
				
				String ans1 = a1Text.getText().toString().trim();
				String ans2 = a2Text.getText().toString().trim();
				String ans3 = a3Text.getText().toString().trim();
				String ans4 = a4Text.getText().toString().trim();
				String ans5 = a5Text.getText().toString().trim();
				
				final ArrayList<String> ansList = new ArrayList<String>();
				if(ans1.length() > 0){
					ansList.add(ans1);
				}
				if(ans2.length() > 0){
					ansList.add(ans2);
				}
				if(ans3.length() > 0){
					ansList.add(ans3);
				}
				if(ans4.length() > 0){
					ansList.add(ans4);
				}
				if(ans5.length() > 0){
					ansList.add(ans5);
				}
				if(ansList.size() < 2){
					Toast toast = Toast.makeText(act, "Atleast two answers required", Toast.LENGTH_SHORT);
	            	toast.show();
	            	return;
				}
				
				ParseUser curUser = ParseUser.getCurrentUser();
				if(curUser == null){
					Toast toast = Toast.makeText(act, "Not logged in", Toast.LENGTH_SHORT);
	            	toast.show();
	            	return;
				}
				
				final ParseObject qObj = new ParseObject("Question");
				qObj.put("user", curUser);
				qObj.put("data", question);
				
				for(int i=1;i<=ansList.size();i++){
					qObj.put(("ans" + i + "_text"), ansList.get(i-1));
					qObj.put(("ans" + i + "_count"), 0);
				}
				
				listener.showLoading("Saving...", true);
				
				qObj.saveInBackground(new SaveCallback() {
					
					@Override
					public void done(ParseException exp) {

						listener.showLoading("Saving...", false);
						
						if(exp != null){
							Toast toast = Toast.makeText(act, "Save failed", Toast.LENGTH_SHORT);
			            	toast.show();
			            	return;
						}
						
						addNewQuestion(qObj, ansList, act.getSupportFragmentManager());
						
						act.getSupportFragmentManager().popBackStack();
					}
				});
				
			}
		});
        
        return rootView;
    }
	
	private void addNewQuestion(ParseObject qObj, ArrayList<String> ansList, FragmentManager fm){
		
		FeedQueryAdapter.QuestionData qData = new FeedQueryAdapter.QuestionData();
		qData.id = qObj.getObjectId();
		
		ParseUser curUser = ParseUser.getCurrentUser();
		ParseFile file = curUser.getParseFile("img");
		if(file != null){
			qData.profile = file.getUrl();
		}
		
		qData.createdAt = qObj.getCreatedAt();
		qData.updatedAt = qObj.getUpdatedAt();
		qData.votedByMe = false;
		qData.myAnswerId = 0;
		qData.data = qObj.getString("data");
		
		qData.userId = curUser.getObjectId();
		qData.userName = curUser.getString("name");
		
		qData.answer1 = getAnswerData(ansList.get(0));
		qData.answer2 = getAnswerData(ansList.get(1));
		if(ansList.size() > 2){
			qData.answer3 = getAnswerData(ansList.get(2));
		}
		if(ansList.size() > 3){
			qData.answer4 = getAnswerData(ansList.get(3));
		}
		if(ansList.size() > 4){
			qData.answer5 = getAnswerData(ansList.get(4));
		}
		
		QuestionStore store = (QuestionStore)fm.findFragmentByTag(FeedFragment.STORE_NAME);
		if(store != null){
			store.getData().questions.add(0, qData);
		}
	}
	
	private FeedQueryAdapter.QuestionData.AnswerData getAnswerData(String ans){
		FeedQueryAdapter.QuestionData.AnswerData answer1 = new FeedQueryAdapter.QuestionData.AnswerData();
		answer1.data = ans;
		answer1.voteCount = 0;
		
		return answer1;
	}
}
