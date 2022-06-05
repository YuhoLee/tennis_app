package com.example.tennis_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class LoadingActivity extends AppCompatActivity {
    ImageView splashGif;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        splashGif = (ImageView) findViewById(R.id.loading_gif);
        Glide.with(this).load(R.raw.tennis_background).into(splashGif);

        startLoading();
    }

    public void startLoading(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getBaseContext(), LoginRegisterMain.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
