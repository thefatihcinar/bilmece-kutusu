package com.example.user.bilmece;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Opening extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    // for future technologies sound

    TextView tvFuture;
    // text view for the FUTURE text
    TextView tvTechnologies;
    // text view for the TECHNOLOGIES text


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        mediaPlayer = MediaPlayer.create(this, R.raw.future_technologies_introduction);
        mediaPlayer.start();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.newBlack));
        }

        tvFuture = (TextView) findViewById(R.id.tvFuture);
        tvTechnologies = (TextView) findViewById(R.id.tvTechnologies);

        tvFuture.animate().alphaBy(1).setDuration(2450);
        // first display the FUTURE text

        new Handler().postDelayed(new Runnable() {
            public void run() {
                // after 2.5 seconds
                // displaye TECHNOLOGIES text for 2 seconds
                tvTechnologies.animate().alphaBy(1).setDuration(2000);
            }
        }, 2500);


        new Handler().postDelayed(new Runnable() {
            public void run() {
                // after 5 seconds
                // go to the starting game activity
                Intent intent = new Intent(Opening.this, StartActivity.class);
                startActivity(intent);
            }
        }, 5000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mediaPlayer.release();
        // release the media player
    }
}
