package com.sd.sdnews;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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
    ImageButton darkModeToggle;

    private static final String PREFS_NAME = "SDNewsPrefs";
    private static final String KEY_DARK_MODE = "darkMode";


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
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isdarkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        AppCompatDelegate.setDefaultNightMode(
                isdarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        recyclerView = findViewById(R.id.mRecyclerView);
        privacyPolicy = findViewById(R.id.privacyPolicy);
        darkModeToggle = findViewById(R.id.darkModeToggle);

        darkModeToggle.setImageResource(
                isdarkMode ? R.drawable.ic_light_mode : R.drawable.ic_dark_mode

        );

        darkModeToggle.setOnClickListener(v -> {
            boolean currentlyDark = prefs.getBoolean(KEY_DARK_MODE, false);
            boolean newMode = !currentlyDark;
            prefs.edit().putBoolean(KEY_DARK_MODE,newMode).apply();

            AppCompatDelegate.setDefaultNightMode(
                    newMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );

        });

        loadNews();
        swipeRefresh.setOnRefreshListener(this::loadNews);
        privacyPolicy.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://sites.google.com/view/sdnews-privacypolicy/home"));
            startActivity(browserIntent);
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

