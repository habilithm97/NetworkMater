package com.example.networkmater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class GsonCardView extends AppCompatActivity {

    private static final String TAG = "GsonCardView";

    static RequestQueue requestQueue; // 요청 큐는 한 번만 만들어 계속 사용할 수 있기 때문에 static 변수로 선언함

    RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gson_card_view);

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        GsonRequest();
    }

    public void GsonRequest() {
        String url = "https://www.kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json?key=0850a722a3fec449b4bce97d7bca5433&targetDt=20200302";

        // URL에서 문자열 응답을 요청함
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                println("응답 : " + response);

                processResponse(response); // 응답을 받았을 때 호출됨
            }
        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        println("에러 : " + error.getMessage());
                    }
                }
        ) {
            // 서버에 전송할 데이터를 Map 객체에 담아 반환하게 되면
            // 이 메서드에서 반환한 Map 객체의 데이터를 웹의 질의 문자열 형식으로 만들어 요청 큐에서 서버 요청 시 서버에 전송됨
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };

        request.setShouldCache(false); // 이전 응답 결과를 사용하지 않겠다면 캐시를 사용하지 않도록 false
        requestQueue.add(request);
        println("요청 전송. ");
    }

    public void println(String data) {
        Log.d(TAG, data);
    }

    public void processResponse(String response) {
        Gson gson = new Gson();
        MovieList movieList = gson.fromJson(response, MovieList.class); // 응답 받은 JSON 문자열을 MovieList 객체로 변환함

        println("영화 정보 수 : " + movieList.boxOfficeResult.dailyBoxOfficeList.size());

        for(int i = 0; i < movieList.boxOfficeResult.dailyBoxOfficeList.size(); i++) {
            // 영화 목록 객체에서 데이터를 가져와 그 수 만큼 어댑터로 표시함
            Movie movie = movieList.boxOfficeResult.dailyBoxOfficeList.get(i);
            adapter.addItem(movie);
        }
        adapter.notifyDataSetChanged();
    }
}