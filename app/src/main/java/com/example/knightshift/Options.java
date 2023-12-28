package com.example.knightshift;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Options extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
    }

    public void goToOption1(View v) {
        Intent i = new Intent(this, Analyse.class);
        startActivity(i);
    }

    public void goToOption2(View v) {
        Intent i = new Intent(this, Analyse.class);
        startActivity(i);
    }

    public void goToOption3(View v) {
        Intent i = new Intent(this, Analyse.class);
        startActivity(i);
    }

}