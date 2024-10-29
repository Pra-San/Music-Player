package com.example.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.File;
import java.util.ArrayList;

public class MusicService extends Service {

    private MediaPlayer mediaPlayer;
    private ArrayList<File> songList;
    private int currentPosition = 0;
    private boolean isPlaying = false;
    private final IBinder binder = new MusicBinder();

    public static final String ACTION_SONG_CHANGED = "com.example.musicplayer.ACTION_SONG_CHANGED";
    public static final String EXTRA_SONG_NAME = "com.example.musicplayer.EXTRA_SONG_NAME";

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("songList") && intent.hasExtra("position")) {
            songList = (ArrayList<File>) intent.getSerializableExtra("songList");
            currentPosition = intent.getIntExtra("position", 0);
            playSong();
        }
        return START_STICKY;
    }

    private void playSong() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        Uri uri = Uri.parse(songList.get(currentPosition).toString());
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.setOnPreparedListener(mp -> {
            mp.start();
            isPlaying = true;
            sendSongChangedBroadcast(); // Notify that a new song has started
        });
        mediaPlayer.setOnCompletionListener(mp -> playNext()); // Auto-play next song on completion
    }

    private void sendSongChangedBroadcast() {
        Intent intent = new Intent(ACTION_SONG_CHANGED);
        intent.putExtra(EXTRA_SONG_NAME, songList.get(currentPosition).getName().replace(".mp3", ""));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void pauseOrResume() {
        if (mediaPlayer != null) {
            if (isPlaying) {
                mediaPlayer.pause();
                isPlaying = false;
            } else {
                mediaPlayer.start();
                isPlaying = true;
            }
        }
    }

    public void playNext() {
        if (currentPosition < songList.size() - 1) {
            currentPosition++;
        } else {
            currentPosition = 0;
        }
        playSong();
    }

    public void playPrevious() {
        if (currentPosition > 0) {
            currentPosition--;
        } else {
            currentPosition = songList.size() - 1;
        }
        playSong();
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public int getDuration() {
        return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
    }

    public int getCurrentPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }

    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        super.onDestroy();
    }
}
