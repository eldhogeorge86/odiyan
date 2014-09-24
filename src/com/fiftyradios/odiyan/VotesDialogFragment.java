package com.fiftyradios.odiyan;

import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class VotesDialogFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		final Bundle args = getArguments();
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    View v = inflater.inflate(R.layout.votes_dialog, null);
	    ListView lv = (ListView)v.findViewById(R.id.votes_list);
	    
	    final FragmentActivity act = getActivity();
	    
	    ParseQueryAdapter<ParseObject> adapter =
	    		  new ParseQueryAdapter<ParseObject>(getActivity(), new ParseQueryAdapter.QueryFactory<ParseObject>() {
	    		    public ParseQuery<ParseObject> create() {
	    		      ParseQuery query = new ParseQuery("Vote");
	    		      query.whereEqualTo("question", ParseObject.createWithoutData("Question", args.getString("qid")));
	    		      query.orderByDescending("createdAt");
	    		      query.include("user");
	    		      return query;
	    		    }
	    		  }){

					@Override
					public View getItemView(ParseObject vote, View v,
							ViewGroup arg2) {
						if (v == null) {
						    v = View.inflate(getContext(), R.layout.vote_item, null);
						  }
						
						ParseImageView img = (ParseImageView)v.findViewById(R.id.vote_pic_img);
						ImageView img2 = (ImageView)v.findViewById(R.id.vote_pic_img2);
						
						ParseUser user = vote.getParseUser("user");
						String name = user.getString("name");
						ParseFile file = user.getParseFile("img");
						if(file != null){
							img2.setVisibility(View.GONE);
							img.setVisibility(View.VISIBLE);
							img.setPlaceholder(act.getResources().getDrawable(R.drawable.unknown));
							img.setParseFile(file);
							img.loadInBackground();
						}else{
							img.setVisibility(View.GONE);
							img2.setVisibility(View.VISIBLE);
						}
						
						TextView userTxt = (TextView)v.findViewById(R.id.voted_user);
						userTxt.setText(user.getString("name"));
						TextView ansTxt = (TextView)v.findViewById(R.id.voted_ans);
						ansTxt.setText(args.getString("ans" + vote.getInt("ans")));						
						
						  return v;
					}
	    	
	    };
	    
	    lv.setAdapter(adapter);
	    
	    builder.setView(v);      
	    
	    return builder.create();
	}
}
