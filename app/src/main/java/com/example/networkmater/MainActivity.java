package com.example.networkmater;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/*
*소켓 : 소켓 통신에서는 클라이언트와 서버 사이의 연결이 지속되고 실시간으로 서로 데이터를 주고 받음
 -> 소켓 연결 등을 시도하거나 응답을 받아서 처리할 때 스레드를 사용해야됨
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    Handler handler = new Handler();

    TextView tv, tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText edt = (EditText) findViewById(R.id.edt);
        tv = (TextView) findViewById(R.id.tv);
        tv2 = (TextView) findViewById(R.id.tv2);

        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String data = edt.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //sendData(data);
                    }
                }).start();
            }
        });

        Button btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //startServer();
                    }
                }).start();
            }
        });
    }

    /*
    public void sendData(String data) {
    }

    public void startServer() {
    } */

    public void printClientLog(final String data) { // final을 사용한 이유 : 파라미터가 그대로 전달되어야하므로
        Log.d(TAG, data);
        handler.post(new Runnable() {
            @Override
            public void run() {
                tv.append(data + "\n");
            }
        });
    }

    public void printServerLog(final String data) {
        { // final을 사용한 이유 : 파라미터가 그대로 전달되어야하므로
            Log.d(TAG, data);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    tv2.append(data + "\n");
                }
            });
        }
    }
}