package com.example.basketballcourts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Splash extends AppCompatActivity {
    private ImageView img;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        img = findViewById(R.id.imageView);
        sp = getSharedPreferences("Users", MODE_PRIVATE);

        Thread splash = new Thread() {
            public void run() {
                MediaPlayer music = MediaPlayer.create(Splash.this, R.raw.sound2opening);
                try {
                    music.start();
                    Animation anim = AnimationUtils.loadAnimation(Splash.this, R.anim.tween);
                    img.startAnimation(anim);
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } finally {
                    if (music.isPlaying()) {
                        music.stop();
                    }
                    music.release();
                }

                boolean flag = sp.getBoolean("isChecked", false);
                Intent go;
                if (flag) {
                    go = new Intent(Splash.this, HomePage.class);
                } else {
                    go = new Intent(Splash.this, MainActivity.class);
                }
                startActivity(go);
                finish();
            }
        };
        splash.start();
    }
}
