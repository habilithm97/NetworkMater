package com.example.networkmater;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

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
                        sendData(data);
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
                        startServer();
                    }
                }).start();
            }
        });
    }


    public void sendData(String data) { // 클라이언트 기능 소켓
        try {
            int portNum = 5001; // 포트 번호
            Socket socket = new Socket("localhost", portNum); // 소켓 객체 생성 및 서버 접속
            printClientLog("소켓으로 서버 접속 완료. ");

            // 소켓 객체로 데이터 전송하기
            // ObjectOutputStream과 ObjectInputStream은 실제 앱에서는 자바가 아닌 다른 언어로 만들어진 서버와 통신할 경우가 있기 때문에 잘 사용하지않음
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream()); // 보내기 위한 통로 생성
            outputStream.writeObject(data);
            outputStream.flush();
            printClientLog("데이터 전송 완료. ");

            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            printClientLog("서버로부터 받아옴 : " + inputStream.readObject());
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startServer() { // 서버 기능 소켓
        try {
            int portNum = 5001;

            ServerSocket serverSocket = new ServerSocket(portNum); // 소켓 서버 객체 생성
            printServerLog(portNum + "서버 시작함. ");

            while(true) {
                Socket socket = serverSocket.accept(); // 클라이언트에서 요청이 왔을 때 대응해주는 객체(클라이언트 요청 정보), serverSocket.accept() : 대기 상태

                // IP주소와 포트번호 받음
                InetAddress clientHost = socket.getLocalAddress();
                int clientPort = socket.getPort();
                printServerLog("클라이언트 연결 완료 : " + clientHost + " : " + clientPort);

                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream()); // 들어오는 데이터 처리
                Object obj = inputStream.readObject(); // 클라이언트에서 보낸 객체 읽기
                printServerLog("데이터 받음 : " + obj);

                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream()); // 클라이언트 쪽으로 데이터 보냄
                outputStream.writeObject("서버로부터 받은 " + obj);
                outputStream.flush(); // writeObject()하면 버퍼에 남아 있을 수 있기 때문에 flush()
                printServerLog("데이터 보냄. ");

                socket.close(); // 한정적인 리소스를 낭비하지않고 유지하려면 소켓 객체 연결을 끊어야함
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printClientLog(final String data) { // final을 사용한 이유 : 파라미터가 그대로 전달되어야하므로
        Log.d(TAG, data);
        handler.post(new Runnable() { // 러너블 객체로 텍스트뷰에 접근
            @Override
            public void run() {
                tv.append(data + "\n");
            }
        });
    }

    public void printServerLog(final String data) { // final을 사용한 이유 : 파라미터가 그대로 전달되어야하므로
        { // final을 사용한 이유 : 파라미터가 그대로 전달되어야하므로
            Log.d(TAG, data);
            handler.post(new Runnable() { // 러너블 객체로 텍스트뷰에 접근
                @Override
                public void run() {
                    tv2.append(data + "\n");
                }
            });
        }
    }
}