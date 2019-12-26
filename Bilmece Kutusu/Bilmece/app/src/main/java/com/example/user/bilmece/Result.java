package com.example.user.bilmece;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class Result extends AppCompatActivity {

    MediaPlayer gameOverSound;
    // for game-over sound effect

    ImageView imageGameOver;
    // the game over image on the screen

    ImageView buttonReplay;
    // the replay button

    boolean canClick;
    // whether the user can click the replay button or not

    MediaPlayer touchSound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        SetStatusBarColor();

        gameOverSound = MediaPlayer.create(Result.this,R.raw.game_over);
        /* make the sound after 200 miliseconds */
        new Handler().postDelayed(new Runnable() {
            public void run() {
                gameOverSound.start();
            }
        }, 200);

        // Initialize the touch sound for replay button
        touchSound = MediaPlayer.create(Result.this, R.raw.touch_sound);

        buttonReplay = (ImageView) findViewById(R.id.buttonReplay);
        imageGameOver = (ImageView) findViewById(R.id.imageGameOver);

        // First Initially HIDE GAME OVER AND REPLAY BUTTON

        imageGameOver.setAlpha(0f);
        buttonReplay.setVisibility(View.INVISIBLE);

        // initally the user cant click the replay button
        // because it's invisible

        canClick = false;

        imageGameOver.animate().alpha(1).setDuration(3000);
        // ANIMATE THE GAME OVER IMAGE
        // make it visible in 2 seconds

        // Make the replay button CLICKABLE AND VISIBLE AFTER 3 SECONDS

        new Handler().postDelayed(new Runnable() {
            public void run() {
                canClick = true;
                buttonReplay.setVisibility(View.VISIBLE);
            }
        }, 2200);

        // SET THE ON-CLICK LISTENER
        buttonReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(canClick == false) return;
                // PROTECTION MECHANISM
                // if the user is allowed to click

                touchSound.start(); // make the touch sound

                Intent myIntent = new Intent(Result.this, Game.class);

                finish(); // destroy this activity and then go to the game
                startActivity(myIntent);
                return;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        gameOverSound.release();
        // release the media-player for game-over sound

        touchSound.release();
        // release the touch sound

    }

    private void SetStatusBarColor(){

        // UPDATE the status bar color with blue
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.gameOverBackground));
        }
    }
}
