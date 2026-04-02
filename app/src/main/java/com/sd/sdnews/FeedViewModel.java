package com.sd.sdnews;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class FeedViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<Model>> articles = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);

    public LiveData<ArrayList<Model>> getArticles() { return articles; }
    public LiveData<Boolean> getIsLoading()         { return isLoading; }
    public LiveData<String>  getError()             { return error; }

    public void loadFeed(String url, String sourceName) {
        isLoading.setValue(true);
        error.setValue(null);

        new Thread(() -> {
            try {
                ArrayList<Model> items = RSSFeedParser.getFeedItems(url, sourceName);
                // postValue is safe to call from background threads
                articles.postValue(items);
                isLoading.postValue(false);
            } catch (Exception e) {
                error.postValue("Couldn't load feed. Pull to refresh.");
                isLoading.postValue(false);
            }
        }).start();
    }
}