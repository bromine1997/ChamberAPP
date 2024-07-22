package com.example.test;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import mraa.Uart;
import mraa.UartParity;

public class CO2sensor {
    private static final String TAG = "SprintIR";
    private static final int SPRINT_BUFSIZE = 15; // 작은 버퍼 크기로 설정

    private Uart UART0; // UART 인터페이스 객체

    private BlockingQueue<String> rawDataQueue = new LinkedBlockingQueue<>(4096);
    private ExecutorService dataReadingExecutor;
    private ExecutorService dataProcessingExecutor;

    private volatile boolean running = true;


    public CO2sensor() {
        try {
            UART0 = new Uart(0);
            if (UART0 != null) {
                UART0.setBaudRate(9600);
                UART0.setMode(8, UartParity.UART_PARITY_NONE, 1);
                UART0.setFlowcontrol(false,false);
                Log.d(TAG, "UART0 initialized and baudrate set.");
            } else {
                Log.e(TAG, "Failed to initialize UART");
            }
        } catch (Exception e) {
            Log.e(TAG, "UART initialization error", e);
        }
        dataReadingExecutor = Executors.newSingleThreadExecutor();
        dataProcessingExecutor = Executors.newSingleThreadExecutor();
    }


    public void init() {
        try {
            calibration();
            Mode_2_select();

           // startDataReadingTask();
           // startDataProcessingTask();
        } catch (Exception e) {
            Log.e(TAG, "Initialization error", e);
        }
    }

    public void loopbackCommand(String message) {
        dataReadingExecutor.submit(() -> {
            try {
                String recieveStr = "null";

                flush();
                UART0.writeStr(message);

                Thread.sleep(10); // 데이터 도착 시간 대기

                while (!UART0.dataAvailable());

                recieveStr = UART0.readStr(50); // 수신 데이터 읽기
                Log.d(TAG, "Receive: " + recieveStr );


            } catch (Exception e) {
                Log.e(TAG, "Loopback test error", e);
            }
        });
    }

    public  void Mode_2_select(){
        loopbackCommand("K 2\r\n");
    }

    public void calibration(){
        loopbackCommand("A 32\r\n");  // send: "A 32\r\n"  필터 설정

        loopbackCommand("Q\r\n");  // send: "G\r\n"      calibration 400ppm

    }

    private void startDataReadingTask() {
        flush();
        dataReadingExecutor.submit(() -> {
            while (running) {
                try {
                    if (UART0.dataAvailable()) {
                        serialEvent2();
                    } else {
                        Thread.sleep(10);  // Wait for a short period before checking again
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Data reading error", e);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });
    }

    private void serialEvent2() {
        try {
            while (UART0.dataAvailable()) {
                String inStr = UART0.readStr(SPRINT_BUFSIZE); // 작은 버퍼 크기로 읽기
                Log.d(TAG, "Raw data: " + inStr);
                if (inStr != null && inStr.length() > 0) {
                    rawDataQueue.offer(inStr);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading UART data", e);
        }
    }
    public String getData() {
        try {
            return rawDataQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "";
        }
    }

    private void startDataProcessingTask() {
        dataProcessingExecutor.submit(() -> {
            while (running) {
                try {
                    String inStr = rawDataQueue.take();
                    if (inStr.contains("?")) {
                        Log.e(TAG, "Invalid data received: " + inStr);
                        continue; // 무효한 데이터를 무시하고 다음 데이터를 읽음
                    }

                    StringBuilder inputString = new StringBuilder();
                    boolean stringComplete = false;
                    for (char inChar : inStr.toCharArray()) {
                        inputString.append(inChar);
                        if (inChar == '\n') {
                            stringComplete = true;
                        }
                    }

                    if (stringComplete) {
                        Log.d(TAG, "String complete, adding to queue: " + inputString.toString());
                        rawDataQueue.offer(inputString.toString().trim());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    Log.e(TAG, "Data processing interrupted", e);
                } catch (Exception e) {
                    Log.e(TAG, "Error processing UART data", e);
                }
            }
        });
    }

    public void flush() {
        if (UART0 != null) {
            while (UART0.dataAvailable()) {
                UART0.readStr(SPRINT_BUFSIZE);
            }
        }
    }

    // 리소스 정리
    public void cleanup() {
        UART0.delete(); // UART 인터페이스 닫기
    }
}
