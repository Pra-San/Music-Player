package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
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

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        //Permission from User for External Storage Read Access using Dexter Library
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    //If Permission granted
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Toast.makeText(MainActivity.this, "External Storage Permission Given", Toast.LENGTH_SHORT).show();

                        //Getting a List of Songs in the form of a String

                        ArrayList<File> Songs = getSongs(Environment.getExternalStorageDirectory());
                        String[] mySongs = new String[Songs.size()];
                        for(int i = 0;i<Songs.size();i++){
                            mySongs[i] = Songs.get(i).getName().replace(".mp3", "");
                        }

                        //Setting Recycler View

                        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                        CustomAdapter adapter = new CustomAdapter(mySongs);
                        recyclerView.setAdapter(adapter);

                        //Alternate way in ListView
//                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,mySongs);
//                        listView.setAdapter(adapter);



                    }

                    //If Permission is not granted
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    //Permission not granted and App reopened
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }

    //Method that returns an ArrayList of Files whose name ends with .mp3 and does not start with a .
    private ArrayList<File> getSongs(File file){
        ArrayList<File> arrayList = new ArrayList<>();
        File listFiles[] = file.listFiles();
        if(listFiles!=null){
            for(File curr:listFiles){
                if(!curr.isHidden() && curr.isDirectory()){
                    arrayList.addAll(getSongs(curr));
                }
                else{
                    if(curr.getName().endsWith(".mp3") && !curr.getName().startsWith(".")){
                        arrayList.add(curr);
                    }
                }
            }
        }
        return arrayList;
    }
}