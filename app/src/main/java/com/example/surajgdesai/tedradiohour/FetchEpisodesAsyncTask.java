package com.example.surajgdesai.tedradiohour;

import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Suraj G Desai on 03/08/2017.
 */

public class FetchEpisodesAsyncTask extends AsyncTask<String, Void, ArrayList<TedRadioPodcast>> {

    IGetEpisodes mainActivity;

    public FetchEpisodesAsyncTask(IGetEpisodes mainActivity) {
        this.mainActivity = mainActivity;
    }

    public static interface IGetEpisodes {
        public void fetchEpisodes(ArrayList<TedRadioPodcast> gameList);
    }

    @Override
    protected ArrayList<TedRadioPodcast> doInBackground(String... params) {
        BufferedReader reader = null;
        try {
            URL url = new URL(params[0]);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            InputStream in = httpURLConnection.getInputStream();
            return PodcastDetailsXMLParser.PodcastDetailsPullParser.parsePodcastListPullParser(in);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<TedRadioPodcast> gameList) {
        mainActivity.fetchEpisodes(gameList);
    }
}
