package com.example.musicplayer;

import static com.example.musicplayer.MainActivity.getSongs;
import static com.example.musicplayer.MainActivity.Songs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class PlaySong extends AppCompatActivity {
    private TextView textView;
    private ImageButton previous;
    private ImageButton play_pause;
    private ImageButton next;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    //private ArrayList<File> Songs;
    private static boolean isPlaying = false;
    private Thread UpdateSeek;
    int[] song_position = new int[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        Intent intent = getIntent();
        //Bundle bundle = intent.getExtras();
        //Songs = (ArrayList) bundle.getParcelableArrayList("com.example.musicplayer.CustomAdapter.SongList");
        String song_name = intent.getStringExtra("com.example.musicplayer.CustomAdapter.SongName");
        song_position = new int[]{intent.getIntExtra("com.example.musicplayer.CustomAdapter.SongPosition", 0)};
        textView = findViewById(R.id.mp_song_text);
        previous = findViewById(R.id.previous);
        play_pause = findViewById(R.id.play_pause);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);


        //Added for Marquee
        textView.setSelected(true);
        textView.setText(song_name);

        //On Click Listeners for Image Buttons
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(song_position[0] !=0){
                    song_position[0]--;
                }
                else{
                    song_position[0]=Songs.size()-1;
                }
                Uri uri = Uri.parse(Songs.get(song_position[0]).toString());
                textView.setText(Songs.get(song_position[0]).getName());
                mediaPlayer = MediaPlayer.create(PlaySong.this, uri);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                    }
                });
                isPlaying = true;
                seekBar.setProgress(0);
                play_pause.setImageResource(R.drawable.ic_baseline_pause_24);
            }
        });

        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    isPlaying = false;
                    play_pause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                }
                else{
                    mediaPlayer.start();
                    isPlaying = true;
                    play_pause.setImageResource(R.drawable.ic_baseline_pause_24);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(song_position[0] !=Songs.size()-1){
                    song_position[0]++;
                }
                else{
                    song_position[0]=0;
                }
                Uri uri = Uri.parse(Songs.get(song_position[0]).toString());
                textView.setText(Songs.get(song_position[0]).getName().replace(".mp3", ""));
                mediaPlayer = MediaPlayer.create(PlaySong.this, uri);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                    }
                });
                isPlaying = true;
                seekBar.setProgress(0);
                play_pause.setImageResource(R.drawable.ic_baseline_pause_24);

            }
        });


        //SeekBar Listener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b && isPlaying){
                    mediaPlayer.seekTo(i);

                }
                else if(b && !mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(i);

                    mediaPlayer.pause();
                    isPlaying = false;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(isPlaying) {
                    mediaPlayer.pause();
                    play_pause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(isPlaying){
                    mediaPlayer.start();
                    play_pause.setImageResource(R.drawable.ic_baseline_pause_24);
                }
                else{
                    mediaPlayer.pause();
                    play_pause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                }

            }
        });

        //Starts Playing as soon as a song is clicked
        Uri uri = Uri.parse(Songs.get(song_position[0]).toString());
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
        isPlaying =true;
        seekBar.setMax(mediaPlayer.getDuration());

        //SeekBar Updater
        UpdateSeek = new Thread(){
            @Override
            public void run() {
                int CurrentPosition = 0;
                try{
                    while (mediaPlayer!=null && CurrentPosition < mediaPlayer.getDuration()) {
                        try {
                            if (mediaPlayer != null & mediaPlayer.isPlaying())
                                CurrentPosition = mediaPlayer.getCurrentPosition();
                            if (seekBar != null)
                                seekBar.setProgress(CurrentPosition);
                            Thread.sleep(1000);
                        }
                        catch (NullPointerException e){
                            e.printStackTrace();
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

//                    try {
//                        synchronized (this) {
//                            this.wait();
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

                }
                catch(IllegalStateException e){
                    e.printStackTrace();
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        };

        UpdateSeek.start();


    }
    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isPlaying = false;
        UpdateSeek.interrupt();
        UpdateSeek = null;
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
    }

}