package com.bowie.routetest;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bowie.routetest.db.AssetsDatabaseManager;

public class lanucherActivity extends Activity {

    private EditText et_name,et_pwd;
    private Button btn_login;
    private Handler mHandler;
    private Runnable mRunnable;
    private static final long SPLASH_DURATION = 2500L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lanucher);
        findView();
        initDb();
    }

    private void findView(){
        et_name = (EditText)findViewById(R.id.et_name);
        et_pwd = (EditText)findViewById(R.id.et_pwd);
    }

    private void initDb(){
        AssetsDatabaseManager.initManager(getApplication());
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        SQLiteDatabase db = mg.getDatabase("route.db");
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

    public void actionLogin(View view){
        String name = et_name.getText().toString().trim();
        String pwd = et_pwd.getText().toString().trim();
        checkAccount(name,pwd);
    }

    private void checkAccount(String name,String pwd){
        if(name.length() > 6){
            String genPwd = name.substring(name.length() - 6);
            if(genPwd.equals(pwd)){
                Intent loginIntent = new Intent(lanucherActivity.this,MainActivity.class);
                startActivity(loginIntent);
                finish();
            }else{
                Toast.makeText(this, "密码不正确", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "账号不正确", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mHandler.postDelayed(mRunnable, SPLASH_DURATION);
    }
}
