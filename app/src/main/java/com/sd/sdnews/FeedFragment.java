package com.sd.sdnews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class FeedFragment extends Fragment {

    private static final String ARG_URL    = "url";
    private static final String ARG_SOURCE = "source";

    private RecyclerView recyclerView;
    private FeedViewModel viewModel;

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
        recyclerView = view.findViewById(R.id.mRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        String url        = requireArguments().getString(ARG_URL);
        String sourceName = requireArguments().getString(ARG_SOURCE);

        // ViewModel survives rotation and fragment detach — no more crashes
        viewModel = new ViewModelProvider(this).get(FeedViewModel.class);

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            swipeRefresh.setRefreshing(isLoading);
            if (!isLoading && viewModel.getArticles().getValue() != null) {
                Toast.makeText(requireContext(), "News Updated..!!", Toast.LENGTH_SHORT).show();
            }
        });

        // Observe articles — update adapter when data arrives
        viewModel.getArticles().observe(getViewLifecycleOwner(), items -> {
            recyclerView.setAdapter(new RecylerViewAdapter(requireContext(), items));
        });

        // Observe errors — show empty state message (Fix 4 will add the view)
        viewModel.getError().observe(getViewLifecycleOwner(), errorMsg -> {
            // wired up in Fix 4 when we add the empty state view
        });

        // Only fetch if we don't already have data (e.g. after rotation)
        if (viewModel.getArticles().getValue() == null) {
            viewModel.loadFeed(url, sourceName);
        }

        swipeRefresh.setOnRefreshListener(() -> viewModel.loadFeed(url, sourceName));
    }

    public void scrollToTop() {
        if (recyclerView != null) {
            recyclerView.smoothScrollToPosition(0);
        }
    }
}
