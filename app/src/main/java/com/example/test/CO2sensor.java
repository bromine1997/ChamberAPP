package com.example.test;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import mraa.Uart;
import mraa.TinkerBoard2UART;
import mraa.UartParity;

public class CO2sensor {
    private static final String TAG = "CO2sensor";
    private static final char header_H = 0xFE ; //
    private static final char device_Addr = 0xA6; //
    private static final char data_Length = 0x00; //
    private static final char get_Dis_CMD = 0x01; //
    private static final char checksum = (device_Addr+data_Length+get_Dis_CMD); //
    private Uart  UART0; // UART 인터페이스 객체

    private HandlerThread uartHandlerThread; // 스레드 생성
    private Handler uartHandler; // 스레드 핸들러

    public CO2sensor(long baudrate) {

        UART0 = new Uart(0);
        UART0.setBaudRate(baudrate);

//        // HandlerThread 생성
//        uartHandlerThread = new HandlerThread("UARTHandlerThread");
//        uartHandlerThread.start();
//        uartHandler = new Handler(uartHandlerThread.getLooper()); // 스레드 루퍼


    }

    public void loopbackTest(final String message) {
        uartHandler.post(() -> {
            try {
                // 데이터 전송
                UART0.writeStr(message);
                Log.d(TAG, "Sent: " + message);

                // 데이터 수신 (전송된 문자열과 동일한 길이로 읽음)
                String readMessage = UART0.readStr(message.length());
                Log.d(TAG, "Received: " + readMessage);
            } catch (Exception e) {
                Log.e(TAG, "UART Communication Error", e);
            }
        });
    }

    // 리소스 정리
    public void cleanup() {
        uartHandlerThread.quitSafely(); // 스레드 종료
        UART0.delete(); // UART 인터페이스 닫기
    }

}
