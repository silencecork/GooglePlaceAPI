package com.example.googleplacesimpledemo;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.silencecork.google.search.nearby.GooglePlaceApi;
import com.silencecork.google.search.nearby.OnSearchResultListener;
import com.silencecork.google.search.nearby.Place;

public class MainActivity extends Activity implements OnSearchResultListener {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        GooglePlaceApi placeApi = new GooglePlaceApi("", this);
        
        placeApi.search(24.989926, 121.545414, 2000, "food");
    }
    
    @Override
    public void onDestroy() { 
    	super.onDestroy();
    }

	@Override
	public void onResult(List<Place> places) {
		StringBuilder builder = new StringBuilder();
		for (Place place : places) {
			builder.append(place.getName());
			builder.append(" ");
			builder.append(place.getRating());
			builder.append("\n");
		}
		TextView text = (TextView) findViewById(R.id.result);
		text.setText(builder.toString());
	}

	@Override
	public void onFail() {
		
	}
}