package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class subactivity_START extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_substart);

        // 버튼 인스턴스 초기화
        Button loginButton = findViewById(R.id.loginButton);
        Button runButton = findViewById(R.id.runButton);
        Button editButton = findViewById(R.id.editButton);
        Button patientButton = findViewById(R.id.patientButton);
        Button logButton = findViewById(R.id.logButton);
        Button gasAnalyzerButton = findViewById(R.id.gasAnalyzerButton);
        Button exitButton = findViewById(R.id.exitButton);

        // LOGIN 버튼 이벤트 리스너 설정
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(subactivity_START.this, Suba.class);
//                startActivity(intent);
//            }
//        });

        // RUN 버튼 이벤트 리스너 설정
        runButton.setOnClickListener(v -> {
            Intent intent = new Intent(subactivity_START.this, subactivity_RUN.class);
            startActivity(intent);
        });

        // EDIT 버튼 이벤트 리스너 설정
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(subactivity_START.this, subactivity_EDIT.class);
            startActivity(intent);
        });

        // PATIENT 버튼 이벤트 리스너 설정
        patientButton.setOnClickListener(v -> {
            Intent intent = new Intent(subactivity_START.this, subactivity_PATIENT.class);
            startActivity(intent);
        });

        // LOG 버튼 이벤트 리스너 설정
        logButton.setOnClickListener(v -> {
            Intent intent = new Intent(subactivity_START.this, subactivity_LOG.class);
            startActivity(intent);
        });

        // GAS ANALYZER 버튼 이벤트 리스너 설정
        gasAnalyzerButton.setOnClickListener(v -> {
            Intent intent = new Intent(subactivity_START.this, subactivity_GASANALYZER.class);
            startActivity(intent);
        });

        // EXIT 버튼 이벤트 리스너 설정
//        exitButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 앱 종료
//                finish();
//            }
//        });
    }
}