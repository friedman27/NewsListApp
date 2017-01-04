package com.example.android.newslist;


import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.example.android.newslist.MainActivity.LOG_TAG;
import static com.example.android.newslist.R.id.date;

public class NewsAdapter extends ArrayAdapter<NewsStory> {

    private static final String TITLE_SEPARATOR = " : ";

    public NewsAdapter(Activity context, List<NewsStory> newsStories) {
        super(context, 0, newsStories);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        NewsStory currentStory = getItem(position);

        // Separate Title from Subtitle
        String originalTitle = currentStory.getmTitle();
        String title = null;
        String subtitle = null;
        if (currentStory.getmTitle().contains(TITLE_SEPARATOR)) {
            String[] parts = originalTitle.split(TITLE_SEPARATOR);
            title = parts[0];
            subtitle = parts[1];
        } else {
            title = currentStory.getmTitle();
            subtitle = null;
        }

        // Title
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title);
        titleTextView.setText(title);

        // Subtitle
        TextView subtitleTextView = (TextView) listItemView.findViewById(R.id.subtitle);
        subtitleTextView.setText(subtitle);

        // Author
        TextView authorFirstName = (TextView) listItemView.findViewById(R.id.author);
        authorFirstName.setText(String.valueOf(currentStory.getmAuthor()));


        // Date
        Date dateObject = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss");
        try {
            dateObject = ft.parse(currentStory.getmTime());
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Could not format date in NewsAdapter Class.");
            e.printStackTrace();
        }

        TextView dateTextView = (TextView) listItemView.findViewById(date);
        // Format the date string (i.e. "Mar 3, 1984")
        String formattedDate = formatDate(dateObject);
        dateTextView.setText(formattedDate);

        // Time
        TextView timeTextView = (TextView) listItemView.findViewById(R.id.time);
        // Format the time string (i.e. "4:30PM")
        String formattedTime = formatTime(dateObject);
        timeTextView.setText(formattedTime);

        // Result Number
        TextView resultNumberTextView = (TextView) listItemView.findViewById(R.id.result_number);
        resultNumberTextView.setText(String.valueOf(currentStory.getmResultNumber()));

        return listItemView;
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted time string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }
}
