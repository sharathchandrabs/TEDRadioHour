package com.example.surajgdesai.tedradiohour;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.IOException;

public class PodcastMediaFragment extends Fragment implements View.OnTouchListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {
    private SeekBar seekBarProgress;
    private MediaPlayer mediaPlayer;
    private ImageView playPauseImageView;
    private final Handler handler = new Handler();
    private int mediaFileLengthInMilliseconds;
    IUpdateMediaStatus mainActivity;

    public static interface IUpdateMediaStatus {
        public void onStopMedia();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedLayout = inflater.inflate(R.layout.fragment_podcast_media, container, false);
        try {
            seekBarProgress = (SeekBar) inflatedLayout.findViewById(R.id.mediaSeeker);
            playPauseImageView = (ImageView) inflatedLayout.findViewById(R.id.playPauseImageView);
            seekBarProgress.setMax(99);
            seekBarProgress.setOnTouchListener(this);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnCompletionListener(this);
            playPauseImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        primarySeekBarProgressUpdater();
                        playPauseImageView.setImageResource(R.drawable.ic_action_pause);
                    } else {
                        mediaPlayer.pause();
                        playPauseImageView.setImageResource(R.drawable.ic_action_play);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inflatedLayout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mainActivity = (IUpdateMediaStatus) context;
        } catch (ClassCastException ex) {
            Log.e("AdapterIntUnimplemented", ex.getMessage().toString());
            throw new ClassCastException(context.toString() + "should implement IUpdateMediaStatus");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void playMedia(TedRadioPodcast radioPodcast, boolean startPlaying) {
        stopMedia();
        try {
            mediaPlayer.setDataSource(radioPodcast.getMp3Url());
            mediaPlayer.prepare();
            mediaFileLengthInMilliseconds = mediaPlayer.getDuration();
            if (startPlaying) {
                playPauseImageView.setImageResource(R.drawable.ic_action_pause);
                mediaPlayer.start();
                primarySeekBarProgressUpdater();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopMedia() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        seekBarProgress.setProgress(0);
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBarProgress.setSecondaryProgress(percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        handler.removeCallbacksAndMessages(null);
        playPauseImageView.setImageResource(R.drawable.ic_action_play);
        mainActivity.onStopMedia();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.mediaSeeker) {
            if (mediaPlayer.isPlaying()) {
                SeekBar sb = (SeekBar) v;
                int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
                mediaPlayer.seekTo(playPositionInMillisecconds);
            }
        }
        return false;
    }

    private void primarySeekBarProgressUpdater() {
        seekBarProgress.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaFileLengthInMilliseconds) * 100));
        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    primarySeekBarProgressUpdater();
                }
            };

            handler.postDelayed(notification, 1000);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMedia();
    }

    @Override
    public void onPause() {
        super.onPause();
        mediaPlayer.pause();
        playPauseImageView.setImageResource(R.drawable.ic_action_play);
    }
}
