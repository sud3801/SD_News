package com.example.sdnews;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Model> model = new ArrayList<>();
    RecyclerView recyclerView;
    RecylerViewAdapter adapter;
    SwipeRefreshLayout swipeRefresh;


    public void buttonDownScroll(View view){
        recyclerView.scrollBy(0,recyclerView.getWidth());
    }
    public void buttonUpScroll(View view){
        recyclerView.scrollBy(0,-recyclerView.getWidth());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        recyclerView = findViewById(R.id.mRecyclerView);
        loadNews();
        swipeRefresh.setOnRefreshListener(this::loadNews);
    }
    private void loadNews(){

        swipeRefresh.setRefreshing(true); // show refresh spinner
        new Thread(()->{
            model = RSSFeedParser.getFeedItems("https://www.thehindu.com/feeder/default.rss");
            runOnUiThread(this::run);
        }).start();
    }

    private void run() {
        adapter = new RecylerViewAdapter(MainActivity.this, model);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(adapter);
        swipeRefresh.setRefreshing(false);
        Toast.makeText(MainActivity.this, "News Updated..!!", Toast.LENGTH_SHORT).show();
    }
}

