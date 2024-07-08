package com.example.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import android.widget.TextView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class subactivity_RUN extends AppCompatActivity {

    private BroadcastReceiver adcValuesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.example.test.ADC_VALUES".equals(intent.getAction())) {
                int[] adcValues = intent.getIntArrayExtra("adcValues");
                if (adcValues != null) {
                    showGasAnalyzerDialog(adcValues);
                    // 팝업창을 띄운 후 리시버를 등록 해제하여 한 번만 팝업이 뜨도록 함
                    LocalBroadcastManager.getInstance(subactivity_RUN.this).unregisterReceiver(this);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subrun);

        List<String[]> profileData = loadProfileData();
        updateChart(profileData);
        updateUI(profileData);

        Button btnGasAnalyzer = findViewById(R.id.btnGasAnalyzer);
        btnGasAnalyzer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 데이터를 요청하는 인텐트를 서비스로 보냄
                Intent requestIntent = new Intent(subactivity_RUN.this, MyService.class);
                requestIntent.setAction("com.example.test.action.REQUEST_ADC_VALUES");
                startService(requestIntent);

                // 버튼 클릭 시 브로드캐스트 리시버를 등록
                LocalBroadcastManager.getInstance(subactivity_RUN.this).registerReceiver(adcValuesReceiver,
                        new IntentFilter("com.example.test.ADC_VALUES"));



            }
        });
    }

    private void showGasAnalyzerDialog(int[] adcValues) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Gas Analyzer");

        // adcValues를 문자열로 변환하여 표시
        StringBuilder message = new StringBuilder();
        for (int value : adcValues) {
            message.append("ADC Value: ").append(value).append("\n");
        }

        builder.setMessage(message.toString());
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private List<String[]> loadProfileData() {
        List<String[]> profileData = new ArrayList<>();
        try (FileInputStream fis = openFileInput("profile_data.json");
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            Gson gson = new Gson();
            Type type = new TypeToken<List<String[]>>() {}.getType();
            profileData = gson.fromJson(sb.toString(), type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return profileData;
    }

    private void updateChart(List<String[]> data) {
        LineChart chart = findViewById(R.id.lineChart);
        List<Entry> entries = new ArrayList<>();
        float currentTime = 0;
        for (String[] section : data) {
            float startPressure = Float.parseFloat(section[1]);
            float endPressure = Float.parseFloat(section[2]);
            float duration = Float.parseFloat(section[3]);
            entries.add(new Entry(currentTime, startPressure));
            currentTime += duration;
            entries.add(new Entry(currentTime, endPressure));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Pressure Over Time");
        dataSet.setColor(ColorTemplate.getHoloBlue());
        dataSet.setLineWidth(2.5f);
        dataSet.setDrawCircles(true);
        dataSet.setDrawValues(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.BLUE);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }

    private void updateUI(List<String[]> data) {
        if (!data.isEmpty()) {
            TextView chamberPressure = findViewById(R.id.chamberPressure);
            chamberPressure.setText(data.get(0)[2] + " ATA"); // 첫 섹션의 종료 압력
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(adcValuesReceiver);
    }
}
