package com.example.user.bilmece;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class Win extends AppCompatActivity {

    ImageView imageCongrats;
    // the congrats image on the screen

    ImageView buttonReplay;
    // the replay button

    boolean canClick;
    // whether the user can click the replay button or not

    MediaPlayer touchSound;

    MediaPlayer winningSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);

        SetStatusBarColor();

        // Initialize the touch sound for replay button
        touchSound = MediaPlayer.create(Win.this, R.raw.touch_sound);

        // Initialize the winning sound
        // AND PLAY

        winningSound = MediaPlayer.create(Win.this,R.raw.win_soundeffect);
        winningSound.start();

        buttonReplay = (ImageView) findViewById(R.id.buttonReplay2);
        imageCongrats = (ImageView) findViewById(R.id.imageCongrats);

        // First Initially HIDE GAME OVER AND REPLAY BUTTON

        imageCongrats.setAlpha(0f);
        buttonReplay.setVisibility(View.INVISIBLE);

        // initally the user cant click the replay button
        // because it's invisible

        canClick = false;

        imageCongrats.animate().alpha(1).setDuration(2500);
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

                Intent myIntent = new Intent(Win.this, Game.class);

                finish(); // destroy this activity and then go to the game
                startActivity(myIntent);
                return;
            }
        });



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        touchSound.release();
        winningSound.release();
    }


    private void SetStatusBarColor(){

        // UPDATE the status bar color with blue
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.winScreenBackground));
        }

    }
}
