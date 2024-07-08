package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class subactivity_SETTINGS extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subsettings);

        // I/O Port 버튼을 찾아옵니다.
        Button btnIOPort = findViewById(R.id.btnIOPort);

        // btnConnectionPort 버튼을 찾아옵니다.
        Button btnConnectionPort = findViewById(R.id.btnConnectionStatus);

        // 버튼에 클릭 이벤트 리스너를 추가합니다.
        btnIOPort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent를 생성하여 subactivity_IOPORT 액티비티를 시작합니다.
                Intent intent = new Intent(subactivity_SETTINGS.this, subactivity_IOPORT.class);
                startActivity(intent);
            }
        });

        btnConnectionPort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent를 생성하여 subactivity_CONNECTIOINSTATUS 액티비티를 시작합니다.
                Intent intent = new Intent(subactivity_SETTINGS.this, subactivity_CONNECTIOINSTATUS.class);
                startActivity(intent);
            }
        });
    }
}