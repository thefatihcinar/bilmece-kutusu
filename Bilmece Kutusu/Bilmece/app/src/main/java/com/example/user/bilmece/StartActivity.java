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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    // for introduction sound effect

    MediaPlayer touchSound;

    ImageView startGameButton;
    // start game button

    TextView tvBilmeceKutusu;

    ImageView theBox;

    boolean Clickable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        SetStatusBarColor();

        touchSound = MediaPlayer.create(StartActivity.this, R.raw.touch_sound);
        mediaPlayer = MediaPlayer.create(StartActivity.this,R.raw.awesome_introduction);

        startGameButton = (ImageView) findViewById(R.id.imageViewStartGame);
        tvBilmeceKutusu = (TextView) findViewById(R.id.tvBilmeceKutusu);
        theBox = (ImageView) findViewById(R.id.imageViewTheBox);

        startGameButton.setVisibility(View.INVISIBLE);
        tvBilmeceKutusu.setAlpha(0f);
        theBox.setAlpha(0f);

        // initially the user can't click to the start button
        Clickable = false;

        // make the introduction sound
        mediaPlayer.start();

        // ANIMATIONS
        tvBilmeceKutusu.animate().alpha(1).setDuration(1700);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                theBox.animate().alpha(1).setDuration(1000);
            }
        }, 1000);


        new Handler().postDelayed(new Runnable() {
            public void run() {
                Clickable = true;
                startGameButton.setVisibility(View.VISIBLE);
            }
        }, 2600);


        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Clickable == false) return;

                touchSound.start();

                Intent intent = new Intent(StartActivity.this, Game.class);
                // the game is about to start  - going to the game activity

                finish();
                // destroy this activity

                startActivity(intent);
            }
        });



    }

    private void SetStatusBarColor(){

        // UPDATE the status bar color with blue
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.statusBarBlue));
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        touchSound.release();

        mediaPlayer.release();


    }
}
