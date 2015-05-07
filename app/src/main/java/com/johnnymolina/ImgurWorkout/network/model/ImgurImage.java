package com.johnnymolina.ImgurWorkout.network.model;

import io.realm.RealmObject;

/**
 * Created by Johnny Molina on 3/25/2015.
 */
public class ImgurImage extends RealmObject {
    private String id;
    private String title;
    private String description;
    private String link;
    private String sysLink;
    private String album;
    private int length;



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

}
