package com.example.myapplication.Clothes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Item implements Serializable {
    private ArrayList<String> contents;
    private ArrayList<String> formats;
    private String publisher;
    private Date createdAt;
    private String kind;
    private String id;

    public Item(ArrayList contents, ArrayList formats, String publisher, Date createdAt,  String kind) {
        this.contents=contents;
        this.formats=formats;
        this.publisher=publisher;
        this.createdAt=createdAt;
        this.kind=kind;
    }

//    public Item(ArrayList contents, ArrayList formats, String publisher, Date createdAt, String kind, String id) {
//        this.contents=contents;
//        this.formats=formats;
//        this.publisher=publisher;
//        this.createdAt=createdAt;
//        this.kind=kind;
//        this.id=id;
//    }

    public Map<String, Object> getItemInfo(){
        Map<String, Object> itemData = new HashMap<>();
        itemData.put("contents",contents);
        itemData.put("formats",formats);
        itemData.put("publisher",publisher);
        itemData.put("createdAt",createdAt);
        itemData.put("kind",kind);
        return  itemData;
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
    public String getKind(){return this.kind;}
    public void setKind(String kind){this.kind=kind; }

    public String getId(){
        return this.id;
    }
    public void setId(String id){
        this.id = id;
    }


}
