package com.yamilab.animalsounds;

import java.io.Serializable;

/**
 * Created by Михаил on 29.03.2017.
 */

public class LinkItem implements Serializable {

    private String name;
    private Integer image;

    private String link;

    public LinkItem(String name,  String link){
        this.name= name;


        this.link = link;
    }

    public LinkItem() {

    }

    public String getName(){
        return name;
    }

    public void  setName(String name){
        this.name = name;
    }

    public Integer getImage(){
        return image;
    }

    public void  setImage(Integer image){
        this.image = image;
    }



    public String getLink(){
        return link;
    }


}
