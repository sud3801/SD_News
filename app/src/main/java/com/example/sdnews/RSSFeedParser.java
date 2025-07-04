package com.example.sdnews;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RSSFeedParser {

    public static ArrayList<Model> getFeedItems(String urlString) {
        ArrayList<Model> items = new ArrayList<>();
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            InputStream inputStream = conn.getInputStream();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            String title = null;
            String pubDate = null;
            String description = null;
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
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagName.equalsIgnoreCase("item")) {
                            items.add(new Model(title, pubDate, description));
                            insideItem = false;
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
