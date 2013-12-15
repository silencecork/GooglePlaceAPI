package com.silencecork.google.search.nearby;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

public class GooglePlaceApi {
	
	private static final String TAG = "GooglePlaceApi";
	
	private static final int VALID_REQUEST_DELAY = 2000;
	
	private static final Uri SEARCH_BASE_URL = Uri.parse("https://maps.googleapis.com/maps/api/place/nearbysearch/json");

	private String mAPIKey;
	
	private OnSearchResultListener mListener;
	
	private static class SearchRequest {
		double latitude;
		double longitude;
		int radius;
		String type;
		
		SearchRequest(double lat, double lng, int r, String t) {
			latitude = lat;
			longitude = lng;
			radius = r;
			type = t;
		}
	}
	
	public GooglePlaceApi(String apiKey, OnSearchResultListener listener) {
		if (TextUtils.isEmpty(apiKey)) {
			throw new NullPointerException("no assigned api key");
		}
		mAPIKey = apiKey;
		mListener = listener;
	}
	
	/**
	 * 
	 * @param latitude
	 * @param longitude
	 * @param radius
	 * @param type support type: http://code.google.com/intl/fr/apis/maps/documentation/places/supported_types.html
	 */
	public void search(double latitude, double longitude, int radius, String type) {
		SearchRequest request = new SearchRequest(latitude, longitude,radius, type);
		
		AsyncTask<SearchRequest, Void, List<Place>> task = new AsyncTask<SearchRequest, Void, List<Place>>() {

			@Override
			protected List<Place> doInBackground(SearchRequest... arg0) {
				SearchRequest request = arg0[0];
				if (request == null) {
					return null;
				}
				
				if (TextUtils.isEmpty(mAPIKey)) {
		            Log.e(TAG, "API_KEY can not null");
		            return null;
		        }
				
				double latitude = request.latitude;
				double longitude = request.longitude;
				int radius = request.radius;
				String type = request.type;
				List<Place> placeList = new ArrayList<Place>();
				boolean result = retrievePlaces(latitude, longitude, radius, type, null, placeList);
		        
				return (result) ? placeList : null;
			}

			@Override
			protected void onPostExecute(List<Place> result) {
				if (result == null) {
					if (mListener != null) mListener.onFail();
            	} else {
            		if (mListener != null) mListener.onResult(result);
            	}
			}
			
		};
		
		task.execute(request);
		
	}
	
	private boolean retrievePlaces(double latitude, double longitude, int radius, String type, String pageToken, List<Place> list) {
		String data = searchPlace(latitude, longitude, radius, type, pageToken);
        if (data != null) {
            try {
            	
            	if (!parsingData(data, list)) {
            		return false;
            	}
            	JSONObject json = new JSONObject(data);
            	String nextPageToken = null;
                if (json.has("next_page_token")) {
                	nextPageToken = json.getString("next_page_token");
                }
                if (!TextUtils.isEmpty(nextPageToken)) {
                	Thread.sleep(VALID_REQUEST_DELAY);
                	retrievePlaces(latitude, longitude, radius, type, nextPageToken, list);
                }
                
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return false;
	}
	
	private String searchPlace(double latitude, double longitude, int radius, String type, String nextPageToken) {
		Uri.Builder builder = SEARCH_BASE_URL.buildUpon();
        
        builder.appendQueryParameter("location", latitude + "," + longitude);
        builder.appendQueryParameter("radius", Integer.toString(radius));
        
        if (!TextUtils.isEmpty(type)) {
            builder.appendQueryParameter("types", type);
        }
        
        if (!TextUtils.isEmpty(nextPageToken)) {
            builder.appendQueryParameter("pagetoken", nextPageToken);
        }
        
        builder.appendQueryParameter("language", "zh-TW");
        
        builder.appendQueryParameter("sensor", "false");
        builder.appendQueryParameter("key", mAPIKey);
        
        String data = null;
		try {
			data = Util.get(builder.build().toString());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return data;
	}
	
	private boolean parsingData(String data, List<Place> list) throws JSONException {
		JSONObject json = new JSONObject(data);
        String status = json.getString("status");
        if (!"OK".equals(status)) {
        	Log.e(TAG, "error status " + status);
        	Log.e(TAG, "data " + data);
        	return false;
        }
        
        JSONArray array = json.getJSONArray("results");
        int count = array.length();
        
        for (int i = 0; i < count; i++) {
            JSONObject entry = array.getJSONObject(i);
            Place place = new Place(entry);
            list.add(place);
        }
        return true;
	}
}
