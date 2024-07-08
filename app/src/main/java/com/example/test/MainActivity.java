package com.example.test;

import static com.example.test.R.id.btnStopService;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    // 버튼 변수 선언
    Button startButton, settingButton, exitButton,ServiceStart,ServiceStop;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // findViewById를 통해 버튼 인스턴스 초기화
        startButton = findViewById(R.id.btnStart);
        settingButton = findViewById(R.id.btnSettings);
        exitButton = findViewById(R.id.btnExit);
        ServiceStart =findViewById(R.id.btnStartService);
        ServiceStop =findViewById(R.id.btnStopService);

        // START 버튼 이벤트 리스너 설정
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, subactivity_START.class);
                startActivity(intent);
            }
        });

        // SETTINGS 버튼 이벤트 리스너 설정
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, subactivity_SETTINGS.class);
                startActivity(intent);
            }
        });

        // EXIT 버튼 이벤트 리스너 설정
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 예시로 subactivity_SETTINGS를 시작하고 있으나,
                // 실제로는 앱 종료 또는 해당 기능에 맞는 액티비티를 시작해야 합니다.
                Intent intent = new Intent(MainActivity.this, subactivity_SETTINGS.class);
                startActivity(intent);
            }
        });
        ServiceStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(getApplicationContext(), MyService.class);
                startService(serviceIntent);
            }
        });

        ServiceStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(getApplicationContext(), MyService.class);
                stopService(serviceIntent);
            }
        });


    }
}
