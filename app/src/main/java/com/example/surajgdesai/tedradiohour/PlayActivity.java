package com.example.surajgdesai.tedradiohour;

import android.app.ProgressDialog;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

public class PlayActivity extends AppCompatActivity implements PodcastMediaFragment.IUpdateMediaStatus {

    TedRadioPodcast tedRadioPodcast = null;
    PodcastMediaFragment podcastMediaFragment = null;
    TextView titleTextView, descriptionTextView, dateTextView, playdurationTextView;
    ImageView playepisodeImageView;
    SimpleDateFormat dateFormat;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            try {
                dateFormat = new SimpleDateFormat(getResources().getString(R.string.PlayActivityTimeFormat));
                tedRadioPodcast = (TedRadioPodcast) extras.getSerializable(getResources().getString(R.string.PlayActivity));
                progressDialog = new ProgressDialog(this);
                progressDialog.setCancelable(false);
                progressDialog.setTitle(getResources().getString(R.string.LoadingEpisode) + tedRadioPodcast.getTitle());
                progressDialog.show();
                podcastMediaFragment = (PodcastMediaFragment) getFragmentManager().findFragmentById(R.id.staticFragment);
                Toolbar myToolbar = (Toolbar) findViewById(R.id.playActivitytoolbar);
                setSupportActionBar(myToolbar);
                TextView actionBarTitle = (TextView) findViewById(R.id.toolbarTitle);
                actionBarTitle.setText(getResources().getString(R.string.play_activity));
                myToolbar.findViewById(R.id.refreshIcon).setVisibility(View.INVISIBLE);
                titleTextView = (TextView) findViewById(R.id.titleTextView);
                titleTextView.setText(tedRadioPodcast.getTitle());
                descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
                descriptionTextView.setText(Html.fromHtml(getResources().getString(R.string.DescriptionLabel) + tedRadioPodcast.getDescription()));
                descriptionTextView.setMovementMethod(new ScrollingMovementMethod());
                dateTextView = (TextView) findViewById(R.id.dateTextView);
                dateTextView.setText(getResources().getString(R.string.PublicationLabel) + dateFormat.format(tedRadioPodcast.getPublicationDate()));
                playdurationTextView = (TextView) findViewById(R.id.playdurationTextView);

                String sequenceCaptureTime = "";

                long longVal = tedRadioPodcast.getDuration();
                int hours = (int) longVal / 3600;
                int remainder = (int) longVal - hours * 3600;
                int mins = remainder / 60;
                remainder = remainder - mins * 60;
                int secs = remainder;

                sequenceCaptureTime = getResources().getString(R.string.DurationLabel) + hours + getResources().getString(R.string.TimeSeparator) + mins + getResources().getString(R.string.TimeSeparator) + secs;

                playdurationTextView.setText(sequenceCaptureTime);
                playepisodeImageView = (ImageView) findViewById(R.id.playepisodeImageView);

                String logoUrl = tedRadioPodcast.getImageUrl() == null || tedRadioPodcast.getImageUrl() == "" ? "" : tedRadioPodcast.getImageUrl();

                if (logoUrl.equals("")) {
                    Picasso.with(this)
                            .load(R.drawable.image)
                            .placeholder(R.drawable.image)
                            .error(R.drawable.image)
                            .into(playepisodeImageView);
                } else {
                    Picasso.with(this)
                            .load(logoUrl)
                            .placeholder(R.drawable.image)
                            .error(R.drawable.image)
                            .into(playepisodeImageView);
                }

                podcastMediaFragment.playMedia(tedRadioPodcast, false);

                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.NoMessageFromMainActivity), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onStopMedia() {
        podcastMediaFragment.playMedia(tedRadioPodcast, false);
    }
}
