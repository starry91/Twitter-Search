package com.home.twitter;

/**
 * Created by praveen on 1/26/18.
 */


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TweetAdapter extends BaseAdapter {
    ArrayList<Tweet> tweetList;
    ArrayList<String> score;
    ArrayList<String> mag;
    Context context;

    public TweetAdapter(Context context, ArrayList<Tweet> tweetList, ArrayList<String> score, ArrayList<String> mag) {
        this.tweetList = tweetList;
        this.score = score;
        this.mag = mag;
        this.context = context;
    }

    @Override
    public int getCount() {
        return tweetList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.tweet_list_item, null);
        }

        Tweet tweet = tweetList.get(position);
        String tweetscore = score.get(position);
        String tweetmag = mag.get(position);
        TextView txtTweet = (TextView) convertView.findViewById(R.id.txtTweet);
        TextView txtTweetBy = (TextView) convertView.findViewById(R.id.txtTweetBy);
        //TextView txtScore = (TextView) convertView.findViewById(R.id.txtScore);
        TextView txtMag = (TextView) convertView.findViewById(R.id.txtMag);

        txtTweet.setText(tweet.getTweet());
        txtTweetBy.setText(tweet.getTweetBy());
        txtMag.setText("Score "+tweetscore + " Magnitute: "+ tweetmag);
        //txtScore.setText(tweetmag);

        return convertView;
    }
}
