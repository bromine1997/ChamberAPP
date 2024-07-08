package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;





public class subactivity_EDIT extends AppCompatActivity {
    private Runnable runnableCode;
    private LineChart chart;
    private Handler handler = new Handler(Looper.getMainLooper());

    private List<String[]> currentProfile = new ArrayList<>();
    private int currentSectionIndex = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subedit);

        // MRAA 초기화
        initializeUIComponents();
        initializeChart();
        initializetableChart();

        // Runnable 객체 초기화
        runnableCode = new Runnable() {
            @Override
            public void run() {
                // 여기에 주기적으로 실행할 코드를 넣습니다.
                // 예: 현재 시간 표시, UI 업데이트 등
            }
        };
    }


    private void initializetableChart() {
        TableLayout tableChart = findViewById(R.id.Table);

        // 헤더 행 추가
        TableRow headerRow = new TableRow(this);
        headerRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        String[] headers = {"#", "Start P", "End P", "Time(min)"};
        for (String header : headers) {
            TextView textView = new TextView(this);
            textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            textView.setPadding(5, 5, 5, 5);
            textView.setText(header);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setBackground(ContextCompat.getDrawable(this, R.drawable.border));
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
            textView.setTypeface(null, Typeface.BOLD);

            headerRow.addView(textView);
        }

        tableChart.addView(headerRow);

        // 초기 프로파일 데이터 추가
        updateTable(currentProfile);
    }




    private void initializeUIComponents() {
        // UI 컴포넌트 초기화 및 이벤트 리스너 설정

        Button buttonIncreaseSections = findViewById(R.id.buttonIncreaseSections);
        Button buttonDecreaseSections = findViewById(R.id.buttonDecreaseSections);
        final TextView valueNumberOfSections = findViewById(R.id.valueNumberOfSections);

        Button buttonDecreaseControlSection = findViewById(R.id.buttonDecreaseControlSection);
        Button buttonIncreaseControlSection = findViewById(R.id.buttonIncreaseControlSection);
        final TextView valueControlSection = findViewById(R.id.valueControlSection);

        Button buttonDecreaseEndPressure = findViewById(R.id.buttonDecreaseEndPressure);
        Button buttonIncreaseEndPressure = findViewById(R.id.buttonIncreaseEndPressure);
        final TextView valueEndPressure = findViewById(R.id.valueEndPressure);

        Button buttonDecreaseTime = findViewById(R.id.buttonDecreaseTime);
        Button buttonIncreaseTime = findViewById(R.id.buttonIncreaseTime);
        final TextView valueTime = findViewById(R.id.valueTime);

        Button buttonDecreaseFlow = findViewById(R.id.buttonDecreaseFlow);
        Button buttonIncreaseFlow = findViewById(R.id.buttonIncreaseFlow);
        final TextView valueFlow = findViewById(R.id.valueFlow);


        Button btnNew = findViewById(R.id.btnNew);
        Button btnOpen = findViewById(R.id.btnOpen);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnExit = findViewById(R.id.btnExit);
        Button btnCurve = findViewById(R.id.btnCurve);


        // Control section  갯수 조절 및 생성
        buttonIncreaseSections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numSections = Integer.parseInt(valueNumberOfSections.getText().toString());
                numSections++;
                valueNumberOfSections.setText(String.valueOf(numSections));
                // 섹션을 추가하는 로직
                addSection();
            }
        });

        buttonDecreaseSections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numSections = Integer.parseInt(valueNumberOfSections.getText().toString());
                if (numSections > 1) {
                    numSections--;
                    valueNumberOfSections.setText(String.valueOf(numSections));
                    // 섹션을 제거하는 로직
                    removeSection();
                }
            }
        });
        //


        // Control Section 선택
        buttonDecreaseControlSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int section = Integer.parseInt(valueControlSection.getText().toString());
                    if (section > 1) {
                        section -= 1;
                        valueControlSection.setText(String.valueOf(section));
                        currentSectionIndex = section - 1;
                        updateUIWithCurrentSection();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        buttonIncreaseControlSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int section = Integer.parseInt(valueControlSection.getText().toString());
                    if (section < currentProfile.size()) {
                        section += 1;
                        valueControlSection.setText(String.valueOf(section));
                        currentSectionIndex = section - 1;
                        updateUIWithCurrentSection();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        //



        buttonDecreaseEndPressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float currentValue = Float.parseFloat(valueEndPressure.getText().toString());
                if(currentValue > 1) {
                    currentValue -= 0.1; // 0.1 감소
                    valueEndPressure.setText(String.format(Locale.US, "%.1f", currentValue));
                    updateCurrentSectionData();
                } else {
                    currentValue = 1; // 최소값 1로 설정
                    valueEndPressure.setText(String.format(Locale.US, "%.1f", currentValue)); // UI 업데이트 누락 수정
                }
            }
        });

        buttonIncreaseEndPressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float currentValue = Float.parseFloat(valueEndPressure.getText().toString());
                currentValue += 0.1;
                valueEndPressure.setText(String.format(Locale.US, "%.1f", currentValue));
                updateCurrentSectionData();
            }
        });



        buttonDecreaseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float currentValue = Float.parseFloat(valueTime.getText().toString());
                currentValue -= 1;
                valueTime.setText(String.format(Locale.US, "%.1f", currentValue));
                updateCurrentSectionData();
            }
        });


        buttonIncreaseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float currentValue = Float.parseFloat(valueTime.getText().toString());
                currentValue += 1;
                valueTime.setText(String.format(Locale.US, "%.1f", currentValue));
                updateCurrentSectionData();
            }
        });

        buttonDecreaseFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float currentValue = Float.parseFloat(valueFlow.getText().toString());
                currentValue -= 1; // 감소 단위 설정
                valueFlow.setText(String.format(Locale.US, "%.1f", currentValue));
            }
        });

        buttonIncreaseFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float currentValue = Float.parseFloat(valueFlow.getText().toString());
                currentValue += 1; // 증가 단위 설정
                valueFlow.setText(String.format(Locale.US, "%.1f", currentValue));
            }
        });


        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String[]> newProfile = new ArrayList<>();
                newProfile.add(new String[]{"1", "1.0", "1.0", "5"});
                updateTable(newProfile);
                currentProfile = newProfile;
                currentSectionIndex = 0;

                updateUIWithCurrentSection();

                TextView valueNumberOfSections = findViewById(R.id.valueNumberOfSections);
                valueNumberOfSections.setText("1");
            }
        });

        btnCurve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //updateChart();
            }
        });


        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                loadProfileDataFromFile();
                updateChart();
                updateTable(currentProfile);
                updateNumberOfSections(); // 이 함수를 호출하여 섹션 수를 업데이트

                // 추가: 섹션 컨트롤 인덱스 초기화 및 UI 업데이트
                if (!currentProfile.isEmpty()) {
                    currentSectionIndex = 0; // 첫 번째 섹션을 기본값으로 설정
                    valueControlSection.setText(String.valueOf(currentSectionIndex + 1));
                    updateUIWithCurrentSection(); // 현재 섹션에 맞게 UI 업데이트
                } else {
                    valueControlSection.setText("0");
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileData();
                Toast.makeText(subactivity_EDIT.this, "저장되었습니다", Toast.LENGTH_SHORT).show();

            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 앱 종료
            }
        });

        btnCurve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 비선형 압력 변화를 적용
                applyCurve();

            }
        });


    }
    private void updateTable(List<String[]> profileData) {
        TableLayout tableChart = findViewById(R.id.Table);

        if (tableChart.getChildCount() > 1) {
            tableChart.removeViews(1, tableChart.getChildCount() - 1);
        }

        int totalDuration = 0;

        for (int i = 0; i < profileData.size(); i++) {
            String[] dataRow = profileData.get(i);
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            for (String cellData : dataRow) {
                TextView textView = new TextView(this);
                textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                textView.setPadding(5, 5, 5, 5);
                textView.setText(cellData);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setBackground(ContextCompat.getDrawable(this, R.drawable.border));
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);

                tableRow.addView(textView);
            }

            tableChart.addView(tableRow);

            try {
                totalDuration += Integer.parseInt(dataRow[3]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        TableRow totalRow = new TableRow(this);
        totalRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        TextView totalLabelTextView = new TextView(this);
        totalLabelTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        totalLabelTextView.setPadding(5, 5, 5, 5);
        totalLabelTextView.setText("Total");
        totalLabelTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        totalLabelTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.border));
        totalLabelTextView.setTextColor(Color.BLACK);
        totalLabelTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
        totalLabelTextView.setTypeface(null, Typeface.BOLD);
        totalRow.addView(totalLabelTextView);

        for (int i = 0; i < 2; i++) {
            TextView emptyTextView = new TextView(this);
            emptyTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            emptyTextView.setPadding(5, 5, 5, 5);
            emptyTextView.setText("");
            emptyTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            emptyTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.border));
            emptyTextView.setTextColor(Color.BLACK);
            emptyTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
            totalRow.addView(emptyTextView);
        }

        TextView totalValueTextView = new TextView(this);
        totalValueTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        totalValueTextView.setPadding(5, 5, 5, 5);
        totalValueTextView.setText(String.valueOf(totalDuration));
        totalValueTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        totalValueTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.border));
        totalValueTextView.setTextColor(Color.BLACK);
        totalValueTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
        totalRow.addView(totalValueTextView);

        tableChart.addView(totalRow);
    }

    private void addSection() {
        // 새로운 섹션 추가 로직
        currentProfile.add(new String[]{String.valueOf(currentProfile.size() + 1), "1.0", "1.0", "5"});
        updateTable(currentProfile);
    }


    private void removeSection() {
        // 마지막 섹션 제거 로직
        if (!currentProfile.isEmpty()) {
            currentProfile.remove(currentProfile.size() - 1);
        }
        updateTable(currentProfile);
    }
    private void applyCurve() {
        double factor = 1.10; // 증가 요소, 10% 증가를 의미
        for (String[] section : currentProfile) {
            double startPressure = Double.parseDouble(section[1]);
            double endPressure = Double.parseDouble(section[2]);

            // 비선형 증가: 각 압력 값을 지정된 팩터로 곱합니다.
            startPressure *= factor;
            endPressure *= factor;

            // 업데이트된 값으로 섹션 데이터를 변경
            section[1] = String.format(Locale.US, "%.2f", startPressure);
            section[2] = String.format(Locale.US, "%.2f", endPressure);
        }

        // 테이블과 차트를 업데이트
        updateTable(currentProfile);
        updateChart();
    }

    private void updateUIWithCurrentSection() {
        if (currentProfile != null && !currentProfile.isEmpty()) {
            String[] currentSection = currentProfile.get(currentSectionIndex);

            // TextView를 올바른 데이터로 업데이트
            ((TextView) findViewById(R.id.valueEndPressure)).setText(currentSection[2]); // End Pressure
            ((TextView) findViewById(R.id.valueTime)).setText(currentSection[3]); // Time
            ((TextView) findViewById(R.id.valueFlow)).setText("Flow value here"); // 예시, 실제로는 Flow 데이터를 할당해야 함
            ((TextView) findViewById(R.id.valueControlSection)).setText(String.valueOf(currentSectionIndex + 1));

            // Start Pressure는 추가로 표시가 필요하다면 여기서 업데이트
        }
    }





    private void updateCurrentSectionData() {
        String[] currentSection = currentProfile.get(currentSectionIndex);
        currentSection[2] = ((TextView) findViewById(R.id.valueEndPressure)).getText().toString(); // End Pressure
        currentSection[3] = ((TextView) findViewById(R.id.valueTime)).getText().toString(); // Time

        // 이전 섹션이 있으면 현재 섹션의 Start Pressure를 이전 섹션의 End Pressure로 설정
        if (currentSectionIndex > 0) {
            String[] previousSection = currentProfile.get(currentSectionIndex - 1);
            currentSection[1] = previousSection[2]; // 현재 섹션의 Start Pressure
        }

        // 다음 섹션이 있으면 다음 섹션의 Start Pressure를 현재 섹션의 End Pressure로 설정
        if (currentSectionIndex < currentProfile.size() - 1) {
            String[] nextSection = currentProfile.get(currentSectionIndex + 1);
            nextSection[1] = currentSection[2]; // 다음 섹션의 Start Pressure
        }

        updateTable(currentProfile);
        updateChart();
    }


    private void initializeChart() {
        chart = findViewById(R.id.chart);

        List<Entry> entries = new ArrayList<>();
        PressureTimeChart pressureTimeChart = new PressureTimeChart(chart, currentProfile);
        pressureTimeChart.drawChart();
    }

    private void updateChart() {
        List<Entry> entries = new ArrayList<>();
        float currentTime = 0;

        for (String[] dataPoint : currentProfile) {
            float duration = Float.parseFloat(dataPoint[3]);
            float startPressure = Float.parseFloat(dataPoint[1]);
            float endPressure = Float.parseFloat(dataPoint[2]);

            entries.add(new Entry(currentTime, startPressure));
            currentTime += duration;
            entries.add(new Entry(currentTime, endPressure));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Pressure Over Time");
        dataSet.setColor(Color.BLACK);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setLineWidth(3f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }




    private void createChartWithData(List<String[]> data) {
        List<Entry> entries = new ArrayList<>();

        // 데이터 포인트 변환
        for (int i = 0; i < data.size(); i++) {
            // 데이터 구조를 바탕으로 x축 위치와 y축 값을 얻습니다.
            String[] row = data.get(i);
            float xValue = Float.parseFloat(row[0]);
            float yValue = Float.parseFloat(row[1]);

            // Entry 리스트에 추가
            entries.add(new Entry(xValue, yValue));
        }

        // LineDataSet 생성
        LineDataSet dataSet = new LineDataSet(entries, "Label");
        dataSet.setColor(ColorTemplate.getHoloBlue());
        dataSet.setLineWidth(2.5f);
        dataSet.setDrawCircles(true);
        dataSet.setDrawValues(false);
        dataSet.setDrawFilled(true); // 이 부분을 추가하여 선 아래를 채우도록 합니다.
        dataSet.setFillColor(Color.BLUE); //
        dataSet.setDrawCircleHole(false);
        dataSet.setColor(Color.BLACK);

        // LineData에 DataSet 추가
        LineData lineData = new LineData(dataSet);

        // 차트 설정
        chart.setData(lineData);
        chart.invalidate(); // 차트 업데이트

        // 축 및 기타 설정
        configureChartAppearance();
    }

    public class PressureTimeChart {

        private LineChart chart;
        private List<String[]> data;

        public PressureTimeChart(LineChart chart, List<String[]> data) {
            this.chart = chart;
            this.data = data;
        }

        public void drawChart() {
            List<Entry> entries = new ArrayList<>();
            float currentTime = 0;

            for (String[] dataPoint : data) {
                // 데이터에서 시간과 압력 값 추출
                float duration = Float.parseFloat(dataPoint[3]); // 4번째 요소가 시간(분)
                float startPressure = Float.parseFloat(dataPoint[1]); // 2번째 요소가 시작 압력
                float endPressure = Float.parseFloat(dataPoint[2]); // 3번째 요소가 끝 압력

                // 시작 압력의 데이터 포인트
                entries.add(new Entry(currentTime, startPressure));
                currentTime += duration; // 다음 시간 계산을 위해 지속 시간 추가
                // 끝 압력의 데이터 포인트
                entries.add(new Entry(currentTime, endPressure));
            }

            // 데이터셋 생성
            LineDataSet dataSet = new LineDataSet(entries, "Pressure Over Time");
            dataSet.setColor(Color.BLACK);
            dataSet.setDrawCircles(false); // 선만 표시
            dataSet.setDrawValues(false);
            dataSet.setFillColor(Color.BLUE); //
            dataSet.setLineWidth(3f);


            configureChartAppearance();

            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);
            chart.invalidate(); // 차트를 다시 그림
        }
    }
    private void configureChartAppearance() {
        // 차트 출현 설정 (X축, Y축)
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();
        leftAxis.setSpaceTop(100f);
        leftAxis.setGranularity(0.25f);
        xAxis.setSpaceMin(2f);
        xAxis.setSpaceMax(10f);

        rightAxis.setEnabled(false); // 오른쪽 Y축 비활성화

        // 기타 출현 관련 설정은 제공된 코드를 참고하시기 바랍니다.
    }

    private void saveProfileData() {
        Gson gson = new Gson();
        String json = gson.toJson(currentProfile);
        try (FileOutputStream fos = openFileOutput("profile_data.json", Context.MODE_PRIVATE)) {
            fos.write(json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadProfileDataFromFile() {
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
            currentProfile = gson.fromJson(sb.toString(), type);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load profile data.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateNumberOfSections() {
        if (currentProfile != null) {
            int numSections = currentProfile.size();
            TextView valueNumberOfSections = findViewById(R.id.valueNumberOfSections);
            valueNumberOfSections.setText(String.valueOf(numSections));
        }
    }

    protected void onStart() {
        super.onStart();
        // 예를 들어, 1000ms(1초) 마다 runnableCode를 실행하도록 합니다.
        handler.postDelayed(runnableCode, 1000);
    }


    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnableCode);
    }

}
