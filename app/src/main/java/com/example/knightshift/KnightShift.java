package com.example.knightshift;

import android.app.Application;

import io.realm.Realm;

public class KnightShift extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
