package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class subactivity_SPLASH extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subsplash);

        // 스플래시 화면을 2초간 보여주고 로그인 화면으로 넘어가기
        int SPLASH_DISPLAY_LENGTH = 4000; // 스플래시 화면 지속 시간 (밀리초 단위)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 인텐트를 사용하여 로그인 화면으로 전환
                Intent intent = new Intent(subactivity_SPLASH.this, subactivity_LOGIN.class);
                startActivity(intent);

                // 스플래시 액티비티 종료
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}