package com.iridescent07.smart_citizens_feedback;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    private static int TIME_OUT = 5000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(()->{
            Intent i = new Intent(MainActivity.this, Login.class);
            startActivity(i);
            finish();
        },TIME_OUT);
    }
}
