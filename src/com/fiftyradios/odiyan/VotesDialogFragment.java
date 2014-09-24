package com.fiftyradios.odiyan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

public class VotesDialogFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		final Bundle args = getArguments();
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    View v = inflater.inflate(R.layout.votes_dialog, null);
	    ListView lv = (ListView)v.findViewById(R.id.votes_list);
	    final View pb = v.findViewById(R.id.votes_pb);
	    
	    VotesQueryAdapter adapter = new VotesQueryAdapter(getActivity(), args, new FeedQueryAdapter.OnLoadListener() {
			
			@Override
			public void onLoading() {
				pb.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onLoaded() {
				pb.setVisibility(View.GONE);
			}
		});
	    
	    lv.setAdapter(adapter);
	    
	    builder.setView(v);      
	    
	    return builder.create();
	}
}
