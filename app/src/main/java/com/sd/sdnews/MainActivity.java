//package com.sd.sdnews;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.app.AppCompatDelegate;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
//
//
//import java.util.ArrayList;
//
//public class MainActivity extends AppCompatActivity {
//
//    ArrayList<Model> model = new ArrayList<>();
//    RecyclerView recyclerView;
//    RecylerViewAdapter adapter;
//    SwipeRefreshLayout swipeRefresh;
//    TextView privacyPolicy;
//    ImageButton darkModeToggle;
//
//    private static final String PREFS_NAME = "SDNewsPrefs";
//    private static final String KEY_DARK_MODE = "darkMode";
//
//
//    public void buttonDownScroll(View view){
//        recyclerView.scrollBy(0,recyclerView.getWidth());
//    }
//    public void buttonUpScroll(View view){
//        recyclerView.scrollBy(0,-recyclerView.getWidth());
//    }
//    public void buttonContact(View view) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Contact Me");
//        builder.setMessage("Reach me at: \n\nsudarshan.cse065@gmail.com");
//        builder.setPositiveButton("Ok",null);
//        builder.show();
//    }
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//        boolean isdarkMode = prefs.getBoolean(KEY_DARK_MODE, false);
//        AppCompatDelegate.setDefaultNightMode(
//                isdarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
//        );
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        swipeRefresh = findViewById(R.id.swipeRefresh);
//        recyclerView = findViewById(R.id.mRecyclerView);
//        privacyPolicy = findViewById(R.id.privacyPolicy);
//        darkModeToggle = findViewById(R.id.darkModeToggle);
//
//        darkModeToggle.setImageResource(
//                isdarkMode ? R.drawable.ic_light_mode : R.drawable.ic_dark_mode
//
//        );
//
//        darkModeToggle.setOnClickListener(v -> {
//            boolean currentlyDark = prefs.getBoolean(KEY_DARK_MODE, false);
//            boolean newMode = !currentlyDark;
//            prefs.edit().putBoolean(KEY_DARK_MODE,newMode).apply();
//
//            AppCompatDelegate.setDefaultNightMode(
//                    newMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
//            );
//
//        });
//
//        loadNews();
//        swipeRefresh.setOnRefreshListener(this::loadNews);
//        privacyPolicy.setOnClickListener(v -> {
//            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("https://sites.google.com/view/sdnews-privacypolicy/home"));
//            startActivity(browserIntent);
//        });
//    }
//    private void loadNews(){
//
//        swipeRefresh.setRefreshing(true); // show refresh spinner
//        new Thread(()->{
//            model = RSSFeedParser.getFeedItems("https://www.thehindu.com/feeder/default.rss");
//            runOnUiThread(this::run);
//        }).start();
//    }
//
//    private void run() {
//        adapter = new RecylerViewAdapter(MainActivity.this, model);
//        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
//        recyclerView.setAdapter(adapter);
//        swipeRefresh.setRefreshing(false);
//        Toast.makeText(MainActivity.this, "News Updated..!!", Toast.LENGTH_SHORT).show();
//    }
//
//}
//

package com.sd.sdnews;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME   = "SDNewsPrefs";
    private static final String KEY_DARK_MODE = "darkMode";

    private ViewPager2 viewPager;
    private TabLayout categoryTabs;

    private FeedSource currentSource;
    private final FeedSource[] allSources = FeedSource.getAllSources();

    // Bottom nav item IDs in order — must match bottom_nav_menu.xml order
    private final int[] navIds = {
            R.id.nav_thehindu,
            R.id.nav_ndtv,
            R.id.nav_toi
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager     = findViewById(R.id.viewPager);
        categoryTabs  = findViewById(R.id.categoryTabs);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        ImageButton darkModeToggle = findViewById(R.id.toggle_dark_mode);
        TextView privacyPolicy = findViewById(R.id.privacyPolicy);

        darkModeToggle.setImageResource(
                isDarkMode ? R.drawable.ic_light_mode : R.drawable.ic_dark_mode
        );
        darkModeToggle.setOnClickListener(v -> {
            boolean current = prefs.getBoolean(KEY_DARK_MODE, false);
            prefs.edit().putBoolean(KEY_DARK_MODE, !current).apply();
            AppCompatDelegate.setDefaultNightMode(
                    !current ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        privacyPolicy.setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://sites.google.com/view/sdnews-privacypolicy/home")))
        );

        // Start with The Hindu
        currentSource = allSources[0];
        loadSource(currentSource);

        // Bottom nav switches source
        bottomNav.setOnItemSelectedListener(item -> {
            for (int i = 0; i < navIds.length; i++) {
                if (item.getItemId() == navIds[i]) {
                    currentSource = allSources[i];
                    loadSource(currentSource);
                    return true;
                }
            }
            return false;
        });
    }

    private void loadSource(FeedSource source) {
        List<String> labels = new ArrayList<>(source.categoryUrls.keySet());
        List<String> urls   = new ArrayList<>(source.categoryUrls.values());

        CategoryPagerAdapter adapter = new CategoryPagerAdapter(this, urls, source.name);
        viewPager.setAdapter(adapter);

        // Detach old mediator if any, then attach new one
        new TabLayoutMediator(categoryTabs, viewPager,
                (tab, position) -> tab.setText(labels.get(position))
        ).attach();
    }

    public void buttonContact(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Contact Me")
                .setMessage("Reach me at: \n\nsudarshan.cse065@gmail.com")
                .setPositiveButton("Ok", null)
                .show();
    }

    // ViewPager2 adapter — one FeedFragment per category
    static class CategoryPagerAdapter extends FragmentStateAdapter {
        private final List<String> urls;
        private final String sourceName;

        CategoryPagerAdapter(FragmentActivity fa, List<String> urls, String sourceName) {
            super(fa);
            this.urls = urls;
            this.sourceName = sourceName;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return FeedFragment.newInstance(urls.get(position), sourceName);
        }

        @Override
        public int getItemCount() { return urls.size(); }
    }
}
