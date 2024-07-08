package com.example.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;

public class subactivity_LOGIN extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sublogin);

        // 뷰 바인딩
        EditText editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        Button buttonLogin = (Button) findViewById(R.id.buttonLogin);

        //Focus 요청
        editTextUsername.requestFocus();


        // 버튼 클릭 리스너 설정
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditText에서 사용자 이름과 비밀번호 가져오기
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();
                // 로그인 검증
                validateLogin(username, password);
            }
        });
    }

    // 로그인 검증 메서드
    private void validateLogin(String username, String password) {
        if(username.equals("admin") && password.equals("1234")) {
            // 로그인 성공
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // 로그인 액티비티를 스택에서 제거
        } else {
            // 로그인 실패
            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
        }
    }



}

