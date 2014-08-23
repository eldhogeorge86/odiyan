package com.fiftyradios.odiyan;

import java.util.Locale;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeFragment extends Fragment implements ActionBar.TabListener {

	private ViewPager mViewPager;
	private SectionsPagerAdapter mSectionsPagerAdapter;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        
        mSectionsPagerAdapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        
        mViewPager.setAdapter(mSectionsPagerAdapter);
        
        final ActionBar actBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        actBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        actBar.removeAllTabs();
        
        actBar.addTab(actBar.newTab().setIcon(R.drawable.my_feed).setTabListener(this));
        actBar.addTab(actBar.newTab().setIcon(R.drawable.friends).setTabListener(this));
        actBar.addTab(actBar.newTab().setIcon(R.drawable.notifications).setTabListener(this));
        actBar.addTab(actBar.newTab().setIcon(R.drawable.more).setTabListener(this));
        
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
            	actBar.setSelectedNavigationItem(position);
            }
        });
        
        return rootView;
    }
	
	@Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {        	
    	
    	int pos = tab.getPosition();
    	if(pos == 0){
    		setTitle(R.string.my_feed);
    	}
    	else if(pos == 1){
    		setTitle(R.string.friends);
    	}
    	else if(pos == 2){
    		setTitle(R.string.notifications);
    	}
    	else if(pos == 3){
    		setTitle(R.string.more);
    	}
    	
    	mViewPager.setCurrentItem(pos);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
    
    private void setTitle(int resId){
    	ActionBarActivity act = (ActionBarActivity)getActivity();
    	if(act != null){
    		ActionBar actBar = act.getSupportActionBar();
    		if(actBar != null){
    			actBar.setTitle(resId);
    		}
    	}
    	
    }
    
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
        	switch (position) {
        		case 0:
        			return new FeedFragment();
        		case 1:
        			return new FriendsFragment();
        		case 2:
        			return new NotificationFragment();
        		case 3:
        			return new MoreFragment();
        	}
        	
        	return null;
        }

        @Override
        public int getCount() {

            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.my_feed).toUpperCase(l);
                case 1:
                    return getString(R.string.friends).toUpperCase(l);
                case 2:
                    return getString(R.string.notifications).toUpperCase(l);
                case 3:
                    return getString(R.string.more).toUpperCase(l);
            }
            return null;
        }
    }
}
