package com.example.hyojin.myapplication.activity.data;

import java.io.Serializable;

/**
 * Created by Hyojin on 2016-05-21.
 */
public class Book implements Serializable {
    String title;
    String author;
    String publish ;
    String category;
    String description;
    String cover;

    public Book(String title, String author, String publish , String category, String description, String cover) {
        this.title=title;
        this.author=author;
        this.publish=publish;
        this.category=category;
        this.description=description;
        this.cover=cover;
    }


    public String getThumbnailUrl() {
        return cover;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.cover = thumbnailUrl;
    }

    public String getTitle(){ return title; }

    public String getDirector(){
        return author+"/"+publish;
    }

    public String getGenre(){
        return category;
    }
}
