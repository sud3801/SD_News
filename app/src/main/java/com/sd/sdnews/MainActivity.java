package com.sd.sdnews;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
    TextView privacyPolicy;

    public void buttonDownScroll(View view){
        recyclerView.scrollBy(0,recyclerView.getWidth());
    }
    public void buttonUpScroll(View view){
        recyclerView.scrollBy(0,-recyclerView.getWidth());
    }
    public void buttonContact(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Contact Me");
        builder.setMessage("Reach me at: \n\nsudarshan.cse065@gmail.com");
        builder.setPositiveButton("Ok",null);
        builder.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        recyclerView = findViewById(R.id.mRecyclerView);
        privacyPolicy = findViewById(R.id.privacyPolicy);
        loadNews();
        swipeRefresh.setOnRefreshListener(this::loadNews);
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://sites.google.com/view/sdnews-privacypolicy/home"));
                startActivity(browserIntent);
            }
        });
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

