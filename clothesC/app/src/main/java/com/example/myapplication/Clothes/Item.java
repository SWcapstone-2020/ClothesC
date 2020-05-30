package com.example.myapplication.Clothes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Item implements Serializable {
    private String title;
    private ArrayList<String> contents;
    private ArrayList<String> formats;
    private String publisher;
    private Date createdAt;
    private String season;
    private String kind;
    private String id;

    public Item(String title, ArrayList contents, ArrayList formats, String publisher, Date createdAt, String season, String kind) {
        this.title=title;
        this.contents=contents;
        this.formats=formats;
        this.publisher=publisher;
        this.createdAt=createdAt;
        this.season=season;
        this.kind=kind;
    }

    public Item(String title, ArrayList contents, ArrayList formats, String publisher, Date createdAt, String season, String kind, String id) {
        this.title=title;
        this.contents=contents;
        this.formats=formats;
        this.publisher=publisher;
        this.createdAt=createdAt;
        this.season=season;
        this.kind=kind;
        this.id=id;
    }

    public Map<String, Object> getItemInfo(){
        Map<String, Object> itemData = new HashMap<>();
        itemData.put("title",title);
        itemData.put("contents",contents);
        itemData.put("formats",formats);
        itemData.put("publisher",publisher);
        itemData.put("createdAt",createdAt);
        itemData.put("season",season);
        itemData.put("kind",kind);
        return  itemData;
    }


    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public ArrayList<String> getContents(){
        return this.contents;
    }
    public void setContents(ArrayList<String> contents){
        this.contents = contents;
    }
    public ArrayList<String> getFormats(){
        return this.formats;
    }
    public void setFormats(ArrayList<String> formats){
        this.formats = formats;
    }
    public String getPublisher(){
        return this.publisher;
    }
    public void setPublisher(String publisher){
        this.publisher = publisher;
    }
    public Date getCreatedAt(){
        return this.createdAt;
    }
    public void setCreatedAt(Date createdAt){
        this.createdAt = createdAt;
    }
    public String getSeason(){return this.season;}
    public void setSeason(String season){this.season=season; }
    public String getKind(){return this.kind;}
    public void setKind(String kind){this.kind=kind; }

    public String getId(){
        return this.id;
    }
    public void setId(String id){
        this.id = id;
    }


}
