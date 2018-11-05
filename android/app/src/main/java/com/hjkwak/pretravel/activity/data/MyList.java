package com.hjkwak.pretravel.activity.data;

import java.io.Serializable;

/**
 * Created by Hyojin on 2016-05-22.
 */
public class MyList implements Serializable {
    String text_uid, country, city, con_title, con_data1, con_data2, con_data3, con_data4,  con_photo, created_at;
    int recommend;
    public MyList(String text_uid, String country, String city, String con_title,
                  String con_data1, String con_data2, String con_data3, String con_data4, String con_photo, int recommend, String created_at) {
        this.text_uid=text_uid;
        this.country=country;
        this.city=city;
        this.con_title= con_title;
        this.con_data1 =con_data1;
        this.con_data2=con_data2;
        this.con_data3=con_data3;
        this.con_data4=con_data4;
        this.con_photo = con_photo;
        this.recommend = recommend;
        this.created_at= created_at;

    }

    public String getdate(){ return created_at;}

    public String getCountry(){return country;}
    public String getCity(){return city;}

    public String getThumbnailUrl() {
        return con_photo;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.con_photo = thumbnailUrl;
    }

    public String getTitle(){ return con_title; }
    public int getRecommend(){return recommend;}

    public String getDirector(){return con_data1;
    }
    public String getGenre(){
        return con_data2;
    }
    public String getCon_data3(){return con_data3;}
    public String getCon_data4(){return con_data4;}
    public String getText_uid(){return text_uid;}

    public String getWhere(){
        if(city.equals("")||city.equals("null"))
            return country;
        else
            return country+" "+city;
    }
}
