package com.fiftyradios.odiyan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FeedFragment extends Fragment implements EndlessListView.EndlessListener, FeedQueryAdapter.OnMoreLoadListener {

	public static final String STORE_NAME = "questions";
	private QuestionStore mStore;
	private FeedQueryAdapter mAdapter;
	private EndlessListView mListView;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
        View rootView = inflater.inflate(R.layout.feed_fragment, container, false); 
        
        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeView.setColorScheme(R.color.swipe_color_1, R.color.swipe_color_2, R.color.swipe_color_3, R.color.swipe_color_4);
        
        mListView = (EndlessListView)rootView.findViewById(R.id.feed_list);
        
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
        	mAdapter = new FeedQueryAdapter(getActivity(), listner);
        	mStore = new QuestionStore(mAdapter.getData());
        	fm.beginTransaction().add(mStore, STORE_NAME).commit();
        }
        else{
        	mAdapter = new FeedQueryAdapter(getActivity(), mStore.getData(), listner);
        }

        swipeView.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				mAdapter.refreshData();
			}
		});
        
        mAdapter.setMoreListner(this);
        mListView.setLoadingView(R.layout.more_loading);
        mListView.setAdapter(mAdapter);
        
        mListView.setListener(this);
        
        return rootView;
    }
	
	@Override
	public void loadData() {
		if(mAdapter != null){
			mAdapter.loadMore();
		}
	}
	
	@Override
	public void onMoreLoading() {
		
	}

	@Override
	public void onMoreLoaded() {
		if(mListView != null){
			mListView.finishLoading();
		}		
	}

	@Override
	public void onMoreLoadFailed() {
		if(mListView != null){
			mListView.finishLoading();
		}
	}
	
	public class QuestionStore extends Fragment{
    	
    	private FeedQueryAdapter.FeedData mData;
    	
    	public QuestionStore(){
    		
    	}
    	
    	public QuestionStore(FeedQueryAdapter.FeedData data){
    		mData = data;
    	}
    	
    	public FeedQueryAdapter.FeedData getData(){
    		return mData;
    	}
    	
    	@Override
    	public void onCreate(Bundle savedInstanceState) {
    	    super.onCreate(savedInstanceState);
    	    
    	    setRetainInstance(true);
    	  }
    }
}
