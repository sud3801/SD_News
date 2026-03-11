//package com.sd.sdnews;
//
//import android.util.Xml;
//
//import org.xmlpull.v1.XmlPullParser;
//
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//
//public class RSSFeedParser {
//
//    public static ArrayList<Model> getFeedItems(String urlString) {
//        ArrayList<Model> items = new ArrayList<>();
//        try {
//            URL url = new URL(urlString);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.connect();
//
//            InputStream inputStream = conn.getInputStream();
//            XmlPullParser parser = Xml.newPullParser();
//            parser.setInput(inputStream, null);
//
//            int eventType = parser.getEventType();
//            String title = null;
//            String pubName = null;
//            String pubDate = null;
//            String description = null;
//            String link = null;
//            boolean insideItem = false;
//
//            while (eventType != XmlPullParser.END_DOCUMENT) {
//                String tagName = parser.getName();
//
//                switch (eventType) {
//                    case XmlPullParser.START_TAG:
//                        if (tagName.equalsIgnoreCase("item")) {
//                            insideItem = true;
//                        } else if (insideItem) {
//                            if (tagName.equalsIgnoreCase("title")) {
//                                title = parser.nextText();
//                            } else if (tagName.equalsIgnoreCase("pubDate")) {
//                                pubDate = parser.nextText();
//                            } else if (tagName.equalsIgnoreCase("description")) {
//                                description = parser.nextText();
//                            }
//                            else if (tagName.equalsIgnoreCase("link")) {
//                                link = parser.nextText();
//                            }
//
//                        }
//                        break;
//
//                    case XmlPullParser.END_TAG:
//                        if (tagName.equalsIgnoreCase("item")) {
//                            pubName = "The Hindu";
//                            items.add(new Model(title, pubName, pubDate, description,link));
//                            insideItem = false;
//                        }
//                        break;
//                }
//                eventType = parser.next();
//            }
//
//            inputStream.close();
//            conn.disconnect();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return items;
//    }
//}
package com.sd.sdnews;

import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RSSFeedParser {

    // Original method — keeps backward compatibility


    // New overload — used by FeedFragment
    public static ArrayList<Model> getFeedItems(String urlString, String sourceName) {
        ArrayList<Model> items = new ArrayList<>();
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.connect();

            InputStream inputStream = conn.getInputStream();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            String title = null, pubDate = null, description = null, link = null;
            boolean insideItem = false;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equalsIgnoreCase("item")) {
                            insideItem = true;
                        } else if (insideItem) {
                            if (tagName.equalsIgnoreCase("title")) {
                                title = parser.nextText();
                            } else if (tagName.equalsIgnoreCase("pubDate")) {
                                pubDate = parser.nextText();
                            } else if (tagName.equalsIgnoreCase("description")) {
                                description = parser.nextText();
                            } else if (tagName.equalsIgnoreCase("link")) {
                                link = parser.nextText();
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagName.equalsIgnoreCase("item")) {
                            items.add(new Model(title, sourceName, pubDate, description, link));
                            insideItem = false;
                            title = pubDate = description = link = null;
                        }
                        break;
                }
                eventType = parser.next();
            }
            inputStream.close();
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }
}