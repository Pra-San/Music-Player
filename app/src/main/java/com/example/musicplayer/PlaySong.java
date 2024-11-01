package com.example.musicplayer;

import static com.example.musicplayer.MainActivity.getSongs;
import static com.example.musicplayer.MainActivity.Songs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import android.os.Handler;

public class PlaySong extends AppCompatActivity {
    private TextView textView;
    private ImageButton previous;
    private ImageButton play_pause;
    private ImageButton next;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private MusicService musicService;
    private boolean isBound = false;
    private ArrayList<File> songList;
    private int songPosition;
    private Handler seekBarHandler = new Handler();

    private final BroadcastReceiver songChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MusicService.ACTION_SONG_CHANGED.equals(intent.getAction())) {
                String songName = intent.getStringExtra(MusicService.EXTRA_SONG_NAME);
                if (songName != null) {
                    textView.setText(songName);
                    seekBar.setMax(musicService.getDuration());
                    updateSeekBar();
                    play_pause.setImageResource(R.drawable.ic_baseline_pause_24);
                }
            } else if (MusicService.ACTION_STATUS_CHANGED.equals(intent.getAction())) {
                boolean isPlaying = intent.getBooleanExtra(MusicService.EXTRA_IS_PLAYING, false);
                play_pause.setImageResource(isPlaying ? R.drawable.ic_baseline_pause_24 : R.drawable.ic_baseline_play_arrow_24);
                if (isPlaying) {
                    updateSeekBar();
                } else {
                    seekBarHandler.removeCallbacksAndMessages(null);  // Stop updates when paused
                }
            }
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            isBound = true;
            seekBar.setMax(musicService.getDuration());
            updateSeekBar();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        textView = findViewById(R.id.mp_song_text);
        previous = findViewById(R.id.previous);
        play_pause = findViewById(R.id.play_pause);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);


        Intent intent = getIntent();
        songList = (ArrayList<File>) intent.getSerializableExtra("songList");
        songPosition = intent.getIntExtra("songPosition", 0);

        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("songList", songList);
        serviceIntent.putExtra("position", songPosition);
        startService(serviceIntent);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);


        previous.setOnClickListener(view -> {
            if (isBound) {
                musicService.playPrevious();
            }
        });

        play_pause.setOnClickListener(view -> {
            if (isBound) {
                musicService.pauseOrResume();
                play_pause.setImageResource(musicService.isPlaying() ? R.drawable.ic_baseline_pause_24 : R.drawable.ic_baseline_play_arrow_24);
                if (musicService.isPlaying()) {
                    updateSeekBar();  // Resume updating seek bar when playback resumes
                }
            }
        });

        next.setOnClickListener(view -> {
            if (isBound) {
                musicService.playNext();
            }
        });


        //SeekBar Listener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && isBound) {
                    musicService.seekTo(progress);
                }
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicService.ACTION_SONG_CHANGED);
        filter.addAction(MusicService.ACTION_STATUS_CHANGED);
        LocalBroadcastManager.getInstance(this).registerReceiver(songChangedReceiver, filter);
    }

    private void updateSeekBar() {
        if (isBound && musicService.isPlaying()) {
            seekBar.setProgress(musicService.getCurrentPosition());
            seekBarHandler.postDelayed(this::updateSeekBar, 1000);  // Schedule next update only if playing
        } else {
            seekBarHandler.removeCallbacksAndMessages(null);  // Clear updates when stopped or paused
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
        seekBarHandler.removeCallbacksAndMessages(null);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(songChangedReceiver);
    }

}