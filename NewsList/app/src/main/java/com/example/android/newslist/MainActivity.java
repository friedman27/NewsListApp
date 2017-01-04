package com.example.android.newslist;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.android.newslist.R.id.search;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsStory>> {

    // Loader ID
    private static final int NEWS_LOADER_ID = 1;

    // Search Key used to identify if the search value is different from the results already displayed
    private String mSearchKey;

    ListView newsListView;

    EditText mSearchQuery;
    Button mSearchButton;
    private String mSearchEntry;
    private static final String NEWS_REQUEST_URL = "http://content.guardianapis.com/search?q=";
    private static final String NEWS_REQUEST_KEY  = "&api-key=test&show-tags=contributor";

    public static final String LOG_TAG = MainActivity.class.getName();

    private NewsAdapter mNewsAdapter;
    private TextView mEmptyStateTextView;

    private String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchButton = (Button) findViewById(R.id.search_button);
        mSearchQuery = (EditText) findViewById(search);

        // Find a reference to the {@link ListView} in the layout
        newsListView = (ListView) findViewById(R.id.list);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of earthquakes as input
        mNewsAdapter = new NewsAdapter(this, new ArrayList<NewsStory>());
        newsListView.setAdapter(mNewsAdapter);

        ProgressBar mLoadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
        mLoadingSpinner.setVisibility(View.GONE);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchEntry = mSearchQuery.getText().toString().replaceAll(" ", "+");
                Log.e(LOG_TAG, "Value of searchEntry from editText field: " + mSearchEntry);

                if(mSearchEntry == "") {
                    newsListView.setVisibility(View.GONE);
                    mEmptyStateTextView.setText(R.string.enter_search);
                    Log.e(LOG_TAG, "Value of searchEntry is: " + mSearchEntry);
                } else {
                        if (isNetworkAvailable()) {
                            mEmptyStateTextView.setVisibility(View.GONE);
                            // Set an item click listener on the ListView, which sends an intent to a web browser
                            // to open a website with more information about the selected earthquake.
                            newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                    // Find the current earthquake that was clicked on
                                    NewsStory currentStory = mNewsAdapter.getItem(position);

                                    // Convert the String URL into a URI object (to pass into the Intent constructor)
                                    Uri newsUri = Uri.parse(currentStory.getmURL());

                                    // Create a new intent to view the news URI
                                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                                    // Send the intent to launch a new activity
                                    startActivity(websiteIntent);
                                }
                            });

                            // Get a reference to the LoaderManager, in order to interact with loaders.
                            LoaderManager loaderManager = getLoaderManager();

                            Log.e(LOG_TAG, "value of searchkey: " + mSearchKey);
                            if (mSearchKey != mSearchEntry) {
                                loaderManager.restartLoader(NEWS_LOADER_ID, null, MainActivity.this);
                            }
                            loaderManager.initLoader(NEWS_LOADER_ID, null, MainActivity.this);
                            Log.e(LOG_TAG, "loaderManager.initLoader() called");
                        } else {
                            newsListView.setEmptyView(mEmptyStateTextView);
                            ProgressBar mLoadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
                            mLoadingSpinner.setVisibility(View.GONE);
                            // Set empty state text to display "No internet connection."
                            mEmptyStateTextView.setText(R.string.no_internet);
                        }
                    }

            }
        });
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
        }

        return true;
    }

    @Override
    public Loader<List<NewsStory>> onCreateLoader(int id, Bundle args) {
        Log.e(LOG_TAG, "onCreateLoader called");
        mSearchKey = mSearchEntry;

        ProgressBar mLoadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
        mLoadingSpinner.setVisibility(View.VISIBLE);
        newsListView.setVisibility(View.GONE);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse(NEWS_REQUEST_URL + mSearchEntry + NEWS_REQUEST_KEY);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("from_date", date);
        uriBuilder.appendQueryParameter("orderBy", orderBy);

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<NewsStory>> loader, List<NewsStory> newsStories) {
        Log.e(LOG_TAG, "onLoadFinished called");
        // Hide the progress bar from visibility once the data is loaded
        ProgressBar mLoadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
        mLoadingSpinner.setVisibility(View.GONE);
        // Set empty state text to display "No News Story Results Found."
        mEmptyStateTextView.setText(R.string.empty_view);
        newsListView.setVisibility(View.VISIBLE);
        mNewsAdapter.clear();

        if (newsStories != null && !newsStories.isEmpty()) {
            mNewsAdapter.addAll(newsStories);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsStory>> loader) {
        Log.e(LOG_TAG, "onLoaderReset called");
        mNewsAdapter.clear();
    }
}
