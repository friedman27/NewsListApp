package com.example.android.newslist;


import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<NewsStory>> {

    /** Tag for log messages */
    private static final String LOG_TAG = NewsLoader.class.getName();

    /** Query URL */
    private String mUrl;

    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.e(LOG_TAG, "onStartLoading called");
        forceLoad();
    }


    @Override
    public List<NewsStory> loadInBackground() {
        Log.e(LOG_TAG, "loadInBackground called");
        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<NewsStory> newsStories = QueryUtils.fetchNewsData(mUrl);
        return newsStories;
    }
}

