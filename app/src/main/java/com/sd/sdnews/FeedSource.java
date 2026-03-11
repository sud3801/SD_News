package com.sd.sdnews;

import java.util.LinkedHashMap;
import java.util.Map;
public class FeedSource {
    public final String name;
    public final Map<String, String> categoryUrls;

    public FeedSource(String name, Map<String, String> categoryUrls) {
        this.name = name;
        this.categoryUrls = categoryUrls;
    }

    public static FeedSource[] getAllSources(){
    return new FeedSource[]{
            theHindu(),
            ndtv(),
            timesOfIndia()
        };
    }

    private static FeedSource theHindu() {
        Map<String, String> urls = new LinkedHashMap<>();
        urls.put("Latest Stories", "https://www.thehindu.com/feeder/default.rss");
        urls.put("World", "https://www.thehindu.com/news/international/feeder/default.rss");
        urls.put("Science", "https://www.thehindu.com/sci-tech/science/feeder/default.rss");
        urls.put("Technology", "https://www.thehindu.com/sci-tech/technology/feeder/default.rss");
        urls.put("Sports", "https://www.thehindu.com/sport/feeder/default.rss");
        return new FeedSource("The Hindu", urls);
    }

    private static FeedSource ndtv() {
        Map<String, String> urls = new LinkedHashMap<>();
        urls.put("Top Stories", "https://feeds.feedburner.com/ndtvnews-top-stories");
        urls.put("World",       "https://feeds.feedburner.com/ndtvnews-world-news");
        urls.put("Business",     "https://feeds.feedburner.com/ndtvprofit-latest");
        urls.put("Technology",  "https://feeds.feedburner.com/gadgets360-latest");
        urls.put("Sports",      "https://feeds.feedburner.com/ndtvsports-latest");
        return new FeedSource("NDTV", urls);
    }

//    private static FeedSource bbc() {
//        Map<String, String> urls = new LinkedHashMap<>();
//        urls.put("Top Stories", "https://feeds.bbci.co.uk/news/rss.xml?edition=int");
//        urls.put("World",       "https://feeds.bbci.co.uk/news/world/rss.xml");
//        urls.put("Science",     "https://feeds.bbci.co.uk/news/science_and_environment/rss.xml");
//        urls.put("Technology",  "https://feeds.bbci.co.uk/news/technology/rss.xml");
//        urls.put("Sports",      "https://feeds.bbci.co.uk/sport/rss.xml");
//        return new FeedSource("BBC News", urls);
//    }

    private static FeedSource timesOfIndia() {
        Map<String, String> urls = new LinkedHashMap<>();
        urls.put("Top Stories", "https://timesofindia.indiatimes.com/rssfeedstopstories.cms");
        urls.put("World",       "https://timesofindia.indiatimes.com/rssfeeds/296589292.cms");
        urls.put("Science",     "https://timesofindia.indiatimes.com/rssfeeds/-2128672765.cms");
        urls.put("Technology",  "https://timesofindia.indiatimes.com/rssfeeds/-2128672765.cms");
        urls.put("Sports",      "https://timesofindia.indiatimes.com/rssfeeds/4719148.cms");
        return new FeedSource("Times of India", urls);
    }

}