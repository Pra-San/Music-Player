package com.example.musicplayer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.media.app.NotificationCompat.MediaStyle;

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
    public static final String ACTION_PLAY_PAUSE = "com.example.musicplayer.ACTION_PLAY_PAUSE";
    public static final String ACTION_NEXT = "com.example.musicplayer.ACTION_NEXT";
    public static final String ACTION_PREVIOUS = "com.example.musicplayer.ACTION_PREVIOUS";
    public static final String ACTION_STATUS_CHANGED = "com.example.musicplayer.ACTION_STATUS_CHANGED";
    public static final String EXTRA_IS_PLAYING = "com.example.musicplayer.EXTRA_IS_PLAYING";

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case ACTION_PLAY_PAUSE:
                        pauseOrResume();
                        break;
                    case ACTION_NEXT:
                        playNext();
                        break;
                    case ACTION_PREVIOUS:
                        playPrevious();
                        break;
                }
            } else if (intent.hasExtra("songList") && intent.hasExtra("position")) {
                songList = (ArrayList<File>) intent.getSerializableExtra("songList");
                currentPosition = intent.getIntExtra("position", 0);
                playSong();
            }
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
            sendSongChangedBroadcast();
            showNotification();
        });
        mediaPlayer.setOnCompletionListener(mp -> playNext());
    }

    private void sendSongChangedBroadcast() {
        Intent intent = new Intent(ACTION_SONG_CHANGED);
        intent.putExtra(EXTRA_SONG_NAME, songList.get(currentPosition).getName().replace(".mp3", ""));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendStatusChangedBroadcast() {
        Intent intent = new Intent(ACTION_STATUS_CHANGED);
        intent.putExtra(EXTRA_IS_PLAYING, isPlaying);
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
            sendStatusChangedBroadcast();
            showNotification();
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

    private void showNotification() {
        Intent playPauseIntent = new Intent(this, MusicService.class).setAction(ACTION_PLAY_PAUSE);
        PendingIntent playPausePendingIntent = PendingIntent.getService(this, 0, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent nextIntent = new Intent(this, MusicService.class).setAction(ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getService(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent previousIntent = new Intent(this, MusicService.class).setAction(ACTION_PREVIOUS);
        PendingIntent previousPendingIntent = PendingIntent.getService(this, 0, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "MusicPlayerChannel")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Playing: " + songList.get(currentPosition).getName().replace(".mp3", ""))
                .addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", previousPendingIntent)
                .addAction(isPlaying ? R.drawable.ic_baseline_pause_24 : R.drawable.ic_baseline_play_arrow_24, "Play/Pause", playPausePendingIntent)
                .addAction(R.drawable.ic_baseline_skip_next_24, "Next", nextPendingIntent)
                .setStyle(new MediaStyle().setShowActionsInCompactView(1))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(isPlaying);

        startForeground(1, notificationBuilder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("MusicPlayerChannel", "Music Player", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Music Player Notifications");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
