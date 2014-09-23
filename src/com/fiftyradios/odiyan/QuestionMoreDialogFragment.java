package com.fiftyradios.odiyan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class QuestionMoreDialogFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Bundle args = getArguments();
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    View v = inflater.inflate(R.layout.q_more_dialog, null);
	    Button votes = (Button)v.findViewById(R.id.view_votes_btn);
	    if(args.getBoolean("votedByMe")){
	    	votes.setVisibility(View.VISIBLE);
		    votes.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					QuestionMoreDialogFragment.this.dismiss();
					DialogFragment dialog = new VotesDialogFragment();
					dialog.setArguments(args);
			        dialog.show(((FragmentActivity)getActivity()).getSupportFragmentManager(), "VotesDialogFragment");
				}
			});
	    } else{
	    	votes.setVisibility(View.GONE);
	    }
	    
	    builder.setView(v);      
	    
	    return builder.create();
	}

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);
		
		
	}
	
	
}
