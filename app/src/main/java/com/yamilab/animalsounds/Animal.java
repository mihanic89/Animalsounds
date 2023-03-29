package com.yamilab.animalsounds;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import java.io.Serializable;

/**
 * Created by Михаил on 29.03.2017.
 */

public class Animal implements Serializable {
    private Context context;
    private String name;
    private Integer imageSmall;
    private Integer imageBig;
    private Integer sound;
    private boolean isGIF=false;
    private String gifHref;
    private String wikiName=null;

    public boolean isGIF() {
        return isGIF;
    }

    public String getGifHref() {
        return gifHref;
    }



    public  Animal  (String name, Integer imageSmall, Integer sound){
        this.name= name;
        this.imageSmall = imageSmall;
        this.isGIF=false;
        this.sound = sound;
        this.gifHref="";
    }

    public Animal(String name, Integer imageSmall, Integer sound, boolean isGIF, String gifHref){
        this.name= name;
        this.imageSmall = imageSmall;
        this.sound = sound;
        this.isGIF= isGIF;
        this.gifHref=gifHref;
        this.wikiName =null;
    }

    public Animal(String name, Integer imageSmall, Integer sound, boolean isGIF, String gifHref, String wikiName){
        this.name= name;
        this.imageSmall = imageSmall;
        this.sound = sound;
        this.isGIF= isGIF;
        this.gifHref=gifHref;
        this.wikiName = wikiName;
    }

    public Animal() {

    }
    public  Animal  (String name, Integer imageSmall, Integer sound, Context context){
        this.name= name;
        this.imageSmall = imageSmall;

        this.sound = sound;
        this.context=context;
    }

    public Animal(Context context){
        this.context=context;
    }

    public String getName(){
        String[] retval =name.split("#");
        return retval[0];
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



    public Integer getSound(){
        return sound;
    }

    public void  setSound(Integer sound){
        this.sound = sound;
    }

    public int getWidth(){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        //int width = size.x %3;
        return size.x %3;
    }

    public String getWikiName(){

        String[] retval =name.split("#");
        String answer = name;

        if (retval.length>1 ){
            answer = retval[1];
        }

            return answer;

    }
}
