package com.example.networkmater;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebRequestActivity extends AppCompatActivity {

    Handler handler = new Handler();

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_request);

        EditText edt = (EditText)findViewById(R.id.edt);
        Button btn = (Button)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt.getText().toString().equals("") || edt.getText().toString() == null) {
                    Toast.makeText(getApplicationContext(), "입력된 텍스트가 없습니다. ", Toast.LENGTH_SHORT).show();
                } else {
                    final String urlStr = edt.getText().toString();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            request(urlStr);
                        }
                    }).start();
                }
            }
        });
        tv = (TextView)findViewById(R.id.tv);
    }

    public void request(String urlStr) {
        StringBuilder output = new StringBuilder(); // 문자열이 하나 이상 있을 때 이것들을 더하기위해

        try {
            URL url = new URL(urlStr); // URL 객체 생성

            HttpURLConnection connection = (HttpURLConnection)url.openConnection(); // 파라미터로 전달된 URL 문자열을 이용해 생성된 객체의 openConnection()을 호출하면
            // HttpURLConnection 객체가 반환됨

            if(connection != null) {
                connection.setConnectTimeout(10000); // 10초동안 기다려서 응답없으면 끝남
                connection.setRequestMethod("GET"); // GET 방식으로 요청함
                connection.setDoInput(true); // 이 객체의 입력이 가능하도록함

                int responseCode = connection.getResponseCode(); // 내부적으로 웹서버에 페이지를 요청함
                // 입력 데이터를 받을 수 있는 Reader 객체 생성
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())); // 받을 수 있는 통로 생성
                String line = null;
                while (true) {
                    line = reader.readLine(); // 한 줄씩 읽음
                    if(line == null) {
                        break;
                    }

                    output.append(line + "\n");
                }
                reader.close(); // 다 쓰고 닫기
                connection.disconnect(); // 연결 끊기
            }
        } catch (Exception e) {
            println("예외 발생 : " + e.toString());
        }
        println("응답 : " + output.toString());
    }

    public void println(final String data) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                tv.append(data + "\n");
            }
        });
    }
}