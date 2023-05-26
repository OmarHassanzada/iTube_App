package com.example.myapplication;

import java.util.List;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;


public class MyPlaylistActivity extends AppCompatActivity {

    private ListView playlistLV;
    private ArrayAdapter<String> playlistAdapter;
    private long userID;
    private DatabaseHelper DBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_playlist);

        // Getting the user id
        userID = getIntent().getLongExtra("userId", -1);

        // creating the views
        playlistLV = findViewById(R.id.listViewPlaylist);

        //instantiating the database
        DBHelper = new DatabaseHelper(this);

        //retrieivng the playlist
        displayPlaylist();
    }

    private void displayPlaylist() {
        List<String> playlist = DBHelper.getPlaylist(userID);

        // Create adapter and set it to the ListView
        playlistAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, playlist);
        playlistLV.setAdapter(playlistAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the database connection
        DBHelper.close();
    }
}
