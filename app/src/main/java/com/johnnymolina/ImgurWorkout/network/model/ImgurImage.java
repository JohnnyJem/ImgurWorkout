package com.johnnymolina.ImgurWorkout.network.model;

import java.util.List;

import io.realm.RealmObject;


/*-------- This is the realm.io model used to store JSON list image objects from the main JSON object from Imgur.com api calls*/

public class ImgurImage extends RealmObject {
    private String id;
    private String title;
    private String description;
    private String link;
    private String sysLink;
    //Album string associates this object with its ImgurAlbum model object.
    // Consists of AlbumID+AlbumUnique generated through NextSessionId() method.
    private String album;
    private int length;

    private boolean switchValue;//false = Timed   true = Sets
    private int spinner1;
    private int spinner2;
    private int spinner3;
    private int spinner4;
    private int spinner5;

    private int slideValue; // from 0 - 120 s's


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSysLink() {
        return sysLink;
    }

    public void setSysLink(String sysLink) {
        this.sysLink = sysLink;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isSwitchValue() {
        return switchValue;
    }


    public int getSlideValue() {
        return slideValue;
    }

    public void setSlideValue(int slideValue) {
        this.slideValue = slideValue;
    }

    public void setSwitchValue(boolean switchValue) {
        this.switchValue = switchValue;
    }



    public int getSpinner1() {
        return spinner1;
    }

    public void setSpinner1(int spinner1) {
        this.spinner1 = spinner1;
    }

    public int getSpinner2() {
        return spinner2;
    }

    public void setSpinner2(int spinner2) {
        this.spinner2 = spinner2;
    }

    public int getSpinner3() {
        return spinner3;
    }

    public void setSpinner3(int spinner3) {
        this.spinner3 = spinner3;
    }

    public int getSpinner5() {
        return spinner5;
    }

    public void setSpinner5(int spinner5) {
        this.spinner5 = spinner5;
    }

    public int getSpinner4() {
        return spinner4;
    }

    public void setSpinner4(int spinner4) {
        this.spinner4 = spinner4;
    }


}
