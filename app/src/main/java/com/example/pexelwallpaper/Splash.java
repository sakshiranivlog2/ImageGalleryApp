package com.example.pexelwallpaper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends AppCompatActivity {

    Handler h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash );

        getSupportActionBar().hide();

        h = new Handler();
        h.postDelayed( new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(Splash.this, MainActivity.class );
                startActivity( i );
                finish();

            }
        }, 2500 );



    }
}
