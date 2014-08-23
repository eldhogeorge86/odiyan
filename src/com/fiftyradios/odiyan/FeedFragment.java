package com.fiftyradios.odiyan;

import java.util.ArrayList;

import com.parse.ParseObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class FeedFragment extends Fragment {

	static final String STORE_NAME = "questions";
	private QuestionStore mStore;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
        View rootView = inflater.inflate(R.layout.feed_fragment, container, false);
        
        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeView.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				swipeView.setRefreshing(false);
			}
		});
        
        ListView feedList = (ListView)rootView.findViewById(R.id.feed_list);
        feedList.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
     
            }
     
            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (firstVisibleItem == 0)
                        swipeView.setEnabled(true);
                    else
                        swipeView.setEnabled(false);
            }
        });
        
        FeedQueryAdapter adapter = null;
        
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FeedQueryAdapter.OnLoadListener listner = new FeedQueryAdapter.OnLoadListener() {
			
			@Override
			public void onLoading() {
				swipeView.setRefreshing(true);
			}
			
			@Override
			public void onLoaded() {
				swipeView.setRefreshing(false);
			}
		};
        mStore = (QuestionStore)fm.findFragmentByTag(STORE_NAME);
        if(mStore == null){
        	adapter = new FeedQueryAdapter(getActivity(), listner);
        	mStore = new QuestionStore(adapter.getData());
        	fm.beginTransaction().add(mStore, STORE_NAME).commit();
        }
        else{
        	adapter = new FeedQueryAdapter(getActivity(), mStore.getData(), listner);
        }
        
        feedList.setAdapter(adapter);
        
        return rootView;
    }
	
	public class QuestionStore extends Fragment{
    	
    	private ArrayList<ParseObject> mData;
    	
    	public QuestionStore(){
    		
    	}
    	
    	public QuestionStore(ArrayList<ParseObject> data){
    		mData = data;
    	}
    	
    	public ArrayList<ParseObject> getData(){
    		return mData;
    	}
    	
    	@Override
    	public void onCreate(Bundle savedInstanceState) {
    	    super.onCreate(savedInstanceState);
    	    
    	    setRetainInstance(true);
    	  }
    }
}
