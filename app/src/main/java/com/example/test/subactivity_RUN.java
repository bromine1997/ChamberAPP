package com.example.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.github.mikephil.charting.charts.LineChart;
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

    private Handler handler = new Handler(Looper.getMainLooper());
    private int currentSection = 0;
    private LineChart chart;
    private LineData lineData;
    private float minY = Float.MAX_VALUE;
    private float maxY = Float.MIN_VALUE;
    private TextView elapsedTimeTextView;
    private long startTime;

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

        chart = findViewById(R.id.lineChart);  // LineChart 객체 초기화
        elapsedTimeTextView = findViewById(R.id.elapsedTime);  // Elapsed Time TextView 초기화

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

        Button btnRun = findViewById(R.id.btnRun);
        btnRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSection = 0;  // 섹션을 처음부터 다시 시작
                startTime = System.currentTimeMillis();  // 시작 시간 초기화
                runGraphUpdate(profileData);
                startElapsedTimeUpdate();  // 경과 시간 업데이트 시작
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
            Type type = new TypeToken<List<String[]>>() {
            }.getType();
            profileData = gson.fromJson(sb.toString(), type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return profileData;
    }

    private void updateChart(List<String[]> data) {
        List<Entry> entries = new ArrayList<>();
        float currentTime = 0;
        for (String[] section : data) {
            float startPressure = Float.parseFloat(section[1]);
            float endPressure = Float.parseFloat(section[2]);
            float duration = Float.parseFloat(section[3]);
            entries.add(new Entry(currentTime, startPressure));
            currentTime += duration;
            entries.add(new Entry(currentTime, endPressure));

            // Y축의 최소값과 최대값 업데이트
            if (startPressure < minY) minY = startPressure;
            if (endPressure < minY) minY = endPressure;
            if (startPressure > maxY) maxY = startPressure;
            if (endPressure > maxY) maxY = endPressure;
        }
        LineDataSet dataSet = new LineDataSet(entries, "Pressure Over Time");
        dataSet.setColor(ColorTemplate.getHoloBlue());
        dataSet.setLineWidth(2.5f);
        dataSet.setDrawCircles(true);
        dataSet.setDrawValues(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.BLUE);

        lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();

        // Y축의 범위를 고정
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(minY);
        yAxis.setAxisMaximum(maxY);
        chart.getAxisRight().setEnabled(false);  // 오른쪽 Y축 비활성화
    }

    private void updateUI(List<String[]> data) {
        if (!data.isEmpty()) {
            TextView chamberPressure = findViewById(R.id.chamberPressure);
            chamberPressure.setText(data.get(0)[2] + " ATA"); // 첫 섹션의 종료 압력
        }
    }

    private void runGraphUpdate(List<String[]> profileData) {
        if (currentSection < profileData.size()) {
            String[] section = profileData.get(currentSection);
            float startPressure = Float.parseFloat(section[1]);
            float endPressure = Float.parseFloat(section[2]);
            float duration = Float.parseFloat(section[3]);

            float sectionDurationInSeconds = duration * 60;
            float interval = 1.0f;
            float totalSteps = sectionDurationInSeconds / interval;
            float pressureStep = (endPressure - startPressure) / totalSteps;

            LineDataSet dataSet = new LineDataSet(new ArrayList<>(), "Updated Pressure Over Time");
            dataSet.setColor(Color.RED);
            dataSet.setLineWidth(2.5f);
            dataSet.setDrawCircles(true);
            dataSet.setDrawValues(false);
            dataSet.setDrawFilled(true);
            dataSet.setFillColor(Color.RED);

            lineData.addDataSet(dataSet);
            chart.setData(lineData);
            chart.invalidate();

            handler.postDelayed(new Runnable() {
                float currentTime = currentSection == 0 ? 0 : lineData.getXMax();
                float currentPressure = startPressure;
                int step = 0;

                @Override
                public void run() {
                    if (step <= totalSteps) {
                        dataSet.addEntry(new Entry(currentTime, currentPressure));
                        lineData.notifyDataChanged();
                        chart.notifyDataSetChanged();
                        chart.invalidate();

                        currentTime += interval;
                        currentPressure += pressureStep;
                        step++;
                        handler.postDelayed(this, (long) (interval * 1000));
                    } else {
                        currentSection++;
                        runGraphUpdate(profileData);
                    }
                }
            }, 0);
        }
    }

    private void startElapsedTimeUpdate() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                long elapsedMillis = System.currentTimeMillis() - startTime;
                int seconds = (int) (elapsedMillis / 1000);
                int minutes = seconds / 60;
                int hours = minutes / 60;
                seconds = seconds % 60;
                minutes = minutes % 60;

                String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                elapsedTimeTextView.setText(time);

                // 다음 업데이트 예약
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}