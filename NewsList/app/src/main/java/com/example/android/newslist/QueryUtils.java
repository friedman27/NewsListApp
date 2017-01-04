package com.example.android.newslist;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.newslist.MainActivity.LOG_TAG;

/**
 * Helper methods related to requesting and receiving news data from The Guardian.
 */
public final class QueryUtils {


    // private constructor, no objects created from this class
    private QueryUtils() {
    }

    public static List<NewsStory> fetchNewsData(String requestUrl) {
        Log.e(LOG_TAG, "fetchNewsData called");
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<NewsStory> newsStories = extractNewsStoryFromJson(jsonResponse);

        // Return the {@link Event}
        return newsStories;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<NewsStory> extractNewsStoryFromJson(String newsJSON) {
        Log.e(LOG_TAG, "extractNewsStoryFromJSON called.");
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            Log.e(LOG_TAG, "empty textUtils in extractNewsStoryFrom JSON method.");
            return null;
        }

        ArrayList<NewsStory> newsStories = new ArrayList<>();

        try {
            JSONObject jsonRootObject = new JSONObject(newsJSON);
            JSONObject jsonResponseObject = jsonRootObject.getJSONObject("response");
            JSONArray jsonResultsArray = jsonResponseObject.getJSONArray("results");
            Log.e(LOG_TAG, "jsonResultsArray = " + jsonResultsArray);

            //Iterate the jsonArray and print the info of JSONObjects
            for (int i = 0; i < jsonResultsArray.length(); i++) {
                JSONObject currentStory = jsonResultsArray.getJSONObject(i);


                String title = currentStory.getString("webTitle");
                String time = currentStory.getString("webPublicationDate");
                String url = currentStory.getString("webUrl");


                String authorLastName;
                String authorFirstName;
                String author = "";

                if (currentStory.has("tags")) {
                    ArrayList<String> authors = new ArrayList();
                    JSONArray contributors = currentStory.getJSONArray("tags");
                    if (contributors.length() > 0) {
                        for (int j = 0; j < contributors.length(); j++) {
                            JSONObject currentContributor = contributors.getJSONObject(j);
                            authorFirstName = currentContributor.getString("firstName");
                            authorLastName = currentContributor.getString("lastName");
                            author = authorFirstName + " " + authorLastName;
                            authors.add(author);
                        }
                    }
                    if(authors.isEmpty()) {
                        author = "No Author Listed";
                    } else if(authors.size() > 1) {
                        for (int k = 1; k < authors.size(); k++) {
                            author += ", " + authors.get(k);
                        }
                    }
                }

                int resultNumber = i + 1;

                NewsStory newsStory = new NewsStory(title, author, time, url, resultNumber);
                newsStories.add(newsStory);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the JSON results", e);
        }
        Log.e(LOG_TAG, "value of newsStories after parsing: " + newsStories);
        return newsStories;
    }

}