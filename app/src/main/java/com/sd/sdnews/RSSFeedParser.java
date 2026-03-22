////package com.sd.sdnews;
////
////import android.util.Xml;
////
////import org.xmlpull.v1.XmlPullParser;
////
////import java.io.InputStream;
////import java.net.HttpURLConnection;
////import java.net.URL;
////import java.util.ArrayList;
////
////public class RSSFeedParser {
////
////    public static ArrayList<Model> getFeedItems(String urlString) {
////        ArrayList<Model> items = new ArrayList<>();
////        try {
////            URL url = new URL(urlString);
////            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
////            conn.setRequestMethod("GET");
////            conn.connect();
////
////            InputStream inputStream = conn.getInputStream();
////            XmlPullParser parser = Xml.newPullParser();
////            parser.setInput(inputStream, null);
////
////            int eventType = parser.getEventType();
////            String title = null;
////            String pubName = null;
////            String pubDate = null;
////            String description = null;
////            String link = null;
////            boolean insideItem = false;
////
////            while (eventType != XmlPullParser.END_DOCUMENT) {
////                String tagName = parser.getName();
////
////                switch (eventType) {
////                    case XmlPullParser.START_TAG:
////                        if (tagName.equalsIgnoreCase("item")) {
////                            insideItem = true;
////                        } else if (insideItem) {
////                            if (tagName.equalsIgnoreCase("title")) {
////                                title = parser.nextText();
////                            } else if (tagName.equalsIgnoreCase("pubDate")) {
////                                pubDate = parser.nextText();
////                            } else if (tagName.equalsIgnoreCase("description")) {
////                                description = parser.nextText();
////                            }
////                            else if (tagName.equalsIgnoreCase("link")) {
////                                link = parser.nextText();
////                            }
////
////                        }
////                        break;
////
////                    case XmlPullParser.END_TAG:
////                        if (tagName.equalsIgnoreCase("item")) {
////                            pubName = "The Hindu";
////                            items.add(new Model(title, pubName, pubDate, description,link));
////                            insideItem = false;
////                        }
////                        break;
////                }
////                eventType = parser.next();
////            }
////
////            inputStream.close();
////            conn.disconnect();
////
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////        return items;
////    }
////}
//package com.sd.sdnews;
//
//import android.util.Log;
//import android.util.Xml;
//import org.xmlpull.v1.XmlPullParser;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//
//public class RSSFeedParser {
//    private static final String TAG = "RSSFeedParser";
//
//    // Original method — keeps backward compatibility
//
//
//    // New overload — used by FeedFragment
//    public static ArrayList<Model> getFeedItems(String urlString, String sourceName) {
//        ArrayList<Model> items = new ArrayList<>();
//        try {
//            URL url = new URL(urlString);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setConnectTimeout(10000);
//            conn.setReadTimeout(10000);
//            conn.connect();
//
//            InputStream inputStream = conn.getInputStream();
//            XmlPullParser parser = Xml.newPullParser();
//            parser.setInput(inputStream, null);
//
//            int eventType = parser.getEventType();
//            String title = null, pubDate = null, description = null, link = null;
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
//                            } else if (tagName.equalsIgnoreCase("link")) {
//                                link = parser.nextText();
//                            }
//                        }
//                        break;
//
//                    case XmlPullParser.END_TAG:
//                        if (tagName.equalsIgnoreCase("item")) {
//                            items.add(new Model(title, sourceName, pubDate, description, link));
//                            insideItem = false;
//                            title = pubDate = description = link = null;
//                        }
//                        break;
//                }
//                eventType = parser.next();
//            }
//            inputStream.close();
//            conn.disconnect();
//
//        } catch (Exception e) {
//            Log.e(TAG, "Error parsing RSS feed from " + urlString, e);
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

    public static ArrayList<Model> getFeedItems(String urlString, String sourceName) {
        ArrayList<Model> items = new ArrayList<>();
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            // Some feeds reject requests with no User-Agent and return empty/blocked responses
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.connect();

            InputStream inputStream = conn.getInputStream();
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true); // enables content:encoded
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            String title = null, pubDate = null, description = null,
                    contentEncoded = null, link = null;
            boolean insideItem = false;
            boolean skipTag = false;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName == null) break;

                        if (tagName.equalsIgnoreCase("item")) {
                            insideItem = true;

                        } else if (insideItem) {

                            if (tagName.equalsIgnoreCase("title")) {
                                title = cleanText(parser.nextText());

                            } else if (tagName.equalsIgnoreCase("pubDate")) {
                                pubDate = cleanText(parser.nextText());

                            } else if (tagName.equalsIgnoreCase("description")) {
                                description = stripHtml(parser.nextText());

                            } else if (tagName.equalsIgnoreCase("encoded")) {
                                // content:encoded — richer body text, used by NDTV and others
                                // Only grab it if description is empty, as it can be very long
                                String encoded = stripHtml(parser.nextText());
                                if (contentEncoded == null) contentEncoded = encoded;

                            } else if (tagName.equalsIgnoreCase("link")) {
                                // <link> is tricky — in Atom feeds it's a self-closing tag
                                // with an href attribute, not a text node
                                try {
                                    // First check if it has an href attribute (Atom style)
                                    String href = parser.getAttributeValue(null, "href");
                                    if (href != null && !href.isEmpty()) {
                                        link = href.trim();
                                    } else {
                                        // RSS style — text node
                                        String text = parser.nextText();
                                        if (text != null && !text.isEmpty()) {
                                            link = text.trim();
                                        }
                                    }
                                } catch (Exception e) {
                                    // nextText() can throw if the tag is self-closing
                                    // silently skip — link stays null
                                }
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagName == null) break;

                        if (tagName.equalsIgnoreCase("item")) {
                            // Prefer description; fall back to content:encoded if empty
                            String body = (description != null && !description.isEmpty())
                                    ? description
                                    : contentEncoded;

                            // Only add item if it has at least a title
                            if (title != null && !title.isEmpty()) {
                                items.add(new Model(title, sourceName, pubDate, body, link));
                            }

                            // Reset all fields for the next item
                            title = pubDate = description = contentEncoded = link = null;
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

    /**
     * Strips HTML tags from text returned by feeds.
     * e.g. "<p>Hello <b>world</b></p>" → "Hello world"
     * Also decodes common HTML entities.
     */
    private static String stripHtml(String raw) {
        if (raw == null) return null;
        // Remove all HTML tags
        String stripped = raw.replaceAll("<[^>]*>", "");
        // Decode common HTML entities
        stripped = stripped
                .replace("&amp;",  "&")
                .replace("&lt;",   "<")
                .replace("&gt;",   ">")
                .replace("&quot;", "\"")
                .replace("&#39;",  "'")
                .replace("&nbsp;", " ")
                .replace("&apos;", "'");
        return stripped.trim();
    }

    /**
     * Trims whitespace and normalises line breaks.
     */
    private static String cleanText(String raw) {
        if (raw == null) return null;
        return raw.replaceAll("\\s+", " ").trim();
    }
}