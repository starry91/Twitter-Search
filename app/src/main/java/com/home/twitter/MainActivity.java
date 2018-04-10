package com.home.twitter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.services.language.v1.CloudNaturalLanguage;
import com.google.api.services.language.v1.CloudNaturalLanguageRequestInitializer;
import com.google.api.services.language.v1.model.AnalyzeSentimentRequest;
import com.google.api.services.language.v1.model.AnalyzeSentimentResponse;
import com.google.api.services.language.v1.model.Document;
import com.google.api.services.language.v1.model.Features;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;

public class MainActivity extends AppCompatActivity {

    public Button search;
    public Button clear;
    public EditText txtSearch;
    public ListView tweetDisplay;
    private DatabaseReference mDatabase;
    private DatabaseReference search_word;
    public static final String API_KEY = "AIzaSyDPvwEzYwR2LYbNzNo3P4cUIEm5YA5XYZ8";
    public CloudNaturalLanguage naturalLanguageService;
    public Document document;
    public Features features;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        search_word = mDatabase.child("search_key");
        search = (Button) findViewById(R.id.cmdSearch);
        clear = (Button) findViewById(R.id.clear);
        txtSearch = (EditText) findViewById(R.id.txtSearch);
        tweetDisplay = (ListView) findViewById(R.id.result);


        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                runsearch task = new runsearch();
                task.execute(txtSearch.getText().toString());
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDatabase.setValue(null);
            }
        });
    }

    private class runsearch extends AsyncTask<String, Void, Integer> {

        ArrayList<Tweet> tweets;
        ArrayList<String> score;
        ArrayList<String> mag;
        final int SUCCESS = 0;
        final int FAILURE = SUCCESS + 1;

        protected Integer doInBackground(String... key) {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setApplicationOnlyAuthEnabled(true)
                    .setOAuthConsumerKey("pRs4gHfN2i0mwN1XpkpcWn3N3")
                    .setOAuthConsumerSecret("MSeQpydQL2bb41ox3HcOAyR9KYgsSwMzVfOYKJ5xMvbfcZC7SC");
            OAuth2Token token = null;

            try {
                token = new TwitterFactory(cb.build()).getInstance().getOAuth2Token();
            } catch (TwitterException e) {
                e.printStackTrace();
            }

            cb = new ConfigurationBuilder();
            cb.setApplicationOnlyAuthEnabled(true);
            cb.setOAuthConsumerKey("pRs4gHfN2i0mwN1XpkpcWn3N3");
            cb.setOAuthConsumerSecret("MSeQpydQL2bb41ox3HcOAyR9KYgsSwMzVfOYKJ5xMvbfcZC7SC");
            cb.setOAuth2TokenType(token.getTokenType());
            cb.setOAuth2AccessToken(token.getAccessToken());

            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter twitter = tf.getInstance();
            Query query = new Query(key[0]);
            query.setCount(5);
            QueryResult result = null;
            try {
                result = twitter.search(query);
            } catch (TwitterException e) {
                e.printStackTrace();
            }

            List<twitter4j.Status> tweets = result.getTweets();
            if (tweets != null) {
                naturalLanguageService = new CloudNaturalLanguage.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(),
                        null
                ).setCloudNaturalLanguageRequestInitializer(
                        new CloudNaturalLanguageRequestInitializer(API_KEY)
                ).build();
                document = new Document();
                document.setType("PLAIN_TEXT");
                document.setLanguage("en-US");

                features = new Features();
                features.setExtractEntities(true);
                features.setExtractSyntax(true);
                features.setExtractDocumentSentiment(true);

                AnalyzeSentimentRequest request = new AnalyzeSentimentRequest();
                request.setDocument(document);



                this.tweets = new ArrayList<Tweet>();
                this.score = new ArrayList<String>();
                this.mag = new ArrayList<String>();

                for (twitter4j.Status tweet : tweets) {
                    document.setContent(tweet.getText());
                    try {
                        AnalyzeSentimentResponse response = naturalLanguageService.documents().analyzeSentiment(request).execute();
                        score.add(Float.toString(response.getDocumentSentiment().getScore()));
                        mag.add(Float.toString(response.getDocumentSentiment().getMagnitude()));
                        this.tweets.add(new Tweet("@" + tweet.getUser().getScreenName(), tweet.getText()));
                        String key1 = mDatabase.push().getKey();
                        //mDatabase.child(key1).setValue(new Tweet("@" + tweet.getUser().getScreenName(), tweet.getText()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return SUCCESS;
            }

            return FAILURE;
        }

        protected void onPostExecute(Integer result) {
            if (result == SUCCESS) {
                tweetDisplay.setAdapter(new TweetAdapter(MainActivity.this, tweets, new ArrayList<String>(score), new ArrayList<String>(mag)));
            } else {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        }
    }

}
