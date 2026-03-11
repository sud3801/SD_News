//package com.sd.sdnews;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
//
//import java.util.ArrayList;
//
//public class FeedFragment extends Fragment {
//
//    private static final String ARG_URL         = "feed_url";
//    private static final String ARG_SOURCE_NAME = "source_name";
//
//    private String feedUrl;
//    private String sourceName;
//
//    private RecyclerView recyclerView;
//    private SwipeRefreshLayout swipeRefresh;
//
//    public static FeedFragment newInstance(String url, String sourceName) {
//        FeedFragment fragment = new FeedFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_URL, url);
//        args.putString(ARG_SOURCE_NAME, sourceName);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            feedUrl    = getArguments().getString(ARG_URL);
//            sourceName = getArguments().getString(ARG_SOURCE_NAME);
//        }
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_feed, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        recyclerView = view.findViewById(R.id.fragmentRecyclerView);
//        swipeRefresh = view.findViewById(R.id.fragmentSwipeRefresh);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        loadFeed();
//        swipeRefresh.setOnRefreshListener(this::loadFeed);
//    }
//
//    public void loadFeed() {
//        swipeRefresh.setRefreshing(true);
//        String urlToFetch = feedUrl;
//        String src = sourceName;
//        new Thread(() -> {
//            ArrayList<Model> items = RSSFeedParser.getFeedItems(urlToFetch, src);
//            if (getActivity() != null) {
//                getActivity().runOnUiThread(() -> {
//                    RecylerViewAdapter adapter = new RecylerViewAdapter(getContext(), items);
//                    recyclerView.setAdapter(adapter);
//                    swipeRefresh.setRefreshing(false);
//                });
//            }
//        }).start();
//    }
//}
package com.sd.sdnews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.ArrayList;

public class FeedFragment extends Fragment {

    private static final String ARG_URL    = "url";
    private static final String ARG_SOURCE = "source";

    public static FeedFragment newInstance(String url, String sourceName) {
        FeedFragment f = new FeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        args.putString(ARG_SOURCE, sourceName);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SwipeRefreshLayout swipeRefresh = view.findViewById(R.id.swipeRefresh);
        RecyclerView recyclerView = view.findViewById(R.id.mRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        String url        = requireArguments().getString(ARG_URL);
        String sourceName = requireArguments().getString(ARG_SOURCE);

        loadFeed(url, sourceName, swipeRefresh, recyclerView);
        swipeRefresh.setOnRefreshListener(() ->
                loadFeed(url, sourceName, swipeRefresh, recyclerView));
    }

    private void loadFeed(String url, String sourceName,
                          SwipeRefreshLayout swipeRefresh, RecyclerView recyclerView) {
        swipeRefresh.setRefreshing(true);
        new Thread(() -> {
            ArrayList<Model> items = RSSFeedParser.getFeedItems(url, sourceName);
            requireActivity().runOnUiThread(() -> {
                recyclerView.setAdapter(
                        new RecylerViewAdapter(requireContext(), items));
                swipeRefresh.setRefreshing(false);
                Toast.makeText(requireContext(), "Updated!", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }
}