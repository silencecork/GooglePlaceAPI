package com.silencecork.google.search.nearby;

import java.util.List;

public interface OnSearchResultListener {
	public void onResult(List<Place> places);
	
	public void onFail();
}
