package com.example.knightshift;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.knightshift.db.DatabaseHandle;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.Progress;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //----------------------------------------------------------------------

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        User user = DatabaseHandle.getUser();
        if (user == null || !user.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            startActivity(new Intent(this, DatabaseGui.class));
        }
        finish();

        //----------------------------------------------------------------------
    }
}
