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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class Result extends AppCompatActivity {

    MediaPlayer mediaplayer;
    // for sound

    TextView textViewScoreBoard;
    // Skor tablosu

    ImageView highScoreImage;
    // if high-score has achived, this image will pop


    MediaPlayer mpHighScoreSound;
    // for high score sound

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // UPDATE THE COLOR OF THE STATUS BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.statusBarBlue));
        }

        textViewScoreBoard = (TextView) findViewById(R.id.textViewSkorTable);
        // SKOR TABLOSU

        highScoreImage = (ImageView) findViewById(R.id.imageViewHighScore);
        // HIGH-SCORE image

        mediaplayer = MediaPlayer.create(Result.this,R.raw.jackpot);
        // sound of calculating the score
        mediaplayer.start();

        Intent intent = getIntent();

        final int skor = intent.getIntExtra("skor",0);
        // Learn the score from the game-activity


        // update the textView - but hid eit
        textViewScoreBoard.setText("SKOR\n\n"+ String.valueOf(skor));
        textViewScoreBoard.setAlpha(0f);

        final int HIGH_SCORE_LIMIT = 130;
        // This is the limit of the high-score
        // Minimum limit


        new Handler().postDelayed(new Runnable() {
            public void run() {
                // Reveal the score board after 1.2 seconds
                // while time elapses, user will hear the jackpot sound
                textViewScoreBoard.setAlpha(1);

                // Also check for high-score situation
                if( skor >= HIGH_SCORE_LIMIT){
                    new Handler().postDelayed(new Runnable() {
                        public void run() {

                            mpHighScoreSound = MediaPlayer.create(Result.this,R.raw.high_score_soundeffect);
                            // HIGH SCORE SOUND EFFECT after 1.2 seconds
                            mpHighScoreSound.start();
                            // and reveal the high-score image after some time
                            highScoreImage.setImageResource(R.drawable.high_score);



                        }
                    }, 1200);
                }
            }
        }, 1200);



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // release the mediaplayer for jackpot sound
        mediaplayer.release();
        // release the mediaplayer for high-score sound
        mpHighScoreSound.release();
    }
}
