package com.sd.sdnews;

public class Model {
    String title;
    String date;
    String content;
    String publisher;

    String link;

    public Model(String title, String publisher, String date, String content, String link) {
        this.title = title;
        this.publisher = publisher;
        this.date = date;
        this.content = content;
        this.link=link;
    }

    public String getTitle() {
        return title;
    }

    public String getPublisher(){return publisher;}

    public String getDate() {
        return date;
    }

    public String getContent() {return content;}

    public String getLink(){return link;}
}
