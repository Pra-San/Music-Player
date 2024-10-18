package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    public static ArrayList<File> Songs;

    // Method that returns an ArrayList of Files whose name ends with .mp3 and does not start with a .
    public static ArrayList<File> getSongs(File file) {
        ArrayList<File> ans = new ArrayList<>();
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File curr : listFiles) {
                if (!curr.isHidden() && curr.isDirectory()) {
                    ans.addAll(getSongs(curr));
                } else {
                    if (curr.getName().endsWith(".mp3") && !curr.getName().startsWith(".")) {
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

        // Permission from User for External Storage Read Access using Dexter Library
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {

                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Songs = getSongs(Environment.getExternalStorageDirectory());

                        // Setting Recycler View
                        LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
                        recyclerView.setLayoutManager(llm);
                        CustomAdapter adapter = new CustomAdapter(Songs);
                        recyclerView.setAdapter(adapter);
                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                                llm.getOrientation());
                        recyclerView.addItemDecoration(dividerItemDecoration);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(MainActivity.this, "This Permission is required to proceed further", Toast.LENGTH_SHORT).show();
                    }

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
