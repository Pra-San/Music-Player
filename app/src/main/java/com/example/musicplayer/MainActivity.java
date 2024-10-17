package com.example.musicplayer;

import androidx.annotation.NonNull;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.DexterBuilder;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    //public static HashMap<String,String> mp;
    public static ArrayList<File> Songs;

    //Method that returns an ArrayList of Files whose name ends with .mp3 and does not start with a .
    public static ArrayList<File> getSongs(File file){
        ArrayList<File> ans = new ArrayList<>();
        File[] listFiles = file.listFiles();
        if(listFiles!=null){
            for(File curr:listFiles){
                if(!curr.isHidden() && curr.isDirectory()){
                    ans.addAll(getSongs(curr));
                }
                else{
                    if(curr.getName().endsWith(".mp3") && !curr.getName().startsWith(".")){
                        ans.add(curr);
                    }
                }
            }
        }
        return ans;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);

        //Permission from User for External Storage Read Access using Dexter Library
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    //If Permission granted


                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {


//                        StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                            List<StorageVolume> volumes = storageManager.getStorageVolumes();
//                            int i=0;
//                            while(i<volumes.size()){
//                                Log.d("MyLog",volumes.get(i).toString());
//                                i++;
//                            }
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                                if (volumes.get(0).getDirectory()==Environment.getExternalStorageDirectory()){
//                                    Log.d("MyLog","0Internal");
//                                }
//                                else{
//                                    Log.d("MyLog","0External");
//                                }
//                            }
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                                if (volumes.get(1).getDirectory()==Environment.getExternalStorageDirectory()){
//                                    Log.d("MyLog","1Internal");
//                                }
//                                else{
//                                    Log.d("MyLog","1External");
//                                }
//                            }
//                        }

                        //StorageManager storageManager = (StorageManager)getSystemService(STORAGE_SERVICE);
                        //List<StorageVolume> vol = storageManager.getStorageVolumes();



                        //Getting a List of Songs in the form of a String
                        //songs = getSongs(vol.get(1).getDirectory());
                        Songs = getSongs(Environment.getExternalStorageDirectory());


                        //Setting Recycler View
                        LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
                        recyclerView.setLayoutManager(llm);
                        CustomAdapter adapter = new CustomAdapter(Songs);
                        recyclerView.setAdapter(adapter);
                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                                llm.getOrientation());
                        recyclerView.addItemDecoration(dividerItemDecoration);
                        //recyclerView.se



                    }

                    //If Permission is not granted
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(MainActivity.this, "This Permission is required to proceed further", Toast.LENGTH_SHORT).show();
                    }

                    //Permission not granted and App reopened
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }



    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
//        Intent backToMain = getIntent();
//        int song_position = backToMain.getIntExtra("com.example.musicplayer.PlaySong.SongPosition", 0);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}



