package com.silencecork.google.search.nearby;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

public class GooglePlaceApi {
	
	private static final String TAG = "GooglePlaceApi";
	
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
		        
		        Uri.Builder builder = SEARCH_BASE_URL.buildUpon();
		        
		        builder.appendQueryParameter("location", latitude + "," + longitude);
		        builder.appendQueryParameter("radius", Integer.toString(radius));
		        
		        if (!TextUtils.isEmpty(type)) {
		            builder.appendQueryParameter("types", type);
		        }
		        
		        /*if(!TextUtils.isEmpty(detailName)) {
		            builder.appendQueryParameter("name", detailName);
		        }*/
		        
		        builder.appendQueryParameter("language", "zh-TW");
		        
		        builder.appendQueryParameter("sensor", "false");
		        builder.appendQueryParameter("key", mAPIKey);
		        
		        String data = null;
				try {
					data = Util.get(builder.build().toString());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		        if (data != null) {
		            try {
		                JSONObject json = new JSONObject(data);
		                String status = json.getString("status");
		                if (!"OK".equals(status)) {
		                	Log.e(TAG, "error status " + status);
		                	return null;
		                }
		                JSONArray array = json.getJSONArray("results");
		                int count = array.length();
		                ArrayList<Place> list = new ArrayList<Place>();
		                for (int i = 0; i < count; i++) {
		                    JSONObject entry = array.getJSONObject(i);
		                    Place place = new Place(entry);
		                    list.add(place);
		                }
		                return list;
		            } catch (Exception e) {
		                e.printStackTrace();
		            }
		        }
				return null;
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
}
