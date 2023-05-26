package com.example.myapplication;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;



public class HomeActivity extends AppCompatActivity {

    private EditText linkET;
    private Button playButt;
    private Button addToPlaylistButt;
    private Button myPlaylistButt;
    private long userID;

    private YouTubePlayerView youTubePV;
    private YouTubePlayer youTubePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get user ID from intent
        userID = getIntent().getLongExtra("userId", -1);

        youTubePV = findViewById(R.id.youtube_player_view);

        // Initialize views
        linkET = findViewById(R.id.editTextYouTubeLink);
        playButt = findViewById(R.id.buttonPlay);
        addToPlaylistButt = findViewById(R.id.buttonAddToPlaylist);
        myPlaylistButt = findViewById(R.id.buttonMyPlaylist);

        // when play button is pressed
        playButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String videoLink = linkET.getText().toString();
                if (!videoLink.isEmpty()) {
                    Uri webUri = Uri.parse(videoLink);
                    Intent intent = new Intent(Intent.ACTION_VIEW, webUri);
                    intent.setPackage("com.android.chrome"); // Specify the package name of the web browser you want to use
                    startActivity(intent);
                }
            }
        });



        // Set click listener for add to playlist button
        addToPlaylistButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToPlaylist();
            }
        });

        // Set click listener for my playlist button
        myPlaylistButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, MyPlaylistActivity.class);
                intent.putExtra("userId", userID);
                startActivity(intent);
            }
        });

        youTubePV.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer initializedYouTubePlayer) {
                youTubePlayer = initializedYouTubePlayer;
                playButt.setEnabled(true); // Enable the play button when the player is ready
            }
        });


        getLifecycle().addObserver(youTubePV);
    }

    private void addToPlaylist() {
        String videoUrl = linkET.getText().toString();

        // Validate video URL
        if (!isValidUrl(videoUrl)) {
            Toast.makeText(this, "Invalid YouTube link", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        boolean isAdded = databaseHelper.addLinkToPlaylist(userID, videoUrl);
        databaseHelper.close();

        if (isAdded) {
            Toast.makeText(this, "Link added to playlist", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to add link to playlist", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean isValidUrl(String url) {
        // For simplicity, let's assume any non-empty URL is valid for now
        return !url.isEmpty();
    }
}
