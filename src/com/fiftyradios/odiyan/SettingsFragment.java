package com.fiftyradios.odiyan;

import java.io.ByteArrayOutputStream;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class SettingsFragment  extends Fragment {

	private ParseImageView mImageView;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
        View rootView = inflater.inflate(R.layout.settings_fragment, container, false);

        mImageView = (ParseImageView)rootView.findViewById(R.id.profile_pic_img);
        mImageView.setPlaceholder(getActivity().getResources().getDrawable(R.drawable.unknown));
        
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseFile file = currentUser.getParseFile("img");
        if(file != null){
        	mImageView.setParseFile(file);
        	mImageView.loadInBackground();
        }
        
        Button selBtn = (Button)rootView.findViewById(R.id.select_img_btn);
        selBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				MoreActionListener listener = (MoreActionListener)getActivity();
				listener.pickImage();
			}
		});
        
        Button saveBtn = (Button)rootView.findViewById(R.id.save_img_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				MoreActionListener listener = (MoreActionListener)getActivity();
				String imgPath = listener.getImagePath();
				if(imgPath != null){
					Bitmap img = BitmapFactory.decodeFile(imgPath);
					Bitmap scaledImg = Bitmap.createScaledBitmap(img, 40, 40, false);
					
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					scaledImg.compress(Bitmap.CompressFormat.JPEG, 100, bos);

					byte[] scaledData = bos.toByteArray();
					ParseFile profilePic = new ParseFile("profile.jpg", scaledData);
					
					ParseUser curUser = ParseUser.getCurrentUser();
					curUser.put("img", profilePic);
					
					curUser.saveInBackground(new SaveCallback() {
						
						@Override
						public void done(ParseException exp) {
							if(exp != null){
								Toast toast = Toast.makeText(getActivity(), "Image upload failed", Toast.LENGTH_SHORT);
				            	toast.show();
				            	return;
							}
							ParseUser currentUser = ParseUser.getCurrentUser();
					        ParseFile file = currentUser.getParseFile("img");
					        if(file != null){
					        	mImageView.setParseFile(file);
					        	mImageView.loadInBackground();
					        }	
					        
					        Toast toast = Toast.makeText(getActivity(), "Image upload passed", Toast.LENGTH_SHORT);
			            	toast.show();
						}
					});
				}
			}
		});
        
        return rootView;
    }
}
