package com.example.android.newslist;


public class NewsStory {

    private String mTitle;
    private String mTime;
    private String mURL;
    private int mResultNumber;
    private String mAuthor;


    public NewsStory (String title, String author, String time, String url, int resultNumber) {
        mTitle = title;
        mTime = time;
        mURL = url;
        mResultNumber = resultNumber;
        mAuthor = author;

    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmTime() {
        return mTime;
    }

    public String getmURL() {
        return mURL;
    }

    public int getmResultNumber() {
        return mResultNumber;
    }

    public String getmAuthor() {
        return mAuthor;
    }


}
