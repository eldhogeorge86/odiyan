package com.fiftyradios.odiyan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MoreFragment extends Fragment {

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
        View rootView = inflater.inflate(R.layout.more_fragment, container, false);        
        
        Button settings_btn = (Button)rootView.findViewById(R.id.settings_btn);
        settings_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				MoreActionListener listener = (MoreActionListener)getActivity();
				listener.onSettings();
			}
		});
        
        Button logoutBtn = (Button)rootView.findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				MoreActionListener listener = (MoreActionListener)getActivity();
				listener.onLogout();
			}
		});
        
        return rootView;
    }
}
