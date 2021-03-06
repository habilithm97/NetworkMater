package com.example.networkmater;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/*
*http 프로토콜은 소켓으로 웹서버에 연결한 후에 요청을 전송하고 응답을 받은 다음 연결을 끊음 -> 비연결성(Stateless)
*소켓 : 소켓 통신에서는 클라이언트와 서버 사이의 연결이 지속되고 실시간으로 서로 데이터를 주고 받음
 -> 소켓 연결 등을 시도하거나 응답을 받아서 처리할 때 스레드를 사용해야됨

*자바에서 HTTP 클라이언트를 만드는 가장 간단한 방법은 URL 객체 생성 후 이 객체의 openConnection()을 호출하여 HttpURLConnection 객체를 만드는 것임

*직렬화(Serialize) : 자바 시스템 내부에서 사용되는 Object 또는 Data를 외부의 자바 시스템에서도 사용할 수 있도록 byte 형태로 변환하는 기술
 -JVM의 메모리에 상주되어 있는 객체 데이터를 바이트 형태로 변환하는 기술, 직렬화된 바이트 형태의 데이터를 객체로 변환해서 JVM으로 상주시키는 형태
 -직렬화를 통해 프로그램이 실행되는 동안 생성된 객체를 스트림을 이용해서 지속적으로 보관하거나 전송할 수 있음
*/

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    Handler handler = new Handler();

    TextView tv, tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn3 = (Button)findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WebRequestActivity.class);
                startActivity(intent);
            }
        });

        EditText edt = (EditText) findViewById(R.id.edt);
        tv = (TextView) findViewById(R.id.tv);
        tv2 = (TextView) findViewById(R.id.tv2);

        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edt.getText().toString().equals("") || edt.getText().toString() == null) {
                    Toast.makeText(getApplicationContext(), "입력된 텍스트가 없습니다. ", Toast.LENGTH_SHORT).show();
                } else {
                    final String data = edt.getText().toString();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            sendData(data);
                        }
                    }).start();
                }
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
            // ObjectOutputStream/ObjectInputStream은 실제 앱에서는 자바가 아닌 다른 언어로 만들어진 서버와 통신할 경우가 있기 때문에 잘 사용하지 않음
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream()); // 보내기 위한 통로 생성
            outputStream.writeObject(data); // 객체를 직렬화함
            outputStream.flush(); // writeObject()하면 버퍼에 남아 있을 수 있기 때문에 flush()
            printClientLog("데이터 전송 완료. ");



            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            printClientLog("서버로부터 받아옴 : " + inputStream.readObject()); // 서버에서 보낸 객체 읽기
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