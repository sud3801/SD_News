package com.sd.sdnews;

public class Model {
    String title;
    String date;
    String content;
    String publisher;

    public Model(String title, String publisher, String date, String content) {
        this.title = title;
        this.publisher = publisher;
        this.date = date;
        this.content = content;
        this.publisher = publisher;
    }

    public String getTitle() {
        return title;
    }

    public String getPublisher(){return publisher;}

    public String getDate() {
        return date;
    }

    public String getContent() {return content;}
}
