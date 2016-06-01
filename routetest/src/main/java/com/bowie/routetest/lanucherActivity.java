package com.bowie.routetest;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.bowie.routetest.db.AssetsDatabaseManager;

public class lanucherActivity extends Activity {

    private Handler mHandler;
    private Runnable mRunnable;
    private static final long SPLASH_DURATION = 2500L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lanucher);
        start();
    }

    private void start(){
        mHandler = new Handler();
        mRunnable = new Runnable() {

            @Override
            public void run() {
                AssetsDatabaseManager.initManager(getApplication());
                AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                SQLiteDatabase db = mg.getDatabase("route.db");
                Intent loginIntent = new Intent(lanucherActivity.this,MainActivity.class);
                startActivity(loginIntent);
                finish();

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(mRunnable, SPLASH_DURATION);
    }
}
