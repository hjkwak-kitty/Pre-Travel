package com.example.hyojin.myapplication.activity.data;

import java.io.Serializable;

/**
 * Created by Hyojin on 2016-05-21.
 */
public class Movie implements Serializable {
    String genre;
    String actor;
    String moreinfo;
    String title;
    String year;
    String age;
    String time;
    String director;
    String thumbnailUrl;
    String eng_title;

    public Movie() {
    }

    public Movie(String genre, String actor, String moreinfo, String title, String date, String age, String time, String director, String poster, String eng_title){
        this.genre= genre;
        this.actor=actor;
        this.moreinfo=moreinfo;
        this.title=title;
        this.year=date;
        this.age=age;
        this.time=time;
        this.director=director;
        this.thumbnailUrl=poster;
        this.eng_title=eng_title;
    }


    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }


    public String getTitle(){
        return title+"("+year +")";
    }

    public String getDirector(){
        return director+"/"+time;
    }

    public String getGenre(){
        return genre+"/"+age;
    }
}
