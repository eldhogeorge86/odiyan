package com.fiftyradios.odiyan;

import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.FragmentTransaction;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build;


public class MainActivity extends ActionBarActivity implements
						ParseLoginFragment.ParseLoginFragmentListener,
						ParseLoginHelpFragment.ParseOnLoginHelpSuccessListener,
						ParseOnLoginSuccessListener, ParseOnLoadingListener {

	// Although Activity.isDestroyed() is in API 17, we implement it anyways for older versions.
	private boolean destroyed = false;
	
	private ProgressDialog progressDialog;
	
	private final int fragmentContainer = R.id.container;
	  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
        	
        	ParseUser user = ParseUser.getCurrentUser();
	        if(user == null){
	        	getSupportActionBar().hide();
	        	
	        	getSupportFragmentManager().beginTransaction()
					.replace(fragmentContainer, new ParseLoginFragment())
					.commit();
	        }
	        else{
	        	
	            getSupportFragmentManager().beginTransaction()
	                    .replace(fragmentContainer, new HomeFragment())
	                    .commit();
	        }
        }
    }

    @Override
    protected void onDestroy() {
      super.onDestroy(); 
      
      destroyed = true;
    }
    
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public boolean isDestroyed() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        return super.isDestroyed();
      }
      return destroyed;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		// Required for making Facebook login work
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }
    
    @Override
    public void onSignUpClicked(String username, String password) {
    	// Show the signup form, but keep the transaction on the back stack
    	// so that if the user clicks the back button, they are brought back
    	// to the login form.
    	FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    	transaction.replace(fragmentContainer, new ParseSignupFragment());
    	transaction.addToBackStack(null);
    	transaction.commit();
    }
    
    @Override
    public void onLoginHelpClicked() {
    	// Show the login help form for resetting the user's password.
    	// Keep the transaction on the back stack so that if the user clicks
    	// the back button, they are brought back to the login form.
    	FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    	transaction.replace(fragmentContainer, new ParseLoginHelpFragment());
    	transaction.addToBackStack(null);
    	transaction.commit();
    }
    
    @Override
    public void onLoginHelpSuccess() {
    	// Display the login form, which is the previous item onto the stack
    	getSupportFragmentManager().popBackStackImmediate();
    }
    
    @Override
    public void onLoginSuccess() {
      
    	onLoadingFinish();
    	
    	getSupportActionBar().show();
    	
    	getSupportFragmentManager().beginTransaction()
	        .replace(fragmentContainer, new HomeFragment())
	        .commit();
    }
    
    @Override
    public void onLoadingStart(boolean showSpinner) {
    	if (showSpinner) {
    		progressDialog = ProgressDialog.show(this, null,
    				getString(R.string.com_parse_ui_progress_dialog_text), true, false);
    	}
    }
    
    @Override
    public void onLoadingFinish() {
    	if (progressDialog != null) {
    		progressDialog.dismiss();
    	}
    }
}
