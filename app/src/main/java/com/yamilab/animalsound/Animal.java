package com.yamilab.animalsound;

import java.io.Serializable;

/**
 * Created by Михаил on 29.03.2017.
 */

public class Animal implements Serializable {

    private String name;
    private Integer imageSmall;
    private Integer imageBig;
    private Integer sound;

    public  Animal  (String name, Integer imageSmall, Integer imageBig, Integer sound){
        this.name= name;
        this.imageSmall = imageSmall;
        this.imageBig = imageBig;
        this.sound = sound;
    }

    public Animal() {

    }

    public String getName(){
        return name;
    }

    public void  setName(String name){
        this.name = name;
    }

    public Integer getImageSmall(){
        return imageSmall;
    }

    public void  setImageSmall(Integer imageSmall){
        this.imageSmall = imageSmall;
    }

    public Integer getImageBig(){
        return imageBig;
    }

    public void  setImageBig(Integer imageBig){
        this.imageBig = imageBig;
    }

    public Integer getSound(){
        return sound;
    }

    public void  setSound(Integer sound){
        this.sound = sound;
    }
}
