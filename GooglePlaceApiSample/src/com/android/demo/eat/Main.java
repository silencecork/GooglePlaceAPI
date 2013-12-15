package com.android.demo.eat;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ListView;

import com.silencecork.google.search.nearby.GooglePlaceApi;
import com.silencecork.google.search.nearby.OnSearchResultListener;
import com.silencecork.google.search.nearby.Place;

public class Main extends Activity implements OnSearchResultListener {
	
	
	private GooglePlaceApi place;
	private LocationAdapter adapter;
	private ListView listView;
	private static final String API_KEY = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        if (TextUtils.isEmpty(API_KEY)) {
        	throw new NullPointerException("You must assign API KEY");
        }
        
        place = new GooglePlaceApi(API_KEY, this);
        
        place.search(24.989926, 121.545414, 2000, "food");
        
        listView = (ListView) findViewById(R.id.list);
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    }
    
	@Override
	public void onResult(List<Place> places) {
		adapter = new LocationAdapter(Main.this, places);
		listView.setAdapter(adapter);
	}

	@Override
	public void onFail() {
		
	}
}