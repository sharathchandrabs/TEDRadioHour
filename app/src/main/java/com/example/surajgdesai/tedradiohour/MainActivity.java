package com.example.surajgdesai.tedradiohour;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements FetchEpisodesAsyncTask.IGetEpisodes, PodcastRecyclerViewAdapter.IPlayPodcastMedia, PodcastMediaFragment.IUpdateMediaStatus {

    ProgressDialog progressDialog;
    int currentLayout = -1;
    PodcastRecyclerViewAdapter customAdapter;
    RecyclerView recycleListView;
    ArrayList<TedRadioPodcast> episodesList = null;
    PodcastMediaFragment podcastMediaFragment;
    FragmentManager fragmentManager;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            Toolbar myToolbar = (Toolbar) findViewById(R.id.mainActivitytoolbar);
            setSupportActionBar(myToolbar);
            TextView actionBarTitle = (TextView) findViewById(R.id.toolbarTitle);
            actionBarTitle.setText(R.string.app_name);
            currentLayout = R.layout.podcast_row_item;
            recycleListView = (RecyclerView) findViewById(R.id.recyclerView);

            if (isConnectedOnline()) {
                progressDialog = new ProgressDialog(this);
                progressDialog.setCancelable(false);
                progressDialog.setTitle(getResources().getString(R.string.LoadMessage));
                progressDialog.show();
                new FetchEpisodesAsyncTask(this).execute(getResources().getString(R.string.PodcastUrl));

                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.fragmentHostLinearLayout, new PodcastMediaFragment(), getResources().getString(R.string.PodcastPlayer))
                        .commit();
                getFragmentManager().executePendingTransactions();
                podcastMediaFragment = (PodcastMediaFragment) getFragmentManager().findFragmentByTag(getResources().getString(R.string.PodcastPlayer));
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .hide(podcastMediaFragment)
                        .commit();

                findViewById(R.id.refreshIcon).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (currentLayout) {
                            case R.layout.podcast_grid_item:
                                currentLayout = R.layout.podcast_row_item;
                                setRecycleView(new LinearLayoutManager(MainActivity.this));
                                break;
                            case R.layout.podcast_row_item:
                                GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
                                currentLayout = R.layout.podcast_grid_item;
                                setRecycleView(gridLayoutManager);
                                break;
                        }
                    }
                });
            } else {
                Toast.makeText(this, getResources().getString(R.string.NotConnectedToInternet), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void fetchEpisodes(ArrayList<TedRadioPodcast> episodesList) {
        progressDialog.dismiss();
        try {
            this.episodesList = episodesList;
            Collections.sort(this.episodesList);
            setRecycleView(new LinearLayoutManager(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRecycleView(RecyclerView.LayoutManager layoutManager) {
        customAdapter = new PodcastRecyclerViewAdapter(this, this.episodesList, currentLayout);
        recycleListView.setAdapter(customAdapter);
        recycleListView.setLayoutManager(layoutManager);
    }

    @Override
    public void PlayMedia(TedRadioPodcast tedRadioPodcast, boolean navigateToPlayActivity) {
        try {
            if (navigateToPlayActivity) {
                Intent playIntent = new Intent(this, PlayActivity.class);
                playIntent.putExtra(getResources().getString(R.string.PlayActivity), tedRadioPodcast);
                startActivity(playIntent);
            } else {
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .show(podcastMediaFragment)
                        .commit();
                podcastMediaFragment.playMedia(tedRadioPodcast, true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onStopMedia() {
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .hide(podcastMediaFragment)
                .commit();
    }

    private boolean isConnectedOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
}
