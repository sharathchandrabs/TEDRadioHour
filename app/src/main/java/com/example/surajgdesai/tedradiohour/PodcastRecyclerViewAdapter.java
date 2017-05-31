package com.example.surajgdesai.tedradiohour;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Suraj G Desai on 2/20/2017.
 */

public class PodcastRecyclerViewAdapter extends RecyclerView.Adapter<PodcastRecyclerViewAdapter.ViewHolder> {

    Context gContext;
    List<TedRadioPodcast> gObjects;
    int currentLayout;
    IPlayPodcastMedia mainActivity;

    public PodcastRecyclerViewAdapter(Context context, List<TedRadioPodcast> objects, int layout) {
        this.gContext = context;
        this.gObjects = objects;
        this.currentLayout = layout;

        try {
            mainActivity = (IPlayPodcastMedia) context;
        } catch (ClassCastException ex) {
            Log.e("AdapterIntUnimplemented", ex.getMessage().toString());
            throw ex;
        }
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return gContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView episodeTitleText;
        public ImageView episodeImageLogo;
        public TextView episodeDateText;
        public LinearLayout playButtonLinearLayout;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            try {
                episodeTitleText = (TextView) itemView.findViewById(R.id.episodeDescriptionTextView);
                episodeTitleText.setMovementMethod(new ScrollingMovementMethod());
                episodeDateText = (TextView) itemView.findViewById(R.id.episodeDateTextView);
                episodeImageLogo = (ImageView) itemView.findViewById(R.id.episodeImageView);
                playButtonLinearLayout = (LinearLayout) itemView.findViewById(R.id.playButtonLinearLayout);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public PodcastRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            Context context = parent.getContext();
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View contextView = layoutInflater.inflate(currentLayout, parent, false);
            ViewHolder viewHolder = new ViewHolder(contextView);
            return viewHolder;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onBindViewHolder(PodcastRecyclerViewAdapter.ViewHolder holder, final int position) {
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd, MMM yyyy");
            TedRadioPodcast podcast = gObjects.get(position);
            TextView episodeText = holder.episodeTitleText;
            TextView episodeDateText = holder.episodeDateText;
            ImageView episodeImageLogo = holder.episodeImageLogo;
            LinearLayout playButtonLinearLayout = holder.playButtonLinearLayout;
            episodeText.setText(podcast.getTitle());
            episodeDateText.setText(podcast.getPublicationDate() == null ? "posted date unavailable" : "posted: " + dateFormat.format(podcast.getPublicationDate()).toString());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity.PlayMedia(gObjects.get(position), true);
                }
            });

            String logoUrl = podcast.getImageUrl() == null || podcast.getImageUrl() == "" ? "" : podcast.getImageUrl();

            if (logoUrl.equals("")) {
                Picasso.with(this.getContext())
                        .load(R.drawable.image)
                        .placeholder(R.drawable.image)
                        .error(R.drawable.image)
                        .into(episodeImageLogo);
            } else {
                Picasso.with(this.getContext())
                        .load(logoUrl)
                        .placeholder(R.drawable.image)
                        .error(R.drawable.image)
                        .into(episodeImageLogo);
            }

            playButtonLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity.PlayMedia(gObjects.get(position), false);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return gObjects.size();
    }

    public static interface IPlayPodcastMedia {
        public void PlayMedia(TedRadioPodcast tedRadioPodcast, boolean navigateToPlayActivity);
    }
}
