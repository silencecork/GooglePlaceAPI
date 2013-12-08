package com.silencecork.google.search.nearby;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class Place implements Parcelable {
	
	private String id;
	private String name;
	private String iconUrl;
	private String vicinity;
	private float rating;
	private double latitude;
	private double longitude;
	private String reference;
	
	public Place(Parcel p) {
		id = p.readString();
		name = p.readString();
		iconUrl = p.readString();
		vicinity = p.readString();
		rating = p.readFloat();
		latitude = p.readDouble();
		longitude = p.readDouble();
		reference = p.readString();
	}
	
	public Place(JSONObject entry) throws JSONException {
		String id = entry.getString("id");
        String icon = entry.getString("icon");
        String name = entry.getString("name");
        String reference = entry.getString("reference");
        String vicinity = entry.getString("vicinity");
       
        float rating = 0;
        if (entry.has("rating")) {
            String strRating = entry.getString("rating");
            if (!TextUtils.isEmpty(strRating)) {
            	rating = Float.parseFloat(strRating);
            }
        }
        JSONObject geoObj = entry.getJSONObject("geometry");
        double lat = -1;
        double lng = -1;
        if (geoObj != null) {
            JSONObject locationObj = geoObj.getJSONObject("location");
            if (locationObj != null) {
                String strLat = locationObj.getString("lat");
                String strLng = locationObj.getString("lng");
                lat = Double.parseDouble(strLat);
                lng = Double.parseDouble(strLng);
            }
        }
        this.id = id;
        this.name = name;
        this.iconUrl = icon;
        this.latitude = lat;
        this.longitude = lng;
        this.vicinity = vicinity;
        this.reference = reference;
        this.rating = rating;
	}

	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getIconUrl() {
		return iconUrl;
	}
	
	public String getVicinity() {
		return vicinity;
	}
	
	public float getRating() {
		return rating;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public String getReference() {
		return reference;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p, int arg1) {
		p.writeString(id);
		p.writeString(name);
		p.writeString(iconUrl);
		p.writeString(vicinity);
		p.writeFloat(rating);
		p.writeDouble(latitude);
		p.writeDouble(longitude);
		p.writeString(reference);
	}
	
	public static Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>() {

		@Override
		public Place createFromParcel(Parcel source) {
			return new Place(source);
		}

		@Override
		public Place[] newArray(int size) {
			return new Place[size];
		}
		
	};
}
