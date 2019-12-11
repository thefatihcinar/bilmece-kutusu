package com.example.user.bilmece;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {

    MediaPlayer mediaplayer;
    // media player for the background music

    ImageView startGameButton;
    // start game button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mediaplayer = MediaPlayer.create(StartActivity.this, R.raw.background_music);
        mediaplayer.start();

        startGameButton = (ImageView) findViewById(R.id.imageViewStartGame);

        // UPDATE THE STATUS BAR COLOR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.statusBarBlue));
        }

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaPlayer touchSound = MediaPlayer.create(StartActivity.this, R.raw.touch_sound);
                touchSound.start();

                Intent intent = new Intent(StartActivity.this, Game.class);
                // the game is about to start  - going to the game activity

                touchSound.release();

                intent.putExtra("music_information",mediaplayer.getCurrentPosition());
                // PUT  where the background music left

                finish();
                // destroy this activity

                startActivity(intent);
            }
        });

    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
        mediaplayer.release();
        // release the media player at finish

    }
}
