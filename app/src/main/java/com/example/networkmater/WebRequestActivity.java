package com.example.networkmater;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/*
*HttpURLConnection 객체를 사용하는 것은 요청과 응답에 필요한 코드의 양이 많음(스레드를 사용하면서 넣어야하는 코드 양도 많음)
*Volley 라이브러리 : 웹 요청과 응답을 단순화하기 위한 라이브러리
*Volley 사용법 : 요청 객체 생성 후 요청 큐에 넣으면 됨 -> 요청 큐가 알아서 웹서버에 요청하고 응답까지 받아줌

*Volley의 가장 큰 장점 : 스레드를 신경 쓰지 않아도 됨 -> 메인 스레드에서 UI에 접근할 수 있도록 알아서 해줌(응답 받으면 바로 UI 업데이트)
 */

public class WebRequestActivity extends AppCompatActivity {

    Handler handler = new Handler();

    TextView tv;
    EditText edt2;

    static RequestQueue requestQueue; // 요청 큐 -> 한번만 만들어서 계속 사용함

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_request);

        edt2 = (EditText)findViewById(R.id.edt2);
        Button btn2 = (Button)findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                volleyRequest();
            }
        });

        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext()); // 요청 큐 객체 생성(여러번 생성 되지 않게 null일 경우만 생성)
        }

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

    public void volleyRequest() {
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