package com.fiftyradios.odiyan;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
						
						ParseUser user = vote.getParseUser("user");
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
