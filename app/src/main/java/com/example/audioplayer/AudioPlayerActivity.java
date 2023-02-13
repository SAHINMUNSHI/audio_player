package com.example.audioplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AudioPlayerActivity extends AppCompatActivity {
    TextView titleTV, currentTimeTV, totalTimeTV;
    SeekBar seekBar;
    ImageView audioImageBig, previousBtn, nextBtn, pausePlayBtn;
    ArrayList<AudioModel> audioList;
    AudioModel currentAudio;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    int x = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        titleTV = findViewById(R.id.audio_title);
        currentTimeTV = findViewById(R.id.current_time);
        totalTimeTV = findViewById(R.id.total_time);
        audioImageBig = findViewById(R.id.audio_icon_big);
        seekBar = findViewById(R.id.seek_bar);
        previousBtn =findViewById(R.id.previous);
        nextBtn = findViewById(R.id.next);
        pausePlayBtn = findViewById(R.id.pause_play);

        titleTV.setSelected(true);

        audioList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("List");

        setResourcesWithAudio();

        AudioPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTV.setText(convertMillisToMints(mediaPlayer.getCurrentPosition() + ""));
                    if (mediaPlayer.isPlaying()) {
                        pausePlayBtn.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                        audioImageBig.setRotation(x++);
                    } else {
                        pausePlayBtn.setImageResource(R.drawable.baseline_play_circle_outline_24);
                        audioImageBig.setRotation(0);
                    }
                }
                new Handler().postDelayed(this, 100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mediaPlayer != null && b) {
                    mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
    void setResourcesWithAudio() {
        currentAudio = audioList.get(MyMediaPlayer.currentIndex);

        titleTV.setText(currentAudio.getTitle());
        totalTimeTV.setText(convertMillisToMints(currentAudio.getDuration()));

        pausePlayBtn.setOnClickListener(view -> pausePlay());
        nextBtn.setOnClickListener(view -> playNextAudio());
        previousBtn.setOnClickListener(view -> playPreviousAudio());

        playAudio();
    }
    private void playAudio() {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentAudio.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void playNextAudio() {
        if (MyMediaPlayer.currentIndex == audioList.size() - 1)
            return;
        MyMediaPlayer.currentIndex += 1;
        mediaPlayer.reset();
        setResourcesWithAudio();
    }
    private  void playPreviousAudio() {
        if (MyMediaPlayer.currentIndex == 0)
            return;
        MyMediaPlayer.currentIndex -= 1;
        mediaPlayer.reset();
        setResourcesWithAudio();
    }
    private void pausePlay() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();
    }
    @SuppressLint("DefaultLocale")
    public static String convertMillisToMints(String duration) {
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
}