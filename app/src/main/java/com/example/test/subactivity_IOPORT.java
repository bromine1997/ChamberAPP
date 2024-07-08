package com.example.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class subactivity_IOPORT extends AppCompatActivity {

    private Handler handler = new Handler(Looper.getMainLooper());
    private TextView o2Value, co2Value, humidityValue, tempValue, pressureValue, flowValue;

    private BroadcastReceiver adcValuesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int[] adcValues = intent.getIntArrayExtra("adcValues");
            if (adcValues != null) {
                o2Value.setText(adcValues[0] + " %");
                co2Value.setText(adcValues[1] + " %");
                humidityValue.setText(adcValues[2] + " %");
                tempValue.setText(adcValues[3] + " °C");
                pressureValue.setText(adcValues[4] + " ATA");
                flowValue.setText(adcValues[5] + " lpm");
            }
        }
    };

    private BroadcastReceiver ioStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte inputStatus = intent.getByteExtra("inputStatus", (byte) 0);
            updateTextViewStatus(inputStatus, R.id.switch1_status, 0);
            updateTextViewStatus(inputStatus, R.id.switch2_status, 1);
            updateTextViewStatus(inputStatus, R.id.switch3_status, 2);
            updateTextViewStatus(inputStatus, R.id.switch4_status, 3);
            updateTextViewStatus(inputStatus, R.id.switch5_status, 4);
            updateTextViewStatus(inputStatus, R.id.switch6_status, 5);
            updateTextViewStatus(inputStatus, R.id.switch7_status, 6);
            updateTextViewStatus(inputStatus, R.id.switch8_status, 7);
        }
    };

    private void sensorView_Init(){
        // TextView 초기화
        o2Value = findViewById(R.id.o2_value);
        co2Value = findViewById(R.id.co2_value);
        humidityValue = findViewById(R.id.humidity_value);
        tempValue = findViewById(R.id.temp_value);
        pressureValue = findViewById(R.id.pressure_value);
        flowValue = findViewById(R.id.flow_value);
    }

    private void initializeButtons() {
        Button controlled1 = findViewById(R.id.controlled1);
        controlled1.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyService.class);
            intent.setAction("com.example.test.action.TOGGLE_LED1");
            startService(intent);
        });

        Button controlled2 = findViewById(R.id.controlled2);
        controlled2.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyService.class);
            intent.setAction("com.example.test.action.TOGGLE_LED2");
            startService(intent);
        });

        Button controlled3 = findViewById(R.id.controlled3);
        controlled3.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyService.class);
            intent.setAction("com.example.test.action.TOGGLE_LED3");
            startService(intent);
        });
    }

    private void initializeValveButtons() {
        findViewById(R.id.controlProportionPressButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, MyService.class);
            intent.setAction("com.example.test.action.TOGGLE_PRESS");
            startService(intent);
        });

        findViewById(R.id.controlSolenoidPressButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, MyService.class);
            intent.setAction("com.example.test.action.TOGGLE_PRESS");
            startService(intent);
        });

        findViewById(R.id.controlProportionPressDown).setOnClickListener(v -> {
            Intent intent = new Intent(this, MyService.class);
            intent.setAction("com.example.test.action.PRESS_VALVE_DOWN");
            startService(intent);
        });

        findViewById(R.id.controlProportionPressUP).setOnClickListener(v -> {
            Intent intent = new Intent(this, MyService.class);
            intent.setAction("com.example.test.action.PRESS_VALVE_UP");
            startService(intent);
        });

        findViewById(R.id.controlSolenoidVentButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, MyService.class);
            intent.setAction("com.example.test.action.TOGGLE_VENT");
            startService(intent);
        });

        findViewById(R.id.controlProportionVentButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, MyService.class);
            intent.setAction("com.example.test.action.TOGGLE_VENT");
            startService(intent);
        });

        findViewById(R.id.controlProportionVentDown).setOnClickListener(v -> {
            Intent intent = new Intent(this, MyService.class);
            intent.setAction("com.example.test.action.VENT_VALVE_DOWN");
            startService(intent);
        });

        findViewById(R.id.controlProportionVentUp).setOnClickListener(v -> {
            Intent intent = new Intent(this, MyService.class);
            intent.setAction("com.example.test.action.VENT_VALVE_UP");
            startService(intent);
        });
    }

    private void updateTextViewStatus(byte inputStatus, int textViewId, int bit) {
        TextView textView = findViewById(textViewId);
        boolean isOn = ((inputStatus >> bit) & 1) == 1;
        textView.setText("Switch " + (bit + 1) + ": " + (isOn ? "ON" : "OFF"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(adcValuesReceiver, new IntentFilter("com.example.test.ADC_VALUES"));
        LocalBroadcastManager.getInstance(this).registerReceiver(ioStatusReceiver, new IntentFilter("com.example.test.IO_STATUS_UPDATE"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subioport);
        initializeButtons();
        initializeValveButtons();
        sensorView_Init();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(adcValuesReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(ioStatusReceiver);
    }
}
