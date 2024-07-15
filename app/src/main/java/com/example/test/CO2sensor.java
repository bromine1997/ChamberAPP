package com.example.test;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import mraa.Uart;
import mraa.UartParity;

public class CO2sensor {
    private static final String TAG = "CO2sensor";
    private static final char header_H = 0xFE ; //
    private static final char device_Addr = 0xA6; //
    private static final char data_Length = 0x00; //
    private static final char get_Dis_CMD = 0x01; //
    private static final char checksum = (device_Addr+data_Length+get_Dis_CMD); //
    private Uart UART0; // UART 인터페이스 객체

    private HandlerThread uartHandlerThread; // 스레드 생성
    private Handler uartHandler; // 스레드 핸들러

    public CO2sensor(long baudrate) {
        UART0 = new Uart(0); // UART0 초기화
        if (UART0 != null) {
            UART0.setBaudRate(baudrate);
            UART0.setMode(8, UartParity.UART_PARITY_NONE, 1);
            UART0.setFlowcontrol(false, false);
            UART0.setTimeout(1000, 1000, 1000); // 읽기 및 쓰기 타임아웃 설정 (1초)
            UART0.setNonBlocking(false); // Blocking 모드 설정
            Log.d(TAG, "UART0 initialized and baudrate set.");
        } else {
            Log.e(TAG, "Failed to initialize UART");
        }
    }


    public void loopbackTest(final String message) {
        new LoopbackTask().execute(message);
    }

    private class LoopbackTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String message = params[0];
            try {
                // 데이터 전송
                UART0.writeStr(message);
                Log.d(TAG, "Sent: " + message);

                // 데이터 가용성 확인
                if (UART0.dataAvailable(1000)) { // 1초 동안 데이터 가용성 대기
                    // 데이터 수신 (전송된 문자열과 동일한 길이로 읽음)
                    return UART0.readStr(message.length());
                } else {
                    Log.e(TAG, "No data available for reading");
                    return null;
                }
            } catch (Exception e) {
                Log.e(TAG, "UART Communication Error", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.d(TAG, "Received: " + result);
            } else {
                Log.e(TAG, "No data received");
            }
        }
    }

    public void send(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UART0.writeStr(message);
                    Log.d(TAG, "Sent: " + message);
                } catch (Exception e) {
                    Log.e(TAG, "UART Communication Error", e);
                }
            }
        }).start();
    }

    public String receive(int length) {
        try {
            if (UART0.dataAvailable(1000)) { // 1초 동안 데이터 가용성 대기
                return UART0.readStr(length);
            } else {
                Log.e(TAG, "No data available for reading");
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "UART Communication Error", e);
            return null;
        }
    }

    // 리소스 정리
    public void cleanup() {
        UART0.delete(); // UART 인터페이스 닫기
    }
}
