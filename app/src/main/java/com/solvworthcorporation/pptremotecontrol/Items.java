package com.solvworthcorporation.pptremotecontrol;

import android.content.Intent;


/**
 * Created by packard bell on 30/04/2018.
 */

public class Items {
    private String name;
    private int thumbnail;
    private Intent intent;
    private String link;
    private int color;

    public Items(String name){
        this.name = name;
    }

    public Items(String name, int thumbnail){
        this.thumbnail = thumbnail;
        this.name = name;
    }

    public String getLink(){
        return link;
    }

    public void setLink(String content){
        this.link = content;
    }

    public String getName(){
        return name;
    }

    public void setName(String content){
        this.name = content;
    }

    public int getColor(){
        return color;
    }

    public void setColor(int color){
        this.color = color;
    }

    public int getThumbnail(){
        return thumbnail;
    }

    public void setThumbnail(int content){
        this.thumbnail = content;
    }

    public Intent getIntent(){
        return intent;
    }

    public void setIntent(Intent intent){
        this.intent = intent;
    }

}
