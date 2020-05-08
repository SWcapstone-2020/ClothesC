package com.example.myapplication;

public class Writeinfo {
    private String title;
    private String contents;
    private String publisher;

    public Writeinfo(String title, String contents, String publisher) {
        this.title = title;
        this.contents = contents;
        this.publisher = publisher;
    }
    public String getTitle() {
        return this.title;
    }

    public String getContents() {
        return this.contents;
    }

    public String getPublisher() {
        return this.publisher;
    }


    public void getTitle(String title) {
        this.title = title;
    }

    public void getContents(String contents) {
        this.contents = contents;
    }
    public void getPublisher(String publisher) {
        this.contents = contents;
    }



}

