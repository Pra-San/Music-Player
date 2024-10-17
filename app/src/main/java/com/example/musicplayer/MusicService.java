package com.example.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class MusicService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Handle different actions from the widget
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case "PLAY_PAUSE":
                    handlePlayPause();
                    break;
                case "NEXT":
                    handleNext();
                    break;
                case "PREVIOUS":
                    handlePrevious();
                    break;
                case "FORWARD":
                    handleForward();
                    break;
                case "REWIND":
                    handleRewind();
                    break;
            }
        }
        return START_STICKY;
    }

    private void handlePlayPause() {
        // Implement play/pause logic here
        Toast.makeText(this, "Play/Pause clicked", Toast.LENGTH_SHORT).show();
    }

    private void handleNext() {
        // Implement skip to next logic here
        Toast.makeText(this, "Next clicked", Toast.LENGTH_SHORT).show();
    }

    private void handlePrevious() {
        // Implement skip to previous logic here
        Toast.makeText(this, "Previous clicked", Toast.LENGTH_SHORT).show();
    }

    private void handleForward() {
        // Implement fast forward logic here
        Toast.makeText(this, "Forward clicked", Toast.LENGTH_SHORT).show();
    }

    private void handleRewind() {
        // Implement rewind logic here
        Toast.makeText(this, "Rewind clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Not binding to this service
    }
}
