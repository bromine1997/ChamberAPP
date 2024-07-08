package com.example.test;

import com.example.test.PinController;
import com.example.test.AD5420;
import com.example.test.MAX1032;
import com.example.test.PID;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MyService extends Service {

    private static final String TAG = "MyService";
    private Handler handler;
    private Runnable adcRunnable;
    private Runnable i2cRunnable;
    private Runnable valveRunnable;

    private static final String ACTION_TOGGLE_LED1 = "com.example.test.action.TOGGLE_LED1";
    private static final String ACTION_TOGGLE_LED2 = "com.example.test.action.TOGGLE_LED2";
    private static final String ACTION_TOGGLE_LED3 = "com.example.test.action.TOGGLE_LED3";
    private static final String ACTION_TOGGLE_PRESS = "com.example.test.action.TOGGLE_PRESS";
    private static final String ACTION_TOGGLE_VENT = "com.example.test.action.TOGGLE_VENT";
    private static final String ACTION_PRESS_VALVE_DOWN = "com.example.test.action.PRESS_VALVE_DOWN";
    private static final String ACTION_PRESS_VALVE_UP = "com.example.test.action.PRESS_VALVE_UP";
    private static final String ACTION_VENT_VALVE_DOWN = "com.example.test.action.VENT_VALVE_DOWN";
    private static final String ACTION_VENT_VALVE_UP = "com.example.test.action.VENT_VALVE_UP";
    private static final String ACTION_REQUEST_ADC_VALUES = "com.example.test.action.REQUEST_ADC_VALUES";

    private PinController pinController;
    private MAX1032 max1032;
    private AD5420 ad5420;

    @Override
    public void onCreate() {
        super.onCreate();
        pinController = new PinController();
        max1032 = new MAX1032(1, 18); // SPI 1 bus => SPI5, LatchPin 설정
        ad5420 = new AD5420(0);

        handler = new Handler();

        // Daisy_reset을 실행하고 1밀리초 지연 후 Daisy_Setup을 실행
        ad5420.Daisy_reset();
        try {
            Thread.sleep(1); // 1밀리초 지연
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ad5420.Daisy_Setup();

        adcRunnable = new Runnable() {
            @Override
            public void run() {
                readAndBroadcastAdcValues();
                handler.postDelayed(this, 1000); // 1초마다 실행
            }
        };

        i2cRunnable = new Runnable() {
            @Override
            public void run() {
                readAndBroadcastI2cValues();
                handler.postDelayed(this, 500); // 0.5초마다 실행
            }
        };

        valveRunnable = new Runnable() {
            @Override
            public void run() {
                // PID 제어를 포함한 벨브 제어 로직을 추가
                controlValves();  // PID제어 함수로 변경
                handler.postDelayed(this, 1000); // 1초마다 실행
            }
        };

        // 처음 실행 (1초 후에 첫 실행)
        handler.postDelayed(adcRunnable, 1000);
        handler.postDelayed(i2cRunnable, 500);
        handler.postDelayed(valveRunnable, 1000);
    }

    private void readAndBroadcastAdcValues() {
        int[] adcValues = max1032.readAllChannels();
        Intent intent = new Intent("com.example.test.ADC_VALUES");
        intent.putExtra("adcValues", adcValues);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void readAndBroadcastI2cValues() {
        byte inputStatus = pinController.readInputs();              //외부 입력 스위치 주기적으로 확인
        Intent intent = new Intent("com.example.test.IO_STATUS_UPDATE");
        intent.putExtra("inputStatus", inputStatus);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void controlValves() {
        Log.d(TAG, "Valves PID Start ");

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Valves PID processing");
            }
        }, 1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "서비스가 시작되었습니다.");

        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case ACTION_TOGGLE_LED1:
                        pinController.toggleLed(1);
                        sendBroadcastUpdate("LED1");
                        break;
                    case ACTION_TOGGLE_LED2:
                        pinController.toggleLed(2);
                        sendBroadcastUpdate("LED2");
                        break;
                    case ACTION_TOGGLE_LED3:
                        pinController.toggleLed(3);
                        sendBroadcastUpdate("LED3");
                        break;
                    case ACTION_TOGGLE_PRESS:
                        pinController.toggleControlProportionPress();
                        sendBroadcastUpdate("PRESS");
                        break;
                    case ACTION_TOGGLE_VENT:
                        pinController.toggleControlProportionVent();
                        sendBroadcastUpdate("VENT");
                        break;
                    case ACTION_PRESS_VALVE_DOWN:
                        ad5420.PressValveCurrentUp();
                        sendBroadcastUpdate("PRESS_VALVE_DOWN");
                        break;
                    case ACTION_PRESS_VALVE_UP:
                        ad5420.PressValveCurrentDown();
                        sendBroadcastUpdate("PRESS_VALVE_UP");
                        break;
                    case ACTION_VENT_VALVE_DOWN:
                        ad5420.VentValveCurrentUp();
                        sendBroadcastUpdate("VENT_VALVE_DOWN");
                        break;
                    case ACTION_VENT_VALVE_UP:
                        ad5420.VentValveCurrentDown();
                        sendBroadcastUpdate("VENT_VALVE_UP");
                        break;
                    case ACTION_REQUEST_ADC_VALUES:
                        readAndBroadcastAdcValues();
                        break;
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "서비스가 종료되었습니다.");
        handler.removeCallbacks(adcRunnable);
        handler.removeCallbacks(i2cRunnable);
        handler.removeCallbacks(valveRunnable);
    }

    private void sendBroadcastUpdate(String status) {
        Intent intent = new Intent("com.example.test.IO_STATUS_UPDATE");
        intent.putExtra("status", status);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}